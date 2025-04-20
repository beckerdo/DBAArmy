package info.danbecker.dba;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArmyTest {
    @Test
    public void testBasics() {
        // II/73 OLD SAXON, FRISIAN, BAVARIAN, THURINGIAN & EARLY-ANGLO-SAXON 250AD - 804AD, 1, [Old Saxon, Frisian, Bavarian, Thuringian, Early-Anglo-Saxon] 5
        ArmyHeader header = new ArmyHeader( ArmyRef.parse("II/73"),
"OLD SAXON, FRISIAN, BAVARIAN, THURINGIAN & EARLY-ANGLO-SAXON 250AD - 804AD", 1 );
        // II/73 OLD SAXON, FRISIAN, BAVARIAN, THURINGIAN & EARLY-ANGLO-SAXON 250AD - 804AD, 1, [Old Saxon, Frisian, Bavarian, Thuringian, Early-Anglo-Saxon] 5

//        11/73 Old Saxon
//        Army 250-804 AD, Frisian Army 250-690 AD, Bavarian Army 250-788 AD, Thuringian Army 250-531 AD & 555 AD
//           and Early Anglo-Saxon Armies 428-617 AD
//        1x General (4Wb),
//        10 x warriors (4Wb),
//        1 x archers (Ps).
//        Terrain Types: Arable for Saxons, Littoral for Frisians and Forest for Bavarians & Thuringians. Aggression: 2.
//        Enemies: 11/64a, 11/68a, 11/68b, 11/72b, 1i/72d, 11/73, 11/78a, 11/81a, 11/81b, 11/81c, 11/814,
//           11/83a, 11I/1a, 11/2, 111/3, 11/5, 111/13b, 11/19a, 111/21a, 111/21b, 111/28.
//        Allies for Bavarians only: I11/1a or I1I/1b or 1ll/13a or I1I/13b.

        //II/73, Old Saxon Army 250-804 AD, Frisian Army 250-690 AD, Bavarian Army 250-788 AD, Thuringian Army 250-531 AD & 555 AD and Early Anglo-Saxon Army 428-617,
        //   Agg:2, Top:Arable/Littoral/Forest,
        //   E:II/64a,68a,68b,72b,72d,73,78a,81a,81b,81c,81d,83a,III/1a,2,3,5a,13b,19a,21a,21b,28, A:III/1a,1b,13a,13b (Bavarians only)
        //   4Wb(Gen),10x4Wb,1xPs
        ArmyVariant variant = new ArmyVariant(
                ArmyRef.parse("II/73"), "Old Saxon Army 250-804 AD, Frisian Army 250-690 AD, Bavarian Army 250-788 AD, Thuringian Army 250-531 AD & 555 AD and Early Anglo-Saxon Army 428-617",
                // new TroopDef("1 x General (Cv), 9 x warriors (3Ax or 4Ax), 2 x peasants (7Hd) or javelinmen (Ps)"),
                "4Wb(Gen),10x4Wb,1xPs",
                "Littoral", 2,
                "II/64a,68a,68b,72b,72d",
                "III/1a,1b,13a,13b");

        Army army = new Army( header, List.of( variant) );
        assertEquals( "II/73", army.getArmyRef().toString() );
        assertEquals( 1, army.getVariants().size() );
    }
}