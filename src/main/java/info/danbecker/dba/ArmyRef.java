package info.danbecker.dba;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

/** A reference to an army that consists of the army list section
 * (a roman numeral 1..4), number (army 1..n), and optional version (letter).
 * <p>
 * The typical string form is I/1, I/35a, II/9d, etc. and is enough
 * to identify any army or version in the lists.
 * <p>
 * For example, given the following section/number/version
 * <ul>
 * <li>1,1,0 - signifies group I/1 -EARLY SUMERIAN 3000 BC - 2334 BC &
 *                THE "GREAT REVOLT" CIRCA 2250 BC
 * <li>1,1,1 - signifies army I/1a - Early Sumerian Army 3000-2800 BC
 * <li>1,1,2 - signifies army I/1c - Early Sumerian Army 2799-2334 BC
 * <li>1,1,3 - signifies army I/1c - Great Sumerian Revolt Army 2250 BC
 * <li>0,0,* - signifies illegal reference
 * </ul>
 *
 * @version DBA30
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 *
 * @param section - one-based army list section
 * @param number - one-based army number
 * @param version - one-based version a..z, if version==0 signifies group reference
 */
public record ArmyRef(int section, int number, int version) implements Comparable<ArmyRef> {

    public static final int MIN_SECTION = 1;
    public static final int MAX_SECTION = 4;
    public static final int MIN_NUMBER = 1;
    public static final int MIN_VERSION = 0;

    public ArmyRef {
        if ((section < MIN_SECTION) || (section > MAX_SECTION))
            throw new IllegalArgumentException("section (1..4)=" + section);
        if ((number < MIN_NUMBER) || (number > maxNumber(section)))
            throw new IllegalArgumentException("number (1..N)=" + number);
        if (version < MIN_VERSION)
            throw new IllegalArgumentException("version=" + version + " (<0)");
//        if (version > maxVersion(section, number))
//            throw new IllegalArgumentException("version=" + version + " (>" + maxVersion(section, number) + ")");
    }

    @Override
    public int compareTo(ArmyRef that) {
        if (null == that) return 1;
        if (this.section < that.section) return -1;
        if (this.section > that.section) return 1;
        if (this.number < that.number) return -1;
        if (this.number > that.number) return 1;

        return Integer.compare(this.version, that.version);
    }

