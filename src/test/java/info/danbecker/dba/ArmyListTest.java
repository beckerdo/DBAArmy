package info.danbecker.dba;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.danbecker.dba.ArmyList.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class ArmyListTest {
    @Test
    public void testArmyList() {
        String name = "LATE FRED CLAN";
        assertEquals( "LATE FRED CLAN", name);
    }
}