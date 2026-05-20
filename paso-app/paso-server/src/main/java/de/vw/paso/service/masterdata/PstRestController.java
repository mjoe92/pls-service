package de.vw.paso.service.masterdata;

import de.vw.paso.logic.masterdata.PstManager;
import de.vw.paso.service.masterdata.pst.PstDTO;
import de.vw.paso.service.masterdata.pst.PstListDTO;
import de.vw.paso.service.masterdata.pst.PstRestService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PstRestService.URL)
public class PstRestController implements PstRestService {

    private final PstManager pstManager;

    public PstRestController(PstManager pstManager) {
        this.pstManager = pstManager;
    }

    @Override
    @GetMapping
    public PstListDTO getPsts() {
        return new PstListDTO(pstManager.getPstElements());
    }

    @Override
    @PostMapping(ADD)
    public PstDTO addPst(@RequestBody PstDTO pstDTO) {
        return pstManager.savePst(pstDTO);
    }

    @Override
    @PatchMapping(EDIT)
    public PstDTO editPst(@RequestBody PstDTO pstDTO) {
        return pstManager.edit(pstDTO);
    }

    @Override
    @DeleteMapping(DELETE + "/{id}")
    public void deletePst(@PathVariable Long id) {
        pstManager.delete(id);
    }
}
