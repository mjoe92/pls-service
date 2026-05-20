package de.vw.paso.job.mbt;

import java.util.Collection;
import java.util.Date;

import de.vw.paso.repository.mbt.MbtImportTimeStampRepository;
import de.vw.paso.service.pls.MbtFileDTO;
import de.vw.paso.service.pls.MbtImportTimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MbtDataImportScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(MbtDataImportScheduler.class);

    private final RestTemplate restTemplate;
    private final MbtImporter mbtImporter;
    private final MbtImportTimeStampRepository mbtImportTimeStampRepository;

    @Value("${partlist-service.url}")
    private String plsUrl;

    public MbtDataImportScheduler(RestTemplate restTemplate, MbtImporter mbtImporter,
            MbtImportTimeStampRepository mbtImportTimeStampRepository) {
        this.restTemplate = restTemplate;
        this.mbtImporter = mbtImporter;
        this.mbtImportTimeStampRepository = mbtImportTimeStampRepository;
    }

    @Scheduled(cron = "${cron.mbt.import}")
    public void importMbtDataJob() {
        LOG.info("Starting MBT daily imports");
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(plsUrl + "/mbt-import");

        ResponseEntity<Collection<MbtFileDTO>> request = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET,
                null, new ParameterizedTypeReference<>() { });

        Collection<MbtFileDTO> files = request.getBody();
        if (files == null || files.isEmpty()) {
            LOG.info("No MBT files to import");
            return;
        }

        for (MbtFileDTO mbtFileDTO : files) {
            try {
                mbtImporter.doImport(mbtFileDTO.data());

                MbtImportTimeStamp mbtImportTimeStamp = mbtImportTimeStampRepository.findByFileName(
                        mbtFileDTO.fileName()).orElse(new MbtImportTimeStamp());

                mbtImportTimeStamp.setFileName(mbtFileDTO.fileName());
                mbtImportTimeStamp.setImportDate(Date.from(mbtFileDTO.date()));
                mbtImportTimeStampRepository.save(mbtImportTimeStamp);
            } catch (Exception e) {
                LOG.error("Error while importing MBT file {}: {}", mbtFileDTO.fileName(), e.getMessage(), e);
            }
        }
    }
}
