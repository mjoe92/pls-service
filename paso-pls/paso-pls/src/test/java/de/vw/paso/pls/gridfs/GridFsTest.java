package de.vw.paso.pls.gridfs;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import de.vw.paso.pls.PlsApplicationTests;
import de.vw.paso.pls.repository.GridFsRepository;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

class GridFsTest extends PlsApplicationTests {

  private static final String TEST_PART_LIST_UPLOAD_FILE = "testFile600MBUpload";
  private static final String TEST_PART_LIST_DOWNLOAD_FILE = "testFile600MBDownload";

  @Autowired
  @Qualifier("bucket")
  protected GridFSBucket gridFSBucket;

  @Autowired
  private GridFsRepository gridFsRepository;

  @BeforeAll
  static void setupClass() {
    createLargeBinaryFile();
  }

  @AfterAll
  static void tearDownClass() {
    deleteTestBinaryFiles();
  }

  @Test
  void saveAndReadFile() {
    readFileWithAsyncGridFs(saveFileWithAsyncGridFs());
  }

  @Test
  void findByIdWhenExists() {
    final ObjectId savedFileId =
      gridFsRepository.save("findById", new ByteArrayInputStream("GridFsTest".getBytes()));

    GridFSFile file = gridFsRepository.findById(savedFileId);
    assertNotNull(file);
  }

  @Test
  void findByIdWhenNotExists() {
    GridFSFile file = gridFsRepository.findById(new ObjectId());
    assertNull(file, "File does not exist");
  }

  @Test
  void fileExists() {
    final ObjectId savedFileId = gridFsRepository.save(
      "fileExists", new ByteArrayInputStream("GridFsTest".getBytes()));

    Boolean exist = gridFsRepository.isExist(savedFileId);

    assertNotNull(exist);
    assertTrue(exist, "File does not exist");
  }

  @Test
  void fileNotExists() {
    final Boolean isExist = gridFsRepository.isExist(new ObjectId());

    assertNotNull(isExist);
    assertFalse(isExist, "File exists");
  }

  @Test
  void deleteFileWhenExists() {
    final ObjectId savedFileId = gridFsRepository.save(
      "deleteFile", new ByteArrayInputStream("GridFsTest".getBytes()));

    gridFsRepository.delete(savedFileId);
    GridFSFile deletedFile = gridFSBucket.find(Filters.eq("_id", savedFileId)).first();

    assertNull(deletedFile, "File exists");
  }

  @Test
  void deleteFileWhenNotExists() {
    gridFsRepository.delete(new ObjectId());
  }

  private ObjectId saveFileWithAsyncGridFs() {
    ObjectId persistedFileId = null;

    try {
      final InputStream streamToUploadFrom = new FileInputStream(TEST_PART_LIST_UPLOAD_FILE);
      persistedFileId = gridFsRepository.save(TEST_PART_LIST_UPLOAD_FILE, streamToUploadFrom);
    } catch (final FileNotFoundException exception) {
      exception.printStackTrace();

      fail();
    }

    if (persistedFileId == null) {
      fail("Could not save the file");
    }

    return persistedFileId;
  }

  private void readFileWithAsyncGridFs(final ObjectId savedFileId) {
    try {
      final OutputStream streamFromDownloadTo = new FileOutputStream(TEST_PART_LIST_DOWNLOAD_FILE);

      gridFsRepository.load(new BsonObjectId(savedFileId), streamFromDownloadTo);

      assertTrue(
        compareTestFileContent(TEST_PART_LIST_UPLOAD_FILE, TEST_PART_LIST_DOWNLOAD_FILE),
        "Files content are not equal"
            );
    } catch (final FileNotFoundException exception) {
      exception.printStackTrace();

      fail();
    } catch (final IOException exception) {
      exception.printStackTrace();

      fail("Unexpected error while comparing files content");
    }
  }

  private boolean compareTestFileContent(final String fileName1, final String fileName2) throws IOException {
    final File file1 = new File(fileName1);
    final File file2 = new File(fileName2);

    return ((file1.length() > 0) && (file1.length() == file2.length()) && FileUtils.contentEquals(file1, file2));
  }

  private static void createLargeBinaryFile() {
    try (final OutputStream outputStream = new FileOutputStream(TEST_PART_LIST_UPLOAD_FILE)) {
      for (int index = 0; index < 600; index++) {
        outputStream.write(new byte[1024 * 1024]);
      }

      outputStream.flush();
    } catch (final FileNotFoundException exception) {
      exception.printStackTrace();

      fail(String.format("File not found (%s)", TEST_PART_LIST_UPLOAD_FILE));
    } catch (final IOException exception) {
      exception.printStackTrace();

      fail("Unexpected error");
    }
  }

  private static void deleteTestBinaryFiles() {
    try {
      Files.deleteIfExists(Paths.get("paso-pls", TEST_PART_LIST_UPLOAD_FILE));
      Files.deleteIfExists(Paths.get("paso-pls", TEST_PART_LIST_DOWNLOAD_FILE));
    } catch (final IOException exception) {
      exception.printStackTrace();
    }
  }

}
