package de.vw.paso.pls.datarequest.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import de.vw.paso.pll.preprocessing.TiWhFileType;
import de.vw.paso.pls.datarequest.AbstractDataRequestStrategy;
import de.vw.paso.pls.datarequest.DataRequestException;
import de.vw.paso.pls.datarequest.DataRequestInitException;

public class SshDataRequestStrategy extends AbstractDataRequestStrategy {

    private static final String SYSTEM = "PASO";

    private final String baseDir;

    private final String tiVeronArchiveDir;
    private final String tiPasoArchiveDir;

    private final String remoteInboxDir;
    private final String remoteOutboxDir;

    private final String requestFileWPath;
    private final String requestCode;

    private final SshClient sshClient;

    public SshDataRequestStrategy(SshClient ssh, String stage) {
        this.sshClient = ssh;
        this.requestCode = "P" + stage.charAt(0);
        String stageLowerCase = stage.toLowerCase();

        baseDir = "/home/fkgypbr/paso/tiwh/stage/" + stageLowerCase + "/";
        remoteInboxDir = baseDir + "inbox";
        remoteOutboxDir = baseDir + "outbox";
        requestFileWPath = remoteOutboxDir + "/DGZ.K2HR21.VERON.BEST";
        tiPasoArchiveDir = baseDir + "archiv";
        tiVeronArchiveDir = "/home/fkgypbr/tiwh/stage/" + stageLowerCase + "/archiv";
    }

    @Override
    public void deleteTiWhFiles() throws DataRequestException {
        LOG.info("Delete TiWh files");
        try {
            sshClient.clearDirectory(remoteOutboxDir);
            sshClient.clearDirectory(tiVeronArchiveDir);
        } catch (SftpException | JSchException e) {
            throw new DataRequestException("Could not delete files from TiWh", e);
        }
    }

    @Override
    public List<String> getFileNamesFromInbox() throws DataRequestException {
        LOG.info("Retrieve file names from the inbox at: {}", remoteInboxDir);
        try {
            return getPasoFiles();
        } catch (SftpException | JSchException e) {
            throw new DataRequestException("Could not retrieve file names from Inbox", e);
        }
    }

    @Override
    public List<File> getFilesFromInbox() throws DataRequestException {
        try {
            clearLocalResultTempDirectory();

            Collection<String> pasoFileNames = getPasoFiles();
            if (LOG.isDebugEnabled()) {
                for (String fileName : pasoFileNames) {
                    LOG.debug("File found: {}", fileName);
                }
            }

            Collection<String> fileNames = sshClient.gzipFiles(remoteInboxDir, pasoFileNames);
            if (LOG.isDebugEnabled()) {
                debugLogFolder(remoteInboxDir, "Files before zipping: {}");
            }

            List<File> files = sshClient.downloadFiles(remoteInboxDir, getLocalTempDir().toString(), fileNames);
            if (LOG.isDebugEnabled()) {
                debugLogTemp("Downloaded to TEMP: {}");
            }

            sshClient.remove(remoteInboxDir, fileNames);

            if (LOG.isDebugEnabled()) {
                debugLogFolder(remoteInboxDir, "Files before clearing: {}");
            }

            files = unzipFiles(files);
            if (LOG.isDebugEnabled()) {
                debugLogTemp("In TEMP unzipped: {}");
            }

            return files;
        } catch (SftpException | JSchException | IOException e) {
            throw new DataRequestException("Could not download files from inbox", e);
        }
    }

