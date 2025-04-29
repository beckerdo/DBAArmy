package info.danbecker.dba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ElementTypeTest {
    @BeforeEach
    void setup() {
    }

    @Test
    public void testType() {
        assertEquals( "ELEPHANTS", ElementType.ELEPHANTS.toString());
        assertEquals( "Elephants", ElementType.ELEPHANTS.properCase());

        assertEquals( "LIGHT_HORSE", ElementType.LIGHT_HORSE.toString());
        assertEquals( "Light Horse", ElementType.LIGHT_HORSE.properCase());

        assertNull( ElementType.fromCode( "crud" ));
        assertEquals( ElementType.KNIGHTS, ElementType.fromCode( "6Kn" ));
        assertEquals( ElementType.BOWS, ElementType.fromCode( "4Bw" ));
        assertEquals( ElementType.BOWS, ElementType.fromCode( "3Cb" ));
        assertEquals( ElementType.BOWS, ElementType.fromCode( "8Lb" ));

        assertFalse( ElementType.isDouble( "crud" ));
        assertTrue( ElementType.isDouble( "6Kn" ));
        assertTrue( ElementType.isDouble( "8Bw" ));
        assertFalse( ElementType.isDouble( "CP" ));

        assertFalse( ElementType.isFast( "crud" ));
        assertTrue( ElementType.isFast( "Ps" ));
        assertTrue( ElementType.isFast( "5Hd" ));

        assertFalse( ElementType.isSolid( "crud" ));
        assertTrue( ElementType.isSolid( "Sp" ));
        assertTrue( ElementType.isSolid( "8Sp" ));
        assertTrue( ElementType.isSolid( "7Hd" ));

        assertTrue( ElementType.isMounted( "El" ));
        assertFalse( ElementType.isMounted( "Sp" ));
        assertFalse( ElementType.isFoot( "El" ));
        assertTrue( ElementType.isFoot( "Sp" ));
        assertTrue( ElementType.canDismount( "Mtd-X" ));
        assertFalse( ElementType.canDismount( "Sp" ));

        assertFalse( ElementType.isMissile( "Cv" ));
        assertTrue( ElementType.isMissile( "LH" ));
        assertFalse( ElementType.isCamel( "Cv" ));
        assertTrue( ElementType.isCamel( "LCm" ));
        assertFalse( ElementType.isChariot( "Art" ));
        assertTrue( ElementType.isChariot( "SCh" ));
    }
}