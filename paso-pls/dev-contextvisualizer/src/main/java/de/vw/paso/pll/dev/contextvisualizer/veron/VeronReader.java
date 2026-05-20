package de.vw.paso.pll.dev.contextvisualizer.veron;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VeronReader {

  public VeronElement readVeronFile(File veronFile) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(veronFile), "UTF-8"))) {
      reader.readLine();
      VeronElement root = null;
      Map<String, VeronElement> veronElementMap = new HashMap<>();
      List<VeronElement> elementsToCheckParent = new ArrayList<>();
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        String[] data = line.split("\t");
        VeronElement ve = new VeronElement(data);
        String id = ve.get(VeronFields.Stuecklisten_ID);
        String parentId = ve.get(VeronFields.ZSB_ID);
        veronElementMap.put(id, ve);
        if (StringUtils.isEmpty(parentId)) {
          root = ve;
        } else {
          VeronElement parent = veronElementMap.get(parentId);
          if (parent != null) {
            parent.add(ve);
          } else {
            elementsToCheckParent.add(ve);
          }
        }
      }
      elementsToCheckParent.forEach(e -> {
        String parentId = e.get(VeronFields.ZSB_ID);
        VeronElement parent = veronElementMap.get(parentId);
        if (parent != null) {
          parent.add(e);
        } else {
          throw new RuntimeException("no parent found for:" + parentId);
        }
      });
      return root;
    }
  }
}
