package de.vw.paso.partlist.domain;

import java.io.Serializable;

public interface IPartListChild extends Serializable {

  Long getVehiclePartListId();

  EfsElement asParent();

}
