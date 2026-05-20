package de.vw.paso.pll.preprocessing.formats.raw;

/**
 * Wraps a TI-WH raw format line for access of properties
 */
public abstract class RowWrapper {

	/**
	 * Test if a raw line can be possibly interpreted as this particular row format
	 *
	 * @return true if row format fits
	 */
	public abstract boolean testRowFormat();

}
