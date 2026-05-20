package de.vw.paso.partlist.domain;

import java.util.Date;

public interface IPartListElement<ID> {

  enum Type {
    EFS_ELEMENT,
    EFS_ELEMENT_HISTORY,
    KSL_ELEMENT
  }

  Type getType();

  Long getId();

  void setId(ID id);

  String getNodeId();

  String getNodeLabel();

  Integer getNodeLevel();

  String getNodeType();

  String getNodeValueParent();

  String getNodeValue();

  String getPartNumber();

  String getDescription1();

  String getDescription2();

  Integer getQuantity();

  void setQuantity(Integer quantity);

  String getPrNumberRule();

  void setPrNumberRule(String prNumberRule);

  Date getBeginDate();

  void setBeginDate(Date beginDate);

  Date getEndDate();

  void setEndDate(Date endDate);

  String getSetKey();

  void setSetKey(String setKey);

}
