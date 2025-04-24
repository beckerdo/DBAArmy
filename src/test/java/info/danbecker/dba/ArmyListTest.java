package info.danbecker.dba;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static info.danbecker.dba.ArmyList.*;
import static org.junit.jupiter.api.Assertions.*;

public class ArmyListTest {
    public static String[] LOAD_ARGS = { "-inPath", PATH_DEFAULT, "-nameContains", "Army", "-nameEndsWith", ".csv" };
    @Test
    public void testBasics() {

        Options opts = new Options();
        List<String> inputFiles = new ArrayList<>();
        inputFiles.add( "README.txt" );
        ArmyList.processCommandOptions( LOAD_ARGS, opts, inputFiles );
        assertEquals( 2, inputFiles.size() );
        assertTrue( inputFiles.getFirst().contains( ARMY_HEADER_DEFAULT ));
        assertTrue( inputFiles.getLast().contains( ARMY_DEFAULT ));


        assertEquals( "is", isIsNot( true ));
        assertEquals( "is not", isIsNot( false ));
    }

    @Test
    public void testArmyListByYear() throws IOException {
        // Test function without loading.
        List<Army> armies = ArmyList.getByYear( YearType.parse( "54BC" ) );
        assertEquals( 0, armies.size() );

        // Load something
        ArmyList.main( LOAD_ARGS );

        armies = ArmyList.getByYear( YearType.parse( "54BC" ) );
        assertEquals( 33, armies.size() );
        assertEquals( ArmyRef.parse( "I/7" ), armies.getFirst().getArmyRef() );
        assertEquals( ArmyRef.parse( "II/54" ), armies.getLast().getArmyRef() );

        armies = ArmyList.getByYear( YearType.parse( "1066AD" ) );
        assertEquals( 57, armies.size() );
        assertEquals( ArmyRef.parse( "II/42" ), armies.getFirst().getArmyRef() );
        assertEquals( ArmyRef.parse( "III/80" ), armies.getLast().getArmyRef() );
    }
}