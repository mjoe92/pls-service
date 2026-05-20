package de.vw.paso.client.stueckliste.event;

import java.util.Collection;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import lombok.Getter;

@Getter
public class RevertRevisionEvent {

    private Long revision;
    private Collection<EfsElementDTO> result;

    public RevertRevisionEvent(Long revision, Collection<EfsElementDTO> result) {
        this.revision = revision;
        this.result = result;
    }
}
