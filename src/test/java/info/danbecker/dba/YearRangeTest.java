package info.danbecker.dba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static info.danbecker.dba.YearType.Era.AD;
import static info.danbecker.dba.YearType.Era.BC;
import static org.junit.jupiter.api.Assertions.*;

public class YearRangeTest {
    @BeforeEach
    void setup() {
    }

    @Test
    public void testBasic() {
        YearRange rng = new YearRange(new YearType(44, BC), new YearType(79, AD));
        assertEquals(44, rng.begin().year());
        assertEquals(BC, rng.begin().era());
        assertEquals(79, rng.end().year());
        assertEquals(AD, rng.end().era());

        assertThrows(
            IllegalStateException.class,
            () -> new YearRange(new YearType(79, AD), new YearType(44, BC)));

    }

    @Test
    public void testString()  {
        YearRange rng = new YearRange(new YearType( 44, BC), new YearType( 79, AD) );
        assertEquals( YearType.parse("44BC"), rng.begin());
        assertEquals( YearType.parse("79AD"), rng.end());

        rng = YearRange.parse( "44BC-79AD");
        assertEquals( YearType.parse("44BC"), rng.begin());
        assertEquals( YearType.parse("79AD"), rng.end());
        rng = YearRange.parse( " 44 BC -     79AD   ");
        assertEquals( YearType.parse("44BC"), rng.begin());
        assertEquals( YearType.parse("79AD"), rng.end());
        rng = YearRange.parse( "44 -79AD");
        assertEquals( YearType.parse("44AD"), rng.begin());
        assertEquals( YearType.parse("79AD"), rng.end());
        rng = YearRange.parse( "144BC-79 ");
        assertEquals( YearType.parse("144BC"), rng.begin());
        assertEquals( YearType.parse("79BC"), rng.end());

        assertThrows(
            IllegalStateException.class,
            () -> YearRange.parse( "44-79"));
        assertThrows(
                IllegalStateException.class,
                () -> YearRange.parse( " BC - AD "));
        IllegalStateException e = assertThrows(
                IllegalStateException.class,
                () -> YearRange.parse( "-44 BC - -79AD "));
        System.out.println( e.getMessage());
    }

    @Test
    public void testCompare()  {
        YearRange rng = new YearRange( "45BC", "79AD" );
        assertFalse( rng.contains( YearType.parse( "46BC")));
        assertTrue( rng.contains( YearType.parse( "45BC")));
        assertTrue( rng.contains( YearType.parse( "44BC")));
        assertTrue( rng.contains( YearType.parse( "1BC")));
        assertTrue( rng.contains( YearType.parse( "1AD")));
        assertTrue( rng.contains( YearType.parse( "78AD")));
        assertTrue( rng.contains( YearType.parse( "79AD")));
        assertFalse( rng.contains( YearType.parse( "80AD")));
    }

}