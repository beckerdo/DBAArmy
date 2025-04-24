package info.danbecker.dba;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * An ArmyHeader is the intro to an army as given in the Army Lists.
 * An example is
 * <code>II/73 OLD SAXON, FRISIAN, BAVARIAN, THURINGIAN & EARLY-ANGLO-SAXON 250AD - 804AD</code>
 * <p>s
 * It includes
 * <ul>
 * <li>the ArmyRef with section number, army number</li>
 * <li>the historic description</li>
 * <li>the historic reference book list</li>
 * <li>one or more group names</li>
 * <li>one or more year ranges</li>
 * </ul>
 * <p>
 * For troop definitions, terrain, aggression, etc.
 * @see ArmyVariant
 * </p>
 *
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 * @version DBA30
 */
public class ArmyHeader implements Comparable<ArmyHeader> {
    static Logger LOGGER = Logger.getLogger(ArmyHeader.class.getName());

    final ArmyRef armyRef;
    final String groupName;
    final List<String> names;
    final List<YearRange> years;
    final String historicalDesc;
    final List<String> references;
    final int variantCount;

    /**
     * Construct header from ArmyHeaderBean information.
     * Synthesize and fill in other fields.
     *
     * @param armyRef section and number of army
     * @param groupName name of army
     * @param variantCount count for validity checks
     */
    public ArmyHeader(ArmyRef armyRef, String groupName, int variantCount) {
        if (null == armyRef) throw new IllegalArgumentException("armyRef is null");
        if (0 != armyRef.version()) throw new IllegalArgumentException("armyRef variant should be 0");
        this.armyRef = armyRef;
        if (null == groupName) throw new IllegalArgumentException("army group name");
        this.groupName = groupName;
        if (1 > variantCount) throw new IllegalArgumentException("army variant count should be 1 or greater");
        this.variantCount = variantCount;

        // Construct other fields
        names = getNames( groupName );
        years = getDates( groupName );
        historicalDesc = "Historical description";
        references = List.of( "Cambridge Ancient History Vol. 1 Part 2");
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (!(obj instanceof ArmyHeader)) return false;
        return this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public int compareTo(ArmyHeader that) {
        if (null == that) return -1;
        return this.armyRef.compareTo(that.armyRef);
    }

    /**
     * Returns the ArmyRef
     * @return the section and number (ArmyRef) of this army
     */
    public ArmyRef getArmyRef() {
        return this.armyRef;
    }

    /**
     * Returns the group Name
     * @return the name of this army
     */
    public String getGroupName() {
        return this.groupName;
    }

    /**
     * Returns the year ranges for this army
     * @return year ranges for this army
     */
    public List<YearRange> getYearRanges() {
        return this.years;
    }

    /**
     * Returns the number of variations
     * @return number of variations
     */
    public int getVariantCount() {
        return this.variantCount;
    }

    /**
     * Convert an element to a string.
     * The parse and toString APIs are reciprocal.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( armyRef.toString() );
        sb.append( " " );
        sb.append( groupName );
        return sb.toString();
    }

    /**
     * Return of list of names from a group name.
     * @param groupName string of names such as
     *    "OLD SAXON, FRISIAN, BAVARIAN, THURINGIAN & EARLY-ANGLO-SAXON 250AD - 804AD"
     * @return list of each name in the group name such as
     *    [OLD SAXON, FRISIAN, BAVARIAN, THURINGIAN, EARLY-ANGLO-SAXON]
     *
     */
    public static List<String> getNames( String groupName ) {
        Stream<String> stream = Pattern.compile("[,&]").splitAsStream( groupName );
        return stream
                .map(String::trim)
                .filter( str -> str.matches("^[A-Z -].*"))
                .map( ArmyHeader::getNameNoDates) // remove dates, ranges, and CIRCAS
                .map( ArmyHeader::toDisplayCase)
                .toList();
    }

    public static String getNameNoDates(String nameWithDate ) {
        int loc = nameWithDate.indexOf( "CIRCA" );
        if (0 < loc ) {
            return nameWithDate.substring( 0, loc - 1);
        }
        // Look for numbers in the name
        Matcher matcher = Pattern.compile("\\d+").matcher(nameWithDate);
        if (matcher.find()) {
            loc = nameWithDate.indexOf(matcher.group());
            if (0 < loc ) {
                return nameWithDate.substring( 0, loc - 1);
            }

        }
        return nameWithDate;
    }

    /**
     * Takes a group name, splits it into a list of ,& separated items,
     * removes the names, returns a list of date ranges.
     * @param groupName a Barker heading with ref, army name, and year ranges
     * @return list of year ranges pulled from groupName
     */
    public static List<YearRange> getDates( String groupName ) {
        Stream<String> stream = Pattern.compile("[,&]").splitAsStream( groupName);
        return stream
                .map(String::trim)
                // .filter( str -> str.matches("^[A-Z -].*"))
                .map( ArmyHeader::getDatesNoNames) // remove group names
                .filter( str -> !str.isEmpty() ) // filter out lack of date
                .map( YearRange::parse )
                .toList();
    }

    public static String getDatesNoNames(String nameWithDate ) {
        int loc = nameWithDate.indexOf( "CIRCA" );
        if (0 < loc ) {
            return nameWithDate.substring( loc );
        }

        // Look for numbers in the name
        Matcher matcher = Pattern.compile("\\d+").matcher(nameWithDate);
        if (matcher.find()) {
            loc = nameWithDate.indexOf(matcher.group());
            if (0 <= loc ) {
                // System.out.println( nameWithDate.substring( loc ) );
                return nameWithDate.substring( loc );
            }
        }
        return "";
    }

    /**
     * Convert first chars to upper case, others to lower case.
     * @param s String to convert case
     * @return a display case String
     */
    public static String toDisplayCase(String s) {
        final String ACTIONABLE_DELIMITERS = " '-/\"("; // cause the following character to be capitalized

        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        for (char c : s.toCharArray()) {
            c = (capNext) ? Character.toUpperCase(c) : Character.toLowerCase(c);
            sb.append(c);
            capNext = (ACTIONABLE_DELIMITERS.indexOf(c) >= 0); // explicit cast not needed
        }
        return sb.toString();
    }
}