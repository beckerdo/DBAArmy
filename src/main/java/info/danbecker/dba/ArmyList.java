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
import java.util.logging.Logger;
import java.util.logging.Level;
import static java.lang.String.format;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import com.opencsv.bean.CsvToBeanBuilder;
import info.danbecker.csv.ArmyHeaderBean;
import info.danbecker.csv.ArmyVariantBean;

/**
 * A utility for reading, writing, and analyzing
 * De Bellis Antiquatis (DBA) army lists
 * <p>
 * Some things to do with the utility:
 * <ul>
 * <li>Lookup armies by reference number, name, year, geography, aggression, troop def, etc.
 * <li>Enumerate all permutations of an army troop definition.
 * <li>Test whether an army instance fits a troop definition.
 * <li>Create lists of allied and enemy armies (draw graph? DBA dot diagrams).
 * <li>Create lists of armies with most enemies and allies.
 * <li>Pick a date, show armies, potential battles for that time frame.
 * <li>Input and output army info via CSV or JSON.
 * </ul>
 * <p>
 * Lots of CSV ideas taken from
 * <a href="https://mkyong.com/java/how-to-read-and-parse-csv-file-in-java/">Mkyong.com</a>.
 *
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public class ArmyList {
    public static final String PATH_DEFAULT = "src/main/resources";
    public static final String PATH_DELIM = "/";
    public static final String ARMY_HEADER_DEFAULT = "DBA3.0-ArmyGroupNames.csv";
    public static final String ARMY_DEFAULT = "DBA3.0-ArmyVariants.csv";
    public static final String OUTPUT_DEFAULT = "DBA3.0-ArmyOutput.json";

    static Logger LOGGER = Logger.getLogger(ArmyList.class.getName());
    // Armies is the main container for all armies.
    public static HashMap<ArmyRef,Army> Armies = new HashMap<>();

    // Some configuration parameters via JCommander.org
    public static class Options {
        @Parameter(names = "-inPath", description = "Input path. A directory containing input files.")
        public String inPath = "";
        @Parameter(names = "-inFile", description = "Input file. A file with army list input data.")
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

    /**
     * main method reads the parameters, loads the armies, performs actions on the data.
     *
     * @param args provides the option parameters
     * @throws IOException when files not readable
     */
    public static void main(String[] args) throws IOException {
        LOGGER.setLevel( Level.ALL );
        System.setProperty("java.util.logging.SimpleFormatter.format","%1$tF %1$tT %4$s: %5$s%6$s");
        LOGGER.info( "DBAUtil 1.0.0 by Dan Becker\n" );

        // Process configuration args
        Options opt = new Options();
        List<String> inputFiles = new ArrayList<>();
        if (0 < args.length) {
            processCommandOptions( args, opt, inputFiles );
        }

        // Some app context
        Path rootPath = Paths.get( "." ).normalize().toAbsolutePath();
        boolean isRootPathReadable = Files.isReadable( rootPath );
        boolean isRootPathDir = Files.isDirectory( rootPath );
        System.out.printf( "App context is \"%s\" %s readable, %s directory%n", rootPath, isIsNot( isRootPathReadable ), isIsNot( isRootPathDir ) );

        // Read army headers and add to ArmyList.
        Path inPath = Paths.get( PATH_DEFAULT, ARMY_HEADER_DEFAULT );
        boolean inPathReadable = Files.isReadable( inPath );
        System.out.printf( "Reading headers from \"%s\" %s readable%n", inPath, isIsNot( inPathReadable ));
        @SuppressWarnings("unchecked")
        List<ArmyHeaderBean> headerBeans = new CsvToBeanBuilder(new FileReader( inPath.toString() ))
                .withType(ArmyHeaderBean.class)
                .build()
                .parse();
        headerBeans.forEach( b-> {
            ArmyHeader armyHeader = new ArmyHeader( ArmyRef.parse( b.armyRef ), b.name, b.varCount );
            // System.out.format( "%s %s, %d, %s %d\n", armyHeader.armyRef, armyHeader.groupName, armyHeader.variantCount,
            //     armyHeader.names.toString(), armyHeader.names.size());
            Armies.put(armyHeader.armyRef, new Army( armyHeader, new ArrayList<>() ));
        });

        // Read army variants and add to ArmyList.
        inPath = Paths.get( PATH_DEFAULT, ARMY_DEFAULT );
        inPathReadable = Files.isReadable( inPath );
        System.out.printf( "Reading variants from \"%s\" %s readable%n", inPath, isIsNot( inPathReadable ));
        @SuppressWarnings("unchecked")
        List<ArmyVariantBean> variantBeans = new CsvToBeanBuilder(new FileReader( inPath.toString()))
                .withType(ArmyVariantBean.class)
                .build()
                .parse();
        variantBeans.forEach( b-> {
            ArmyRef armyRef = new ArmyRef( b.book, b.armyNum, 0 );
            ArmyRef varRef = new ArmyRef( b.book, b.armyNum, ArmyRef.getVersionNumber( b.var ) );
            Army army = Armies.get( armyRef );
            if ( null == army ) throw new IllegalArgumentException( "Could not find armyRef " + armyRef );
            // System.out.print( varRef );

            ArmyVariant armyVariant = new ArmyVariant( varRef, b.name, b.getElements(), b.topo, b.agg,
               b.enemies, b.allies);
            army.getVariants().add( armyVariant );
            // System.out.format( " %s, E=%s A=%s%n", armyVariant.variantName, armyVariant.enemies.toString(), armyVariant.allies.toString());
        });
    }

    /**
     * Process command line options from the String arguments.
     * Return list of options and list of input file paths.
     * <p>
     * Note that Windows file names passed from Explorer to this executable
     * throw java.nio.file.InvalidPathException: Illegal char <:> at index 2: /E:\workDirectory\
     * So there is some code to remove the leading / before the drive letter and colon.
     */
    public static void processCommandOptions( String[] args, Options opts, List<String> inputFiles ) {
        JCommander.newBuilder()
                .addObject(opts)
                .build()
                .parse(args);

        boolean IS_WINDOWS = System.getProperty( "os.name" ).contains( "indow" );
        if ( IS_WINDOWS && opts.inPath.startsWith( PATH_DELIM ))
            opts.inPath = opts.inPath.substring(1);
        boolean isInPathReadable = Files.isReadable( Paths.get( opts.inPath ));
        System.out.printf( "Input path \"%s\" %s readable.%n", opts.inPath, isIsNot( isInPathReadable ));
        boolean isInPathDir = Files.isDirectory( Paths.get( opts.inPath ));
        System.out.printf( "Input path \"%s\" %s a directory.%n", opts.inPath, isIsNot( isInPathDir ));
        boolean dirReadable = isInPathReadable && isInPathDir;

        if ( IS_WINDOWS && opts.inFile.startsWith( PATH_DELIM ))
            opts.inFile = opts.inFile.substring(1);
        boolean fileReadable = Files.isReadable( Paths.get( opts.inFile )) &&
                Files.isRegularFile( Paths.get( opts.inFile ));
        System.out.printf( "Input file \"%s\" %s readable.%n", opts.inFile, isIsNot( fileReadable ));

        String comboPathStr = opts.inFile;
        if ( isInPathDir ) {
            if ( opts.inPath.endsWith( PATH_DELIM ) || opts.inFile.startsWith( PATH_DELIM ))
                comboPathStr = opts.inPath + opts.inFile;
            else
                comboPathStr = opts.inPath + PATH_DELIM + opts.inFile;
        }
        if ( IS_WINDOWS && comboPathStr.startsWith( PATH_DELIM ))
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
            inputFiles.add( opts.inFile );
        }
        else if (dirReadable) {
            // dirReadable - list files there
            File[] files = new File(opts.inPath).listFiles();
            if ( null != files ) {
                for (File file : files) {
                    if (file.isFile() && file.canRead()) {
                        if (file.getName().contains(opts.nameContains) ||
                                file.getName().endsWith(opts.nameEndsWith)) {
                            inputFiles.add(file.getPath());
                        }
                    }
                }
            }
        }
        // otherwise - message
        LOGGER.info( format( "Input path \"%s\", file \"%s\", contains \"%s\", endsWith \"%s\" found %d items.%n",
                opts.inPath, opts.inFile, opts.nameContains, opts.nameEndsWith, inputFiles.size()));

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

    /**
     * Returns a list of armies that have the given year in their headers year range.
     * @param year to test for army inclusion
     * @return list of Army with year range spanning the given year
     */
    public static List<Army> getByYear( YearType year ) {
        return Armies.values().stream()
                .filter( army -> army.header.years.stream()
                   .anyMatch(yr -> yr.contains( year )))
                .sorted()
                .toList();
    }

    /**
     * Returns a list of armies that have the given terrain in all the variants.
     * @param terrain  to test for army inclusion
     * @return list of ArmyVariants with the given terrain
     */
    public static List<ArmyVariant> getByTerrain( String terrain ) {
        if ( null == terrain || terrain.isEmpty() ) throw new IllegalArgumentException( "Terrain \"" + terrain + " is an invalid terrain type" );
        final String terrainCase = ArmyHeader.toDisplayCase( terrain );
        return Armies.values().stream()
                .flatMap( army->army.getVariants().stream() )
                .filter( av->av.terrain.contains( TerrainType.fromString( terrain )))
                .sorted()
                .toList();
    }

    /**
     * Returns a list of armies that have the given aggression level.
     * @param aggr an aggression level (0..6 inclusive)
     * @return list of ArmyVariants with the given aggression
     */
    public static List<ArmyVariant> getByAggression( int aggr ) {
        if ( 0 > aggr || 6 < aggr ) throw new IllegalArgumentException( "Aggression " + aggr + " should be in the range (0..6)" );
        return Armies.values().stream()
                .flatMap( army->army.getVariants().stream() )
                .filter( av-> aggr == av.aggression )
                .sorted()
                .toList();
    }

    /**
     * Returns a list of armies that have the given element type
     * @param elementOrCode either an element type name ("BLADES" "BOWS") or an element code ("3Bd" or "4Lb")
     * @return list of ArmyVariants with the given aggression
     */
    public static List<ArmyVariant> getByElementType( String elementOrCode ) {
        ElementType elementType = ElementType.fromString( elementOrCode ); // null if not an ElementType name
        ElementType codeParent = ElementType.fromCode( elementOrCode ); // null if not a, ElementType code
        if ( null == elementType && null == codeParent ) throw new IllegalArgumentException( "Could not find ElementType or element code from String \"" + elementOrCode + "\"" );
        return Armies.values().stream()
                .flatMap( army->army.getVariants().stream() )
                .filter( av -> {
                    List<String> unitList = av.troopDef.getUnitList();
                    if ( null == elementType ) {
                        return unitList.contains(elementOrCode);
                    } else {
                        return unitList.stream()
                            .anyMatch( thisCode -> elementType.equals( ElementType.fromCode(thisCode)));
                    }
                })
                .sorted()
                .toList();
    }
}