package info.danbecker.csv;

import static java.lang.String.format;
import com.opencsv.bean.CsvBindByName;
import info.danbecker.dba.ArmyRef;

/**
 * A bean representing data available from the input CSV.
 * This should be as close to the CSV layout as possible
 * with only String or primitive fields and named columns.
 * Convert to DBA specifics with objects in dba package.
 * <p>
 * Example data
 * <code>
 * Book,Army,Var,Army Name,,Topography,Agg,General,1,2,3,4,5,6,7,8,9,10,Check,,Kn,HCh,Cv,LCh,Cm,LH,LCm,El,SCh,Pk,Sp,Bd,Bw,Lb,Cb,Wb,Hd,Ax,Ps,Art,WWg,CWg,CP,Lit,Enemies,Allies
 * 1,1,a,Early Sumerian Army 3000-2800 BC,a2,Arable,2,3Bd,8x4Bw,3xPs,,,,,,,,,1,,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,"I/1a, 4a, 5a, 6a",I/4a or 5a or 6a
 * 1,1,b,Early Sumerian Army 2799-2334 BC,a2,Arable,2,4Pk or 3Bd or HCh or LCh,1xHCh or 4Bw,6x4Pk,1x4Pk or 4Bw,2xPs,1x3Ax or Ps,,,,,,4,,0,1,0,1,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,"I/1a, 4a, 5a, 5b, 6a, 9, 11a",I/4a or 5a or 5b or 6a or 9
 * </code>
 */
public class ArmyVariantBean {
    @CsvBindByName(column = "Book")
    public int book;

    @CsvBindByName(column = "Army")
    public int armyNum;

    @CsvBindByName(column = "Var")
    public String var;

    @CsvBindByName(column = "Army Name")
    public String name;

    @CsvBindByName(column = "Topography")
    public String topo;

    @CsvBindByName(column = "Agg")
    public int agg;

    @CsvBindByName(column = "Enemies")
    public String enemies;

    @CsvBindByName(column = "Allies")
    public String allies;

    @CsvBindByName(column = "General")
    public String ele0;
    @CsvBindByName(column = "1")
    public String ele1;
    @CsvBindByName(column = "2")
    public String ele2;
    @CsvBindByName(column = "3")
    public String ele3;
    @CsvBindByName(column = "4")
    public String ele4;
    @CsvBindByName(column = "5")
    public String ele5;
    @CsvBindByName(column = "6")
    public String ele6;
    @CsvBindByName(column = "7")
    public String ele7;
    @CsvBindByName(column = "8")
    public String ele8;

    public static final String OR = " or ";
    public static final String BAR = "|";

    public static final String LCAT = ", ";
    public static final String SCAT = ",";

    public String getElements() {
        // Add General to first
        StringBuilder elements = new StringBuilder( ele0 + "+Gen" );
        if( null != ele1 && !ele1.isEmpty()) elements.append( SCAT ).append( ele1 );
        if( null != ele2 && !ele2.isEmpty()) elements.append( SCAT ).append( ele2 );
        if( null != ele3 && !ele3.isEmpty()) elements.append( SCAT ).append( ele3 );
        if( null != ele4 && !ele4.isEmpty()) elements.append( SCAT ).append( ele4 );
        if( null != ele5 && !ele5.isEmpty()) elements.append( SCAT ).append( ele5 );
        if( null != ele6 && !ele6.isEmpty()) elements.append( SCAT ).append( ele6 );
        if( null != ele7 && !ele7.isEmpty()) elements.append( SCAT ).append( ele7 );
        if( null != ele8 && !ele8.isEmpty()) elements.append( SCAT ).append( ele8 );
       return elements.toString();
    }

    public static String getRef(int book, int armyNum, String variant) {
        return format( "%s/%d%s", ArmyRef.getSectionRoman(book), armyNum, variant );
    }

    public String getRef() {
        return format( "%s/%d%s", ArmyRef.getSectionRoman(book), armyNum, var );
    }

    @Override
    public String toString() {
        return format( "%s, %s, Agg:%d, Top:%s, E:%s, A:%s\n   %s",
            getRef(book, armyNum, var), name, agg, topo,
                enemies.replaceAll(LCAT, SCAT), allies.replace(OR, SCAT), getElements());
    }
}