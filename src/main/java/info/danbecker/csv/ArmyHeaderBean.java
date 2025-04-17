package info.danbecker.csv;

import com.opencsv.bean.CsvBindByName;
import static java.lang.String.format;

/**
 * A bean representing data available from the input CSV.
 * which consists of the following columns:
 * - ArmyRef
 * - Variant Count
 * - Army Group name with group names and date ranges
 * <p>
 * {@code @TODO} Eventually the ArmyHeaderBean (with group name) and
 * the Army variants (with army versions, different names, date)
 * must be refactored into proper entities and relations.
 * <p>
 * Example data
 * <code>
 ArmyRef,VarCount,GroupName
 I/1,3,EARLY SUMERIAN 3000BC - 2334BC & THE "GREAT REVOLT" CIRCA 2250BC
 I/2,2,EARLY EGYPTIAN 3000BC - 1541BC
 I/3,1,NUBIAN 3000BC - 1480BC
 * </code>
 */
public class ArmyHeaderBean {
    @CsvBindByName(column = "ArmyRef")
    public String armyRef;

    @CsvBindByName(column = "VarCount")
    public int varCount;

    @CsvBindByName(column = "GroupName")
    public String name;

    @Override
    public String toString() {
        return format( "%s, %d, %s", armyRef, varCount, name);
    }
}