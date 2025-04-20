package info.danbecker.dba;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DBA army lists have years as integers with era as BC or AD.
 *
 * @param year - unsigned year
 * @param era - one of BC or AD
 *
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public record YearType(int year, Era era) implements Comparable<YearType> {
    public enum Era { BC, AD }

    public YearType {
        if (year < 1)
            // IllegalArgumentException preferred, but regExs throw IllegalStateException
            throw new IllegalStateException("year (1..N)=" + year);
    }

    public YearType( YearType year ) {
        this( year.year, year.era );
    }

    public YearType( String year ) {
        this(  parse( year ) ) ;
    }

    @Override
    public int compareTo(YearType that) {
        if (null == that) return -1;
        if (this.era.ordinal() < that.era.ordinal() ) return -1;
        if (this.era.ordinal() > that.era.ordinal()) return 1;
        if (this.era == Era.AD) return Integer.compare(this.year, that.year);
        if (this.era == Era.BC) return Integer.compare(that.year, this.year);
        return 0;
    }

    public boolean equals( YearType other ) {
        return 0 == this.compareTo( other );
    }

    /**
     * Useful for Collections.sort where to sort by the ArmyRef natural order
     */
    public static final Comparator<? super YearType> YearComparator = Comparator.naturalOrder();

    @Override
    public String toString() {
        return year + era.name();
    }

    public final static Pattern pattern = Pattern.compile( "\\s*(?<year>[\\d]+)\\s*(?<era>BC|AD)\\s*" );

    public static YearType parse(String str) {
        Matcher matcher = pattern.matcher( str );
        boolean b = matcher.find(); // must find or namedGroups to get groups

        String yearStr = matcher.group("year");
        String eraStr = matcher.group("era");
        return new YearType(Integer.parseInt(yearStr), Era.valueOf( eraStr ));
    }
}
