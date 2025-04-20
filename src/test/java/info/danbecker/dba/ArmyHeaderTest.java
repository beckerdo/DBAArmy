package info.danbecker.dba;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.danbecker.dba.ArmyHeader.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class ArmyHeaderTest {
    @Test
    public void testBasics() {
        // II/73 OLD SAXON, FRISIAN, BAVARIAN, THURINGIAN & EARLY-ANGLO-SAXON 250AD - 804AD, 1, [Old Saxon, Frisian, Bavarian, Thuringian, Early-Anglo-Saxon] 5
        ArmyHeader test = new ArmyHeader( ArmyRef.parse("II/73"),
            "OLD SAXON, FRISIAN, BAVARIAN, THURINGIAN & EARLY-ANGLO-SAXON 250AD - 804AD", 1 );
        assertEquals( "II/73", test.armyRef.toString());
        assertEquals( "OLD SAXON, FRISIAN, BAVARIAN, THURINGIAN & EARLY-ANGLO-SAXON 250AD - 804AD", test.groupName );

        assertIterableEquals( List.of( "Old Saxon", "Frisian", "Bavarian", "Thuringian", "Early-Anglo-Saxon"),
            test.names );
        assertIterableEquals( List.of( YearRange.parse("250AD-804AD" )), test.years );
        assertEquals( "Historical description", test.historicalDesc );
        assertIterableEquals( List.of( "Cambridge Ancient History Vol. 1 Part 2" ), test.references );

        assertEquals( 1, test.variantCount );
        assertEquals( "II/73 OLD SAXON, FRISIAN, BAVARIAN, THURINGIAN & EARLY-ANGLO-SAXON 250AD - 804AD", test.toString());
    }

    @Test
    public void testGetNameNoDates() {
        String name = "LATE FRED CLAN";
        assertEquals(name, getNameNoDates(name));

        assertEquals(name, getNameNoDates(name + " 35BC"));
        assertEquals(name, getNameNoDates(name + " 4235BC"));
        assertEquals(name, getNameNoDates(name + " 42BC-35BC"));
        assertEquals(name, getNameNoDates(name + " 42BC - 35BC"));
        assertEquals(name, getNameNoDates(name + " 42-35BC"));
        assertEquals(name, getNameNoDates(name + " 42 - 35BC"));
        assertEquals(name, getNameNoDates(name + " 42 - 35 BC"));

        assertEquals(name, getNameNoDates(name + " 35AD"));
        assertEquals(name, getNameNoDates(name + " 35AD-42AD"));

        assertEquals(name, getNameNoDates(name + " 1419AD - 1434AD & 1464AD - 1471AD"));

        assertEquals(name, getNameNoDates(name + " CIRCA 35AD-42AD"));
    }

    @Test
    public void testGetNames() {
        assertIterableEquals(List.of(ArmyHeader.toDisplayCase("NUBIAN")),
            getNames("NUBIAN 3000BC - 1480BC"));
        assertIterableEquals(List.of(ArmyHeader.toDisplayCase("EARLY SUMERIAN"), ArmyHeader.toDisplayCase("THE \"GREAT REVOLT\"")),
            getNames( "EARLY SUMERIAN 3000BC - 2334BC & THE \"GREAT REVOLT\" CIRCA 2250BC" ));
        assertIterableEquals(List.of(ArmyHeader.toDisplayCase("OLD SAXON"), ArmyHeader.toDisplayCase("FRISIAN"), ArmyHeader.toDisplayCase("BAVARIAN"),
            ArmyHeader.toDisplayCase("THURINGIAN"), ArmyHeader.toDisplayCase("EARLY-ANGLO-SAXON")),
            getNames( "OLD SAXON, FRISIAN, BAVARIAN, THURINGIAN & EARLY-ANGLO-SAXON 250AD - 804AD" ));
    }

    @Test
    public void testGetDateNoNames() {
        String name = "LATE FRED CLAN";
        assertEquals( "", getDatesNoNames(name));

        assertEquals("35BC", getDatesNoNames(name + " 35BC"));
        assertEquals( "4235BC", getDatesNoNames(name + " 4235BC"));
        assertEquals( "42BC-35BC", getDatesNoNames(name + " 42BC-35BC"));
        assertEquals( "42BC - 35BC", getDatesNoNames(name + " 42BC - 35BC"));
        assertEquals( "42-35BC", getDatesNoNames(name + " 42-35BC"));
        assertEquals( "42 - 35BC", getDatesNoNames(name + " 42 - 35BC"));
        assertEquals( "42 - 35BC", getDatesNoNames(name + " 42 - 35BC"));

        assertEquals( "35AD", getDatesNoNames(name + " 35AD"));
        assertEquals( "35AD-42AD", getDatesNoNames(name + " 35AD-42AD"));

        assertEquals( "1419AD - 1434AD & 1464AD - 1471AD", getDatesNoNames(name + " 1419AD - 1434AD & 1464AD - 1471AD"));
        assertEquals( "CIRCA 35AD-42AD", getDatesNoNames(name + " CIRCA 35AD-42AD"));
    }

    @Test
    public void testGetDates() {
        assertIterableEquals(List.of(YearRange.parse("3000BC-1480BC") ),
                getDates("NUBIAN 3000BC - 1480BC"));
        assertIterableEquals(List.of( YearRange.parse("3000BC-2334BC"), YearRange.parse("CIRCA 2250BC")),
                getDates( "EARLY SUMERIAN 3000BC - 2334BC & THE \"GREAT REVOLT\" CIRCA 2250BC" ));
        assertIterableEquals(List.of(YearRange.parse("250AD-804AD")),
                getDates( "OLD SAXON, FRISIAN, BAVARIAN, THURINGIAN & EARLY-ANGLO-SAXON 250AD - 804AD" ));
        assertIterableEquals(List.of(YearRange.parse("1419AD-1434AD"), YearRange.parse("1464AD-1471AD")),
                getDates( "HUSSITE 1419AD - 1434AD & NAME 1464AD - 1471AD" )); // list of named date ranges
        assertIterableEquals(List.of(YearRange.parse("1419AD-1434AD"), YearRange.parse("1464AD-1471AD")),
                getDates( "HUSSITE 1419AD - 1434AD & 1464AD - 1471AD" )); // list of named and unamed date ranges
    }
}