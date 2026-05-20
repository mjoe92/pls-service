package de.vw.paso.client.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.ArrayUtils;

/**
 * PasoWildCardPattern erlaubt die Suche nach einem anwenderfreundlichen
 * Suchbegriff und arbeitet intern mit einem regulären Ausdruck.
 *
 * @author eostesi
 */
public class PasoWildCardPattern {

    /**
     * Soll vorne immer eine Wildcard angehängt werden?
     */
    private boolean useWildCardVorn = false;

    /**
     * Soll hinten an den Suchausdruck immer eine Wildcard angehängt werden?
     */
    private boolean useWildCardHinten = true;

    /**
     * Flags, die für alle Patterns immer verwendet werden
     */
    private int flags = Pattern.CASE_INSENSITIVE;

    private char[] specialCharacters = new char[] { '\\', '(', ')', '{', '}', '[', ']' };

    /**
     * Interne Unterscheidung von verschiedenen Feldern, weil Teilenummern gegf.
     * einen anderen Suchausdruck benötigen als Bezeichnung
     */
    public enum FieldType { // NO_UCD (use private)
        DEFAULT, TEILENUMMER
    }

    /**
     * Kompiliertes Regular-Expression-Pattern
     */
    private Pattern pattern;

    /**
     * Erzeugt eine neue Instanz von PasoWildCardPattern. Kann der Suchausdruck
     * nicht in einen gültigen regulären Ausdruck konvertiert werden, gibt es
     * eine Exception.
     *
     * @param searchTerm
     *         Suchbegriff vom Anwender
     * @param type
     *         Art des Feldinhaltes
     * @param wildCardVorn
     *         soll vorn eine wildcard angehängt werden?
     * @param wildCardHinten
     *         soll hinten eine wildcard angehängt werden?
     * @throws Exception
     *         wenn regulärer Ausdruck ungültig
     */
    public PasoWildCardPattern(String searchTerm, FieldType type, boolean wildCardVorn,
            boolean wildCardHinten) // NO_UCD (use private)
            throws Exception {
        this.useWildCardVorn = wildCardVorn;
        this.useWildCardHinten = wildCardHinten;

        String regexp = convertSearchTermToRegularExpression(searchTerm, type);
        try {
            pattern = Pattern.compile(regexp, flags);
        } catch (PatternSyntaxException e) {
            throw new Exception(e);
        }
    }

    /**
     * Erzeugt eine neue Instanz von PasoWildCardPattern. Kann der Suchausdruck
     * nicht in einen gültigen regulären Ausdruck konvertiert werden, gibt es
     * eine Exception. Per default wird hinten eine Wildcard angehängt, vorne
     * nicht.
     *
     * @param searchTerm
     *         Suchbegriff vom Anwender
     * @param type
     *         Art des Feldinhaltes
     * @throws Exception
     *         wenn regulärer Ausdruck ungültig
     */
    public PasoWildCardPattern(String searchTerm, FieldType type) throws Exception {
        this(searchTerm, type, false, true);
    }

    /**
     * Erzeugt eine neue Instanz von PasoWildCardPattern. Kann der Suchausdruck
     * nicht in einen gültigen regulären Ausdruck konvertiert werden, gibt es
     * eine Exception. Per default wird hinten eine Wildcard angehängt, vorne
     * nicht. Als Spaltentyp wird DEFAULT verwendet
     *
     * @param searchTerm
     *         Suchbegriff vom Anwender
     * @throws Exception
     *         wenn regulärer Ausdruck ungültig
     */
    public PasoWildCardPattern(String searchTerm) throws Exception {
        this(searchTerm, FieldType.DEFAULT, true, true);
    }

    /**
     * Versucht den Suchausdruck im Text <code>input</code> zu finden. Gibt den
     * gefundenen Teil zurück.
     *
     * @param input
     *         Text in dem gesucht werden soll
     * @return gefundener String oder <code>null</code> wenn nicht gefunden
     */
    public String matches(String input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.matches() ? matcher.group(1) : null;
    }

    /**
     * Konvertiert einen Suchausdruck des Anwenders (wie z.B. "1K0 ???") in
     * einen regulären Ausdruck.
     *
     * @param search
     *         Suchausdruck
     * @return Regulärer Ausdruck
     */
    private String convertSearchTermToRegularExpression(String search, FieldType type) {
        StringBuffer buf = new StringBuffer();

        // Untersuchen ob User vorn ein * angegeben hat
        boolean wildcardVorn = false;
        if (search.startsWith("*")) {
            wildcardVorn = true;
            search = search.substring(1);
        }

        // Untersuchen ob User hinten ein * angegeben hat
        boolean wildcardHinten = false;
        if (search.endsWith("*")) {
            wildcardHinten = true;
            search = search.substring(0, search.length() - 1);
        }

        // Wildcard vorne ausserhalb der Capture-Group anhängen
        if (useWildCardVorn || wildcardVorn) {
            buf.append("^.*(");
        } else {
            buf.append("^(");
        }

        // Zeichen umwandeln
        for (char c : search.toCharArray()) {

            if (c == '?') {
                // ? entspricht exakt einem Buchstaben oder einer Zahl
                // Davor könnten noch beliebige andere Zeichen kommmen, die
                // nicht Zahlen oder Buchstaben sind
                buf.append("\\W*\\w");
            } else if (c == '*') {
                // * entspricht beliebigen Zeichen
                buf.append(".*");
            } else if (c == ' ' || c == '.') {
                if (type == FieldType.TEILENUMMER) {
                    // Benutzer gibt einen '.' oder ' ' ein und erwartet an der
                    // Stelle
                    // explizit einen Punkt oder ein Leerzeichen (mindestens
                    // eins)
                    buf.append("[\\s\\.]+");
                } else if (type == FieldType.DEFAULT) {
                    // Gibt der User '.' oder ' ', so können im durchsuchten
                    // Text beliebige Sonderzeichen enthalten sein, müssen es
                    // aber nicht
                    buf.append("\\W*");
                }
            } else if (ArrayUtils.contains(specialCharacters, c)) {
                // handling of special characters
                buf.append(Pattern.quote(String.valueOf(c)));
            } else {
                // alle anderen Zeichen wandern direkt in den Regulären Ausdruck
                // Alle anderen Zeichen als Buchstaben und Zahlen werden von dem
                // \W 'gefressen'.
                buf.append("\\W*");
                buf.append(c);
            }
        }

        // Wildcard hinten ausserhalb der Capture-Group anhängen
        if (wildcardHinten || useWildCardHinten) {
            buf.append(").*$");
        } else {
            buf.append(")$");
        }
        return buf.toString();
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

}
