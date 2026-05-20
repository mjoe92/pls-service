package de.vw.paso.pll.preprocessing;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vw.paso.pll.preprocessing.formats.ppf.NodePPF;
import de.vw.paso.pll.preprocessing.formats.raw.MaraWrapper;
import de.vw.paso.pll.preprocessing.reader.EbkVSDReader;
import de.vw.paso.pll.preprocessing.reader.EbomReader;
import de.vw.paso.pll.preprocessing.reader.MaKTxReader;
import de.vw.paso.pll.preprocessing.reader.MaraReader;
import de.vw.paso.pll.preprocessing.reader.NodeReader;
import de.vw.paso.pll.preprocessing.reader.filter.ValidTypeFilter;
import de.vw.paso.pll.preprocessing.writer.PreprocessingContextWriter;

public class PartListPreprocessor {

    private static final Logger LOG = LoggerFactory.getLogger(PartListPreprocessor.class);

    private static final int NODE_BATCH_SIZE = 100;
    private static final PreprocessingContextWriter WRITER = new PreprocessingContextWriter();

    /**
     * This method will process and transform the context so that a pre processed part list can be written from it.
     *
     * <p>
     * It will do the following steps:
     * <ul>
     *   <li>
     *     1. Check for completness - Are all files there?
     *   </li>
     *   <li>
     *    2. Read/filter nodes and create a tree structure from it
     *   </li>
     *   <li>
     *     3. Read ebom and extend tree structure
     *   </li>
     *   <li>
     *     4. Read ebk and add substructures to ebom nodes
     *   </li>
     *   <li>
     *       5. Add MARA information to the tree
     *   </li>
     *   <li>
     *        6. Add translations (GER and ENG) to mara
     *   </li>
     *   <li>
     *        7. Do some cleanup(remove empty nodes)
     *   </li>
     * </ul>
     *
     * @param ctx
     *     context containing the files and the result
     * @throws PreprocessingException
     *     if there is a problem processing the files
     * @throws IOException
     *     if files cannot be read
     * @see de.vw.paso.pll.preprocessing.writer.PreprocessingContextWriter
     */
    public void processPartLists(PreprocessingContext ctx, Writer out) throws IOException {
        try (MaraReader maraReader = new MaraReader(); EbkVSDReader ebkReader = new EbkVSDReader();
            EbomReader ebomReader = new EbomReader(); MaKTxReader makTxReader = new MaKTxReader()) {

            Map<String, Long> maraIndex = maraReader.indexMara(ctx);
            Multimap<String, Long> ebkIndex = ebkReader.indexEbk(ctx);
            Multimap<String, Long> ebomIndex = ebomReader.indexEbom(ctx);
            Table<String, String, Long> makTxIndex = makTxReader.indexMakTx(ctx);

            ebomReader.readRulesFast(ctx);

            List<List<NodePPF>> batchList = readNodeStructure(ctx);

            WRITER.writeHeaderHeader(ctx, out);
            WRITER.writePrNumberRules(ctx, out);

            Iterator<List<NodePPF>> batch = batchList.iterator();
            int batchCount = batchList.size();
            int batchIndex = 0;
            while (batch.hasNext()) {
                LOG.debug("Processing {}/{}", batchIndex + 1, batchCount);

                List<NodePPF> currentBatch = batch.next();
                batch.remove();
                Map<String, NodePPF> currentNodeMap = new HashMap<>();
                currentBatch.forEach(node -> currentNodeMap.put(node.getNodeId(), node));

                ebomReader.readEbomMara(ctx, currentNodeMap, ebomIndex);
                ebkReader.readEbkVSD(ctx, currentNodeMap, ebkIndex);
                Map<String, MaraWrapper> maraMap = maraReader.readMara(ctx, currentNodeMap, maraIndex);
                makTxReader.readTranslations(ctx, maraMap, makTxIndex);

                Iterator<NodePPF> itr = currentBatch.iterator();
                Map<String, Boolean> emptyNodeMap = new HashMap<>();
                while (itr.hasNext()) {
                    NodePPF nextNode = itr.next();
                    Boolean isEmpty = emptyNodeMap.get(nextNode.getNodeId());
                    if (isEmpty == null) {
                        isEmpty = removeIfNoChildren(nextNode, currentNodeMap, emptyNodeMap);
                    }
                    if (!isEmpty) {
                        WRITER.writeNode(nextNode, out);
                    } else {
                        LOG.debug("Node has no children, removing node with id: {}", nextNode.getNodeId());
                    }
                }
                batchIndex++;
                out.flush();
            }
        }
    }

    private boolean removeIfNoChildren(NodePPF node, Map<String, NodePPF> nodeMap, Map<String, Boolean> emptyNodeMap) {
        for (NodePPF child : new ArrayList<>(node.getChildren())) {
            removeIfNoChildren(child, nodeMap, emptyNodeMap);
        }

        if (node.getChildren().isEmpty() && node.getEboms().isEmpty()) {
            NodePPF parentNode = nodeMap.get(node.getParentNodeId());
            if (parentNode != null) {
                parentNode.getChildren().remove(node);
            }
            emptyNodeMap.put(node.getNodeId(), true);
            return true;
        }
        emptyNodeMap.put(node.getNodeId(), false);
        return false;
    }

    /**
     * Reads the nodes from raw file and creates a tree structure from them. Root node will be stored in the PreprocessingContext.
     * <p>
     * Nodes have to be processed first
     *
     * @param ctx
     *     the context containing the file
     * @throws IOException
     *     if the file cannot be read
     */
    private List<List<NodePPF>> readNodeStructure(PreprocessingContext ctx) throws IOException {
        try (NodeReader nodeReader = new NodeReader()) {
            nodeReader.addFilter("Type is 'Z_DOKU' or 'Z_LAW'", new ValidTypeFilter("Z_DOKU", "Z_LAW").negate());
            nodeReader.addFilter("Node label is 'Software'", row -> !row.getNodeLabel().contains("SOFTWARE"));
            nodeReader.addFilter("Node label is 'DOKUMENTATION'", row -> !row.getNodeLabel().contains("DOKUMENTATION"));
            List<NodePPF> nodes = nodeReader.readNodeStructure(ctx);
            return split(nodes);
        }
    }

    private <T> List<List<T>> split(List<T> list) {
        List<List<T>> result = new ArrayList<>(Math.max(list.size() / 100, 1));
        List<List<T>> partition = Lists.partition(list, NODE_BATCH_SIZE);

        for (List<T> subList : partition) {
            result.add(new ArrayList<>(subList));
        }
        return result;
    }

}
