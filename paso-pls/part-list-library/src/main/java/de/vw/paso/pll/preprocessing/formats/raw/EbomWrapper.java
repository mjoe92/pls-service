package de.vw.paso.pll.preprocessing.formats.raw;

public class EbomWrapper extends RowWrapper {

  private String nodeID;
  private int VWS;
  private int sort;
  private String prNrRule;
  private String entwicklungsstandDate;
  private String setKZ;
  private String GWS;
  private String partType;
  private String workPackageNumber;
  private String processStatus;
  private String DMURelevant;
  private String product;
  private String partNumber;
  private String partNumberVornummer;
  private String partNumberMittelGruppe;
  private String partNumberEndNumber;
  private String partNumberIndex;
  private String nodeValue;
  private String nodeType;
  private String nodeLabel;
  private String productStructure;
  private String aggregat;
  private String positionVariant;
  private String deletionFlag;
  private String quantity;
  private String quantityUnit;
  private String quantityUnitExtended;
  private String costGroup;
  private String constructionGroup;
  private String wahlweiseFall;
  private String wahlweiseNr;
  private String einsatzDate;
  private String einsatzSchl;
  private String entfallSchl;
  private String materialType;
  private String weightEstimatedFEDate;
  private String weightCalculatedDate;
  private String weightWeightedFEDate;
  private String weightWeightedDate;
  private String materialThickness;
  private String earliestPVS;
  private String earliestNS;
  private String earliestSOP;
  private String designerName;
  private String designerCostGroup;
  private String designerPhoneNumber;
  private String baukastenKz;
  private String baukastenNodeId;
  private String baukastenSt;
  private String pActivationDate;
  private String konstructureDate;
  private String avonStatus;
  private String entfallDate;

  public EbomWrapper(String line) {
    nodeID = line.substring(0, 32).trim();
    VWS = Integer.parseInt(line.substring(32, 43).trim());
    sort = Integer.parseInt(line.substring(52, 58).trim());
    prNrRule = line.substring(58, 138).trim();
    entwicklungsstandDate = line.substring(138, 148).trim();
    setKZ = line.substring(148, 151).trim();
    GWS = line.substring(151, 152).trim();
    partType = line.substring(152, 153).trim();
    workPackageNumber = line.substring(153, 159).trim();
    processStatus = line.substring(159, 160).trim();
    DMURelevant = line.substring(160, 162).trim();
    product = line.substring(163, 167).trim();
    partNumber = line.substring(167, 185).trim();
    partNumberVornummer = line.substring(167, 170).trim();
    partNumberMittelGruppe = line.substring(170, 173).trim();
    partNumberEndNumber = line.substring(173, 176).trim();
    partNumberIndex = line.substring(176, 178).trim();
    nodeValue = line.substring(185, 225).trim();
    nodeType = line.substring(225, 233).trim();
    nodeLabel = line.substring(233, 293).trim();
    productStructure = line.substring(293, 296).trim();
    aggregat = line.substring(296, 300).trim();
    positionVariant = line.substring(362, 370).trim();
    deletionFlag = line.substring(443, 444).trim();
    quantity = line.substring(444, 452).trim();
    quantityUnit = line.substring(457, 460).trim();
    quantityUnitExtended = line.substring(460, 461).trim();
    costGroup = line.substring(461, 465).trim();
    constructionGroup = line.substring(465, 466).trim();
    wahlweiseFall = line.substring(466, 468).trim();
    wahlweiseNr = line.substring(468, 470).trim();
    einsatzDate = line.substring(553, 563).trim();
    einsatzSchl = line.substring(585, 615).trim();
    entfallSchl = line.substring(615, 645).trim();
    materialType = line.substring(645, 649).trim();
    weightEstimatedFEDate = line.substring(658, 668).trim();
    weightCalculatedDate = line.substring(668, 678).trim();
    weightWeightedFEDate = line.substring(678, 688).trim();
    weightWeightedDate = line.substring(703, 713).trim();
    materialThickness = line.substring(757, 764).trim();
    earliestPVS = line.substring(766, 776).trim();
    earliestNS = line.substring(776, 786).trim();
    earliestSOP = line.substring(786, 796).trim();
    designerName = line.substring(806, 826).trim();
    designerCostGroup = line.substring(826, 831).trim();
    designerPhoneNumber = line.substring(831, 846).trim();
    baukastenKz = line.substring(1029, 1030).trim();
    baukastenSt = line.substring(1030, 1031).trim();
    baukastenNodeId = line.substring(1031, 1063).trim();
    pActivationDate = line.substring(1095, 1105).trim();
    konstructureDate = line.substring(1105, 1115).trim();
    avonStatus = line.substring(1115, 1119).trim();
    entfallDate = line.substring(1119, 1129).trim();
  }

