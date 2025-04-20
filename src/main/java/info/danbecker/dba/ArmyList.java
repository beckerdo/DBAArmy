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
 * <li>Lookup armies by name, reference number, geography, etc.
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
    public static final String PATH_DEFAULT = "e:\\hobbies\\games\\miniatures\\DBA\\";
    public static final String ARMY_HEADER_DEFAULT = PATH_DEFAULT + "DBA3.0-ArmyGroupNames.csv";
    public static final String ARMY_DEFAULT = PATH_DEFAULT + "DBAArmies3.0-Export.csv";
    public static final String OUTPUT_DEFAULT = PATH_DEFAULT + "DBAArmies3.0.json";

    static Logger LOGGER = Logger.getLogger(ArmyList.class.getName());

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

    // Armies is the main container for all armies.
    public static HashMap<ArmyRef,Army> Armies = new HashMap<>();
    // Section might be needed, but all data is available in Armies.
    // public static List<TroopDef> Sections = new ArrayList<>();

    /**
     * main method reads the parameters, loads the armies, performs actions on the data.
     *
     * @param args provides the option parameters
     * @throws IOException
     */
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

        // Read army headers and add to ArmyList.
        @SuppressWarnings("unchecked")
        List<ArmyHeaderBean> headerBeans = new CsvToBeanBuilder(new FileReader(ARMY_HEADER_DEFAULT))
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
        @SuppressWarnings("unchecked")
        List<ArmyVariantBean> variantBeans = new CsvToBeanBuilder(new FileReader(ARMY_DEFAULT))
                .withType(ArmyVariantBean.class)
                .build()
                .parse();
        variantBeans.forEach( b-> {
            ArmyRef armyRef = new ArmyRef( b.book, b.armyNum, 0 );
            ArmyRef varRef = new ArmyRef( b.book, b.armyNum, ArmyRef.getVersionNumber( b.var ) );
            Army army = Armies.get( armyRef );
            if ( null == army ) throw new IllegalArgumentException( "Could not find armyRef " + armyRef );
            System.out.format( "ArmyRef=%s\n", varRef );

            ArmyVariant armyVariant = new ArmyVariant( varRef, b.name, b.getElements(),
                            b.topo, b.agg, b.enemies, b.allies);
            army.getVariants().add( armyVariant );
            System.out.format( "%s, E=%s A=%s\n", armyVariant.variantName, armyVariant.enemies.toString(), armyVariant.allies.toString());
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
}