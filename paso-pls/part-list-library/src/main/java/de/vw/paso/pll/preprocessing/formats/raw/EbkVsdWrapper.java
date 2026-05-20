package de.vw.paso.pll.preprocessing.formats.raw;

public class EbkVsdWrapper extends RowWrapper{

	private String aggregat;
	private String baukastenNodeId;
	private String ebkNodeId;
	private String parentEbkNodeId;
	private String baukastenStatus;
	private String costGroup;
	private String einsatzDate;
	private String einsatzSchl;
  private String entfallDate;
	private String entfallSchl;
  private String entwicklungsstand;
  private String konstructionGroup;
  private String nodeLabel;
  private String pActivationDate;
  private String partNumber;
  private String partNumberParent;
  private String partNumberVornummer;
  private String partNumberMittelGruppe;
  private String partNumberEndNumber;
  private String partNumberIndex;
  private String partType;
  private String prNrRule;
  private String processingStatus;
  private String productStructureKz;
  private int quantity;
  private String quantityUnit;
  private String quantityUnitAddition;
  private String setKz;
  private int sort;
  private String vws;
  private String wahlweiseFall;
  private String wahlweiseNr;
  private String workPackageNumber;

  public EbkVsdWrapper(String line) {
		baukastenNodeId = line.substring(0, 32);
		ebkNodeId = line.substring(32, 64);
		parentEbkNodeId = line.substring(64, 96);
		einsatzSchl = line.substring(113, 143).trim();
		einsatzDate = line.substring(143, 153).trim();
		entfallSchl = line.substring(153, 183).trim();
		entfallDate = line.substring(183, 193).trim();
		partNumberParent = line.substring(193, 211).trim();
		baukastenStatus = line.substring(211, 212).trim();
		partNumber = line.substring(232, 250).trim();
		partNumberVornummer = line.substring(232, 235).trim();
		partNumberMittelGruppe = line.substring(235, 238).trim();
		partNumberEndNumber = line.substring(238, 241).trim();
		partNumberIndex = line.substring(241, 243).trim();
		quantityUnit = line.substring(250, 253).trim();
		quantity = Integer.parseInt(line.substring(253, 263).trim());
		wahlweiseFall = line.substring(268, 272).trim();
		wahlweiseNr = line.substring(272, 274).trim();
		konstructionGroup =line.substring(280, 281).trim();
		costGroup = line.substring(282, 286).trim();
		setKz = line.substring(286, 289).trim();
		productStructureKz = line.substring(289, 292).trim();
		prNrRule = line.substring(292, 372).trim();
		quantityUnitAddition = line.substring(374, 375).trim();
		aggregat = line.substring(375, 379).trim();
		processingStatus = line.substring(379, 380).trim();
		workPackageNumber = line.substring(380, 386).trim();
		pActivationDate = line.substring(387, 397).trim();
		entwicklungsstand = line.substring(397, 407).trim();
		sort = Integer.parseInt(line.substring(447, 455).trim());
		vws = line.substring(455, 464).trim();
		partType = line.substring(491, 492).trim();
		nodeLabel = line.substring(492, 552).trim();
	}

  public String getAggregat() {
  	return aggregat;
  }

  public String getBaukastenNodeId() {
		return baukastenNodeId;
	}

  public String getEbkNodeId() {
    return ebkNodeId;
  }

  public String getParentEbkNodeId() {
    return parentEbkNodeId;
  }

  public String getBaukastenStatus() {
  	return baukastenStatus;
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

  /**
   * 0001-01-01 is the same as null
   */
  public String getEntfallDate() {
    if ("0001-01-01".equals(entfallDate)) {
      return null;
    } return entfallDate;
  }

  public String getEntfallSchl() {
  	return entfallSchl;
  }

  public String getEntwicklungsstand() {
  	return entwicklungsstand;
  }

  public String getKonstructionGroup() {
  	return konstructionGroup;
  }

  public String getNodeLabel() {
  	return nodeLabel;
  }

  public String getpActivationDate() {
    return pActivationDate;
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

  public String getPartType() {
    return partType;
  }

  public String getPartnumberParent() {
  	return partNumberParent;
  }

  public String getPrNrRule() {
  	return prNrRule;
  }

  public String getProcessingStatus() {
    return processingStatus;
  }

  public String getProductStructureKz() {
  	return productStructureKz;
  }

  public int getQuantity() {
  	return quantity;
  }

  public String getQuantityUnit() {
  	return quantityUnit;
  }

  public String getQuantityUnitAddition() {
  	return quantityUnitAddition;
  }

  public String getSeTKz() {
  	return setKz;
  }

  public int getSort() {
  	return sort;
  }

  public String getVWS() {
  	return vws;
  }

  public String getWahlweiseFall() {
  	return wahlweiseFall;
  }

  public String getWahlweiseNr() {
  	return wahlweiseNr;
  }

  public String getWorkPackageNumber() {
  	return workPackageNumber;
  }

  @Override
	public boolean testRowFormat() {
		return getBaukastenNodeId().length() == 32;
	}
}
