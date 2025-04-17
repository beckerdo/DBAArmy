package info.danbecker.dba;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.logging.Logger;
import java.util.logging.Level;
import static java.lang.String.format;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import info.danbecker.csv.ArmyBean;
import com.opencsv.bean.CsvToBeanBuilder;
import info.danbecker.csv.ArmyHeaderBean;

/**
 * A small utility for moving a list of
 * De Bellis Antiquatis (DBA) army lists from
 * a CSV data base to a JSON representation.
 * <p>
 * Some things to do with the utility:
 * <ul>
 * <li>Convert an army CSV to JSON.
 * <li>Enumerate all permutations of an army list.
 * <li>See whether an army instance is an instance of an army.
 * <li>Create lists of allied and enemy armies (draw graph? DBA dot diagrams).
 * <li>Create lists of armies with most enemies and allies.
 * <li>Pick a date, show armies, potential battles for that time frame.
 * </ul>
 * <p>
 * {@code @TODO} Create class to unite ArmyRef with Army.
 * </p>
 * <p>
 * Lots of CSV ideas taken from
 * <a href="https://mkyong.com/java/how-to-read-and-parse-csv-file-in-java/">Mkyong.com</a>.
 *
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public class DBAUtil {
    public static final String PATH_DEFAULT = "e:\\hobbies\\games\\miniatures\\DBA\\";
    public static final String ARMY_HEADER_DEFAULT = PATH_DEFAULT + "DBA3.0-ArmyGroupNames.csv";
    public static final String ARMY_DEFAULT = PATH_DEFAULT + "DBAArmies3.0-Export.csv";
    public static final String OUTPUT_DEFAULT = PATH_DEFAULT + "DBAArmies3.0.json";

    static Logger LOGGER = Logger.getLogger(DBAUtil.class.getName());

    // Some configuration parameters via JCommander.org
    public static class Options {
        @Parameter(names = "-inPath", description = "Input path. A directory containing books.")
        public String inPath = "";
        @Parameter(names = "-inFile", description = "Input file. A file with notes and highlights.")
        public String inFile = "";
        @Parameter(names = "-outPath", description = "Explicit text for output file name.")
        public String outPath = "";
        @Parameter(names = "-outFile", description = "Explicit text for output file name.")
        public String outFile = "";
        @Parameter(names = "-nameContains", description = "Contains pattern for file names.")
        public String nameContains = "Notebook";
        @Parameter(names = "-nameEndsWith", description = "Ends with pattern for file names.")
        public String nameEndsWith = ".html";
    }

    public static HashMap<String, ArmyBean> DBA3Armies = new HashMap<>();

    public static void main(String[] args) throws IOException{
        LOGGER.setLevel( Level.ALL );
        System.setProperty("java.util.logging.SimpleFormatter.format","%1$tF %1$tT %4$s: %5$s%6$s");
        LOGGER.info( "DBAUtil 1.0.0 by Dan Becker\n" );

        // Process configuration args
        Options opt = new Options();
        List<String> inputFiles = new ArrayList<>();
        if (0 < args.length) {
            processCommandOptions( args, opt, inputFiles );
        }

        List<ArmyHeaderBean> headerBeans = new CsvToBeanBuilder(new FileReader(ARMY_HEADER_DEFAULT))
                .withType(ArmyHeaderBean.class)
                .build()
                .parse();

        headerBeans.forEach( b-> {
            String groupName = b.name;
            List<String> names = getNames( groupName );
            System.out.format( "%s, %d, %s %d\n", groupName, b.varCount, names.toString(),
                names.size() );
            }
        );

        // headerBeans.forEach(System.out::println);
//        beans.forEach( b -> {
//                    DBA3Armies.put( b.getRef(), b );
//                    System.out.println( b );
//                }
//        );

//        List<ArmyBean> beans = new CsvToBeanBuilder(new FileReader(ARMY_DEFAULT))
//                .withType(ArmyBean.class)
//                .build()
//                .parse();
//        beans.forEach(System.out::println);
//        beans.forEach( b -> {
//                    DBA3Armies.put( b.getRef(), b );
//                    System.out.println( b );
//                }
//        );
//        String ref = "III/22";
//        System.out.println( ref + "=" + DBA3Armies.get( ref ));
//        ref = "III/22a";
//        System.out.println( ref + "=" + DBA3Armies.get( ref ));
    }

    /**
     * Process command line options from the String arguments.
     * Return list of options and list of input file paths.
     * <p>
     * Note that Windows file names passed from Explorer to this executable
     * throw java.nio.file.InvalidPathException: Illegal char <:> at index 2: /E:\workDirectory\
     * So there is some code to remove the leading / before the drive letter and colon.
     */
    public static void processCommandOptions( String[] args, Options opt, List<String> inputFiles ) throws IOException {
        JCommander.newBuilder()
                .addObject(opt)
                .build()
                .parse(args);

        String pathDelim = "/";
        boolean IS_WINDOWS = System.getProperty( "os.name" ).contains( "indow" );
        if ( IS_WINDOWS && opt.inPath.startsWith( pathDelim ))
            opt.inPath = opt.inPath.substring(1);
        boolean isInPathReadable = Files.isDirectory( Paths.get( opt.inPath ));
        System.out.printf( "Input path \"%s\" %s readable.%n", opt.inPath, isIsNot( isInPathReadable ));
        boolean isInPathDir = Files.isDirectory( Paths.get( opt.inPath ));
        System.out.printf( "Input path \"%s\" %s a directory.%n", opt.inPath, isIsNot( isInPathDir ));
        boolean dirReadable = isInPathReadable && isInPathDir;

        if ( IS_WINDOWS && opt.inFile.startsWith( pathDelim ))
            opt.inFile = opt.inFile.substring(1);
        boolean fileReadable = Files.isReadable( Paths.get( opt.inFile )) &&
                Files.isRegularFile( Paths.get( opt.inFile ));
        System.out.printf( "Input file \"%s\" %s readable.%n", opt.inFile, isIsNot( fileReadable ));

        String comboPathStr = opt.inFile;
        if ( isInPathDir ) {
            if ( opt.inPath.endsWith( pathDelim ) || opt.inFile.startsWith( pathDelim ))
                comboPathStr = opt.inPath + opt.inFile;
            else
                comboPathStr = opt.inPath + pathDelim + opt.inFile;
        }
        if ( IS_WINDOWS && comboPathStr.startsWith( pathDelim ))
            comboPathStr = comboPathStr.substring(1);
        Path comboPath = Paths.get( comboPathStr );
        boolean comboReadable = Files.isReadable( comboPath ) && Files.isRegularFile( comboPath );
        System.out.printf( "Input file \"%s\" %s readable.%n", comboPathStr, isIsNot( comboReadable ));

        if (comboReadable) {
            // Use comboPath
            inputFiles.add( comboPathStr );
        }
        else if (fileReadable) {
            // Use inputPath
            inputFiles.add( opt.inFile );
        }
        else if (dirReadable) {
            // dirReadable - list files there
            File[] files = new File(opt.inPath).listFiles();
            if ( null != files ) {
                for (File file : files) {
                    if (file.isFile() && file.canRead()) {
                        if (file.getName().contains(opt.nameContains) &&
                                file.getName().endsWith(opt.nameEndsWith)) {
                            inputFiles.add(file.getPath());
                        }
                    }
                }
            }
        }
        // otherwise - message
        LOGGER.info( format( "Input path \"%s\", file \"%s\", contains \"%s\", endsWith \"%s\" found %d items.%n",
                opt.inPath, opt.inFile, opt.nameContains, opt.nameEndsWith, inputFiles.size()));

        // Remove files to not use.
        List<String> removes = List.of( "README.txt" );
        for ( String fileName : removes ) {
            inputFiles.remove( fileName );
        }
    }

    /** Change boolean to "is" or "is not" String. */
    public static String isIsNot( boolean is ) {
        return is ? "is" : "is not";
    }

    public static List<String> getNames( String groupName ) {
        Stream<String> stream = Pattern.compile("[,&]").splitAsStream( groupName );
        return stream
            .map(String::trim)
            .filter( str -> str.matches("^[A-Z -].*"))
            .map( DBAUtil::getNameNoDates) // remove dates, ranges, and CIRCAS
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
     * @param groupName
     * @return
     */
    public static List<String> getDates( String groupName ) {
        Stream<String> stream = Pattern.compile("[,&]").splitAsStream( groupName);
        return stream
                .map(String::trim)
                // .filter( str -> str.matches("^[A-Z -].*"))
                .map( DBAUtil::getDatesNoNames) // remove group names
                .filter( str -> !str.isEmpty() ) // filter out lack of date
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
}