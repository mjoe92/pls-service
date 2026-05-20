package de.vw.paso.pls.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.vw.paso.pls.datarequest.ssh.SshClient;
import de.vw.paso.pls.datarequest.ssh.SshCredStore;
import de.vw.paso.pls.model.dto.MbtFileDTO;

@Service
public class MbtImportService {

    private static final Logger LOG = LoggerFactory.getLogger(MbtImportService.class);

    private static final String MBT_TARGET_DIRECTORY = "/home/fkgypbr/paso/mbt/inbox";
    private static final String MBT_BASE_DIRECTORY = "/appl/rvs/usrdat";

    // content: 1 -> DZU.ZU0098A.FAM, 2 -> DZU.ZU0657A.PKZ.SORT, 3 -> DZU.ZU0098A.PRNHVS, 4 -> DZU.ZU0657A.PRZ.SORT
    private static final Map<String, Integer> FILE_NAMES = Map.of("DZU.R11K2H.ZU657A.FAMZIP", 1,
        "DZU.R11K2H.ZU657A.PKZZIP", 2, "DZU.R11K2H.ZU657A.PRNVZIP", 3, "DZU.R11K2H.ZU657A.PRZZIP", 4);
    public static final String MBT_SOURCE_DIRECTORY = "/mbt/";

    private final SshClient sshClient;
    private final String stage;
    private final Path localResultTempDir;

    public MbtImportService(SshCredStore sshCredStore, @Value("${stage}") String stage) {
        this.sshClient = new SshClient(sshCredStore);
        this.stage = stage;

        // setting up local directory
        String localTempDir = System.getProperty("java.io.tmpdir");
        localResultTempDir = Paths.get(localTempDir, "paso", "MBT_data");

        LOG.info("Check local temp folders for MBT import");
        if (localResultTempDir.toFile().exists()) {
            return;
        }

        LOG.info("Create local temp folder: {}", localResultTempDir.toAbsolutePath());
        boolean dirs = localResultTempDir.toFile().mkdirs();
        if (!dirs) {
            LOG.error("Couldn't create directory: {}", localResultTempDir);
        }
    }

    public Collection<MbtFileDTO> doImport() throws Exception {
        return "local".equalsIgnoreCase(stage) ? localImport() : sshImport();
    }

    private Collection<MbtFileDTO> localImport() throws URISyntaxException {
        List<MbtFileDTO> result = new ArrayList<>(4);
        for (String fileName : FILE_NAMES.keySet()) {
            URI uri = MbtImportService.class.getResource(MBT_SOURCE_DIRECTORY + fileName).toURI();
            Path path = Paths.get(uri);
            Entry<String, byte[]> entry = createStringEntry(path.toFile());

            MbtFileDTO mbtFile = new MbtFileDTO(entry.getKey(), entry.getValue(), Instant.now());
            result.add(mbtFile);
        }

        result.sort(Comparator.comparing(mbtFile -> FILE_NAMES.get(mbtFile.fileName())));
        return result;
    }

    private Collection<MbtFileDTO> sshImport() throws JSchException, SftpException {
        // we move the files to the correct directory
        Map<String, ChannelSftp.LsEntry> newFiles = moveFile();

        // we download the files into our temp directory
        Collection<File> mbtCompressedFiles = downloadFiles();

        // we extract the content of the files
        Collection<Map.Entry<String, byte[]>> byteArrays = mbtCompressedFiles.stream().map(this::createStringEntry)
            .filter(Objects::nonNull).toList();

        int size = byteArrays.size();
        if (size != FILE_NAMES.size()) {
            throw new IllegalStateException(FILE_NAMES.size() + "MBT files expected, " + size + " provided");
        }

        // we delete them from our temp directory
        clearLocalTempDirectory();

        return byteArrays.stream().sorted(Comparator.comparing(entry -> FILE_NAMES.get(entry.getKey()))).map(
            entry -> new MbtFileDTO(entry.getKey(), entry.getValue(),
                Instant.ofEpochSecond(newFiles.get(entry.getKey()).getAttrs().getMTime()))).toList();
    }

    public void cleanUpRemoteDirectory() {
        try {
            sshClient.clearMbtDirectory(MBT_TARGET_DIRECTORY, MBT_TARGET_DIRECTORY + "/old", FILE_NAMES.keySet());
        } catch (SftpException | JSchException e) {
            LOG.error("Exception happened while removing old mbt files", e);
        }
    }

    private Map.Entry<String, byte[]> createStringEntry(File file) {
        try (FileInputStream fs = new FileInputStream(file)) {
            return Map.entry(file.getName(), fs.readAllBytes());
        } catch (IOException e) {
            LOG.error("Error while loading file: {}", file.getName());
        }

        return null;
    }

    private Map<String, ChannelSftp.LsEntry> moveFile() throws JSchException, SftpException {
        return sshClient.moveMBTFiles(MBT_BASE_DIRECTORY, MBT_TARGET_DIRECTORY, FILE_NAMES.keySet());
    }

    private Collection<File> downloadFiles() throws SftpException, JSchException {
        return sshClient.downloadFilesMatchingPredicate(MBT_TARGET_DIRECTORY, localResultTempDir.toString(),
            file -> FILE_NAMES.containsKey(file.getFilename()));
    }

    private void clearLocalTempDirectory() {
        File[] subFiles = localResultTempDir.toFile().listFiles();
        if (subFiles == null) {
            return;
        }

        for (File subFile : subFiles) {
            subFile.delete();
        }
    }
}
