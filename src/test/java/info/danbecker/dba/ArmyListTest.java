package info.danbecker.dba;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static info.danbecker.dba.ArmyList.*;
import static java.lang.String.format;
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

    @Test
    public void testArmyListByTerrain() throws IOException {
        // Test function without loading.
        List<ArmyVariant> variants = ArmyList.getByTerrain( "arable" );
        assertEquals( 0, variants.size() );

        // Load something
        ArmyList.main( LOAD_ARGS );

        int count = 0;
        variants = ArmyList.getByTerrain( "arable" );
        // variants.forEach( av-> System.out.format( "%s, %s, %s%n",
        //   av.armyRef, av.variantName, av.terrain ));
        count += variants.size();
        assertEquals( 262, variants.size() );
        assertEquals( ArmyRef.parse( "I/1a" ), variants.getFirst().getArmyRef() );
        assertEquals( ArmyRef.parse( "IV/85b" ), variants.getLast().getArmyRef() );

        variants = ArmyList.getByTerrain( "forest" );
        // variants.forEach( av-> System.out.format( "%s, %s, %s%n",
        //   av.armyRef, av.variantName, av.terrain ));
        count += variants.size();
        assertEquals( 22, variants.size() );
        assertEquals( ArmyRef.parse( "II/47a" ), variants.getFirst().getArmyRef() );
        assertEquals( ArmyRef.parse( "IV/66" ), variants.getLast().getArmyRef() );

        variants = ArmyList.getByTerrain( "hilly" );
        // variants.forEach( av-> System.out.format( "%s, %s, %s%n",
        //    av.armyRef, av.variantName, av.terrain ));
        count += variants.size();
        assertEquals( 96, variants.size() );
        assertEquals( ArmyRef.parse( "I/4a" ), variants.getFirst().getArmyRef() );
        assertEquals( ArmyRef.parse( "IV/84b" ), variants.getLast().getArmyRef() );

        variants = ArmyList.getByTerrain( "steppe" );
        // variants.forEach( av-> System.out.format( "%s, %s, %s%n",
        //    av.armyRef, av.variantName, av.terrain ));
        count += variants.size();
        assertEquals( 54, variants.size() );
        assertEquals( ArmyRef.parse( "I/3" ), variants.getFirst().getArmyRef() );
        assertEquals( ArmyRef.parse( "IV/77" ), variants.getLast().getArmyRef() );

        variants = ArmyList.getByTerrain( "dry" );
        // variants.forEach( av-> System.out.format( "%s, %s, %s%n",
        //    av.armyRef, av.variantName, av.terrain ));
        count += variants.size();
        assertEquals( 41, variants.size() );
        assertEquals( ArmyRef.parse( "I/6a" ), variants.getFirst().getArmyRef() );
        assertEquals( ArmyRef.parse( "IV/71b" ), variants.getLast().getArmyRef() );

        variants = ArmyList.getByTerrain( "tropical" );
        // variants.forEach( av-> System.out.format( "%s, %s, %s%n",
        //    av.armyRef, av.variantName, av.terrain ));
        count += variants.size();
        assertEquals( 33, variants.size() );
        assertEquals( ArmyRef.parse( "I/23a" ), variants.getFirst().getArmyRef() );
        assertEquals( ArmyRef.parse( "IV/72" ), variants.getLast().getArmyRef() );

        variants = ArmyList.getByTerrain( "littoral" );
        // variants.forEach( av-> System.out.format( "%s, %s, %s%n",
        //    av.armyRef, av.variantName, av.terrain ));
        count += variants.size();
        assertEquals( 90, variants.size() );
        assertEquals( ArmyRef.parse( "I/2a" ), variants.getFirst().getArmyRef() );
        assertEquals( ArmyRef.parse( "IV/61" ), variants.getLast().getArmyRef() );

        System.out.println( "ArmyList.getByTerrain found " + count + " variants.");
    }
}