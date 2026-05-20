package de.vw.paso.utility;

import java.util.Collection;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public interface IEfsElementResolverListener {

    void onEfsElementUpdate(Collection<EfsElementDTO> elements);
}
