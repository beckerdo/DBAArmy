package info.danbecker.dba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static info.danbecker.dba.TroopDef.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TroopDefTest {
    @BeforeEach
    void setup() {
    }

    @Test
    public void testOne() throws IllegalArgumentException {
        assertEquals("3xSp", new TroopDef("3xSp").toString());
    }

    @Test
    public void testBasicParse() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new TroopDef(null));
        assertTrue(e.getMessage().contains("null"));
        e = assertThrows(IllegalArgumentException.class,
                () -> new TroopDef(""));
        assertTrue(e.getMessage().contains("empty"));
        e = assertThrows(IllegalArgumentException.class,
                () -> new TroopDef("\n\r \t"));
        assertTrue(e.getMessage().contains("blank"));
        e = assertThrows(IllegalArgumentException.class,
                () -> new TroopDef("(Ps")); // throws error
        assertTrue(e.getMessage().contains("Parse error"));

        TroopDef troopDef = new TroopDef("     Ps ");
        assertEquals("     Ps ", troopDef.getString());
        assertEquals("Ps", troopDef.toString());
    }

    @Test
    public void testTrickyParse() throws IllegalArgumentException {
        // Test various rules and tokens.
        // Codes and spacings
        TroopDef troopDef = new TroopDef("     Cv ");
        assertEquals("     Cv ", troopDef.getString());
        assertEquals("Cv", troopDef.toString());
        assertEquals("3Pk", new TroopDef("3Pk").toString());

        // Multiple
        assertEquals("4x3Pk", new TroopDef("4x3Pk").toString());
        // syntactically legal without constraints
        assertEquals("2x2xSp", new TroopDef("2x2xSp").toString());

        // Group
        assertEquals("(Bd)", new TroopDef("(Bd)").toString());
        assertEquals("2x(Bd)", new TroopDef("2x(Bd)").toString());
        // Tricky Either
        assertEquals("3/4Bw", new TroopDef("3/4Bw").toString());

        // Compounds
        assertEquals("Bw/Cb", new TroopDef("Bw/Cb").toString());
        assertEquals("Kn//Cb", new TroopDef("Kn//Cb").toString());
        assertEquals("Gen+Bd", new TroopDef("Gen+Bd").toString());
        assertEquals("Gen+Bd+Sp", new TroopDef("Gen+Bd+Sp").toString());
        assertEquals("AxorHd", new TroopDef("AxorHd").toString());
        assertEquals("3Axor4Ax", new TroopDef("3Axor4Ax").toString());
        assertEquals("AxorBdorSp", new TroopDef("AxorBdorSp").toString());
        assertEquals("3Axor4AxorHd", new TroopDef("3Ax or 4Ax or Hd").toString());

        // Lists
        assertEquals("Bw,Cb,Lb", new TroopDef("Bw,Cb,Lb").toString());
        assertEquals("Gen,Cv,(LH+Bw)", new TroopDef("Gen,Cv,(LH+Bw)").toString());

        // All terminals
        String[] terminals = {"CP", "Lit", "CWg", "Gen", "El", "3Kn", "4Kn", "6Kn", "Kn", "HCh",
                "Cv", "6Cv", "LCh", "LH", "LCm", "SCh", "Cm", "Mtd-3Bw", "Mtd-4Bw", "Mtd-3Cb", "Mtd-4Cb", "Mtd-3Lb", "Mtd-4Lb",
                "8Sp", "Sp", "4Pk", "3Pk", "Pk", "4Bd", "3Bd", "6Bd", "Bd", "4Ax", "3Ax", "Ax",
                "4Bw", "3Bw", "8Bw", "Bw", "4Cb", "3Cb", "8Cb", "Cb", "4Lb", "3Lb", "8Lb", "Lb",
                "Ps", "4Wb", "3Wb", "Wb", "7Hd", "5Hd", "Hd", "Art", "WWg"};
        for (String terminal : terminals) {
            assertEquals(terminal.replaceAll("\\s", ""), new TroopDef(terminal).toString());
        }
    }

    @Test
    public void testCompoundParse() {
        // More extensive tests of real word lists with generals, numbers, and options
        String[] armies = {
                "3Bd + Gen,8x4Bw,3xPs", // I/1a
                "3Bd or 3/4Bw,6x3Ax or 3Wb,3xPs,1xPs,1xPs", // I/4a
                "3Pk or Sp,3xPk or Sp,2x4Bw,2x4Bw or 3Ax,4xPs", // I/10
                "Cv,Cv,7xSp/4Ax,3xPs", // I/30c
                "LCh or 3Kn,2x3Kn,2xLH,4xSp or 4Ax,3xPs", // I/50
                "HCh/Sp or Cv,3xHCh/Sp or Sp,5xSp,3xPs", // I/56a
                "Cv,Cv,2xPs,2x4Bd,2xSp,2xSp,2x3Ax or 4Ax", // II/10
                "2xSp or (1 x Cv + 1 x El)",  // evaluate which first? Paren, And, Or?
                "3Kn,1xLH,6x4Pk,2x3Ax/4Ax,2xSp or (1xCv+1xEl),1xPs or Art", // II/16b
                "LCh or 4Wb,1xLCh,2xCv,7x4Wb,1xLCh or Ps", // II/30
                "3Kn,1xLH,3x4Ax,2x4Ax or 4Pk or 5Hd,3xPs,2xPs", // II/50
                "Cv//4Wb,7x4Wb,3x3Bw or 7Hd,1xPs", // II/72b
                "El or 3Kn,2x3Kn,1xEl or 3Kn,2x3Bd,3x3Bw,1xCm or 3Bw,1xPs,1xPs or 7Hd", // III/10
                "Kn//Sp,4xKn//Sp,3xKn//Sp or Cv,1xCv or Sp or LH or Ps or 3Bd,1xPs or Cv,2xKn//Sp or LH", // III/15
                "1x (Cv/6Cv),5x(6Cv/Cv),3xLH or Sp,2xLH or 4Bw or Ps,1x3Kn or Ps or Art",  // III/17 DBA
                "1x Cv or 6Cv,5x6Cv or Cv,3xLH or Sp,2xLH or 4Bw or Ps,1x3Kn or Ps or Art",  // III/17 xlsx
                "Cv,5xCv,3xCv or LH,1xLH or Ps,2xCv or 2x7Hd or (1x7Hd + 1x4Wb)", // III/18
                "Cv or 4Bd or CP,2x3Bw,7x4Bd,1xPs or LCm,1xLH or Cm/Bd",  // III/25a
                "Cv,1xCv,2xCv,1xLH,7xLH,1xLH or 3Ax or Ps", // III/30b
                "3/4Bd or Cv,4x4Bd,2xPs or 3Bw,2x5Hd,2xPs,1xLH or Art or 3Bd", // III/50
                "Lit or 4Bw,1x4Bd,5x3Bw,2xPs,3x5Hd", // IV/10
                "3Kn or 6Kn,1x3Kn or 6Kn,1xCv,3x3Kn,1x3Kn or 3Wb,1xLH,1xSp,2x4Cb,1x3Ax or 7Hd", // IV/30
                "1x4Bd or 4Ax or LH,1x3Kn,3xCv,2xLH or Ps or 7Hd,1xSp or 4Ax,3xPs or 3/4Bw", // IV/50
                "Gen+LCh or Gen+Cv",
        };

        for (String army : armies) {
            assertEquals(army.replaceAll("\\s", ""), new TroopDef(army).toString());
        }
    }

    @Test
    public void testAPIs() {
        // Equals, hashcode, compare
        TroopDef expected = new TroopDef("Cv//4Wb,7x4Wb");
        assertNotEquals(null, expected);
        assertNotEquals("Other object", expected.toString());
        assertNotEquals(new TroopDef("Cv/4Wb,4x4Wb"), expected);
        assertEquals(new TroopDef("Cv//4Wb,7x4Wb"), expected);
        assertEquals(new TroopDef("Cv//4Wb,7x4Wb").hashCode(), expected.hashCode());

        assertEquals(0, expected.compareTo(new TroopDef("Cv//4Wb,7x4Wb")));
        assertEquals(-1, expected.compareTo(null));
        assertTrue(0 < expected.compareTo(new TroopDef("Ax")));
        assertTrue(0 > expected.compareTo(new TroopDef("Wb")));
    }

    @Test
    public void testUnitCount() {
        // Must match order and types
        assertIterableEquals(Arrays.asList("Cv", "4Wb", "4Wb", "3Bw", "7Hd", "Ps"),
                new TroopDef("Cv//4Wb,7x4Wb,3x3Bw or 7Hd,1xPs").getUnitList()); // II/72b
        assertIterableEquals(Arrays.asList("El", "3Kn", "3Kn", "El", "3Kn", "3Bd", "3Bw", "Cm", "3Bw", "Ps", "Ps", "7Hd"),
                new TroopDef("El or 3Kn,2x3Kn,1xEl or 3Kn,2x3Bd,3x3Bw,1xCm or 3Bw,1xPs,1xPs or 7Hd").getUnitList()); // III/10
        List<String> listWithDuplicates = List.of("Cv", "4Wb", "4Wb", "3Bw", "7Hd", "Ps");
        List<String> listWithoutDuplicates = new ArrayList<>(new HashSet<>(listWithDuplicates));
        //System.out.print(listWithoutDuplicates);
        assertThat(listWithoutDuplicates, hasSize(5));
        assertThat(listWithoutDuplicates, containsInAnyOrder("Ps", "7Hd", "3Bw", "4Wb", "Cv"));
        assertIterableEquals(List.of( "Bd", "Bd", "Bd" ),
                new TroopDef("1xBd,(2xBd),4xBd").getUnitList()); // II/72b
    }

    @Test
    public void testUnits() {
        assertTrue(new TroopDef("Ps,Wb").containsAllUnits("Wb,Ps")); // simple type match
        assertFalse(new TroopDef("Ps,Wb").containsAllUnits("Wb,Bd")); // simple type match
        assertIterableEquals(new TroopDef("Ax+Bd+Cv").getUnitList(), new TroopDef("Ax,Bd,Cv").getUnitList());
    }

    @Test
    public void testGetAllExprs() {
        assertEquals( 3, getAllExprs( new TroopDef( "Ax,Bd,Cv").tree ).size());
        assertEquals( 2, getAllExprs( new TroopDef( "Ax//Bd").tree ).size());
        assertEquals( "Ax", toStringTree(getAllExprs( new TroopDef( "Ax//Bd").tree ).getFirst()));
        assertEquals( "Bd", toStringTree(getAllExprs( new TroopDef( "Ax//Bd").tree ).getLast()));
        assertEquals( 2, getAllExprs( new TroopDef( "Ax/Bd").tree ).size());
        assertEquals( "Ax",toStringTree(getAllExprs( new TroopDef( "Ax/Bd").tree ).getFirst()));
        assertEquals( "Bd",toStringTree(getAllExprs( new TroopDef( "Ax/Bd").tree ).getLast()));
        assertEquals( "Ax", toStringTree(getAllExprs( new TroopDef( "(Ax)").tree ).getFirst()));
        assertEquals( "Cv", toStringTree(getAllExprs( new TroopDef( "3xCv").tree ).getFirst()));
        assertEquals( 2, getAllExprs( new TroopDef( "Ax+Bd").tree ).size());
        assertEquals( "Ax", toStringTree(getAllExprs( new TroopDef( "Ax+Bd").tree ).getFirst()));
        assertEquals( "Bd", toStringTree(getAllExprs( new TroopDef( "Ax+Bd").tree ).getLast()));
        assertEquals( 3, getAllExprs( new TroopDef( "Ax+Bd+Cv").tree ).size());
        assertEquals( "Ax", toStringTree( getAllExprs( new TroopDef( "Ax+Bd+Cv").tree ).getFirst()));
        assertEquals( "Bd", toStringTree( getAllExprs( new TroopDef( "Ax+Bd+Cv").tree ).get(1)));
        assertEquals( "Cv", toStringTree( getAllExprs( new TroopDef( "Ax+Bd+Cv").tree ).getLast()));
        assertEquals( 4, getAllExprs( new TroopDef( "Ax+Bd+Cv+Hd").tree ).size());
        assertEquals( "Ax", toStringTree(getAllExprs( new TroopDef( "Ax+Bd+Cv+Hd").tree ).getFirst()));
        assertEquals( "Hd", toStringTree(getAllExprs( new TroopDef( "Ax+Bd+Cv+Hd").tree ).getLast()));
        assertEquals( 2, getAllExprs( new TroopDef( "Ax or Bd").tree ).size());
        assertEquals( "Ax", toStringTree(getAllExprs( new TroopDef( "Ax or Bd").tree ).getFirst()));
        assertEquals( "Bd", toStringTree(getAllExprs( new TroopDef( "Ax or Bd").tree ).getLast()));
        assertEquals( 3, getAllExprs( new TroopDef( "Ax or Bd or Cv").tree ).size());
        assertEquals( "Ax", toStringTree(getAllExprs( new TroopDef( "Ax or Bd or Cv").tree ).getFirst()));
        assertEquals( "Bd", toStringTree(getAllExprs( new TroopDef( "Ax or Bd or Cv").tree ).get(1)));
        assertEquals( "Cv", toStringTree(getAllExprs( new TroopDef( "Ax or Bd or Cv").tree ).getLast()));
        assertEquals( 4, getAllExprs( new TroopDef( "Ax or Bd or Cv or Hd").tree ).size());
        assertEquals( "Ax", toStringTree(getAllExprs( new TroopDef( "Ax or Bd or Cv or Hd").tree ).getFirst()));
        assertEquals( "Hd", toStringTree(getAllExprs( new TroopDef( "Ax or Bd or Cv or Hd").tree ).getLast()));

        // Try a few and or combos
        // Note + has precedence, pushed lower in the parse tree
        assertEquals( 3, getAllExprs( new TroopDef( "Ax or Bd + Cv or Hd").tree ).size()); // S
        assertEquals( "Ax",toStringTree(getAllExprs( new TroopDef( "Ax or Bd + Cv or Hd").tree ).getFirst()));
        assertEquals( "Bd+Cv",toStringTree(getAllExprs( new TroopDef( "Ax or Bd + Cv or Hd").tree ).get(1)));
        assertEquals( "Hd",toStringTree(getAllExprs( new TroopDef( "Ax or Bd + Cv or Hd").tree ).getLast()));
        assertEquals( 2, getAllExprs( new TroopDef( "Ax + Bd or Cv + Hd").tree ).size()); // S
        assertEquals( "Ax+Bd",toStringTree(getAllExprs( new TroopDef( "Ax + Bd or Cv + Hd").tree ).getFirst()));
        assertEquals( "Cv+Hd",toStringTree(getAllExprs( new TroopDef( "Ax + Bd or Cv + Hd").tree ).getLast()));
    }

    @Test
    public void testMatchType() {
        // Ultimately we want to see if given an army with options
        // Gen+Cv,5xCv,3xCv or LH,1xLH or Ps,2xCv or 2x7Hd or (1x7Hd + 1x4Wb)", // III/18
        // the following pass
        // Gen+Cv,5xCv,3xLH,Ps,1x4Wb+1x7Hd       Gen+Cv,5xCv,3xCv,LH,2xCv
        // the following do not pass
        // Cv,4xCv,5xLH,Wb,3x7Hd                 Gen+Cv,5xKn,3xCv,Bw,2xCv
        assertTrue(new TroopDef("Ps").matches("Ps")); // simple type match
        assertFalse(new TroopDef("Ps").matches("Wb")); // simple type mismatch
    }

    @Test
    public void testMatchExprs() {
        assertTrue( new TroopDef( "Ps,Ps").matches( "Ps,Ps")); // exprs match
        assertFalse( new TroopDef( "Ps,Ps").matches( "Ps,Ps,Ps")); // exprs counts mismatch
        assertFalse( new TroopDef( "Ps,Ps").matches( "Ps,Hd")); // exprs mismatch
        assertTrue(new TroopDef("Sp,Wb,Bd").matches("Bd,Wb,Sp")); // exprs mis order match
    }

    @Test
    public void testMatchGroup() {
        assertTrue(new TroopDef("(Ps)").matches("Ps")); // group match
        assertTrue(new TroopDef("Ps").matches("(Ps)")); // group match
        assertTrue(new TroopDef("(Ps)").matches("(Ps)")); // group match
        assertTrue(new TroopDef("((Ps))").matches("((Ps))")); // group match
        assertTrue(new TroopDef("((Ps))").matches("(Ps)")); // group match
        assertTrue(new TroopDef("(Ps)").matches("((Ps))")); // group match
    }

    @Test
    public void testMatchMountedInfantry() {
        assertTrue(new TroopDef("Mtd-3Bw").matches("Mtd-3Bw")); // number match
        assertTrue(new TroopDef("Mtd-4Bw").matches("Mtd-4Bw")); // number match
        assertTrue(new TroopDef("Mtd-4Lb").matches("Mtd-4Lb")); // number match
        assertTrue(new TroopDef("Mtd-4Cb").matches("Mtd-4Cb")); // number match

        assertTrue(new TroopDef("2xMtd-3Bw").matches("2xMtd-3Bw")); // number match
        assertTrue(new TroopDef("Mtd-3Bw/Mtd-4Bw").matches("Mtd-3Bw")); // number match
        assertTrue(new TroopDef("Mtd-3Bw/Mtd-4Bw").matches("Mtd-4Bw")); // number match
        assertTrue(new TroopDef("(Mtd-3Bw)").matches("Mtd-3Bw")); // number match
        assertTrue(new TroopDef("Mtd-3Bw or 4Bw").matches("Mtd-3Bw")); // number match
        assertFalse(new TroopDef("Mtd-3Bw").matches("Mtd-8Bw")); // number match
    }

    @Test
    public void testMatchMultiple() {
        assertTrue(new TroopDef("1xPs").matches("Ps")); // number match
        assertFalse(new TroopDef("1xWb").matches("Ps")); // number match
        assertTrue(new TroopDef("Ps").matches("1xPs")); // number 1x match
        assertFalse(new TroopDef("Ps").matches("1xWb")); // number 1x match
        assertTrue(new TroopDef("2xPs").matches("2xPs")); // number match
        assertTrue(new TroopDef("2xPs").matches("2x(Ps)")); // number match
        assertTrue(new TroopDef("2x(Ps)").matches("2xPs")); // number match
        assertFalse(new TroopDef("1xPs").matches("2xPs")); // number mismatch
        assertFalse(new TroopDef("1xPs").matches("1xWb")); // type mismatch
    }

    @Test
    public void testMatchEitherType() {
        assertTrue(new TroopDef("3/4Wb").matches("3Wb/4Wb")); // either type match
        assertTrue(new TroopDef("3/4Wb").matches("4Wb/3Wb")); // either type match
        assertTrue(new TroopDef("3Wb/4Wb").matches("3/4Wb")); // either type match
        assertFalse(new TroopDef("3/4Wb").matches("Wb/4Wb")); // either type mis match
        assertTrue(new TroopDef("3/4Wb").matches("3Wb or 4Wb")); // either type mis match
        assertTrue(new TroopDef("3Wb or 4Wb").matches("3/4Wb")); // or either unit match
        assertTrue(new TroopDef("3/4Wb").matches("3Wb or 4Wb")); // eitther unit or match
        assertTrue(new TroopDef("3Wb or 4Wb").matches("3Wb/4Wb")); // or either match
        assertTrue(new TroopDef("3Wb/4Wb").matches("3Wb or 4Wb")); // either or match
        assertTrue(new TroopDef("3/4Wb").matches("3Wb/4Wb")); // either unit either match
        assertTrue(new TroopDef("3Wb/4Wb").matches("3/4Wb")); // either either unit match
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new TroopDef("3/500Ax"));
        assertTrue(e.getMessage().contains("Parse error") && e.getMessage().contains("expecting"));
    }

    @Test
    public void testMatchEither() {
        assertTrue(new TroopDef("Cv/Wb").matches("Cv/Wb")); // dismount match
        assertFalse(new TroopDef("Cv/Wb").matches("Cv/Bd")); // dismount match
        assertTrue(new TroopDef("Bd/Cv").matches("Cv/Bd")); // dismount match
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new TroopDef("Cv/Wb/Sp"));
        assertTrue(e.getMessage().contains("cardinality 2"));
        assertTrue(new TroopDef("Cv/Wb").matches("Cv")); // either match
        assertTrue(new TroopDef("Cv/Wb").matches("Wb")); // either match
        assertFalse(new TroopDef("Cv/Wb").matches("Hd")); // either mismatch

        assertTrue(new TroopDef("Cv/Wb").isInstance("Cv")); // either match
        assertTrue(new TroopDef("Cv/Wb").isInstance("Wb")); // either match
        assertFalse(new TroopDef("Cv/Wb").isInstance("Hd")); // either mismatch

        // 3Bd or 3/4Bw(Gen)
        assertTrue(new TroopDef("3/4Bw").isInstance("3Bw")); // either match
        assertTrue(new TroopDef("3/4Bw").isInstance("4Bw")); // either match

    }

    @Test
    public void testMatchDismount() {
        assertTrue(new TroopDef("Cv//Wb").matches("Cv//Wb")); // dismount match
        assertFalse(new TroopDef("Cv//Wb").matches("Wb//Bd")); // dismount match
        assertFalse(new TroopDef("Cv//Wb").matches("Wb//Cv")); // dismount match
        assertFalse(new TroopDef("Cv//Wb").matches("Cv")); // dismount match
        assertFalse(new TroopDef("Cv//Wb").matches("Wb")); // dismount match
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new TroopDef("Cv//Wb//Sp"));
        assertTrue(e.getMessage().contains("cardinality 2"));
    }

    @Test
    public void testMatchAnd() {
        assertTrue(new TroopDef("Cv+Bd").matches("Cv+Bd")); // and match
        assertTrue(new TroopDef("Cv+Bd").matches("Bd+Cv")); // and match
        assertFalse(new TroopDef("Cv+Bd").matches("Cv")); // and mismatch
        assertFalse(new TroopDef("Cv+Bd").matches("Bd")); // and mismatch
        assertFalse(new TroopDef("Cv+Bd").matches("Cv+Sp")); // and mismatch
        assertFalse(new TroopDef("Cv+Bd").matches("Kn+Bd")); // and mismatch
        // Every 3 way permutation
        assertTrue(new TroopDef("Ax+Bd+Cv").matches("Ax+Bd+Cv")); // and match
        assertTrue(new TroopDef("Ax+Bd+Cv").matches("Ax+Cv+Bd")); // and match
        assertTrue(new TroopDef("Ax+Bd+Cv").matches("Bd+Ax+Cv")); // and match
        assertTrue(new TroopDef("Ax+Bd+Cv").matches("Bd+Cv+Ax")); // and match
        assertTrue(new TroopDef("Ax+Bd+Cv").matches("Cv+Ax+Bd")); // and match
        assertTrue(new TroopDef("Ax+Bd+Cv").matches("Cv+Bd+Ax")); // and match
        assertTrue(new TroopDef("Ax+Cv+Bd").matches("Ax+Bd+Cv")); // and match
        assertTrue(new TroopDef("Bd+Ax+Cv").matches("Ax+Bd+Cv")); // and match
        assertTrue(new TroopDef("Bd+Cv+Ax").matches("Ax+Bd+Cv")); // and match
        assertTrue(new TroopDef("Cv+Ax+Bd").matches("Ax+Bd+Cv")); // and match
        assertTrue(new TroopDef("Cv+Bd+Ax").matches("Ax+Bd+Cv")); // and match

        assertTrue(new TroopDef("Ax+Bd+Cv+Hd").matches("Hd+Cv+Bd+Ax")); // and match
        assertFalse(new TroopDef("Cv+Bd").matches("Cv")); // and mismatch
        assertFalse(new TroopDef("Cv").matches("Cv+Bd")); // and mismatch

        assertTrue(new TroopDef("Ax+Bd").isInstance("Ax+Bd")); // either match
        assertTrue(new TroopDef("Ax+Bd").isInstance("Bd+Ax")); // either match
        assertFalse(new TroopDef("Ax+Bd").isInstance("Ax")); // either match
        assertFalse(new TroopDef("Ax+Bd").isInstance("Bd")); // either match
        assertFalse(new TroopDef("Ax+Bd").isInstance("Hd")); // either mismatch
    }

    @Test
    public void testMatchOr() {
        assertTrue(new TroopDef("Cv or Bd").matches("Cv or Bd")); // or match
        assertTrue(new TroopDef("Cv or Bd").matches("Bd or Cv")); // or match
        assertTrue(new TroopDef("Cv or Bd").matches("Cv")); // or match (one matches)
        assertTrue(new TroopDef("Cv or Bd").matches("Bd")); // or match (one matches)
        assertTrue(new TroopDef("Cv or Bd").matches("Cv or Sp")); // or match (one matches)
        assertFalse(new TroopDef("Cv or Bd").matches("Kn or Sp")); // or mismatch (none)
        assertTrue(new TroopDef("Cv or Bd").matches("Kn or Bd")); // or mismatch (one matches)

        assertTrue(new TroopDef("Ax or Bd or Cv or Hd").matches("Bd or Ax or Cv or Hd")); // or match
        assertTrue(new TroopDef("Ax or Bd or Cv or Hd").matches("Hd or Cv or Bd or Ax")); // or match
        assertTrue(new TroopDef("Ax or Bd or Cv or Hd").matches("Ax or Bd or Cv")); // or match
        assertTrue(new TroopDef("Ax or Bd or Cv or Hd").matches("Ax or Bd")); // or match
        assertTrue(new TroopDef("Ax or Bd or Cv or Hd").matches("Hd")); // or match
        assertTrue(new TroopDef("Ax or Bd or Cv or Hd").matches("Ax")); // or match
        assertTrue(new TroopDef("Ax or Bd or Cv or Hd").matches("Hd")); // or match
        assertTrue(new TroopDef("Hd").matches("Ax or Bd or Cv or Hd")); // or match
        assertTrue(new TroopDef("Ax or Bd").matches("Ax or Bd or Cv or Hd")); // or match
        assertTrue(new TroopDef("Cv").matches("Cv or Bd")); // or mismatch
    }

    @Test
    public void testMatchAndOr() {
        assertTrue(new TroopDef("Ax or Bd + Cv or Hd").matches("Ax")); // or match
        assertTrue(new TroopDef("Ax or Bd + Cv or Hd").matches("Hd")); // or match
        assertTrue(new TroopDef("Ax or Bd + Cv or Hd").matches("Bd+Cv")); // or match
        assertTrue(new TroopDef("(Ax or Bd) + (Cv or Hd)").matches("Ax+Cv")); // or match
        assertFalse(new TroopDef("Ax or Bd + Cv or Hd").matches("Ax+Hd")); // or match
        assertTrue(new TroopDef("Bd+Cv").matches("Ax or Bd + Cv or Hd")); // or match (FAIL!)
        assertFalse(new TroopDef("Ax or Bd + Cv or Hd").matches("Bd+Hd")); // or match (FAIL!)
        assertTrue(new TroopDef("(Ax or Bd) + (Cv or Hd)").matches("Bd+Hd")); // or match (PASS!)
        assertFalse(new TroopDef("Ax or Bd + Cv or Hd").matches("Ax+Ax")); // or match
        assertFalse(new TroopDef("Ax or Bd + Cv or Hd").matches("Ax+Bd")); // or match
        assertFalse(new TroopDef("Ax or Bd + Cv or Hd").matches("Bd+Ax")); // or match
        assertFalse(new TroopDef("Ax or Bd + Cv or Hd").matches("Bd+Bd")); // or match

        assertTrue(new TroopDef("Ax+Bd or Cv+Hd").matches("Ax+Bd")); // or match
        assertTrue(new TroopDef("Ax+Bd or Cv+Hd").matches("Cv+Hd")); // or match (FAIL!)
        assertTrue(new TroopDef("(Ax+Bd) or (Cv+Hd)").matches("Cv+Hd")); // or match (FAIL!)
        assertFalse(new TroopDef("Ax+Bd or Cv+Hd").matches("Ax+Ax")); // or match
        assertFalse(new TroopDef("Ax+Bd or Cv+Hd").matches("Ax+Hd")); // or match
        assertTrue(new TroopDef("Ax+Bd or Cv+Hd").matches("Bd+Ax")); // or match
        assertFalse(new TroopDef("Ax+Bd or Cv+Hd").matches("Bd+Cv")); // or match
        assertFalse(new TroopDef("Ax+Bd or Cv+Hd").matches("Bd+Hd")); // or match
    }

    @Test
    public void testCompoundMatch() {
        // More extensive tests of real word lists with generals, numbers, and options
        String[] armies = {
                "3Bd or 3/4Bw,6x3Ax/3Wb,3xPs,1xPs,1xPs", // I/4a
                "3Pk or Sp,3xPk or Sp,2x4Bw,2x4Bw or 3Ax,4xPs", // I/10
                "Cv,Cv,7xSp/4Ax,3xPs", // I/30c
                "LCh or 3Kn,2x3Kn,2xLH,4xSp or 4Ax,3xPs", // I/50
                "HCh/Sp or Cv,3xHCh/Sp or Sp,5xSp,3xPs", // I/56a
                "Cv,Cv,2xPs,2x4Bd,2xSp,2xSp,2x3Ax or 4Ax", // II/10
                "2xSp or (1 x Cv + 1 x El)",  // evaluate which first? Paren, And, Or?
                "3Kn,1xLH,6x4Pk,2x3Ax/4Ax,2xSp or (1xCv+1xEl),1xPs or Art", // II/16b
                "LCh or 4Wb,1xLCh,2xCv,7x4Wb,1xLCh or Ps", // II/30
                "3Kn,1xLH,3x4Ax,2x4Ax or 4Pk or 5Hd,3xPs,2xPs", // II/50
                "Cv//4Wb,7x4Wb,3x3Bw or 7Hd,1xPs", // II/72b
                "El or 3Kn,2x3Kn,1xEl or 3Kn,2x3Bd,3x3Bw,1xCm or 3Bw,1xPs,1xPs or 7Hd", // III/10
                "Kn//Sp,4xKn//Sp,3xKn//Sp or Cv,1xCv or Sp or LH or Ps or 3Bd,1xPs or Cv,2xKn//Sp or LH", // III/15
                "1x (Cv/6Cv),5x(6Cv/Cv),3xLH or Sp,2xLH or 4Bw or Ps,1x3Kn or Ps or Art",  // III/17 DBA
                "1x Cv or 6Cv,5x6Cv or Cv,3xLH or Sp,2xLH or 4Bw or Ps,1x3Kn or Ps or Art",  // III/17 xlsx
                "Cv,5xCv,3xCv or LH,1xLH or Ps,2xCv or 2x7Hd or (1x7Hd + 1x4Wb)", // III/18
                "Cv or 4Bd or CP,2x3Bw,7x4Bd,1xPs or LCm,1xLH or Cm/Bd",  // III/25a
                "Cv,1xCv,2xCv,1xLH,7xLH,1xLH or 3Ax or Ps", // III/30b
                "3/4Bd or Cv,4x4Bd,2xPs or 3Bw,2x5Hd,2xPs,1xLH or Art or 3Bd", // III/50
                "Lit or 4Bw,1x4Bd,5x3Bw,2xPs,3x5Hd", // IV/10
                "3Kn or 6Kn,1x3Kn or 6Kn,1xCv,3x3Kn,1x3Kn or 3Wb,1xLH,1xSp,2x4Cb,1x3Ax or 7Hd", // IV/30
                "1x4Bd or 4Ax or LH,1x3Kn,3xCv,2xLH or Ps or 7Hd,1xSp or 4Ax,3xPs or 3/4Bw", // IV/50
                "Gen+LCh or Gen+Cv", //4Wb
        };

        for (String army : armies) {
            assertEquals(army.replaceAll("\\s", ""), new TroopDef(army).toString());
            //assertTrue(new TroopDef(armies[0]).matches("Kn//Sp,4xKn//Sp,3xCv,LH,1xPs,2xLH"));
        }
    }

    @Test
    public void testCountOff() {
        String delim = ",";
        assertThat( TroopDef.countOff( 0, List.of( List.of("1","2","3")), delim ),
                contains("1","2","3"));
        assertThat( TroopDef.countOff( 0, List.of( List.of("4","5"), List.of("1","2","3")), delim),
            contains( "4,1","4,2","4,3","5,1","5,2","5,3" ));
        assertThat( TroopDef.countOff( 0, List.of(List.of("6"), List.of("4","5"), List.of("1","2","3")), delim),
            contains( "6,4,1", "6,4,2", "6,4,3", "6,5,1", "6,5,2", "6,5,3" ));
    }

    @Test
    public void testPermutations() {
        // expr type #exprType
        assertThat( new TroopDef( "Ax" ).permute(), contains( "Ax" ));
        // exprs: expr (LIST_DELIM expr)*
        assertThat( new TroopDef( "Gen,Ax" ).permute(), contains( "Gen,Ax" ));
        // expr INTEGER MULTIPLE_DELIM expr   #exprMultiple
        assertThat( new TroopDef( "Gen,2xAx" ).permute(), contains( "Gen,2xAx" ));
        // expr (OR_DELIM expr)+            #exprOr
        assertThat( new TroopDef( "Ax or Bd,Cv" ).permute(), contains("Ax,Cv","Bd,Cv" ));
        assertThat( new TroopDef( "Ax,Bd or Cv" ).permute(), contains("Ax,Bd","Ax,Cv" ));
        assertThat( new TroopDef( "Ax or Bd or Cv,Hd" ).permute(), contains("Ax,Hd","Bd,Hd", "Cv,Hd" ));
        assertThat( new TroopDef( "Ax,Bd or Cv or Hd" ).permute(), contains("Ax,Bd","Ax,Cv", "Ax,Hd" ));
        assertThat( new TroopDef( "Ax or 2xBd,Cv" ).permute(), contains("Ax,Cv","2xBd,Cv" ));
        //  expr (AND_DELIM expr)+        #exprAnd
        assertThat( new TroopDef( "Ax+Bd,Cv" ).permute(), contains("Ax+Bd,Cv" ));
        assertThat( new TroopDef( "Ax,Bd+Cv" ).permute(), contains("Ax,Bd+Cv" ));
        assertThat( new TroopDef( "Ax or Bd,Cv+Hd" ).permute(), contains("Ax,Cv+Hd", "Bd,Cv+Hd" ));
        // expr GROUP_OPEN expr GROUP_CLOSE   #exprGroup
        assertThat( new TroopDef( "(Ax or Bd)" ).permute(), contains("Ax","Bd" ));
        assertThat( new TroopDef( "(Ax + Bd)" ).permute(), contains("Ax+Bd" ));
        // expr expr DISMOUNT_DELIM expr      #exprDismount
        assertThat( new TroopDef( "Ax//Bd,Cv" ).permute(), contains("Ax//Bd,Cv" ));
        assertThat( new TroopDef( "Ax,Bd//Cv" ).permute(), contains("Ax,Bd//Cv" ));
        assertThat( new TroopDef( "Ax//Bd or Cv" ).permute(), contains("Ax//Bd","Cv" ));
        assertThat( new TroopDef( "Ax or Bd//Cv" ).permute(), contains("Ax","Bd//Cv" ));
        // expr EITHER_DELIM expr        #exprEither
        assertThat( new TroopDef( "Ax/Bd" ).permute(), contains("Ax","Bd" ));
//      assertThat( new Army( "Ax/Bd+Cv" ).permute(), contains("Ax+Cv","Bd+Cv" )); // FAIL!
        assertThat( new TroopDef( "Ax/Bd+Cv" ).permute(), contains("Ax","Bd+Cv" ));
        assertThat( new TroopDef( "Ax or Bd+Cv" ).permute(), contains("Ax","Bd+Cv" ));
        assertThat( new TroopDef( "Ax+Bd/Cv" ).permute(), contains("Ax+Bd","Cv" ));
        assertThat( new TroopDef( "Ax+Bd or Cv" ).permute(), contains("Ax+Bd","Cv" ));
        assertThat( new TroopDef( "Ax/Bd or Cv" ).permute(), contains("Ax","Bd","Cv" ));
        assertThat( new TroopDef( "Ax or Bd / Cv" ).permute(), contains("Ax","Bd","Cv" ));
        assertThat( new TroopDef( "(Ax/Bd) or Cv" ).permute(), contains("Ax","Bd","Cv" ));
        assertThat( new TroopDef( "Ax/(Bd or Cv)" ).permute(), contains("Ax","Bd","Cv" ));
        // expr INTEGER EITHER_DELIM type     #exprEitherUnit
        assertThat( new TroopDef( "3/4Ax" ).permute(), contains("3Ax","4Ax" ));
    }

    @Test
    public void testLargePermutations() {
        // More extensive tests of real word lists with generals, numbers, and options
        String[] armies = {
                "3Bd or 3/4Bw,6x3Ax or 3Wb,3xPs,1xPs,1xPs", // I/4a
                "3Pk or Sp,3xPk or Sp,2x4Bw,2x4Bw or 3Ax,4xPs", // I/10
                "Cv,Cv,7xSp/4Ax,3xPs", // I/30c
                "LCh or 3Kn,2x3Kn,2xLH,4xSp or 4Ax,3xPs", // I/50
                "HCh/Sp or Cv,3xHCh/Sp or Sp,5xSp,3xPs", // I/56a
                "Cv,Cv,2xPs,2x4Bd,2xSp,2xSp,2x3Ax or 4Ax", // II/10
                "2xSp or (1 x Cv + 1 x El)",  // useful test case
                "2x(1 x Cv + 1 x El)", // useful test case (handling 1x well?)
                "3Kn,1xLH,6x4Pk,2x3Ax/4Ax,2xSp or (1xCv+1xEl),1xPs or Art", // II/16b
                "LCh or 4Wb,1xLCh,2xCv,7x4Wb,1xLCh or Ps", // II/30
                "3Kn,1xLH,3x4Ax,2x4Ax or 4Pk or 5Hd,3xPs,2xPs", // II/50
                "Cv//4Wb,7x4Wb,3x3Bw or 7Hd,1xPs", // II/72b
                "El or 3Kn,2x3Kn,1xEl or 3Kn,2x3Bd,3x3Bw,1xCm or 3Bw,1xPs,1xPs or 7Hd", // III/10
                "Kn//Sp,4xKn//Sp,3xKn//Sp or Cv,1xCv or Sp or LH or Ps or 3Bd,1xPs or Cv,2xKn//Sp or LH", // III/15
                "1x (Cv/6Cv),5x(6Cv/Cv),3xLH or Sp,2xLH or 4Bw or Ps,1x3Kn or Ps or Art",  // III/17 DBA
                "1x Cv or 6Cv,5x6Cv or Cv,3xLH or Sp,2xLH or 4Bw or Ps,1x3Kn or Ps or Art",  // III/17 xlsx
                "Cv,5xCv,3xCv or LH,1xLH or Ps,2xCv or 2x7Hd or (1x7Hd + 1x4Wb)", // III/18
                "Cv or 4Bd or CP,2x3Bw,7x4Bd,1xPs or LCm,1xLH or Cm/Bd",  // III/25a
                "Cv,1xCv,2xCv,1xLH,7xLH,1xLH or 3Ax or Ps", // III/30b
                "3/4Bd or Cv,4x4Bd,2xPs or 3Bw,2x5Hd,2xPs,1xLH or Art or 3Bd", // III/50
                "Lit or 4Bw,1x4Bd,5x3Bw,2xPs,3x5Hd", // IV/10
                "3Kn or 6Kn,1x3Kn or 6Kn,1xCv,3x3Kn,1x3Kn or 3Wb,1xLH,1xSp,2x4Cb,1x3Ax or 7Hd", // IV/30
                "1x4Bd or 4Ax or LH,1x3Kn,3xCv,2xLH or Ps or 7Hd,1xSp or 4Ax,3xPs or 3/4Bw", // IV/50
        };

        int largestPermCount = Integer.MIN_VALUE;
        for ( String armyStr : armies ) {
            System.out.println("Test=" + armyStr);
            TroopDef troopDef = new TroopDef( armyStr );
            List<String> permutations = troopDef.permute();
            System.out.println("Perm count=" + permutations.size());
            if ( largestPermCount < permutations.size()) {
                largestPermCount = permutations.size();
            }
            int permi = 0;
            for (String perm : permutations) {
                System.out.println("Perm " + permi++ + "=" + perm);
                // Test that each perm matches the original
                TroopDef permTroopDef = new TroopDef(perm);
                assertTrue(troopDef.matches(permTroopDef));
            }
        }
        System.out.println( "Largest perm count=" + largestPermCount );
    }

    @Test
    public void testPermutationRecursive() {
        System.out.println("Recursive:");
        printAllRecursive(new Integer[]{3, 7, 13}, ", ");
        System.out.println("Ordered:");
        printAllOrdered(new Integer[]{3, 7, 13}, ", ");
    }
}