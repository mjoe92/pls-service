package de.vw.paso.logic.masterdata;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import de.vw.paso.logic.user.UserManager;
import de.vw.paso.partlist.domain.Pst;
import de.vw.paso.repository.partlist.PstRepository;
import de.vw.paso.service.masterdata.pst.PstDTO;
import de.vw.paso.util.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PstManager {

    private final PstRepository pstRepository;
    private final UserManager userManager;

    public PstManager(PstRepository pstRepository, UserManager userManager) {
        this.pstRepository = pstRepository;
        this.userManager = userManager;
    }

    public List<PstDTO> getPstElements() {
        return pstRepository.findAll().stream().map(Pst::toDTO).toList();
    }

    @Transactional
    public void delete(Long id) {
        checkIsAdmin();

        Pst pst = pstRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PST with id " + id + " does not exist"));

        pstRepository.saveAll(
                pst.getChildren().stream().peek(childPST -> childPST.setParent(pst.getParent())).toList());

        pstRepository.delete(pst);
    }

    @Transactional
    public PstDTO edit(PstDTO pstDTO) {
        checkIsAdmin();

        Pst pst = pstRepository.findById(pstDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("PST with id " + pstDTO.getId() + " does not exist"));

        Pst pstParent = pstDTO.getParentId() == null ? null : pstRepository.findById(pstDTO.getParentId()).orElse(null);

        pst.setName(pstDTO.getName());
        pst.setParent(pstParent);
        pst.setDescDe(pstDTO.getDescDe());
        pst.setDescEn(pstDTO.getDescEng());

        pst.setUserChange(userManager.getCurrentUserId());
        pst.setTimestampChange(Timestamp.from(new Date().toInstant()));

        pst = pstRepository.save(pst);

        return Pst.toDTO(pst);
    }

    @Transactional
    public PstDTO savePst(PstDTO pstDTO) {
        checkIsAdmin();

        if (pstRepository.findByName(pstDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("PST with name " + pstDTO.getName() + " already exists");
        }

        Pst parent = pstDTO.getParentId() == null ? null : pstRepository.findById(pstDTO.getParentId()).orElse(null);

        Pst pst = new Pst();
        pst.setName(pstDTO.getName());
        pst.setDescEn(pstDTO.getDescEng());
        pst.setDescDe(pstDTO.getDescDe());
        pst.setParent(parent);
        pst.setUserCreate(userManager.getCurrentUserId());
        pst.setTimestampCreate(Timestamp.from(new Date().toInstant()));

        pst = pstRepository.save(pst);

        return Pst.toDTO(pst);
    }

    private void checkIsAdmin() {
        if (!userManager.isCurrentUserAdmin()) {
            throw new UnauthorizedException();
        }
    }
}
