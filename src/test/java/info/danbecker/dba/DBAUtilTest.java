package info.danbecker.dba;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.danbecker.dba.DBAUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class DBAUtilTest {
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
        assertIterableEquals(List.of("NUBIAN"),
            getNames("NUBIAN 3000BC - 1480BC"));
        assertIterableEquals(List.of("EARLY SUMERIAN", "THE \"GREAT REVOLT\""),
            getNames( "EARLY SUMERIAN 3000BC - 2334BC & THE \"GREAT REVOLT\" CIRCA 2250BC" ));
        assertIterableEquals(List.of("OLD SAXON", "FRISIAN", "BAVARIAN", "THURINGIAN", "EARLY-ANGLO-SAXON"),
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
        assertIterableEquals(List.of("3000BC - 1480BC"),
                getDates("NUBIAN 3000BC - 1480BC"));
        assertIterableEquals(List.of( "3000BC - 2334BC", "CIRCA 2250BC"),
                getDates( "EARLY SUMERIAN 3000BC - 2334BC & THE \"GREAT REVOLT\" CIRCA 2250BC" ));
        assertIterableEquals(List.of("250AD - 804AD"),
                getDates( "OLD SAXON, FRISIAN, BAVARIAN, THURINGIAN & EARLY-ANGLO-SAXON 250AD - 804AD" ));
        assertIterableEquals(List.of("1419AD - 1434AD", "1464AD - 1471AD"),
                getDates( "HUSSITE 1419AD - 1434AD & NAME 1464AD - 1471AD" )); // list of named date ranges
        assertIterableEquals(List.of("1419AD - 1434AD", "1464AD - 1471AD"),
                getDates( "HUSSITE 1419AD - 1434AD & 1464AD - 1471AD" )); // list of named and unamed date ranges
    }
}