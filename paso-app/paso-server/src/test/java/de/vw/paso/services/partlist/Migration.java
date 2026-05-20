package de.vw.paso.services.partlist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled(
        "Just for importing old Part List from csv. Therefore change application-test.properties to db paso for the test run")
public class Migration extends AbstractEfsTests {

    @PersistenceContext
    private EntityManager entityManager;

    private VehiclePartListDTO vehiclePartList;
    private final Map<Long, Long> excelIdToDbId;

    public Migration() {
        excelIdToDbId = new HashMap<>();
    }

    enum Mengeneinheit {

        STUECK("Stück", "ST"), GRAMM("Gramm", "G");

        private final String shortcut;
        private final String excelText;

        Mengeneinheit(String excelText, String shortcut) {
            this.shortcut = shortcut;
            this.excelText = excelText;
        }

        public static String get(String text) {
            for (Mengeneinheit m : Mengeneinheit.values()) {
                if (m.excelText.equals(text)) {
                    return m.shortcut;
                }
            }

            return StringConstant.EMPTY;
        }
    }

    @BeforeEach
    public void initTestCase() {
        vehiclePartList = createVehiclePartList();
        VehicleConfigDTO config = vehiclePartList.getVehicleConfig();
        config.setVehicleProject(get5G0VehicleProject());
        saveVehicleConfigConsumer.saveVehicleConfig(config, null);
    }

    @Test
    public void migrate() throws Exception {
        File file = new File("src/test/resources/ExampleData.csv");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1));
        while (reader.ready()) {
            createNewEntry(reader.readLine());
        }

        reader.close();
    }

    private void createNewEntry(String rowString) {
        try {
            EfsElementMaraDTO mara = createMara(rowString);
            EfsElementDTO element = createNode(rowString, mara);

            saveEfsElementConsumer.saveEfsElement(element);
            excelIdToDbId.put(longConvert(rowString.split(StringConstant.SEMICOLON)[0]),
                    saveEfsElementConsumer.getResult().getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private EfsElementDTO createNode(String rowString, EfsElementMaraDTO mara) {
        String[] splitRows = getSplitRows(rowString);
        Long parentId = getParentId(longConvert(splitRows[1]));
        EfsElementDTO element = PartListFactory.createEfsElement(parentId, mara, Integer.parseInt(splitRows[6]),
                Mengeneinheit.get(splitRows[7]), vehiclePartList.getId());
        element.setWeightControlFlag(WeightControlFlag.getType(splitRows[8]));
        element.setPrNumberRule(splitRows[13]);
        element.setAggregate(splitRows[15]);
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date date = format.parse(splitRows[16]);
            element.setBeginDate(date);
        } catch (ParseException ignored) {
        }

        try {
            Date date = format.parse(splitRows[17]);
            element.setEndDate(date);
        } catch (ParseException ignored) {
        }

        element.setBeginDateKey(splitRows[18]);
        element.setEndDateKey(splitRows[19]);
        element.setConstructionsGroup(splitRows[22]);
        element.setSetKey(splitRows[23]);
        element.setCostGroup(splitRows[24]);
        element.setProductStructure(splitRows[25]);

        return element;
    }

    private Long longConvert(String value) {
        return value.equals(StringConstant.EMPTY) ? null : Long.valueOf(value);
    }

    private Long getParentId(Long id) {
        return excelIdToDbId.get(id);
    }

    private EfsElementMaraDTO createMara(String rowString) {
        String[] splitRows = getSplitRows(rowString);
        EfsElementMaraDTO mara = PartListFactory.createEfsElementMara(splitRows[3], createPartNumber(splitRows[2]));
        mara.setWeightWeightedTe(Double.valueOf(splitRows[10]));
        mara.setWeightEstimatedTe(Double.valueOf(splitRows[11]));
        mara.setWeightCalculatedTe(Double.valueOf(splitRows[9]));
        mara.setWeightWeightedProd(Double.valueOf(splitRows[12]));
        DateFormat format = new SimpleDateFormat("ddMMyy");
        try {
            Date date = format.parse(splitRows[20]);
            mara.setDrawingDate(date);
        } catch (ParseException ignored) {
        }

        mara.setConstructionsState(splitRows[21]);
        mara.setDescription2De(splitRows[4]);
        mara.setDescription1En(splitRows[26]);
        mara.setDescription2En(splitRows[27]);
        return mara;
    }

    private String[] getSplitRows(String rowString) {
        String[] splitRows = rowString.split(StringConstant.SEMICOLON);
        while (splitRows.length < 28) {
            splitRows = List.of(splitRows, StringConstant.EMPTY).toArray(new String[0]);
        }

        return splitRows;
    }

    private String createPartNumber(String entry) {
        entry = entry.replace(StringConstant.SPACE, StringConstant.EMPTY);
        if (entry.length() > 12) {
            throw new IllegalArgumentException("entry for part number too long" + entry);
        }

        StringBuilder entryBuilder = new StringBuilder(entry);
        while (entryBuilder.length() < 11) {
            entryBuilder.append(StringConstant.DASH);
        }

        return entryBuilder.toString();
    }
}
