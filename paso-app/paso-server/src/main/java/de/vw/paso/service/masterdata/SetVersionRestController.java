package de.vw.paso.service.masterdata;

import java.util.List;

import de.vw.paso.logic.masterdata.SetVersionManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.mapper.SetVersionMapper;
import de.vw.paso.partlist.domain.SetVersion;
import de.vw.paso.service.masterdata.setversion.AddSetVersionRequestDTO;
import de.vw.paso.service.masterdata.setversion.SetVersionDTO;
import de.vw.paso.service.masterdata.setversion.SetVersionListDTO;
import de.vw.paso.service.masterdata.setversion.SetVersionRestService;
import de.vw.paso.service.masterdata.setversion.UpdateSetVersionRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = SetVersionRestService.URL)
public class SetVersionRestController implements SetVersionRestService {

    private final SetVersionManager manager;
    private final UserManager userManager;

    public SetVersionRestController(SetVersionManager manager, UserManager userManager) {
        this.manager = manager;
        this.userManager = userManager;
    }

    @Override
    @GetMapping
    public SetVersionListDTO loadSetVersions() {
        List<SetVersionDTO> setVersions = manager.loadSetVersions().stream().map(SetVersionMapper::toDto).toList();
        return new SetVersionListDTO(setVersions);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SetVersionDTO addSetVersion(@RequestBody AddSetVersionRequestDTO requestDTO) {
        userManager.requireAdminUser();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        SetVersion setVersionToAdd = new SetVersion();
        setVersionToAdd.setName(requestDTO.name());
        setVersionToAdd.setChange(userId);

        SetVersion setVersion = manager.addSetVersion(setVersionToAdd, requestDTO.copyFromSetVersionId());

        return SetVersionMapper.toDto(setVersion);
    }

    @Override
    @PutMapping(path = "/{setVersionId}")
    public SetVersionDTO updateSetVersion(@PathVariable Long setVersionId,
            @RequestBody UpdateSetVersionRequestDTO requestDTO) {
        userManager.requireAdminUser();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        SetVersion setVersionToUpdate = new SetVersion();
        setVersionToUpdate.setName(requestDTO.name());
        setVersionToUpdate.setId(setVersionId);
        setVersionToUpdate.setChange(userId);

        SetVersion setVersion = manager.updateSetVersion(setVersionToUpdate);

        return SetVersionMapper.toDto(setVersion);
    }

    @Override
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSetVersion(@RequestBody Long id) {
        userManager.requireAdminUser();

        manager.deleteSetVersion(id);
    }
}
