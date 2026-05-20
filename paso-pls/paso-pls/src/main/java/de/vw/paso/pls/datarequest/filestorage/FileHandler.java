package de.vw.paso.pls.datarequest.filestorage;

import org.bson.types.ObjectId;

public interface FileHandler {

  byte[] readFileFromStorage(ObjectId fileName);

  ObjectId writeFileToStorage(byte[] data);

  void deleteFileFromStorage(ObjectId fileName);
}
