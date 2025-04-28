package info.danbecker.dba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class GeographicIndexTest {
    @BeforeEach
    void setup() {
    }

    @Test
    public void testBasics() {
        // Traversal, name aggregation
        assertEquals( "World", GeographicIndex.WORLD.getName() );

        // System.out.println( Geographic.WORLD.getNames() );
        assertEquals( List.of( "Africa", "America", "Asia", "Europe", "India", "Orient"), GeographicIndex.WORLD.getNames());

        // System.out.println( Geographic.WORLD.getAllNames() );
        List<String> allNames = GeographicIndex.WORLD.getAllNames();
        assertEquals( 28, allNames.size());
        assertEquals("World",allNames.getFirst());
        assertEquals("Africa", allNames.get(1));
        assertEquals("Tibet", allNames.getLast());

        // Parents
        assertNull( GeographicIndex.WORLD.getParent() );
        List<GeographicIndex> regions = GeographicIndex.WORLD.getRegions();
        regions
            .forEach(g -> assertEquals( GeographicIndex.WORLD, g.getParent()) );
    }

    @Test
    public void testArmies() {
        // Find
        Optional<GeographicIndex> geo = GeographicIndex.find( "Arabia" );
        assertTrue( geo.isPresent() );
        assertEquals( "Arabia", geo.get().getName() );
        geo = GeographicIndex.find( "Atlantis" );
        assertFalse( geo.isPresent() );

        assertEquals( GeographicIndex.find( "Asia" ).get(), GeographicIndex.find( "Arabia" ).get().getParent() );

        // Army aggregation
        GeographicIndex africa = GeographicIndex.find( "Africa" ).get();
        List<ArmyRef> armies = africa.getAllArmyRefs();
        // System.out.println( "Armies=" + ArmyRef.toStringCompact(armies));
        assertEquals(28, armies.size());
        assertEquals("I/2", armies.getFirst().toString());
        assertEquals("I/17", armies.get(1).toString());
        assertEquals("III/75", armies.getLast().toString());
        armies.sort( Comparator.naturalOrder() );
        System.out.println( "Armies Africa sorted=" + ArmyRef.toStringCompact(armies));
        assertEquals(28, armies.size());
        assertEquals("I/2", armies.getFirst().toString());
        assertEquals("I/3", armies.get(1).toString());
        assertEquals("IV/45", armies.getLast().toString());

        GeographicIndex world = GeographicIndex.find( "World" ).get();
        armies = world.getAllArmyRefs();
        armies.sort( Comparator.naturalOrder() );
        System.out.println( "Armies All=" + ArmyRef.toStringCompact(armies));
        assertEquals(340, armies.size());

        // Note. Should probably update the DBA3.0 list with variant letters, which Barker omits
        // Otherwise you have repeats in the list such as I/14 and IV/84.
        assertEquals("I/1", armies.getFirst().toString());
        assertEquals("I/2", armies.get(1).toString());
        assertEquals("IV/84", armies.get(339).toString());
        assertEquals("IV/84", armies.getLast().toString());
    }
}