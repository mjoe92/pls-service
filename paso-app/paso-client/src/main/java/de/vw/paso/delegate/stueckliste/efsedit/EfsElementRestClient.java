package de.vw.paso.delegate.stueckliste.efsedit;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.partlist.efsedit.CopyOrMoveEfsElementDTO;
import de.vw.paso.service.partlist.efsedit.CopyOrMoveVehiclePartListDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementListDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementRestService;
import de.vw.paso.service.partlist.efsedit.SaveEfsElementListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class EfsElementRestClient implements EfsElementRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public EfsElementRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public EfsElementListDTO loadPartList(Long vehicleConfigId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getEfsElementsRequest = new HttpGet(
                pasoServerHost + EfsElementRestService.URL + StringConstant.SLASH + vehicleConfigId);

        List<EfsElementDTO> efsElementDTOS = httpClient.execute(getEfsElementsRequest, EfsElementListDTO.class)
                .efsElementDTOS();
        for (EfsElementDTO efsElementDTO : efsElementDTOS) {
            setParentChildRelation(efsElementDTOS, efsElementDTO);
        }

        return new EfsElementListDTO(efsElementDTOS);
    }

    @Override
    public void createEfs(VehicleConfigDTO vehicleConfig) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(vehicleConfig);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(pasoServerHost + EfsElementRestService.URL);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            httpClient.execute(httpPost);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EfsElementDTO saveEfsElement(EfsElementDTO efsElementDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(efsElementDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPut = new HttpPut(pasoServerHost + EfsElementRestService.URL);
            httpPut.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPut, EfsElementDTO.class);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EfsElementListDTO saveEfsElements(SaveEfsElementListDTO changeDto) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(changeDto);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPut = new HttpPut(pasoServerHost + EfsElementRestService.URL + EfsElementRestService.SAVE_All);
            httpPut.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPut, EfsElementListDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EfsElementListDTO deleteEfsElements(EfsElementListDTO efsElementListDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(efsElementListDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPut = new HttpPut(pasoServerHost + EfsElementRestService.URL + EfsElementRestService.DELETE);
            httpPut.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPut, EfsElementListDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EfsElementListDTO copyEfsElements(CopyOrMoveEfsElementDTO copyOrMoveEfsElementDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(copyOrMoveEfsElementDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(
                    pasoServerHost + EfsElementRestService.URL + EfsElementRestService.COPY_EFS);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, EfsElementListDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EfsElementListDTO moveEfsElements(CopyOrMoveEfsElementDTO copyOrMoveEfsElementDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(copyOrMoveEfsElementDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(
                    pasoServerHost + EfsElementRestService.URL + EfsElementRestService.MOVE_EFS);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, EfsElementListDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EfsElementListDTO copyEfsElementsPartList(CopyOrMoveVehiclePartListDTO copyOrMoveEfsElementDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(copyOrMoveEfsElementDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(
                    pasoServerHost + EfsElementRestService.URL + EfsElementRestService.COPY_PART_LIST);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, EfsElementListDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EfsElementListDTO moveEfsElementsPartList(CopyOrMoveVehiclePartListDTO copyOrMoveEfsElementDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(copyOrMoveEfsElementDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(
                    pasoServerHost + EfsElementRestService.URL + EfsElementRestService.MOVE_PART_LIST);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, EfsElementListDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void setParentChildRelation(Collection<EfsElementDTO> efsElementDTOS, EfsElementDTO efsElement) {
        List<EfsElementDTO> children = new ArrayList<>(efsElementDTOS.size());
        for (EfsElementDTO efsElementDTO : efsElementDTOS) {
            if (efsElement.getId() != null && efsElementDTO.getParentId() != null && efsElementDTO.getParentId()
                    .equals(efsElement.getId())) {
                children.add(efsElementDTO);
            }
        }

        efsElement.setChildren(children);
        for (EfsElementDTO child : children) {
            child.setParent(efsElement);
        }
    }
}
