package info.danbecker.dba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.danbecker.dba.TerrainType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TerrainTypeTest {
    @BeforeEach
    void setup() {
    }

    @Test
    public void testBasic() {
        assertEquals( "Arable", ARABLE.initCap());
        assertEquals( "Forest", FOREST.initCap());
        assertEquals( "Hilly", HILLY.initCap());
        assertEquals( "Steppe", STEPPE.initCap());
        assertEquals( "Dry", DRY.initCap());
        assertEquals( "Tropical", TROPICAL.initCap());
        assertEquals( "Littoral", LITTORAL.initCap());

        assertEquals( "Ar", ARABLE.abbr());
        assertEquals( "Fo", FOREST.abbr());
        assertEquals( "Hi", HILLY.abbr());
        assertEquals( "St", STEPPE.abbr());
        assertEquals( "Dr", DRY.abbr());
        assertEquals( "Tr", TROPICAL.abbr());
        assertEquals( "Li", LITTORAL.abbr());
    }

    @Test
    public void testParsing() {
        assertEquals( ARABLE, TerrainType.valueOf( "ARABLE"));
        assertEquals( FOREST, TerrainType.fromString("forest"));

        assertIterableEquals( List.of( HILLY ), TerrainType.listFromString( "Hilly"));
        assertIterableEquals( List.of( STEPPE, DRY ), TerrainType.listFromString("Steppe/Dry" ));

        IllegalArgumentException ie = assertThrows(
                IllegalArgumentException.class,
                () -> TerrainType.listFromString("" )
        );
        assertTrue(ie.getMessage().contains("No enum constant "));
        ie = assertThrows(
                IllegalArgumentException.class,
                () -> TerrainType.listFromString( "Fred/Farkle" )
        );
        assertTrue(ie.getMessage().contains("No enum constant "));
    }
}