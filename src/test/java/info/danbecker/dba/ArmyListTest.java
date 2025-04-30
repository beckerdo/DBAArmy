package info.danbecker.dba;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static info.danbecker.dba.ArmyList.*;
import static org.junit.jupiter.api.Assertions.*;

public class ArmyListTest {
    public final static String[] LOAD_ARGS = {"-inPath", PATH_DEFAULT, "-nameContains", "Army", "-nameEndsWith", ".csv"};
    public final static int ALL_VARIANT_COUNT = 605; // hard coded but makes testing easier

    @Test
    public void testBasics() {
        Options opts = new Options();
        List<String> inputFiles = new ArrayList<>();
        inputFiles.add("README.txt");
        ArmyList.processCommandOptions(LOAD_ARGS, opts, inputFiles);
        assertEquals(2, inputFiles.size());
        assertTrue(inputFiles.getFirst().contains(ARMY_HEADER_DEFAULT));
        assertTrue(inputFiles.getLast().contains(ARMY_DEFAULT));


        assertEquals("is", isIsNot(true));
        assertEquals("is not", isIsNot(false));
    }

    @Test
    public void testArmyListByYear() throws IOException {
        // Load something
        ArmyList.main(LOAD_ARGS);

        List<Army> armies = ArmyList.getByYear(YearType.parse("54BC"));
        assertEquals(33, armies.size());
        assertEquals(ArmyRef.parse("I/7"), armies.getFirst().getArmyRef());
        assertEquals(ArmyRef.parse("II/54"), armies.getLast().getArmyRef());

        armies = ArmyList.getByYear(YearType.parse("1066AD"));
        assertEquals(57, armies.size());
        assertEquals(ArmyRef.parse("II/42"), armies.getFirst().getArmyRef());
        assertEquals(ArmyRef.parse("III/80"), armies.getLast().getArmyRef());
    }

    @Test
    public void testArmyListByTerrain() throws IOException {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> ArmyList.getByTerrain(""));
        assertTrue(e.getMessage().contains("invalid terrain"));

        // Load something
        ArmyList.main(LOAD_ARGS);

        int count = 0;
        List<ArmyVariant> variants = ArmyList.getByTerrain("arable");
        // variants.forEach( av-> System.out.format( "%s, %s, %s%n",
        //   av.armyRef, av.variantName, av.terrain ));
        count += variants.size();
        assertEquals(269, variants.size());
        assertEquals(ArmyRef.parse("I/1a"), variants.getFirst().getArmyRef());
        assertEquals(ArmyRef.parse("IV/85b"), variants.getLast().getArmyRef());

        variants = ArmyList.getByTerrain("forest");
        // variants.forEach( av-> System.out.format( "%s, %s, %s%n",
        //   av.armyRef, av.variantName, av.terrain ));
        count += variants.size();
        assertEquals(27, variants.size());
        assertEquals(ArmyRef.parse("II/47a"), variants.getFirst().getArmyRef());
        assertEquals(ArmyRef.parse("IV/66"), variants.getLast().getArmyRef());

        variants = ArmyList.getByTerrain("hilly");
        // variants.forEach( av-> System.out.format( "%s, %s, %s%n",
        //    av.armyRef, av.variantName, av.terrain ));
        count += variants.size();
        assertEquals(97, variants.size());
        assertEquals(ArmyRef.parse("I/4a"), variants.getFirst().getArmyRef());
        assertEquals(ArmyRef.parse("IV/84b"), variants.getLast().getArmyRef());

        variants = ArmyList.getByTerrain("steppe");
        // variants.forEach( av-> System.out.format( "%s, %s, %s%n",
        //    av.armyRef, av.variantName, av.terrain ));
        count += variants.size();
        assertEquals(55, variants.size());
        assertEquals(ArmyRef.parse("I/3"), variants.getFirst().getArmyRef());
        assertEquals(ArmyRef.parse("IV/77"), variants.getLast().getArmyRef());

        variants = ArmyList.getByTerrain("dry");
        // variants.forEach( av-> System.out.format( "%s, %s, %s%n",
        //    av.armyRef, av.variantName, av.terrain ));
        count += variants.size();
        assertEquals(41, variants.size());
        assertEquals(ArmyRef.parse("I/6a"), variants.getFirst().getArmyRef());
        assertEquals(ArmyRef.parse("IV/71b"), variants.getLast().getArmyRef());

        variants = ArmyList.getByTerrain("tropical");
        // variants.forEach( av-> System.out.format( "%s, %s, %s%n",
        //    av.armyRef, av.variantName, av.terrain ));
        count += variants.size();
        assertEquals(33, variants.size());
        assertEquals(ArmyRef.parse("I/23a"), variants.getFirst().getArmyRef());
        assertEquals(ArmyRef.parse("IV/72"), variants.getLast().getArmyRef());

        variants = ArmyList.getByTerrain("littoral");
        // variants.forEach( av-> System.out.format( "%s, %s, %s%n",
        //    av.armyRef, av.variantName, av.terrain ));
        count += variants.size();
        assertEquals(91, variants.size());
        assertEquals(ArmyRef.parse("I/2a"), variants.getFirst().getArmyRef());
        assertEquals(ArmyRef.parse("IV/61"), variants.getLast().getArmyRef());

