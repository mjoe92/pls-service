package de.vw.paso.service.mbt;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.vw.paso.job.mbt.MbtDataImportScheduler;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.repository.mbt.MbtImportTimeStampRepository;
import de.vw.paso.service.pls.MbtImportTimeStamp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MbtRestService.URL)
public class MbtImportRestController implements MbtRestService {

    private final MbtDataImportScheduler mbtDataImportScheduler;
    private final UserManager userManager;
    private final MbtImportTimeStampRepository mbtImportTimeStampRepository;

    public MbtImportRestController(MbtDataImportScheduler mbtDataImportScheduler,
            MbtImportTimeStampRepository mbtImportTimeStampRepository, UserManager userManager) {
        this.mbtDataImportScheduler = mbtDataImportScheduler;
        this.mbtImportTimeStampRepository = mbtImportTimeStampRepository;
        this.userManager = userManager;
    }

    @Override
    @GetMapping
    public void importData() {
        userManager.requireAdminUser();

        try (ExecutorService executorService = Executors.newSingleThreadScheduledExecutor()) {
            executorService.submit(mbtDataImportScheduler::importMbtDataJob);
        }
    }

    @Override
    @GetMapping(DATE)
    public Date getImportDateForFile() {
        userManager.requireAdminUser();

        return mbtImportTimeStampRepository.findAll().stream().map(MbtImportTimeStamp::getImportDate).sorted().limit(1)
                .findFirst().orElseThrow();
    }
}