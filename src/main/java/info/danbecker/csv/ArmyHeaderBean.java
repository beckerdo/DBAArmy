package info.danbecker.csv;

import com.opencsv.bean.CsvBindByName;
import static java.lang.String.format;

/**
 * A bean representing data available from the input CSV.
 * which consists of the following columns:
 * - ArmyRef
 * - Variant Count
 * - Army Group name
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
    private String armyRef;

    @CsvBindByName(column = "VarCount")
    private int varCount;

    @CsvBindByName(column = "GroupName")
    private String name;

    @Override
    public String toString() {
        return format( "%s, %d, %s", armyRef, varCount, name);
    }
}