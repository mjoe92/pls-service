package de.vw.paso.delegate.mbtimport;

import java.util.Date;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.mbt.MbtRestService;
import org.apache.hc.client5.http.classic.methods.HttpGet;

public class MbtImportRestClient implements MbtRestService {

    private final PasoRestClient httpClient;

    public MbtImportRestClient(PasoRestClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public void importData() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(pasoServerHost + URL);

        httpClient.execute(httpGetRequest);
    }

    @Override
    public Date getImportDateForFile() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(pasoServerHost + URL + DATE);

        return httpClient.execute(httpGetRequest, Date.class);
    }
}