  public String getAggregat() {
    return aggregat;
  }

  public String getBaukastenKz() {
    return baukastenKz;
  }

  public String getBaukastenNodeId() {
    return baukastenNodeId;
  }

  public String getBaukastenSt() {
    return baukastenSt;
  }

  public String getConstructionGroup() {
    return constructionGroup;
  }

  public String getCostGroup() {
    return costGroup;
  }

  public String getEinsatzDate() {
    return einsatzDate;
  }

  public String getEinsatzSchl() {
    return einsatzSchl;
  }

  public String getEntfallDate() {
    return entfallDate;
  }

  public String getEntfallSchl() {
    return entfallSchl;
  }

  public String getEntwicklungsstandDate() {
    return entwicklungsstandDate;
  }

  public String getGWS() {
    return GWS;
  }

  public String getPartType() {
    return partType;
  }

  public String getWorkPackageNumber() {
    return workPackageNumber;
  }

  public String getProcessStatus() {
    return processStatus;
  }

  public String getDMURelevant() {
    return DMURelevant;
  }

  public String getNodeId() {
    return nodeID;
  }

  public String getNodeLabel() {
    return nodeLabel;
  }

  public String getNodeType() {
    return nodeType;
  }

  public String getNodeValue() {
    return nodeValue;
  }

  public String getPartNumber() {
    return partNumber;
  }

  public String getPartNumberVornummer() {
    return partNumberVornummer;
  }

  public String getPartNumberMittelGruppe() {
    return partNumberMittelGruppe;
  }

  public String getPartNumberEndNumber() {
    return partNumberEndNumber;
  }

  public String getPartNumberIndex() {
    return partNumberIndex;
  }

  public String getPrNrRule() {
    return prNrRule;
  }

  public String getProduct() {
    return product;
  }

  public String getProductStructure() {
    return productStructure;
  }

  public String getPositionVariant() {
    return positionVariant;
  }

  public String getDeletionFlag() {
    return deletionFlag;
  }

  public String getQuantity() {
    return quantity;
  }

  public String getQuantityUnit() {
    return quantityUnit;
  }

  public String getQuantityUnitExtended() {
    return quantityUnitExtended;
  }

  public String getSetKZ() {
    return setKZ;
  }

  public int getSort() {
    return sort;
  }

  public int getVWS() {
    return VWS;
  }

  public String getWahlweiseFall() {
    return wahlweiseFall;
  }

  public String getWahlweiseNr() {
    return wahlweiseNr;
  }

  public String getMaterialType() {
    return materialType;
  }

  public String getWeightEstimatedFEDate() {
    return weightEstimatedFEDate;
  }

  public String getWeightCalculatedDate() {
    return weightCalculatedDate;
  }

  public String getWeightWeightedFEDate() {
    return weightWeightedFEDate;
  }

  public String getWeightWeightedDate() {
    return weightWeightedDate;
  }

  public String getMaterialThickness() {
    return materialThickness;
  }

  public String getEarliestPVS() {
    return earliestPVS;
  }

  public String getEarliestNS() {
    return earliestNS;
  }

  public String getEarliestSOP() {
    return earliestSOP;
  }

  public String getDesignerName() {
    return designerName;
  }

  public String getDesignerCostGroup() {
    return designerCostGroup;
  }

  public String getDesignerPhoneNumber() {
    return designerPhoneNumber;
  }

  public String getPActivationDate() {
    return pActivationDate;
  }

  public String getKonstructureDate() {
    return konstructureDate;
  }

  public String getAvonStatus() {
    return avonStatus;
  }

  @Override
  public boolean testRowFormat() {
//		return getNodeId().length() == 32 && getPartNumber().length() > 6;
    return getNodeId().length() == 32;
  }

}
