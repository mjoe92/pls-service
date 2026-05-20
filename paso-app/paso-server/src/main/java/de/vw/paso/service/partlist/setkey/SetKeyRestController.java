package de.vw.paso.service.partlist.setkey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.vw.paso.logic.partlist.SetKeyManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.partlist.domain.SetKey;
import de.vw.paso.partlist.domain.SetKeyVersionPK;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = SetKeyRestService.URL)
public class SetKeyRestController implements SetKeyRestService {

    private final SetKeyManager manager;
    private final UserManager userManager;

    public SetKeyRestController(SetKeyManager manager, UserManager userManager) {
        this.manager = manager;
        this.userManager = userManager;
    }

    @Override
    @GetMapping
    public SetKeyListDTO loadSetKeys() {
        List<SetKeyDTO> setKeys = manager.loadSetKeys().stream().map(this::convertToDTO).toList();
        return new SetKeyListDTO(setKeys);
    }

    @Override
    @GetMapping(path = "/{setVersionId}")
    public SetKeyListDTO loadSetKeys(@PathVariable Long setVersionId) {
        List<SetKeyDTO> setKeys = manager.loadSetKeys(setVersionId).stream().map(this::convertToDTO).toList();
        return new SetKeyListDTO(setKeys);
    }

    @Override
    @PostMapping
    public SetKeysDTO saveSetKeys(@RequestBody SetKeysDTO setKeysToSave) {
        userManager.requireAdminUser();

        Collection<SetKeyDTO> setKeys = setKeysToSave.setKeys();
        Collection<SetKeyDTO> result = new ArrayList<>(setKeys.size());
        for (SetKeyDTO setKeyDTO : setKeys) {
            SetKey setKeyToSave = new SetKey(setKeyDTO.getSetKeyName(), setKeyDTO.getDescription(),
                    setKeyDTO.getParentName(), setKeyDTO.getSetVersionId());
            SetKey setKey = manager.saveSetKey(setKeyToSave);
            SetKeyDTO converted = convertToDTO(setKey);
            result.add(converted);
        }

        return new SetKeysDTO(result);
    }

    @Override
    @PutMapping
    public SetKeyDTO updateSetKey(@RequestBody UpdateSetKeyDTO updateSetKeyDTO) {
        userManager.requireAdminUser();

        SetKeyDTO oldSetKeyDto = updateSetKeyDTO.oldSetKey();
        SetKey oldSetKey = new SetKey(oldSetKeyDto.getSetKeyName(), oldSetKeyDto.getDescription(),
                oldSetKeyDto.getParentName(), oldSetKeyDto.getSetVersionId());

        SetKeyDTO newSetKeyDto = updateSetKeyDTO.newSetKey();
        SetKey newSetKey = new SetKey(newSetKeyDto.getSetKeyName(), newSetKeyDto.getDescription(),
                newSetKeyDto.getParentName(), newSetKeyDto.getSetVersionId());

        SetKey updatedSetKey = manager.updateSetKey(oldSetKey, newSetKey);

        return convertToDTO(updatedSetKey);
    }

    @Override
    @DeleteMapping
    public void removeSetKey(@RequestParam Long setVersionId, @RequestParam String setKeyName) {
        userManager.requireAdminUser();

        SetKeyVersionPK setKeyId = new SetKeyVersionPK();
        setKeyId.setSetKey(setKeyName);
        setKeyId.setVersion(setVersionId);

        manager.removeSetKey(setKeyId);
    }

    private SetKeyDTO convertToDTO(SetKey setKey) {
        return new SetKeyDTO(setKey.getSetKey(), setKey.getDescription(), setKey.getParentSetKey(),
                setKey.getVersion());
    }
}
