package de.vw.paso.pls.datarequest.ssh;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The SshClient encapsulates the JSCH library and all access to it.
 * It is used to connect to a remote host and perform several file and exec operations.
 * It is responsible for session and channel management (open and close) and does not provide
 * that capability to the outside.
 *
 * @implNote The JSCH Library is used, which is completely undocumented.
 * All the code here has been written and tested to the best of our ability,
 * but written without knowledge of best practices, since there are none.
 */
public class SshClient {

    private static final Logger LOG = LoggerFactory.getLogger(SshClient.class);

    private static final String FILE_TRANSFER_PROTOCOL = "sftp";
    private static final String EXEC_PROTOCOL = "exec";

    private static final int TIMEOUT_SESSION_MS = 180000;
    private static final int TIMEOUT_CHANNEL_MS = 60000;

    private final String host;
    private final int port;
    private final String userName;
    private final String password;

    public SshClient(SshCredStore sshCredStore) {
        host = sshCredStore.getHost();
        port = sshCredStore.getPort();
        userName = sshCredStore.getUserName();
        password = sshCredStore.getPassword();
    }

    public void checkConnection(String dir) throws JSchException, SftpException {
        LOG.info("Check connection. Will list files of directory: {} as a test", dir);

        Session session = null;
        ChannelSftp fileChannel = null;
        try {
            session = openSession();
            fileChannel = openFileChannel(session);

            Collection<LsEntry> lsEntries = getLsFiles(fileChannel, dir);
            LOG.info("Successfully connected. Files: {}", lsEntries.size());
        } finally {
            closeFileChannel(fileChannel);
            closeSession(session);
        }
    }

    public void clearDirectory(String directory) throws SftpException, JSchException {
        LOG.info("Clear directory: {}", directory);

        Session session = null;
        ChannelSftp fileChannel = null;
        try {
            session = openSession();
            fileChannel = openFileChannel(session);

            Collection<LsEntry> lsEntries = getLsFiles(fileChannel, directory);
            for (LsEntry fileEntry : lsEntries) {
                rm(fileChannel, directory + "/" + fileEntry.getFilename());
            }
        } finally {
            closeFileChannel(fileChannel);
            closeSession(session);
        }
    }

    public void clearMbtDirectory(String targetDir, String dirToCompare, Collection<String> fileNames)
            throws SftpException, JSchException {
        Session session = null;
        ChannelSftp fileChannel = null;
        try {
            session = openSession();
            fileChannel = openFileChannel(session);

            Collection<LsEntry> files = getLsFiles(fileChannel, dirToCompare);
            Map<String, LsEntry> fileNamesWithLargestDateInBaseDirectory = buildLargestDateOfFileMap(fileNames, files);

            for (LsEntry file : getLsFiles(fileChannel, targetDir)) {
                String type = file.getFilename().split("-")[0];
                LsEntry fileInBaseDirectory = fileNamesWithLargestDateInBaseDirectory.get(type);
                if (fileInBaseDirectory != null && fileInBaseDirectory.getAttrs().getMTime() > file.getAttrs()
                        .getMTime()) {
                    rm(fileChannel, targetDir + "/" + file.getFilename());
                }
            }
        } finally {
            closeFileChannel(fileChannel);
            closeSession(session);
        }
    }

    public void copy(String src, String dest, Collection<String> fileNames) throws JSchException, SftpException {
        Session session = null;
        ChannelSftp fileChannel = null;
        try {
            session = openSession();
            fileChannel = openFileChannel(session);

            for (String fileName : fileNames) {
                cp(fileChannel, src + "/" + fileName, dest + "/" + fileName);
            }
        } finally {
            closeFileChannel(fileChannel);
            closeSession(session);
        }
    }

    public List<File> downloadFiles(String src, String dest, Collection<String> fileNames)
            throws JSchException, SftpException {
        Session session = null;
        ChannelSftp fileChannel = null;
        try {
            session = openSession();
            fileChannel = openFileChannel(session);

            List<File> newFiles = new ArrayList<>();
            for (String fileName : fileNames) {
                String srcFile = src + "/" + fileName;
                String destFile = dest + "/" + fileName;

                download(fileChannel, srcFile, destFile);
                File fileAtDest = new File(destFile);
                newFiles.add(fileAtDest);
            }

            return newFiles;
        } finally {
            closeFileChannel(fileChannel);
            closeSession(session);
        }
    }

    public List<File> downloadFilesMatchingPredicate(String src, String dest, Predicate<LsEntry> filePred)
            throws JSchException, SftpException {
        Session session = null;
        ChannelSftp fileChannel = null;
        try {
            session = openSession();
            fileChannel = openFileChannel(session);

            List<File> newFiles = new ArrayList<>();
            Collection<LsEntry> lsFiles = getLsFiles(fileChannel, src);
            for (LsEntry file : lsFiles) {
                if (filePred.test(file)) {
                    String srcFile = src + "/" + file.getFilename();
                    String destFile = dest + "/" + file.getFilename();

                    download(fileChannel, srcFile, destFile);
                    File fileAtDest = new File(destFile);
                    newFiles.add(fileAtDest);
                }
            }
            return newFiles;
        } finally {
            closeFileChannel(fileChannel);
            closeSession(session);
        }
    }

