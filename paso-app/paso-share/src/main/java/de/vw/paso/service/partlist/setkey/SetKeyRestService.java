package de.vw.paso.service.partlist.setkey;

public interface SetKeyRestService {

    String URL = "/api/set-keys";

    SetKeyListDTO loadSetKeys();

    SetKeyListDTO loadSetKeys(Long setVersionId);

    SetKeysDTO saveSetKeys(SetKeysDTO setKeys);

    SetKeyDTO updateSetKey(UpdateSetKeyDTO updateSetKeyDTO);

    void removeSetKey(Long setVersionId, String setKeyName);
}
