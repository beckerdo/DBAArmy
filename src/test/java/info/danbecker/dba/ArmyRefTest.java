package info.danbecker.dba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArmyRefTest {
    @BeforeEach
    void setup() {
    }

    @Test
    public void testToString() {
        // Army I/1 group with versions a,b,c
        ArmyRef ar = new ArmyRef(1, 1, 0);
        assertEquals(1, ar.section());
        assertEquals(1, ar.number());
        assertEquals(0, ar.version());
        assertEquals("I/1", ar.toString());

        ArmyRef ara = new ArmyRef(1, 1, 1);
        assertEquals(1, ara.section());
        assertEquals(1, ara.number());
        assertEquals(1, ara.version());
        assertEquals("I/1a", ara.toString());

        ArmyRef arb = new ArmyRef(1, 1, 2);
        assertEquals(1, arb.section());
        assertEquals(1, arb.number());
        assertEquals(2, arb.version());
        assertEquals("I/1b", arb.toString());

        ArmyRef arc = new ArmyRef(1, 1, 3);
        assertEquals(1, arc.section());
        assertEquals(1, arc.number());
        assertEquals(3, arc.version());
        assertEquals("I/1c", arc.toString());

        // Army I/3 should NOT return versions
        ara = new ArmyRef(1, 3, 0);
        assertEquals(1, ara.section());
        assertEquals(3, ara.number());
        assertEquals(0, ara.version());
        assertEquals("I/3", ara.toString());

        ara = new ArmyRef(1, 3, 1);
        assertEquals(1, ara.section());
        assertEquals(3, ara.number());
        assertEquals(1, ara.version());
        assertEquals("I/3a", ara.toString());
    }

    @Test
    public void testStringParse()  {
        // Army I/1 group with versions a,b,c
        ArmyRef ar = ArmyRef.parse("I/1");
        assertEquals(1, ar.section());
        assertEquals(1, ar.number());
        assertEquals(0, ar.version());
        ArmyRef ara = ArmyRef.parse("I/1a");
        assertEquals(1, ara.section());
        assertEquals(1, ara.number());
        assertEquals(1, ara.version());
        ArmyRef arb = ArmyRef.parse("I/1b");
        assertEquals(1, arb.section());
        assertEquals(1, arb.number());
        assertEquals(2, arb.version());
        ArmyRef arc = ArmyRef.parse("I/1c");
        assertEquals(1, arc.section());
        assertEquals(1, arc.number());
        assertEquals(3, arc.version());

        // Army I/3 should NOT return versions
        ar = ArmyRef.parse("I/3");
        assertEquals(1, ar.section());
        assertEquals(3, ar.number());
        assertEquals(0, ar.version());
        ar = ArmyRef.parse("I/3a");
        assertEquals(1, ar.section());
        assertEquals(3, ar.number());
        assertEquals(1, ar.version());

        ar = ArmyRef.parse("II/2");
        assertEquals(2, ar.section());
        ar = ArmyRef.parse("III/3");
        assertEquals(3, ar.section());
        ar = ArmyRef.parse("IV/4");
        assertEquals(4, ar.section());

        assertEquals( 0, ArmyRef.getVersionNumber( null ));
        assertEquals( 0, ArmyRef.getVersionNumber( "" ));
        assertEquals( 0, ArmyRef.getVersionNumber( " " ));
        assertEquals( 1, ArmyRef.getVersionNumber( "a" ));
        assertEquals( 2, ArmyRef.getVersionNumber( "B" ));
        assertEquals( 3, ArmyRef.getVersionNumber( "ceeee" ));

        // Does it grab the ArmyRef from a large army variant name?
        assertEquals( ArmyRef.parse("II/8a" ),ArmyRef.parse("II/8a Bruttian or Lucanian Armies 420-203 BC" ));
        assertEquals( ArmyRef.parse("I/3" ),ArmyRef.parse("I/3 Nubian Army 3000 BC - 1480 BC" ));

        // Some illegal tests, bad section, army, group
        IllegalStateException ise = assertThrows(
                IllegalStateException.class,
                () -> ArmyRef.parse("XYZ/2")
        );
        assertTrue(ise.getMessage().contains("parse of "));
    }

    @Test
    public void testListParse() throws IllegalStateException {
        // List<ArmyRef> ar = ArmyRef.parseList("I/17,II/28e,21c,21f,21h,IV/41i,41j");
        ArmyRef expectedAr = ArmyRef.parse( "I/1" );
        List<ArmyRef> lar = ArmyRef.parseList("I/1");
        assertEquals(1, lar.size());
        assertEquals( expectedAr, lar.getFirst());
        lar = ArmyRef.parseList("I/1");
        assertEquals(1, lar.size());
        assertEquals(  expectedAr, lar.getFirst());
        lar = ArmyRef.parseList("   I/1   ,   ");
        assertEquals(1, lar.size());
        assertEquals(  expectedAr, lar.getFirst());

        lar = ArmyRef.parseList("I/17,II/28e");
        assertEquals(2, lar.size());
        assertEquals(  ArmyRef.parse("I/17" ), lar.getFirst());
        assertEquals(  ArmyRef.parse("II/28e" ), lar.getLast());
        lar = ArmyRef.parseList("I/17 II/28e");
        assertEquals(2, lar.size());
        assertEquals(  ArmyRef.parse("I/17" ), lar.getFirst());
        assertEquals(  ArmyRef.parse("II/28e" ), lar.getLast());
        lar = ArmyRef.parseList("I/17   ,   II/28e   ,");
        assertEquals(2, lar.size());
        assertEquals(  ArmyRef.parse("I/17" ), lar.getFirst());
        assertEquals(  ArmyRef.parse("II/28e" ), lar.getLast());

        lar = ArmyRef.parseList("I/17,27 , 37a");
        assertEquals(3, lar.size());
        assertEquals(  ArmyRef.parse("I/17" ), lar.get(0));
        assertEquals(  ArmyRef.parse("I/27" ), lar.get(1));
        assertEquals(  ArmyRef.parse("I/37a" ), lar.get(2));

        String expectedArl = "I/17,II/27,28,III/37,38c,IV/47a,48d,I/18";
        lar = ArmyRef.parseList( expectedArl );
        assertEquals(  ArmyRef.parse("I/17" ), lar.get(0));
        assertEquals(  ArmyRef.parse("II/27" ), lar.get(1));
        assertEquals(  ArmyRef.parse("II/28" ), lar.get(2));
        assertEquals(  ArmyRef.parse("III/37" ), lar.get(3));
        assertEquals(  ArmyRef.parse("III/38c" ), lar.get(4));
        assertEquals(  ArmyRef.parse("IV/47a" ), lar.get(5));
        assertEquals(  ArmyRef.parse("IV/48d" ), lar.get(6));
        assertEquals(  ArmyRef.parse("I/18" ), lar.get(7));

        // Test String compare
        assertEquals( expectedArl, ArmyRef.toStringCompact( lar ));

        // Test some empty lists.
        assertIterableEquals( List.of(), ArmyRef.parseList( null ));
        assertIterableEquals( List.of(), ArmyRef.parseList( "" ));
        assertIterableEquals( List.of(), ArmyRef.parseList( " " ));

        // Enemies/Allies quirky ones with parens ands ors.
        assertIterableEquals( List.of( ArmyRef.parse("I/6b"), ArmyRef.parse("I/25a"), ArmyRef.parse("I/39a"), ArmyRef.parse("I/41a")),
                ArmyRef.parseList( "I/6b or 25a or (39a and/or 41a)" )); // I/37a
        assertIterableEquals( List.of(ArmyRef.parse("I/39b"), ArmyRef.parse("I/41a"), ArmyRef.parse("I/45"), ArmyRef.parse("I/51")),
                ArmyRef.parseList( "I/(39b and/or 41a) or (45 or 51)" )); //I/37b
    }

    @Test
    public void testCompare() {
        String[] armies = { "III/17", "IV/1b", "IV/1a", "II/21", "I/10b", "I/10", "I/10a", "I/1" };
        List<String> sorted = Arrays.stream( armies )
                .sorted()
                .collect(Collectors.toList());
        assertEquals( Arrays.asList( "I/1", "I/10", "I/10a", "I/10b", "II/21", "III/17", "IV/1a", "IV/1b"  ), sorted);
   }

    @Test
    public void testVersionMax() throws IOException {
        // Tests that hard coded version counts are the same as the config files.
        // Load something
        ArmyList.main( ArmyListTest.LOAD_ARGS );

        for ( int section = ArmyRef.MIN_SECTION; section <= ArmyRef.MAX_SECTION; section++) {
            for ( int number = ArmyRef.MIN_NUMBER; number <= ArmyRef.maxNumber( section ); number++) {
                int hardVarCount = ArmyRef.maxVersion( section, number );
                ArmyRef armyRef = new ArmyRef(section,number,0);
                Army army = ArmyList.Armies.get( armyRef );
                assertNotNull( army, String.format( "ArmyRef %s not in ArmyList", armyRef ) );
                int configVarCount = army.header.variantCount;
                // System.out.format( "%s ref max=%d, csv max=%d%n", armyRef, hardVarCount, configVarCount );
                assertEquals( configVarCount, hardVarCount,
                   String.format( "%s ref max=%d, csv max=%d%n", armyRef, hardVarCount, configVarCount ));
            } // number
        } // section
    }
}