    public List<String> gzipFiles(String directory, Collection<String> fileNames) throws JSchException {
        LOG.info("Gzip files in directory: {}", directory);

        Session session = null;
        try {
            session = openSession();

            List<String> newFileNames = new ArrayList<>();
            for (String fileName : fileNames) {
                if (fileName.endsWith(".gz")) {
                    newFileNames.add(fileName);
                    continue;
                }

                gzip(session, directory + "/" + fileName);
                newFileNames.add(fileName + ".gz");
            }

            return newFileNames;
        } finally {
            closeSession(session);
        }
    }

    public List<LsEntry> listFiles(String directory) throws SftpException, JSchException {
        Session session = null;
        ChannelSftp fileChannel = null;
        try {
            session = openSession();
            fileChannel = openFileChannel(session);

            return getLsFiles(fileChannel, directory);
        } finally {
            closeFileChannel(fileChannel);
            closeSession(session);
        }
    }

    public void move(String src, String dest, Collection<String> fileNames) throws JSchException, SftpException {
        Session session = null;
        ChannelSftp fileChannel = null;
        try {
            session = openSession();
            fileChannel = openFileChannel(session);

            for (String fileName : fileNames) {
                mv(fileChannel, src + "/" + fileName, dest + "/" + fileName);
            }
        } finally {
            closeFileChannel(fileChannel);
            closeSession(session);
        }
    }

    public Map<String, LsEntry> moveMBTFiles(String baseDir, String targetDir, Collection<String> fileNames)
            throws JSchException, SftpException {
        LOG.info("Moving files: {} from {} to {} for MBT", fileNames.size(), baseDir, targetDir);

        Session session = null;
        ChannelSftp fileChannel = null;
        try {
            session = openSession();
            fileChannel = openFileChannel(session);

            Collection<LsEntry> baseDirFiles = getLsFiles(fileChannel, baseDir);

            //We filter out the biggest index
            Map<String, LsEntry> fileNamesWithLargestDateInBaseDirectory = buildLargestDateOfFileMap(fileNames,
                    baseDirFiles);

            Collection<LsEntry> files = getLsFiles(fileChannel, targetDir);
            for (LsEntry file : files) {
                String fileName = file.getFilename();

                LsEntry fileEntry = fileNamesWithLargestDateInBaseDirectory.get(fileName);
                if (fileEntry == null) {
                    LOG.info("File entry not found: {}", fileName);
                    continue;
                }

                if (file.getAttrs().getMTime() < fileEntry.getAttrs().getMTime()) {
                    String baseName = targetDir + "/" + fileName;
                    String targetName = targetDir + "/old/" + fileName + "-" + file.getAttrs().getMTime();

                    mv(fileChannel, baseName, targetName);
                }
            }

            Map<String, LsEntry> fileNamesInTargetDirectory = files.stream().map(file -> Map.entry(
                            Arrays.stream(file.getFilename().split("\\.")).limit(4).collect(Collectors.joining(".")), file))
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                            (v1, v2) -> v1.getAttrs().getMTime() < v2.getAttrs().getMTime() ? v2 : v1));

            for (Entry<String, LsEntry> entry : fileNamesWithLargestDateInBaseDirectory.entrySet()) {
                LsEntry file = entry.getValue();
                String fileName = file.getFilename();

                SftpATTRS attrs = file.getAttrs();
                LsEntry fileEntry = fileNamesInTargetDirectory.get(fileName);
                if (fileEntry == null || attrs.getMTime() > fileEntry.getAttrs().getMTime()) {
                    String type = entry.getKey();
                    String src = baseDir + "/" + fileName;
                    String dest = targetDir + "/" + type;

                    ChannelExec commandChannel = null;
                    String command = "mv " + src + " " + dest;
                    try {
                        commandChannel = (ChannelExec) session.openChannel(EXEC_PROTOCOL);
                        commandChannel.setInputStream(null);
                        commandChannel.setOutputStream(null);
                        commandChannel.setErrStream(null);
                        commandChannel.setCommand(command);

                        // will execute the command.
                        commandChannel.connect(TIMEOUT_CHANNEL_MS);
                    } finally {
                        if (commandChannel != null && commandChannel.isConnected()) {
                            commandChannel.disconnect();
                        }
                    }
                }
            }

