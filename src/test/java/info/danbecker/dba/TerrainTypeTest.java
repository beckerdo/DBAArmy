package info.danbecker.dba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static info.danbecker.dba.TerrainType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TerrainTypeTest {
    @BeforeEach
    void setup() {
    }

    @Test
    public void testBasic() {
//        for ( TerrainType terrain : TerrainType.values() ) {
//            System.out.println( terrain.initCap());
//        }
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
}