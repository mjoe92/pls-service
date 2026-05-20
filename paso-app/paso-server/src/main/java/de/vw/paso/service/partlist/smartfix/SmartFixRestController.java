package de.vw.paso.service.partlist.smartfix;

import java.util.Collection;
import java.util.List;

import de.vw.paso.logic.user.UserManager;
import de.vw.paso.partlist.domain.smartfix.SmartFix;
import de.vw.paso.repository.partlist.SmartFixRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SmartFixRestService.URL)
public class SmartFixRestController implements SmartFixRestService {

    private final SmartFixRepository smartFixRepository;
    private final UserManager userManager;

    public SmartFixRestController(SmartFixRepository smartFixRepository, UserManager userManager) {
        this.smartFixRepository = smartFixRepository;
        this.userManager = userManager;
    }

    @Override
    @GetMapping
    public SmartFixListDTO loadAll() {
        userManager.requireAdminUser();

        List<SmartFixDTO> smartFixDTOS = smartFixRepository.findAll().stream().map(this::convertToDTO).toList();
        return new SmartFixListDTO(smartFixDTOS);
    }

    @Override
    @PostMapping
    public SmartFixDTO save(@RequestBody SmartFixDTO fix) {
        userManager.requireAdminUser();

        SmartFix toSave = smartFixRepository.save(toEntity(fix));
        return convertToDTO(toSave);
    }

    @Override
    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        userManager.requireAdminUser();

        smartFixRepository.deleteById(id);
    }

    @Override
    @GetMapping(path = "/{fields}")
    public SmartFixListDTO loadByFields(@PathVariable Collection<String> fields) {
        userManager.requireAdminUser();

        List<SmartFixDTO> smartFixDTOS = smartFixRepository.findByField(fields).stream().map(this::convertToDTO)
                .toList();
        return new SmartFixListDTO(smartFixDTOS);
    }

    private SmartFix toEntity(SmartFixDTO smartFixDTO) {
        SmartFix smartFix = new SmartFix();
        smartFix.setId(smartFixDTO.getId());
        smartFix.setName(smartFixDTO.getName());
        smartFix.setActive(smartFixDTO.isActive());
        smartFix.setField(smartFixDTO.getField());
        smartFix.setOldValue(smartFixDTO.getOldValue());
        smartFix.setNewValue(smartFixDTO.getNewValue());
        smartFix.setDescription(smartFixDTO.getDescription());

        return smartFix;
    }

    private SmartFixDTO convertToDTO(SmartFix smartFix) {
        SmartFixDTO dto = new SmartFixDTO();
        dto.setId(smartFix.getId());
        dto.setName(smartFix.getName());
        dto.setActive(smartFix.isActive());
        dto.setField(smartFix.getField());
        dto.setOldValue(smartFix.getOldValue());
        dto.setNewValue(smartFix.getNewValue());
        dto.setDescription(smartFix.getDescription());

        return dto;
    }
}