            return Stream.concat(fileNamesWithLargestDateInBaseDirectory.entrySet().stream(),
                    fileNamesInTargetDirectory.entrySet().stream()).collect(
                    Collectors.toMap(Entry::getKey, Entry::getValue,
                            (v1, v2) -> v1.getAttrs().getMTime() < v2.getAttrs().getMTime() ? v2 : v1));
        } finally {
            closeFileChannel(fileChannel);
            closeSession(session);
        }
    }

    public void remove(String src, Collection<String> fileNames) throws JSchException, SftpException {
        Session session = null;
        ChannelSftp fileChannel = null;
        try {
            session = openSession();
            fileChannel = openFileChannel(session);

            for (String fileName : fileNames) {
                rm(fileChannel, src + "/" + fileName);
            }
        } finally {
            closeFileChannel(fileChannel);
            closeSession(session);
        }
    }

    public void unzip(String src, String fileName) throws JSchException {
        Session session = null;
        ChannelSftp fileChannel = null;
        try {
            session = openSession();
            fileChannel = openFileChannel(session);

            unzip(session, src, fileName);
        } finally {
            closeFileChannel(fileChannel);
            closeSession(session);
        }
    }

    public void uploadFileBytesAndSendCommand(byte[] data, String destination, String command)
            throws SftpException, JSchException {

        Session session = null;
        ChannelSftp fileChannel = null;
        try {
            session = openSession();
            fileChannel = openFileChannel(session);

            try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
                LOG.info("Upload {} bytes to {}", data.length, destination);
                fileChannel.put(bais, destination);

                LOG.info("Execute command: {}", command);
                sendCommand(session, command);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } finally {
            closeFileChannel(fileChannel);
            closeSession(session);
        }
    }

    private Map<String, LsEntry> buildLargestDateOfFileMap(Collection<String> fileNames, Collection<LsEntry> files) {
        return files.stream().filter(file -> containsFileIdentifier(fileNames, file)).map(file -> Map.entry(
                        Arrays.stream(file.getFilename().split("\\.")).limit(4).collect(Collectors.joining(".")), file))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                        (v1, v2) -> v1.getAttrs().getMTime() < v2.getAttrs().getMTime() ? v2 : v1));
    }

    private void closeFileChannel(ChannelSftp fileChannel) {
        if (fileChannel != null && fileChannel.isConnected()) {
            fileChannel.disconnect();
        }
    }

    private void closeSession(Session session) {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    private boolean containsFileIdentifier(Collection<String> fileNames, LsEntry file) {
        String fileIdentifier = Arrays.stream(file.getFilename().split("\\.")).limit(4)
                .collect(Collectors.joining("."));
        return fileNames.contains(fileIdentifier);
    }

    private void cp(ChannelSftp fileChannel, String src, String dest) throws SftpException {
        LOG.info("cp {} to {}", src, dest);
        ByteArrayOutputStream tempOutput = new ByteArrayOutputStream(1024);
        try (tempOutput) {
            fileChannel.get(src, tempOutput);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        try (ByteArrayInputStream tempInput = new ByteArrayInputStream(tempOutput.toByteArray())) {
            fileChannel.put(tempInput, dest);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void download(ChannelSftp fileChannel, String src, String dest) throws SftpException {
        LOG.info("Download file {} to {}", src, dest);
        fileChannel.get(src, dest);
    }

    private void gzip(Session session, String src) throws JSchException {
        LOG.info("Gzip {}", src);
        sendCommand(session, "gzip " + src);
    }

    private List<LsEntry> getLsFiles(ChannelSftp fileChannel, String dir) throws SftpException {
        return fileChannel.ls(dir).stream().filter(e -> !e.getAttrs().isDir()).toList();
    }

    private void mv(ChannelSftp fileChannel, String src, String dest) throws SftpException {
        LOG.info("mv {} to {}", src, dest);
        fileChannel.rename(src, dest);
    }

    private ChannelSftp openFileChannel(Session session) throws JSchException {
        Channel channel = session.openChannel(FILE_TRANSFER_PROTOCOL);
        ChannelSftp fileChannel = (ChannelSftp) channel;
        channel.connect(TIMEOUT_CHANNEL_MS);

        return fileChannel;
    }

    private Session openSession() throws JSchException {
        Session session = new JSch().getSession(userName, host, port);
        session.setThreadFactory(Thread.ofVirtual().factory());

        session.setConfig("StrictHostKeyChecking", "no");
        session.setConfig("PreferredAuthentications", "password");

        session.setPassword(password);
        session.setTimeout(TIMEOUT_SESSION_MS);
        session.connect();

        return session;
    }

    private void rm(ChannelSftp fileChannel, String src) throws SftpException {
        LOG.info("rm {}", src);
        fileChannel.rm(src);
    }

    private void sendCommand(Session session, String command) throws JSchException {
        ChannelExec commandChannel = null;
        try {
            commandChannel = (ChannelExec) session.openChannel(EXEC_PROTOCOL);
            commandChannel.setCommand(command);

            try {
                InputStream commandOutput = commandChannel.getInputStream();

                // will execute the command.
                commandChannel.connect(TIMEOUT_CHANNEL_MS);

                StringBuilder outputBuffer = new StringBuilder();
                int readByte = commandOutput.read();
                while (readByte != 0xffffffff) {
                    outputBuffer.append((char) readByte);
                    readByte = commandOutput.read();
                }
                LOG.info("Command exit code: {}, output: {}", commandChannel.getExitStatus(), outputBuffer);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } finally {
            if (commandChannel != null && commandChannel.isConnected()) {
                commandChannel.disconnect();
            }
        }
    }

    private void unzip(Session session, String src, String fileName) throws JSchException {
        String file = src + "/" + fileName;

        LOG.info("Unzip {} -d {}", file, src);
        sendCommand(session, "unzip " + file + " -d " + src);
    }
}