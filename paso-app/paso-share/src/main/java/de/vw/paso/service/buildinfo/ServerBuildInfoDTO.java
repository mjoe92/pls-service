package de.vw.paso.service.buildinfo;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServerBuildInfoDTO implements Serializable {

    private String[] profiles;
    private String buildNumber;
    private Date buildDate;
    private String stage;
}
