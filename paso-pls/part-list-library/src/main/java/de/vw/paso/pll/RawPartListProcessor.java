package de.vw.paso.pll;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.vw.paso.pll.preprocessing.PartListPreprocessor;
import de.vw.paso.pll.preprocessing.PreprocessingContext;
import de.vw.paso.pll.preprocessing.reader.FileChecker;

public final class RawPartListProcessor {

    private static final PartListPreprocessor PART_LIST_PREPROCESSOR = new PartListPreprocessor();
    private static final FileChecker FILE_CHECKER = new FileChecker();

    /** 32MiB to reserve more space when creating the zipped file. */
    private static final int MIB32 = 33554432;
    /** Charset used by TiWh and their part list files. */
    private static final Charset CP_1252 = Charset.forName("Cp1252");

    private RawPartListProcessor() {
        throw new IllegalArgumentException("Should not be instantiated");
    }

    public static byte[] processFiles(final List<File> files) throws IOException {
        final PreprocessingContext context = new PreprocessingContext(files);
        FILE_CHECKER.checkCompleteness(context);

        ByteArrayOutputStream out = new ByteArrayOutputStream(MIB32);
        try (out; ZipOutputStream zipOut = new ZipOutputStream(out);
            OutputStreamWriter wr = new OutputStreamWriter(zipOut, CP_1252)) {
            ZipEntry zipEntry = new ZipEntry("result.ppf");
            zipOut.putNextEntry(zipEntry);

            PART_LIST_PREPROCESSOR.processPartLists(context, wr);
        }
        return out.toByteArray();
    }
}
