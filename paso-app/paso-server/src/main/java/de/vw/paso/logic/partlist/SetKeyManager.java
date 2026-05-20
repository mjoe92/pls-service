package de.vw.paso.logic.partlist;

import java.util.Collection;
import java.util.Objects;

import de.vw.paso.masterdata.domain.Product;
import de.vw.paso.partlist.domain.SetKey;
import de.vw.paso.partlist.domain.SetKeyVersionPK;
import de.vw.paso.repository.partlist.SetKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetKeyManager {

    private final SetKeyRepository setKeyRepository;

    public SetKeyManager(SetKeyRepository setKeyRepository) {
        this.setKeyRepository = setKeyRepository;
    }

    public Collection<SetKey> loadSetKeys() {
        return setKeyRepository.findAll();
    }

    public Collection<SetKey> loadSetKeys(Long setKeysVersion) {
        return setKeysVersion == null ? setKeyRepository.findAllByIdVersion(Product.INITIAL_SET_VERSION_ID) :
                setKeyRepository.findAllByIdVersion(setKeysVersion);
    }

    @Transactional
    public SetKey saveSetKey(SetKey newSetKey) {
        return setKeyRepository.existsById(newSetKey.getId()) ? null : setKeyRepository.save(newSetKey);
    }

    @Transactional
    public SetKey updateSetKey(SetKey oldSetKey, SetKey newSetKey) {
        if (!Objects.equals(oldSetKey.getSetKey(), newSetKey.getSetKey()) && setKeyRepository.existsById(
                newSetKey.getId())) {
            return null;
        }

        if (hasIdChanged(oldSetKey, newSetKey)) {
            updateWithChildren(oldSetKey, newSetKey);
        } else {
            updateSingleSetKey(newSetKey);
        }

        return newSetKey;
    }

    private boolean hasIdChanged(SetKey oldSetKey, SetKey newSetKey) {
        return !oldSetKey.getId().equals(newSetKey.getId());
    }

    private void updateWithChildren(SetKey oldSetKey, SetKey newSetKey) {
        setKeyRepository.saveAndFlush(newSetKey);
        Collection<SetKey> setKeysToUpdate = setKeyRepository.findAllByParentSetKeyAndIdVersion(oldSetKey.getSetKey(),
                oldSetKey.getVersion());

        for (SetKey setKey : setKeysToUpdate) {
            setKey.setParentSetKey(newSetKey.getSetKey());
            setKey.setParent(newSetKey);
        }

        setKeyRepository.saveAllAndFlush(setKeysToUpdate);
        setKeyRepository.deleteById(oldSetKey.getId());
    }

    private void updateSingleSetKey(SetKey updatedSetKey) {
        setKeyRepository.save(updatedSetKey);
    }

    @Transactional
    public void removeSetKey(SetKeyVersionPK setKeyId) {
        Collection<SetKey> children = setKeyRepository.findAllByParentSetKeyAndIdVersion(setKeyId.getSetKey(),
                setKeyId.getVersion());
        for (SetKey child : children) {
            removeSetKey(child.getId());
        }

        setKeyRepository.deleteById(setKeyId);
    }
}
