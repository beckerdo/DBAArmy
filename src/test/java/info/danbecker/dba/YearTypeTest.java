package info.danbecker.dba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static info.danbecker.dba.YearType.Era.*;

public class YearTypeTest {
    @BeforeEach
    void setup() {
    }

    @Test
    public void testBasic()  {
        assertEquals( "44BC", new YearType( 44, BC).toString());
        assertEquals( "44BC", new YearType( 44, BC).toString());

        assertEquals( "79AD", new YearType( 79, AD).toString());

        assertEquals( "44BC", new YearType( "44BC" ).toString());
        assertEquals( "79AD", new YearType( "79AD" ).toString());

        YearType yt = YearType.parse( "44BC" );
        assertEquals( 44, yt.year());
        assertEquals( BC, yt.era());
        yt = YearType.parse( "   79 AD" );
        assertEquals( 79, yt.year());
        assertEquals( AD, yt.era());

        assertEquals( yt,  YearType.parse( "79 AD" ));

        assertThrows(
            IllegalStateException.class,
            () -> new YearType( -10, BC));
        assertThrows(
            IllegalStateException.class,
            () -> new YearType( 0, AD));
        assertThrows(
            IllegalStateException.class,
            () -> YearType.parse( "44"));
        assertThrows(
            IllegalStateException.class,
            () -> YearType.parse( "79 ZZ"));
    }

    @Test
    public void testCompare()  {
        YearType y44BC = new YearType( 44, BC);
        assertEquals( -1, y44BC.compareTo( null ));
        assertEquals( 1, y44BC.compareTo( YearType.parse( "45BC" ) ));
        assertEquals( 0, y44BC.compareTo( YearType.parse( "44BC" ) ));
        assertEquals( -1, y44BC.compareTo( YearType.parse( "40BC" ) ));
        YearType y79AD = new YearType( 79, AD);
        assertEquals( -1, y79AD.compareTo( null ));
        assertEquals( 1, y79AD.compareTo( YearType.parse( "78AD" ) ));
        assertEquals( 0, y79AD.compareTo( YearType.parse( "79AD" ) ));
        assertEquals( -1, y79AD.compareTo( YearType.parse( "85AD" ) ));
    }
}