    @Override
    public void moveFileFromArchive(String productId) throws DataRequestException {
        try {
            int requestId = createRequestId(productId);
            LOG.info("Search Paso file in: {} for product id: {} with request id: {}", tiVeronArchiveDir, productId,
                    requestId);

            if (LOG.isDebugEnabled()) {
                debugLogFolder(tiVeronArchiveDir, "Veron dir: {}");
            }

            Optional<String> pasoFileOptional = sshClient.listFiles(tiVeronArchiveDir).stream()
                    .filter(file -> file.getFilename().contains("paso." + requestId))
                    .max(Comparator.comparing(file -> file.getAttrs().getATime()))
                    .map(ChannelSftp.LsEntry::getFilename);
            if (pasoFileOptional.isEmpty()) {
                return;
            }

            String pasoFile = pasoFileOptional.get();
            LOG.info("Paso file found: {}", pasoFile);

            Collection<LsEntry> files = sshClient.listFiles(tiPasoArchiveDir);
            Collection<String> fileNames = files.stream().map(LsEntry::getFilename).toList();
            sshClient.remove(tiPasoArchiveDir, fileNames);

            if (LOG.isDebugEnabled()) {
                debugLogFolder(tiPasoArchiveDir, "Files before copy: {}");
            }

            sshClient.copy(tiVeronArchiveDir, tiPasoArchiveDir, List.of(pasoFile));

            if (LOG.isDebugEnabled()) {
                debugLogFolder(tiPasoArchiveDir, "Files after copy: {}");
            }

            sshClient.unzip(tiPasoArchiveDir, pasoFile);

            if (LOG.isDebugEnabled()) {
                debugLogFolder(tiPasoArchiveDir, "Files after unzip: {}");
            }

            sshClient.remove(tiPasoArchiveDir, List.of(pasoFile));

            files = sshClient.listFiles(tiPasoArchiveDir);
            fileNames = files.stream().map(LsEntry::getFilename).filter(filename -> filename.endsWith(".ctl")).toList();

            sshClient.remove(tiPasoArchiveDir, fileNames);
            if (LOG.isDebugEnabled()) {
                debugLogFolder(tiPasoArchiveDir, "Files after ctrl remove: {}");
            }

            fileNames = files.stream().map(LsEntry::getFilename).filter(filename -> !filename.endsWith(".ctl"))
                    .filter(filename -> filename.contains(".PASO.")).toList();
            sshClient.move(tiPasoArchiveDir, remoteInboxDir, fileNames);
            if (LOG.isDebugEnabled()) {
                debugLogFolder(tiPasoArchiveDir, "Files after move: {}");
            }

            LOG.info("Moved TiWh files to PASO inbox");
            if (LOG.isDebugEnabled()) {
                debugLogFolder(remoteInboxDir, "Files after move in remote inbox: {}");
            }
        } catch (SftpException | JSchException e) {
            throw new DataRequestException("Could not move files from archive", e);
        }
    }

    @Override
    public void onInit() throws DataRequestInitException {
        try {
            sshClient.checkConnection(tiVeronArchiveDir);
        } catch (SftpException | JSchException e) {
            throw new DataRequestInitException("Connection check failed", e);
        }
    }

    @Override
    public void sendTiWhRequest(String productId) throws DataRequestException {
        try {
            int requestId = createRequestId(productId);
            LOG.info("Send request for product id: {} with request id: {}", productId, requestId);

            byte[] requestFile = createRequestFileBytes(productId, requestId);
            String requestFilePostFix = requestCode + requestId;
            String sendCommand = "sh " + baseDir + "send-order-file.sh " + requestFilePostFix;
            sshClient.uploadFileBytesAndSendCommand(requestFile, requestFileWPath + "." + requestFilePostFix,
                    sendCommand);
        } catch (SftpException | JSchException e) {
            throw new DataRequestException("Could not send TiWh request", e);
        }
    }

    private int createRequestId(String productId) {
        int requestId = productId.hashCode();
        if (requestId > 99999999) {
            throw new IllegalArgumentException(
                    "Request id is too long. TiWh does not seem to support ids over 8 digits.");
        }

        return requestId;
    }

    private byte[] createRequestFileBytes(String productId, int requestId) {
        StringWriter w = new StringWriter();
        w.append("ID        ").append(requestCode).append(String.valueOf(requestId)).append("\n");
        w.append("PROD       ").append(productId).append("\n");
        return w.getBuffer().toString().getBytes(StandardCharsets.US_ASCII);
    }

    private void debugLogFolder(String dir, String message) throws SftpException, JSchException {
        Collection<LsEntry> lsEntries = sshClient.listFiles(dir);
        for (LsEntry lsEntry : lsEntries) {
            LOG.debug(message, lsEntry.getFilename());
        }
    }

    private void debugLogTemp(String format) {
        File[] localFiles = getLocalTempDir().toFile().listFiles();
        if (localFiles == null) {
            return;
        }

        for (File localFile : localFiles) {
            LOG.debug(format, localFile);
            LOG.debug("File size: {} MB", localFile.length() / 1000);
        }
    }

    private List<String> getPasoFiles() throws SftpException, JSchException {
        return sshClient.listFiles(remoteInboxDir).stream().map(ChannelSftp.LsEntry::getFilename)
                .filter(fileName -> fileName.contains(SYSTEM)).filter(TiWhFileType::isKnownType).toList();
    }

    private void unzipFileToTarget(File source, File target) throws IOException {
        LOG.info("Unzip from {} to {}", source, target);
        try (FileInputStream in = new FileInputStream(source); GZIPInputStream gis = new GZIPInputStream(in)) {
            Files.copy(gis, target.toPath());
        }
    }

    private List<File> unzipFiles(Collection<File> files) throws IOException {
        List<File> allFiles = new ArrayList<>();
        for (File file : files) {
            String newFileName = file.getName().substring(0, file.getName().length() - 3);
            File targetFile = Paths.get(file.getParentFile().getAbsolutePath(), newFileName).toFile();

            unzipFileToTarget(file, targetFile);
            allFiles.add(targetFile);
        }

        return allFiles;
    }
}