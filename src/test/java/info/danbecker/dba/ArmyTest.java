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

        // II/73, Old Saxon Army 250-804 AD, Frisian Army 250-690 AD, Bavarian Army 250-788 AD, Thuringian Army 250-531 AD & 555 AD
        //    and Early Anglo-Saxon Army 428-617,
        // Def: 1x General (4Wb), 10 x warriors (4Wb), 1 x archers (Ps).
        // Agg:2, Top:Arable/Littoral/Forest,
        // E:II/64a,68a,68b,72b,72d,73,78a,81a,81b,81c,81d,83a,III/1a,2,3,5a,13b,19a,21a,21b,28,
        // A:III/1a,1b,13a,13b (Bavarians only)
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