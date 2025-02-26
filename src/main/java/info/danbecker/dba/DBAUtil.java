package info.danbecker.dba;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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
    public static final String PATH_DEFAULT = "E:\\hobbies\\games\\miniatures\\DBA\\";
    public static final String ARMY_HEADER_DEFAULT = PATH_DEFAULT + "DBA3.0-ArmyGroupNames.csv";
    public static final String ARMY_DEFAULT = PATH_DEFAULT + "DBAArmies3.0-Export.csv";
    public static final String OUTPUT_DEFAULT = PATH_DEFAULT + "DBAArmies3.0.json";

    // Consider Sealed records version of command line options
    // https://www.infoq.com/articles/data-oriented-programming-java/
    // sealed interface Option {
    //     record InputFile(Path path) implements Option { }
    //     record OutputFile(Path path) implements Option { }
    //     record MaxLines(int maxLines) implements Option { }
    //     record PrintLineNumbers() implements Option { }
    // }
    // static List<Option> parseOptions(String[] args) { ... }

    public static HashMap<String, ArmyBean> DBA3Armies = new HashMap<>();
    public static void main(String[] args) throws IOException{
        List<ArmyHeaderBean> headerBeans = new CsvToBeanBuilder(new FileReader(ARMY_HEADER_DEFAULT))
                .withType(ArmyHeaderBean.class)
                .build()
                .parse();

        headerBeans.forEach(System.out::println);
//        beans.forEach( b -> {
//                    DBA3Armies.put( b.getRef(), b );
//                    System.out.println( b );
//                }
//        );
        List<ArmyBean> beans = new CsvToBeanBuilder(new FileReader(ARMY_DEFAULT))
                .withType(ArmyBean.class)
                .build()
                .parse();
        beans.forEach(System.out::println);
        beans.forEach( b -> {
                    DBA3Armies.put( b.getRef(), b );
                    System.out.println( b );
                }
        );
//        String ref = "III/22";
//        System.out.println( ref + "=" + DBA3Armies.get( ref ));
//        ref = "III/22a";
//        System.out.println( ref + "=" + DBA3Armies.get( ref ));
    }
}