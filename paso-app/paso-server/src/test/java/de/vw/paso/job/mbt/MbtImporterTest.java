package de.vw.paso.job.mbt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.exception.MBTImportException;
import de.vw.paso.masterdata.domain.Product;
import de.vw.paso.pr.PrNumber;
import de.vw.paso.pr.PrNumberFamily;
import de.vw.paso.repository.masterdata.PrNumberAssignmentRepository;
import de.vw.paso.repository.masterdata.PrNumberFamilyRepository;
import de.vw.paso.repository.masterdata.PrNumberRepository;
import de.vw.paso.repository.masterdata.ProductRepository;
import de.vw.paso.utility.StringConstant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class MbtImporterTest extends AbstractServiceTests {

    private static final String FIRST_KEY = "XX1";
    private static final String LAST_KEY = "XX5";
    private static final Collection<String> KEYS = List.of(FIRST_KEY, "XX2", "XX3", "XX4", LAST_KEY);

    @Autowired
    private MbtImporter mbtImporter;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private PrNumberRepository prNumberRepository;
    @Autowired
    private PrNumberFamilyRepository prNumberFamilyRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PrNumberAssignmentRepository prNumberAssignmentRepository;

    @Test
    public void importFromNonExistingFile() {
        assertThrows(MBTImportException.class, () -> doImport(StringConstant.EMPTY, mbtImporter));
    }

    @Test
    public void importPrNumberFamiliesTest() {
        try {
            doImport(MbtImporter.PR_FAMILIE, mbtImporter);

            String result = prNumberFamilyRepository.findFirstByNameOrderByIdDesc(LAST_KEY).getName();
            assertEquals(LAST_KEY, result);
        } catch (IOException | MBTImportException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void importPrNumberTest() {
        try {
            Collection<PrNumberFamily> prNumberFamilies = KEYS.stream().map(this::createPrNumberFamily).toList();

            prNumberFamilyRepository.saveAll(prNumberFamilies);

            doImport(MbtImporter.PR_NUMMER, mbtImporter);

            String toCheck = prNumberRepository.findByNameIn(KEYS).stream().map(PrNumber::getName).distinct()
                    .findFirst().orElseThrow();

            assertEquals(FIRST_KEY, toCheck);
        } catch (IOException | MBTImportException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void importProductTest() {
        try {
            doImport(MbtImporter.PRODUKT, mbtImporter);
            Collection<Product> products = productRepository.findByProductKeyIn(KEYS);
            assertEquals(KEYS, products.stream().map(Product::getProductKey).toList());
        } catch (IOException | MBTImportException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void importPrNumberAssignmentTest() {
        try {
            PrNumberFamily prNumberFamily = new PrNumberFamily();
            prNumberFamily.setName("DEF");

            Collection<Product> products = new ArrayList<>(KEYS.size());
            Collection<PrNumber> prNumbers = new ArrayList<>(KEYS.size());
            Collection<String> prNumberNames = new ArrayList<>(KEYS.size());
            for (String key : KEYS) {
                products.add(createProduct(key));
                prNumberNames.add(key);
                PrNumber prNumber = createPrNumber(key, prNumberFamily);
                prNumbers.add(prNumber);
            }

            prNumberFamilyRepository.save(prNumberFamily);
            productRepository.saveAll(products);
            prNumberRepository.saveAll(prNumbers);

            doImport(MbtImporter.PR_NUMMER_ASSIGNMENT, mbtImporter);

            Collection<String> toCheck = prNumberAssignmentRepository.findAll().stream()
                    .map(assignment -> assignment.getPrNumber().getName()).filter(prNumberNames::contains).distinct()
                    .toList();

            assertEquals(KEYS, toCheck);
        } catch (IOException | MBTImportException e) {
            throw new RuntimeException(e);
        }
    }

    private PrNumber createPrNumber(String name, PrNumberFamily prNumberFamily) {
        PrNumber prNumber = new PrNumber();
        prNumber.setName(name);
        prNumber.setPrNumberFamily(prNumberFamily);

        return prNumber;
    }

    private Product createProduct(String name) {
        Product product = new Product();
        product.setProductKey(name);
        product.setProductType(StringConstant.EMPTY);
        product.setSetVersionId(1L);
        product.setUserCreate("SYSTEM");
        product.setTimestampCreate(new Timestamp(System.currentTimeMillis()));
        return product;
    }

    private PrNumberFamily createPrNumberFamily(String name) {
        PrNumberFamily prNumberFamily = new PrNumberFamily();
        prNumberFamily.setName(name);

        return prNumberFamily;
    }

    private void doImport(String fileName, MbtImporter mbtImporter) throws IOException, MBTImportException {
        Resource resource = resourceLoader.getResource(
                "classpath:scheduledmbtimportertestfiles" + File.separator + fileName);
        mbtImporter.doImport(resource.getContentAsByteArray());
    }
}
