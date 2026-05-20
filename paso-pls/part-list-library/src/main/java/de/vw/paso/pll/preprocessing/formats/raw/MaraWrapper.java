package de.vw.paso.pll.preprocessing.formats.raw;

public class MaraWrapper extends RowWrapper {

	private String translationGer;
	private String translationEn;
	private String partNumber;
	private String partNumberVornummer;
	private String partNumberMittelGruppe;
	private String partNumberEndNumber;
	private String partNumberIndex;
	private int weightEstimatedFE;
	private String weightEstimatedFEDate;
	private int weightCalculatedFE;
	private String weightCalculatedFEDate;
	private int weightMeasuredFE;
	private String weightMeasuredFEDate;
	private String weightWeightedProd;
	private String weightWeightedProdDate;
	private String zsbKz;
	private String constructionsState;
	private String drawingDate;
	private String basicMaterial;
	private String quality;
	private String materialThickness;
	private String seeDrawing;
	private String responsibleConstr1;
	private String responsibleConstr2;
	private String buildSampleApproval;
	private String technicallyOkay;
	private String releaseDateSoll;
	private String designerName;
	private String designerCostGroup;
	private String designerPhoneNumber;
	private String kStandReleaseDate;
	private String tioFreiReleaseDate;
	private String drawingStatus;
	private String buildSampleApprovalTargetDate;
	private String description2;
	private String MFPStatus;
	private String MFPThickness;
	private String kseKz;
	private String weightAcceptedFromEPIS;

	public MaraWrapper(String line) {
		partNumber = line.substring(0, 18).trim();
		partNumberVornummer = line.substring(0, 3).trim();
		partNumberMittelGruppe = line.substring(3, 6).trim();
		partNumberEndNumber = line.substring(6, 9).trim();
		partNumberIndex = line.substring(9, 11).trim();
    weightEstimatedFE = Integer.parseInt(line.substring(48, 57).trim());
    weightEstimatedFEDate = line.substring(48, 57).trim();
    weightCalculatedFE = Integer.parseInt(line.substring(68, 77).trim());
    weightCalculatedFEDate = line.substring(78, 88).trim();
    weightMeasuredFE = Integer.parseInt(line.substring(88, 97).trim());
    weightMeasuredFEDate = line.substring(98, 108).trim();
    weightWeightedProd = line.substring(108, 123).trim();
    weightWeightedProdDate = line.substring(123, 133).trim();
    zsbKz = line.substring(133, 134).trim();
    constructionsState = line.substring(135, 173).trim();
    drawingDate = line.substring(173,183).trim();
    basicMaterial = line.substring(184, 187).trim();
    quality = line.substring(187, 227).trim();
    materialThickness = line.substring(231, 238).trim();
    seeDrawing = line.substring(239,251).trim();
    responsibleConstr1 = line.substring(251, 252).trim();
    responsibleConstr2 = line.substring(252, 253).trim();
    buildSampleApproval = line.substring(254, 255).trim();
    technicallyOkay = line.substring(270, 273).trim();
    releaseDateSoll = line.substring(273, 283).trim();
    designerName = line.substring(325, 345).trim();
    designerCostGroup = line.substring(345, 350).trim();
    designerPhoneNumber = line.substring(350, 365).trim();
    kStandReleaseDate = line.substring(399, 409).trim();
    tioFreiReleaseDate = line.substring(423, 433).trim();
    drawingStatus = line.substring(569, 571).trim();
    buildSampleApprovalTargetDate = line.substring(571, 581).trim();
    description2 = line.substring(582, 600).trim();
    MFPStatus = line.substring(600, 604).trim();
    MFPThickness = line.substring(604, 611).trim();
    kseKz = line.substring(614, 615).trim();
    weightAcceptedFromEPIS = line.substring(646, 647).trim();
	}

	@Override
	public boolean testRowFormat() {
		return getPartnumber().length() > 6 && getWeightEstimatedFE() > -1;
	}

	public String getPartnumber() {
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

	public int getWeightEstimatedFE() {
		return weightEstimatedFE;
	}

	public String getWeightEstimatedFEDate() {
		return weightEstimatedFEDate;
	}

  public int getWeightCalculatedFE() {
    return weightCalculatedFE;
  }

  public void setWeightCalculatedFE(int weightCalculatedFE) {
    this.weightCalculatedFE = weightCalculatedFE;
  }

  public String getWeightCalculatedFEDate() {
    return weightCalculatedFEDate;
  }

	public int getWeightMeasuredFE() {
		return weightMeasuredFE;
	}

	public String getWeightMeasuredFEDate() {
		return weightMeasuredFEDate;
	}

	public String getWeightWeightedProd() {
		return weightWeightedProd;
	}

	public String  getWeightWeightedProdDate() {
		return weightWeightedProdDate;
	}

	public String getZsbKz() {
		return zsbKz;
	}

	public String getConstructionsState() {
		return constructionsState;
	}

	public String getDrawingDate() {
		return drawingDate;
	}

	public String getBasicMaterial() {
		return basicMaterial;
	}

  public String getQuality() {
    return quality;
  }

  public String getMaterialThickness() {
    return materialThickness;
  }

  public String getSeeDrawing() {
    return seeDrawing;
  }

  public String getResponsibleConstr1() {
    return responsibleConstr1;
  }

  public String getResponsibleConstr2() {
    return responsibleConstr2;
  }

  public String getBuildSampleApproval() {
    return buildSampleApproval;
  }

  public String getTechnicallyOkay() {
    return technicallyOkay;
  }

  public String getReleaseDateSoll() {
    return releaseDateSoll;
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

  public String getkStandReleaseDate() {
    return kStandReleaseDate;
  }

  public String getTioFreiReleaseDate() {
    return tioFreiReleaseDate;
  }

  public String getDrawingStatus() {
    return drawingStatus;
  }

  public String getBuildSampleApprovalTargetDate() {
    return buildSampleApprovalTargetDate;
  }

	public String getDescription2() {
		return description2;
	}

  public String getMFPStatus() {
    return MFPStatus;
  }

  public String getMFPThickness() {
    return MFPThickness;
  }

  public String getKseKz() {
    return kseKz;
  }

  public String getWeightAcceptedFromEPIS() {
    return weightAcceptedFromEPIS;
  }

	public String getTranslationGer() {
		return translationGer;
	}

	public void setTranslationGer(String translationGer) {
		this.translationGer = translationGer;
	}

	public String getTranslationEn() {
		return translationEn;
	}

	public void setTranslationEn(String translationEn) {
		this.translationEn = translationEn;
	}
}