    /** Common Roman numeral string 1..10 and "N" (nulla) for zero. */
    public static final String[] ROMAN_NUM = { "N", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X" };
    public static int parseSection(String secStr) {
        return switch( secStr ) {
            case "I" -> 1;
            case "II" -> 2;
            case "III" -> 3;
            case "IV" -> 4;
            default -> throw new IllegalStateException( "Section=" + secStr  );
        };
    }

    public String getSectionRoman() {
        return getSectionRoman( section );
    }
    /** Get the roman number version of the section. */
    public static String getSectionRoman( int section ) {
        return switch( section ) {
            case 1,2,3,4 -> ROMAN_NUM[ section ];
            default -> throw new IllegalStateException( "Section=" + section  );
        };
    }

    public static final String VER_STRING = "abcdefghijklmnopqrstuvwxyz";
    public static String getVersionLetter( int version ) {
        if ( 0 == version ) return "";
        if (( 0 < version ) && ( VER_STRING.length() > version))
            return VER_STRING.substring(version - 1, version );
        return "";
    }

    public String getVersionLetter() {
        return getVersionLetter( version );
    }

    @Override
    public String toString() {
        return format("%s/%d%s", ROMAN_NUM[ section ], number, getVersionLetter() );
    }

    /** Parse a single ArmyRef from the String.
     */
    public final static Pattern refPattern = Pattern.compile( "\\s*(?<sec>[IV]+)/(?<num>[\\d]+)(?<ver>[a-z]?)\\s*" );
    public static ArmyRef parse( String str ) throws IllegalStateException {
        try {
            Matcher matcher = refPattern.matcher(str);
            boolean b = matcher.find(); // must find or namedGroups to get groups

            String secStr = matcher.group("sec");
            int sec = parseSection(secStr);

            int armyNum = Integer.parseInt(matcher.group("num"));
            if ((armyNum < 1) || (armyNum > maxNumber(sec)))
                throw new IllegalStateException("Army number=" + matcher.group("num"));

            int version = 0;
            String ver = matcher.group("ver");
            if ((null != ver) && (!ver.isBlank())) {
                version = VER_STRING.indexOf(ver) + 1;
            }
            return new ArmyRef(sec, armyNum, version);
        } catch ( IllegalStateException e ) {
            throw new IllegalStateException( "Reg ex parse of \"" + str + "\"");
        }
    }

    /** Parse a comma-separated List of ArmyRef from the String.
     * String may have omitted book such as
     * "I/47,II/18e,31c,31f,31h,31i,31j".
     * <p>
     * This method is the recipricol of {@link #toStringCompact}.
     */
    public final static Pattern listPattern = Pattern.compile( "\\s*(?<ref>[IV/]*[\\d]+[a-z]?)[\\s,]*");
    public static List<ArmyRef> parseList( String str ) throws IllegalStateException {
        Matcher matcher = listPattern.matcher( str );
        // boolean b = matcher.find(); // must find or namedGroups to get groups

        List<ArmyRef> list = new LinkedList<>();
        String lastSec = "";
        while ( matcher.find() ) {
           String groupRef = matcher.group( "ref");
           // System.out.println( "groupRef=" + groupRef );
           if ((null != groupRef) && (!groupRef.isBlank())) {
               if ( !groupRef.startsWith( "I" ))
                   groupRef = lastSec + "/" + groupRef;
               ArmyRef armyRef = ArmyRef.parse(groupRef);
               lastSec = armyRef.getSectionRoman();
               list.add(armyRef);
           }
        }
        return list;
    }

    /** Return a compact String to represent the given list.
     * String will have omitted book such as
     * "I/47,II/18e,31c,31f,31h,31i,31j".
     * <p>
     * This method is the recipricol of {@link #parseList(String)}.
     *
     */
    public static String toStringCompact( List<ArmyRef> list )  {
        if ( null == list ) return "null";
        if ( list.isEmpty() ) return "";

        int prevSection = 0;
        StringBuilder sb = new StringBuilder();
        for( int ari = 0; ari < list.size(); ari++ ) {
            if (0 < ari) sb.append( "," );
            ArmyRef ar = list.get( ari );
            if ( ar.section == prevSection ) {
                sb.append ( ar.number );
                sb.append ( ar.getVersionLetter() ); // works for 0..z
            } else {
                sb.append( ar );
                prevSection = ar.section;
            }
        }
        return sb.toString();
    }

    /** Returns the max army number for the given section. */
    public static int maxNumber( int section ) {
        return switch( section ) {
            case 1 -> 64; // or length of MAX_VERSION
            case 2 -> 84;
            case 3 -> 80;
            case 4 -> 85;
            default -> throw new IllegalArgumentException( "section (1..4)=" + section);
        };
    }

    /** Version count for each section,number.
     * One-based. Do not use the zero slots. */
    public static final int[][] MAX_VERSION = {
            new int[] {},
            new int[] {
                    0, 3, 2, 1, 4, 4, 3, 4, 3, 1,
                    1, 2, 1, 2, 6, 1, 1, 2, 1, 1,
                    2, 2, 2, 2, 2, 2, 2, 1, 1, 2,
                    3, 2, 3, 2, 3, 4, 3, 2, 1, 2,
                    1, 2, 1, 3, 2, 1, 3, 1, 1, 4,
                    1, 1, 10, 1, 1, 5, 2, 2, 1, 1,
                    3, 2, 1, 1, 3,
            },
            new int[] {
                    0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1,
            },
            new int[] {
                    0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1,
            },
            new int[] {
                    0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1,
            },
    };

    /** Version count for each section,number */
    public static int maxVersion( int section, int number ) {
        if (( section < MIN_SECTION ) || (section > MAX_SECTION)) throw new IllegalArgumentException( "section (1..4)=" + section);
        if (( number < MIN_NUMBER ) || (number > maxNumber( section ))) throw new IllegalArgumentException( "number (1..N)=" + section);
        return MAX_VERSION[ section ][ number ];
    }
}
