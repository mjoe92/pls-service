package de.vw.paso.pll.preprocessing.formats.raw;

public class MaKTxWrapper extends RowWrapper {

	private String line;

	public MaKTxWrapper(String line) {
		this.line = line;
	}

	@Override
	public boolean testRowFormat() {
		return getPartnumber().length() > 6;
	}

	public String getPartnumber() {
		return line.substring(0, 18).trim();
	}

  public String getPartnumberVornummer() {
    return line.substring(0, 3).trim();
  }

  public String getPartnumberMittelGruppe() {
    return line.substring(3, 6).trim();
  }

  public String getPartnumberEndNumber() {
    return line.substring(6, 9).trim();
  }

  public String getPartnumberIndex() {
    return line.substring(9, 11).trim();
  }

  public String getLanguage() {
		return line.substring(18, 19);
	}

	public String getDescription() {
		return line.substring(19, 59).trim();
	}
}
