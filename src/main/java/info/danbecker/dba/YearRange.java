package info.danbecker.dba;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DBA army lists have years as integers with era as BC or AD.
 * Often a range of years is provided such as 9BC - 54 AD.
 *
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public record YearRange(YearType begin, YearType end) implements Comparable<YearRange> {
    public static String RANGE_DELIM = "-";

    public YearRange {
        if (0 < begin.compareTo(end))
            throw new IllegalStateException("begin year " + begin + " should be earlier than end year " + end);
    }

    public YearRange( String begin, String end ) {
        this( YearType.parse( begin ), YearType.parse( end ));
    }

    /** Simply compare begin years to see which is smaller */
    @Override
    public int compareTo(YearRange that) {
        if (null == that) return 1;
        return this.begin.compareTo( that.begin );
    }

    /** States whether a given year is in the range (inclusive)
     * @param that a YearType to be tested
     * @return whether YearType is in the YearRange
     */
    public boolean contains(YearType that) {
        if (null == that) return false;
        int firstCompare = that.compareTo( this.begin );
        if ( 0 > firstCompare )
           return false;
        int secondCompare = that.compareTo( this.end );
        return 0 >= secondCompare;
    }

    @Override
    public String toString() {
        // Consider making compact if eras match, ee.g. 29-4BC.
        return new StringBuilder().append(begin.toString()).append(RANGE_DELIM).append(end.toString()).toString();
    }

    // Examples
    // 1000 BC - 650 BC
    // 1100 - 701 BC
    // V/6a Early Bedouin Army 3000 BC to 1001 BC
    // 1/25 BOSPORAN 310 BC - 107 BC & 10 BC - 375 AD
    // CIRCA 2250BC (becomes 2200BC - 2300BC)
    public final static Pattern pattern =
        Pattern.compile( "\\s*(?<beginY>[\\d]+)\\s*(?<beginE>(BC|AD)?)\\s*-\\s*(?<endY>[\\d]+)\\s*(?<endE>(BC|AD)?)\\s*");

    public static YearRange parse(String str) {
        if ( str.startsWith( "CIRCA ")) {
            str = str.substring( "CIRCA ".length() );
            if (!str.contains( "-" )) {
                // One date, make a range.
                YearType year = YearType.parse( str );
                if (year.era() == YearType.Era.AD)
                    return new YearRange(
                            new YearType(year.year() - 50, year.era()),
                            new YearType(year.year() + 50, year.era()));
                else
                    return new YearRange(
                            new YearType(year.year() + 50, year.era()),
                            new YearType(year.year() - 50, year.era()));
            }
        }
        // No circa, no range
        if (!str.contains( "-" )) {
            // One date, make a range.
            YearType year = YearType.parse( str );
            return new YearRange( year, year );
        }

        Matcher matcher = pattern.matcher( str );
        boolean b = matcher.find(); // must find or namedGroups to get groups

        String beginY = matcher.group("beginY");
        String beginE = matcher.group("beginE");
        String endY = matcher.group("endY");
        String endE = matcher.group("endE");

        if ( beginY.isBlank() && endY.isBlank() )
            throw new IllegalStateException( "Begin and end years blank");
        if ( beginE.isBlank() && endE.isBlank() )
            throw new IllegalStateException( "Begin and end eras blank");
        if ( beginE.isBlank()) beginE = endE;
        if ( endE.isBlank()) endE = beginE;
        return new YearRange(
            new YearType(Integer.parseInt(beginY) , YearType.Era.valueOf( beginE )),
            new YearType(Integer.parseInt(endY) , YearType.Era.valueOf( endE ))
        );
    }
}