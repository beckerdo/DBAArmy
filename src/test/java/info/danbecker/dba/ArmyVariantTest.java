package info.danbecker.dba;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.danbecker.dba.ArmyHeader.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class ArmyVariantTest {
    @Test
    public void testBasics() {
        // public ArmyVariant(ArmyRef armyRef, String variantName, YearRange years, TroopDef troopDef,
        //    String terrain, int aggression, String enemies, String allies)
        // 11/8a Bruttian or Lucanian Armies 420-203 BC
        // 1 x General (Cv), 9 x warriors (3Ax or 4Ax), 2 x peasants (7Hd) or javelinmen (Ps).
        // Terrain Type: Hilly
        // Aggression: 1
        // Enemies: 1/36a, 1/36d, 1/55d, 1/57b, 1/59, 11/5g, 11/8a, 11/8b, 11/8¢, 11/10, 11/13, 11/33.
        // Allies: 11/10 or 11/32a.
        ArmyVariant test = new ArmyVariant(
                ArmyRef.parse("II/8a"),
                "II/8a Bruttian or Lucanian Armies 420-203 BC",
                // new TroopDef("1 x General (Cv), 9 x warriors (3Ax or 4Ax), 2 x peasants (7Hd) or javelinmen (Ps)"),
                "Cv(Gen),9x3Ax|4Ax,2x7Hd|Ps",
                "Hilly", 1,
                "I/36a, I/36d",
                "II/10, II/32a");

        assertEquals("II/8a", test.armyRef.toString());
        assertEquals("II/8a Bruttian or Lucanian Armies 420-203 BC", test.variantName );
        assertEquals("420BC-203BC", test.years.toString() );
        //assertEquals("420-203BC", test.years );
        assertEquals("Hilly", test.terrain );
        assertEquals(1, test.aggression );
        assertIterableEquals(List.of( ArmyRef.parse("I/36a"), ArmyRef.parse("I/36d")), test.enemies );
        assertIterableEquals(List.of( ArmyRef.parse("II/10"), ArmyRef.parse("II/32a")), test.allies );
    }
}