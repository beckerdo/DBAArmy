package info.danbecker.dba;

import java.util.ArrayList;
import java.util.List;
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

    public final static YearType EARLY_YEAR = new YearType( 3000, YearType.Era.BC );
    public final static YearType LATE_YEAR = new YearType( 1580, YearType.Era.AD );

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
    public final static Pattern yearPattern =
        // This pattern matches exactly 2 dates in a range
        // Pattern.compile( "\\s*(?<beginY>[\\d]+)\\s*(?<beginE>(BC|AD)?)\\s*-\\s*(?<endY>[\\d]+)\\s*(?<endE>(BC|AD)?)\\s*");
        // This pattern matches modifiers with one date and multiple groups.
        Pattern.compile( "\\s*(?<modifier>CIRCA|circa|BEFORE|before|AFTER|after)*\\s*(?<year>[\\d]+)\\s*(?<era>(BC|AD)?)\\s*");
    public static YearRange parse(String str) {
        Matcher matcher = yearPattern.matcher( str );
        // boolean b = matcher.find(); // must find or namedGroups to get groups
        int matchCount = 0;
        int patternMatchCount = matcher.groupCount();

        List<String> years = new ArrayList<>();
        List<String> modifiers = new ArrayList<>();
        while( matcher.find() ) {
            String modifier = matcher.group("modifier");
            modifier = (null == modifier) ? "" : modifier.toLowerCase();
            String year = matcher.group("year");
            String era = matcher.group("era");

            years.add( year + era ); // some fixups might come later
            modifiers.add( modifier );
            matchCount++;
        }
        // System.out.format( "Input %s: dates %s, mods %s%n", str, years, modifiers );

        // Lots of fix ups for missing eras,
        return switch (matchCount) {
            case 0 -> throw new IllegalStateException( "Found zero years in \"" + str + "\"");
            case 1 -> {
                if ( "before".equals( modifiers.getFirst()))
                    yield new YearRange( EARLY_YEAR, YearType.parse( years.getFirst()));
                else if ( "after".equals( modifiers.getFirst()))
                    yield new YearRange( YearType.parse( years.getFirst()), LATE_YEAR );
                else if ( "circa".equals( modifiers.getFirst())) {
                    YearType year = YearType.parse( years.getFirst() );
                    if ( "AD".equals( year.era().name() )) {
                        yield new YearRange( new YearType( year.year()-50, year.era() ), new YearType( year.year()+50, year.era() ) );
                    } else {
                        yield new YearRange( new YearType( year.year()+50, year.era() ), new YearType( year.year()-50, year.era() ) );
                    }
                }
                yield new YearRange( years.getFirst(), years.getFirst());
            }
            // Options for 2 or more dates:
            //    -parse first two
            //    -parse first and last (implemented)
            //    -min of evens, max of odds
            //    -throw IllegalStateException
            //    -parse list of dates, two at a time.
            default -> {
                yield fillMissingEra( years.getFirst(), years.getLast() );
            }
        };
    }

    /**
     * If needed, fills in missing eras to the begin or end year.
     * @param yearB beginning year for range
     * @param yearE ending year for range
     * @return YearRange with complete eras
     */
    public static YearRange fillMissingEra( String yearB, String yearE ) {
        if ( yearB.contains("BC") && !yearE.contains("BC") && !yearE.contains( "AD" ))
            return new YearRange( yearB, yearE + "BC");
        else if ( yearB.contains("AD") && !yearE.contains("BC") && !yearE.contains( "AD" ))
            return new YearRange( yearB, yearE + "AD");
        else if ( yearE.contains("BC") && !yearB.contains("BC") && !yearB.contains( "AD" ))
            return new YearRange( yearB + "BC", yearE);
        else if ( yearE.contains("AD") && !yearB.contains("BC") && !yearB.contains( "AD" ))
            return new YearRange( yearB + "AD", yearE);
        return new YearRange( yearB, yearE );
    }
}