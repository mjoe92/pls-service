package de.vw.paso.delegate.stammdaten.prnumber;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.masterdata.prnumber.PrNumberListDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberRestService;
import de.vw.paso.utility.StringConstant;
import org.apache.hc.client5.http.classic.methods.HttpGet;

public class PrNumberRestClient implements PrNumberRestService {

    private final PasoRestClient httpClient;

    public PrNumberRestClient(PasoRestClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public PrNumberListDTO loadPrNumbersForConfig(Long vehicleProjectId) {
        return loadPrNumbers(StringConstant.SLASH + vehicleProjectId);
    }

    @Override
    public PrNumberListDTO loadAll() {
        return loadPrNumbers(null);
    }

    private PrNumberListDTO loadPrNumbers(String pathSuffix) {
        String path = PasoClientProperties.get().getServerUrl() + URL;
        if (pathSuffix != null) {
            path += pathSuffix;
        }

        return httpClient.execute(new HttpGet(path), PrNumberListDTO.class);
    }
}