        // Some variants appear with multiple terrains ("Arrable/Hilly/Forest")
        assertEquals(613, count);
        // assertEquals(ALL_VARIANT_COUNT, count);
    }

    @Test
    public void testArmyListByAggression() throws IOException {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> ArmyList.getByAggression(-1));
        assertTrue(e.getMessage().contains("range (0..6)"));

        // Load something
        ArmyList.main(LOAD_ARGS);

        int count = 0;
        List<ArmyVariant> variants = ArmyList.getByAggression(0);
        // variants.forEach( av-> System.out.format( "%s, %s, Aggr: %d%n",
        //   av.armyRef, av.variantName, av.aggression ));
        count += variants.size();
        assertEquals(80, variants.size());
        assertEquals(ArmyRef.parse("I/10"), variants.getFirst().getArmyRef());
        assertEquals(ArmyRef.parse("IV/84b"), variants.getLast().getArmyRef());

        variants = ArmyList.getByAggression(1);
        // variants.forEach( av-> System.out.format( "%s, %s, Aggr: %d%n",
        //   av.armyRef, av.variantName, av.aggression ));
        count += variants.size();
        assertEquals(194, variants.size());
        assertEquals(274, count);
        assertEquals(ArmyRef.parse("I/2a"), variants.getFirst().getArmyRef());
        assertEquals(ArmyRef.parse("IV/85b"), variants.getLast().getArmyRef());

        variants = ArmyList.getByAggression(2);
        count += variants.size();
        variants = ArmyList.getByAggression(3);
        count += variants.size();
        variants = ArmyList.getByAggression(4);
        count += variants.size();
        variants = ArmyList.getByAggression(5);
        count += variants.size();
        variants = ArmyList.getByAggression(6);
        count += variants.size();
        assertEquals(ALL_VARIANT_COUNT, count);
    }

    @Test
    public void testArmyListDoubleCheck() throws IOException {
        // Load something
        ArmyList.main(LOAD_ARGS);

        List<ArmyVariant> variants = Armies.values().stream()
                .flatMap(army -> army.getVariants().stream())
                // .filter( av->terrainCase.equals( av.terrain ))
                .sorted()
                .toList();

        // Warning, the following armies have multiple terrain choices.
        //    II/8a Bruttian or Lucanian Armies 420-203 BC: Hilly/Arable
        //    II/70a Army of the Burgundi 250-534 AD: Forest/Arable
        //    II/70b Army of the Limigantes 334-359 AD: Steppe/Arable
        //    II/72b Alamanni Army 250-506 AD: Forest/Arable
        //    II/72c Suevi Army 250-584 AD: Forest/Arable
        //    II/72d Other Early Frankish, Rugian or Turcilingi Armies 250-493 AD: Forest/Arable
        //    II/73 Old Saxon Army 250-804 AD, Frisian Army 250-690 AD, Bavarian Army 250-788 AD, Thuringian Army 250-531 AD & 555 AD and Early Anglo-Saxon Army 428-617AD: Arable/Littoral/Forest
        for (ArmyVariant variant : variants) {
            switch ( variant.terrain ) {
                case null ->
                System.out.format("%s %s: %s%n   %d, E:%s, A:%s%n",
                   variant.armyRef, variant.variantName, "null terrain",
                   variant.aggression, variant.enemies.toString(), variant.allies.toString());
                default -> {
                    switch (variant.terrain.size()) {
                        case 0 -> System.out.format("%s %s: %s%n   %d, E:%s, A:%s%n",
                            variant.armyRef, variant.variantName, "empty terrain list",
                            variant.aggression, variant.enemies, variant.allies.toString());
                        case 1 -> {}
                        default -> System.out.format("%s %s: %s%n   %d, E:%s, A:%s%n",
                            variant.armyRef, variant.variantName, variant.terrain,
                            variant.aggression, variant.enemies.toString(), variant.allies.toString());
                    }
                }
            }

        }


        // Print variants to double check values
        for (ArmyVariant variant : variants) {
            System.out.format("%s %s: %s%n   %s %d, E:%s, A:%s%n",
                    variant.armyRef, variant.variantName, variant.troopDef.toString(),
                    variant.terrain, variant.aggression, variant.enemies.toString(), variant.allies.toString());
        }
    }

    @Test
    public void testArmyListByTroopDef() throws IOException {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> ArmyList.getByElementType(""));
        assertTrue(e.getMessage().contains("Could not find ElementType"));

        // Load something
        ArmyList.main(LOAD_ARGS);

        int count = 0;
        List<ArmyVariant> variants = ArmyList.getByElementType( "WWg");
        // variants.forEach( av-> System.out.format( "%s, %s, Ele: %s%n",
        //   av.armyRef, av.variantName, av.troopDef.getUnitList() ));
        count += variants.size();
        assertEquals(12, variants.size());
        assertEquals(ArmyRef.parse("I/60b"), variants.getFirst().getArmyRef());
        assertEquals(ArmyRef.parse("IV/80"), variants.getLast().getArmyRef());

        variants = ArmyList.getByElementType( "Gen");
        // variants.forEach( av-> System.out.format( "%s, %s, Ele: %s%n",
        //  av.armyRef, av.variantName, av.troopDef.getUnitList() ));

        // This should be all of them
        count += variants.size();
        assertEquals(ALL_VARIANT_COUNT, variants.size());
        assertEquals(ArmyRef.parse("I/1a"), variants.getFirst().getArmyRef());
        assertEquals(ArmyRef.parse("IV/85b"), variants.getLast().getArmyRef());
    }
}