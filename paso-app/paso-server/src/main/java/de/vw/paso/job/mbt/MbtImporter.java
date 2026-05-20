package de.vw.paso.job.mbt;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.vw.paso.exception.MBTImportException;
import de.vw.paso.mapper.PrNumberFamilyMapper;
import de.vw.paso.mapper.PrNumberMapper;
import de.vw.paso.masterdata.domain.Product;
import de.vw.paso.pr.PrNumber;
import de.vw.paso.pr.PrNumberAssignment;
import de.vw.paso.pr.PrNumberFamily;
import de.vw.paso.repository.masterdata.PrNumberAssignmentRepository;
import de.vw.paso.repository.masterdata.PrNumberFamilyRepository;
import de.vw.paso.repository.masterdata.PrNumberRepository;
import de.vw.paso.repository.masterdata.ProductRepository;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MbtImporter {

    private final static Logger LOG = LoggerFactory.getLogger(MbtImporter.class);

    final static String PRODUKT = "DZU.ZU0657A.PKZ.SORT";
    final static String PR_FAMILIE = "DZU.ZU0098A.FAM";
    final static String PR_NUMMER = "DZU.ZU0098A.PRNHVS";
    final static String PR_NUMMER_ASSIGNMENT = "DZU.ZU0657A.PRZ.SORT";

    private final static int PR_NUMBER_START = 2, PR_NUMBER_END = 5;
    private final static int STATUS_START = 5, STATUS_END = 8;
    private final static int PR_FAMILY_START = 10, PR_FAMILY_END = 13;
    private final static int LANG_CODE_START = 15, LANG_CODE_END = 20;
    private final static int START_DATE_START = 34, START_DATE_END = 42;
    private final static int END_DATE_START = 42, END_DATE_END = 50;
    private final static int DESC_START = 58;
    //todo: START_DATE_INDEX and END_DATE_INDEX might be switched -> in db the start date >= end date (always) -> relevant?
    private final static int PR_NUMBER_INDEX = 0, PR_FAMILY_INDEX = 2, LANG_CODE_INDEX = 3, DESC_INDEX = 6;

    private final static int PRODUCT_KEY_START = 2, PRODUCT_KEY_END = 8;
    private final static int PRODUCT_TYPE_START = 22, PRODUCT_TYPE_END = 25;
    private final static int PR_NUMBER_ID_START = 8, PR_NUMBER_ID_END = 11;
    private final static int PRODUCT_STATUS_START = 11, PRODUCT_STATUS_END = 12;
    private final static int EINSATZ_START = 42, EINSATZ_END = 50;
    private final static int ENTFALL_START = 50, ENTFALL_END = 58;
    private final static int START_KEY_START = 58, START_KEY_END = 69;
    private final static int END_KEY_START = 69, END_KEY_END = 80;
    private final static int PRODUCT_DESC_START = 80, PRODUCT_DESC_END = 160;

    private final static int PR_FAMILY_NAME_START = 2, PR_FAMILY_NAME_END = 5;
    private final static int PR_FAMILY_LANG_CODE_START = 7, PR_FAMILY_LANG_CODE_END = 12;
    private final static int FAMILY_DESC_START = 42;

    private final static String ENG_CODE = "en_VW";
    private final static String GER_CODE = "de_VW";
    private final static DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final int BATCH_SIZE = 2_000;

    private final EntityManager entityManager;
    private final PrNumberRepository prNumberRepository;
    private final PrNumberFamilyRepository prNumberFamilyRepository;
    private final ProductRepository productRepository;
    private final PrNumberAssignmentRepository prNumberAssignmentRepository;

    private int savedCount;
    private int containedCount;

    public MbtImporter(EntityManager entityManager, PrNumberRepository prNumberRepository,
            PrNumberFamilyRepository prNumberFamilyRepository, ProductRepository productRepository,
            PrNumberAssignmentRepository prNumberAssignmentRepository) {
        this.entityManager = entityManager;
        this.prNumberRepository = prNumberRepository;
        this.prNumberFamilyRepository = prNumberFamilyRepository;
        this.productRepository = productRepository;
        this.prNumberAssignmentRepository = prNumberAssignmentRepository;
    }

    @Transactional
    void doImport(byte[] data) throws MBTImportException {
        LOG.info("Importing {} bytes", data.length);

        try (ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(data))) {
            LOG.info("MBT import started");
            long start = System.currentTimeMillis();
            ZipEntry entry = zipIn.getNextEntry();
            if (entry == null) {
                throw new MBTImportException("Invalid zip archive");
            }

            InputStreamReader inputReader = new InputStreamReader(zipIn, Charset.forName("Cp1252"));
            try (BufferedReader reader = new BufferedReader(inputReader)) {
                String name = entry.getName();
                switch (name) {
                    case PRODUKT:
                        saveProducts(reader);
                        break;
                    case PR_FAMILIE:
                        prNumberAssignmentRepository.deleteUnusedEntries();
                        savePrNumberFamilies(reader);
                        break;
                    case PR_NUMMER:
                        savePrNumbers(reader);
                        break;
                    case PR_NUMMER_ASSIGNMENT:
                        savePrAssignments(reader);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + name);
                }
            }

            LOG.info("MBT import ended in {} s", (System.currentTimeMillis() - start) / 1000);
        } catch (IOException e) {
            throw new MBTImportException("Unexpected exception occurred during import", e);
        }
    }

    private void saveProducts(BufferedReader reader) {
        LOG.info("Saving Products");

        Map<String, String> productKeyAndType = new HashMap<>();
        Collection<String> rows = new HashSet<>();
        reader.lines().forEach(row -> {
            String key = row.substring(PRODUCT_KEY_START, PRODUCT_KEY_END).trim();
            String type = row.substring(PRODUCT_TYPE_START, PRODUCT_TYPE_END).trim();
            productKeyAndType.put(key, type);
            rows.add(key);
        });

        Collection<Product> byProductKeyIn = productRepository.findByProductKeyIn(productKeyAndType.keySet());
        int size = byProductKeyIn.size();
        Collection<String> oldProductKeys = new HashSet<>(size);
        Collection<String> oldProductTypes = new HashSet<>(size);

        byProductKeyIn.stream().parallel().forEach(entry -> {
            oldProductKeys.add(entry.getProductKey());
            oldProductTypes.add(entry.getProductType());
        });

        Collection<Product> products = rows.stream().parallel()
                .filter(produktKey -> !oldProductKeys.contains(produktKey) || !oldProductTypes.contains(produktKey))
                .map(produktKey -> createProduct(produktKey, productKeyAndType)).collect(Collectors.toSet());

        LOG.info("Saving products, size = {}", products.size());
        Collection<Product> savedProducts = productRepository.saveAll(products);

        flushAndClearRepository(productRepository);

        LOG.info("Saved {} products", savedProducts.size());
    }

    private Product createProduct(String produktKey, Map<String, String> productKeyAndType) {
        Product product = new Product();

        // this value is temporary
        product.setSetVersionId(1L);

        // todo: THIS IS FOR AUTOMATIC IMPORT ONLY WE HAVE TO CHANGE THIS IF WE WANT TO ENABLE MANUAL IMPORT IN THE FUTURE
        product.setChange("SYSTEM");

        String trimProductKey = produktKey.trim();
        product.setProductKey(trimProductKey);

        String productKey = productKeyAndType.get(trimProductKey);
        product.setProductType(productKey);

        return product;
    }

    private void savePrNumberFamilies(BufferedReader reader) {
        containedCount = 0;
        LOG.info("Saving Pr Number Families");

        Map<String, String> descEngMap = new HashMap<>();
        Collection<String[]> rows = new ArrayList<>();

        reader.lines().forEach(line -> createPrFamilyName(line, descEngMap, rows));

        for (String[] row : rows) {
            String name = row[0];
            String de = row[1];
            String en = descEngMap.get(name);

            boolean exists = prNumberFamilyRepository.existsByNameAndDescriptionDeAndDescriptionEn(name, de, en);
            if (exists) {
                containedCount++;
                continue;
            }

            PrNumberFamily prNumberFamily = PrNumberFamilyMapper.toEntity(name, de, en);
            prNumberFamilyRepository.save(prNumberFamily);
        }

        flushAndClearRepository(prNumberFamilyRepository);

        LOG.info("{} PR-Families saved and {} are already exist", rows.size() - containedCount, containedCount);
    }

    private void createPrFamilyName(String row, Map<String, String> descEngMap, Collection<String[]> rows) {
        String prFamilyName = row.substring(PR_FAMILY_NAME_START, PR_FAMILY_NAME_END).trim();
        String langCode = row.substring(PR_FAMILY_LANG_CODE_START, PR_FAMILY_LANG_CODE_END).trim();
        String desc = getTextOrNullIfBlank(row.substring(FAMILY_DESC_START).trim());

        if (ENG_CODE.equals(langCode)) {
            descEngMap.put(prFamilyName, desc);
        }

        if (GER_CODE.equals(langCode)) {
            rows.add(new String[] { prFamilyName, desc });
        }
    }

    private void savePrNumbers(BufferedReader reader) {
        savedCount = 0;
        containedCount = 0;
        LOG.info("Saving PR-Numbers");

        Map<String, String> descEngMap = new HashMap<>();
        Collection<String> prNumberNames = new HashSet<>();
        Collection<String> prNumberFamilyNames = new HashSet<>();

        // read and parse lines
        Collection<String[]> rows = reader.lines()
                .map(row -> createPrNumberDetails(row, prNumberNames, prNumberFamilyNames)).peek(columns -> {
                    // capture English descriptions
                    if (ENG_CODE.equals(columns[LANG_CODE_INDEX])) {
                        descEngMap.put(columns[PR_NUMBER_INDEX], columns[DESC_INDEX]);
                    }
                }).filter(columns -> GER_CODE.equals(columns[LANG_CODE_INDEX])).collect(Collectors.toSet());

        // create and update PrNumbers
        Map<String, PrNumberFamily> latestPrFamilies = HashMap.newHashMap(1024);
        Collection<String> skippedPrFamilies = new ArrayList<>(1024);
        for (String[] row : rows) {
            String prFamilyName = row[PR_FAMILY_INDEX];
            if (skippedPrFamilies.contains(prFamilyName)) {
                continue;
            }

            PrNumberFamily prFamily = latestPrFamilies.getOrDefault(prFamilyName,
                    prNumberFamilyRepository.findFirstByNameOrderByIdDesc(prFamilyName));
            if (prFamily == null) {
                LOG.info("No PR-Number family with name {}", prFamilyName);
                skippedPrFamilies.add(prFamilyName);
                continue;
            }

            String name = row[PR_NUMBER_INDEX];
            String de = row[DESC_INDEX];
            String en = descEngMap.get(name);
            if (prNumberRepository.existsByNameAndDescriptionDeAndDescriptionEn(name, de, en)) {
                containedCount++;
                continue;
            }

            PrNumber prNumber = PrNumberMapper.toEntity(name, de, en, prFamily);

            prNumberRepository.save(prNumber);
            savedCount++;

            if (savedCount % BATCH_SIZE == 0) {
                flushAndClearRepository(prNumberRepository);
            }
        }

        flushAndClearRepository(prNumberRepository);

        LOG.info("{} PR-Numbers saved and {} are already exist", savedCount, containedCount);
    }

    private String[] createPrNumberDetails(String row, Collection<String> prNumberNames,
            Collection<String> prNumberFamilyNames) {
        String prNumberName = row.substring(PR_NUMBER_START, PR_NUMBER_END).trim();
        String status = row.substring(STATUS_START, STATUS_END).trim();
        String prNumberFamilyName = row.substring(PR_FAMILY_START, PR_FAMILY_END).trim();
        String langCode = row.substring(LANG_CODE_START, LANG_CODE_END).trim();
        String startDate = row.substring(START_DATE_START, START_DATE_END).trim();
        String endDate = row.substring(END_DATE_START, END_DATE_END).trim();
        String description = getTextOrNullIfBlank(row.substring(DESC_START).trim());

        prNumberNames.add(prNumberName);
        prNumberFamilyNames.add(prNumberFamilyName);

        return new String[] { prNumberName, status, prNumberFamilyName, langCode, startDate, endDate, description };
    }

    private void savePrAssignments(BufferedReader reader) {
        savedCount = 0;
        containedCount = 0;
        LOG.info("Saving Pr Number Zuordnung");

        Map<String, Product> products = new HashMap<>();
        Map<String, PrNumber> prNumbers = new HashMap<>();

        Collection<String> skippedProducts = new ArrayList<>(512);
        Collection<String> skippedPrNumbers = new ArrayList<>(8192);

        String row;
        try {
            while ((row = reader.readLine()) != null) {
                savePrAssignment(row, products, prNumbers, skippedProducts, skippedPrNumbers);
                savedCount++;

                if (savedCount % BATCH_SIZE == 0) {
                    closeAndFlushAssignments(products, prNumbers, skippedProducts, skippedPrNumbers);

                    LOG.info("{} PR-Numbers saved in batch", savedCount);
                }
            }
        } catch (Exception e) {
            LOG.error("Error occurred during assignment saving", e);
        }

        closeAndFlushAssignments(products, prNumbers, skippedProducts, skippedPrNumbers);

        LOG.info("Saving {} PrNumberAssignment(s) has been completed ({} already contained).", savedCount,
                containedCount);
    }

    private void closeAndFlushAssignments(Map<String, Product> products, Map<String, PrNumber> prNumbers,
            Collection<String> skippedProducts, Collection<String> skippedPrNumbers) {
        flushAndClearRepository(prNumberAssignmentRepository);

        products.clear();
        prNumbers.clear();
        skippedProducts.clear();
        skippedPrNumbers.clear();
    }

    private void savePrAssignment(String row, Map<String, Product> products, Map<String, PrNumber> prNumbers,
            Collection<String> skippedProducts, Collection<String> skippedPrNumbers) {
        String productKey = row.substring(PRODUCT_KEY_START, PRODUCT_KEY_END).trim();
        if (skippedProducts.contains(productKey)) {
            return;
        }

        Product product = products.computeIfAbsent(productKey, productRepository::findOneByProductKey);
        if (product == null) {
            skippedProducts.add(productKey);
            LOG.warn("No Product found for key in MBT file: {}", productKey);
            return;
        }

        String prNumberId = row.substring(PR_NUMBER_ID_START, PR_NUMBER_ID_END);
        if (skippedPrNumbers.contains(prNumberId)) {
            return;
        }

        PrNumber prNumber = prNumbers.computeIfAbsent(prNumberId, prNumberRepository::findFirstByNameOrderByIdDesc);
        if (prNumber == null) {
            skippedPrNumbers.add(prNumberId);
            LOG.warn("No PR-Number found for key in MBT file: {}", prNumberId);
            return;
        }

        LocalDate startDate = parseDate(row.substring(EINSATZ_START, EINSATZ_END));
        LocalDate endDate = parseDate(row.substring(ENTFALL_START, ENTFALL_END));
        String status = row.substring(PRODUCT_STATUS_START, PRODUCT_STATUS_END);
        String description = getTextOrNullIfBlank(row.substring(PRODUCT_DESC_START, PRODUCT_DESC_END));
        boolean exists = prNumberAssignmentRepository.existsByProductAndPrNumberAndStartDateAndEndDateAndStatusAndDescription(
                product, prNumber, startDate, endDate, status, description);
        if (exists) {
            containedCount++;
            return;
        }

        PrNumberAssignment assignment = new PrNumberAssignment();
        assignment.setProduct(product);
        assignment.setPrNumber(prNumber);
        assignment.setStartDate(startDate);
        assignment.setEndDate(endDate);
        assignment.setDescription(description);
        assignment.setStatus(status);

        String startKey = getTextOrNullIfBlank(row.substring(START_KEY_START, START_KEY_END));
        assignment.setStartKey(startKey);

        String endKey = getTextOrNullIfBlank(row.substring(END_KEY_START, END_KEY_END));
        assignment.setEndKey(endKey);

        prNumberAssignmentRepository.save(assignment);
    }

    private String getTextOrNullIfBlank(String text) {
        return StringUtils.defaultIfBlank(text.trim(), null);
    }

    private LocalDate parseDate(String dateText) {
        if (!StringUtils.isBlank(dateText)) {
            try {
                return LocalDate.parse(dateText, FORMAT);
            } catch (Exception e) {
                LOG.error("Could not parse date: {}", dateText);
            }
        }

        return LocalDate.now();
    }

    private void flushAndClearRepository(JpaRepository<?, ?> repository) {
        repository.flush();
        entityManager.clear();
    }
}
