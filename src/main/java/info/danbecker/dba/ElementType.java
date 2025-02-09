package info.danbecker.dba;

import java.util.ArrayList;
import java.util.List;

/**
 * An ElementType represents the classes of elements such as
 * CAVALRY, BLADE, etc. along with the codes which are flavors of the type.
 * as listed in the Army lists on DBA 3.0 page 7.
 * <p>
 * Groups and multiples of a type are part of the Element class.
 * <p>
 * Number encodes solid or fast for  PK, BD, AX, BW, WB, HD
 * Psiloi and foot that are listed as 3, 5 or 6 to a base are classed as “Fast” or “Solid” (page 33)
 * DBMM encodes S=Superior, O=Ordinary, F=Fast, I=Inferior, X=Exception
 *
 * {@code @TODO} Grab this grouping from the DBAArmyParser. This is in DBAArmy.g4
 *
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 * @version DBA30
 */
public enum ElementType {
    ELEPHANTS(List.of("El")),
    KNIGHTS(List.of("3Kn", "4Kn", "6Kn", "Kn", "HCh")), // 3Kn vs 4Kn vs 6Kn
    CAVALRY(List.of("Cv", "6Cv", "LCh")), // Cv vs 6Kv
    LIGHT_HORSE(List.of("LH", "LCm")),
    SCYTHED_CHARIOTS(List.of("SCh")),
    CAMELRY(List.of("Cm")),
    MOUNTED_INFANTRY(List.of("Mtd-X")),
    SPEARS(List.of("Sp", "8Sp")), // Distinguish Sp (4 Solid) vs 8Sp (two elements)
    PIKES(List.of("4Pk", "3Pk", "Pk")), // 4Pk Solid vs 3 Pk fast
    BLADES(List.of("4Bd", "3Bd", "6Bd", "Bd")), // 4Bd vs 3Bd vs 6Bd
    AUXILIA(List.of("4Ax", "3Ax", "Ax")),
    BOWS(List.of("4Bw", "3Bw", "8Bw", "4Cb", "3Cb", "8Cb", "4Lb", "3Lb", "8Lb", "Bw", "Cb", "Lb")),
    PSILOI(List.of("Ps")),
    WARBAND(List.of("4Wb", "3Wb", "Wb")),
    HORDES(List.of("7Hd", "5Hd", "Hd")),
    ARTILLERY(List.of("Art")),
    WAR_WAGONS(List.of("WWg")),
    GENERAL(List.of("CP", "Lit", "CWg", "Gen")); // Litter LIT, Command Wagon CWg, or Command Position CP
    // Also Camp Followers and City Denizens

    private final List<String> codes;
    private final static List<String> allCodes = new ArrayList<>(60);

    static {
        for (ElementType type : ElementType.values()) {
            allCodes.addAll(type.getCodes());
        }
    }

    ElementType(List<String> codes) {
        this.codes = codes;
        // codes later added to allCodes in static block
    }

    public List<String> getCodes() {
        return this.codes;
    }

    public static List<String> getAllCodes() {
        return allCodes;
    }

    @Override
    public String toString() { return name(); }
    // public String toString() {     return properCase();    }

    /**
     * Convert type name to proper or title case.
     *
     * @return proper case Type name
     */
    public String properCase() {
        int underscore = name().indexOf("_");
        if (-1 < underscore) {
            // Name has underscore
            String name = name().replace("_", " ");
            return name.substring(0, 1) +
                    name.substring(1, underscore + 1).toLowerCase() +
                    name.substring(underscore + 1, underscore + 2) +
                    name.substring(underscore + 2).toLowerCase();
        }
        return name().substring(0, 1) + name().substring(1).toLowerCase();
    }

    /**
     * Convert String to one of the ElementTypes.
     * I do not know how performant switch is compared
     * to grep and pattern matching.
     *
     * @param code
     * @return
     */
    public static ElementType getType(String code) {
        return switch (code) {
            case "El" -> ELEPHANTS;
            case "3Kn", "4Kn", "6Kn", "Kn", "HCh" -> KNIGHTS;
            case "Cv", "6Cv", "LCh" -> CAVALRY;
            case "LH", "LCm" -> LIGHT_HORSE;
            case "SCh" -> SCYTHED_CHARIOTS;
            case "Cm" -> CAMELRY;
            case "Mtd-X" -> MOUNTED_INFANTRY;
            case "Sp", "8Sp" -> SPEARS;
            case "4Pk", "3Pk", "Pk" -> PIKES;
            case "4Bd", "3Bd", "6Bd", "Bd" -> BLADES;
            case "4Ax", "3Ax", "Ax" -> AUXILIA;
            case "4Bw", "3Bw", "8Bw", "4Cb", "3Cb", "8Cb", "4Lb", "3Lb", "8Lb", "Bw", "Cb", "Lb" -> BOWS;
            case "Ps" -> PSILOI;
            case "4Wb", "3Wb", "Wb" -> WARBAND;
            case "7Hd", "5Hd", "Hd" -> HORDES;
            case "Art" -> ARTILLERY;
            case "WWg" -> WAR_WAGONS;
            case "CP", "Lit", "CWg", "Gen" -> GENERAL;
            default -> null;
        };
    }

    public static boolean isDouble(String code) {
        return switch (code) {
            case "6Kn", "6Cv", "8Sp", "8Bw", "8Cb", "8Lb" -> true;
            default -> false;
        };
    }

    public static boolean isFast(String code) {
        return switch (code) {
            case "3Pk", "3Bd", "6Bd", "3Ax", "3Bw", "3Cb", "3Lb", "Ps", "3Wb", "5Hd" -> true;
            default -> false;
        };
    }

    public static boolean isSolid(String code) {
        return switch (code) {
            case "Sp", "8Sp", "4Bd", "4Ax", "4Bw", "4Cb", "4Lb", "4Wb", "7Hd" -> true;
            default -> false;
        };
    }

    public static boolean canDismount(String code) {
        return switch (code) {
            case "4Kn", "Mtd-X" -> true;
            default -> false;
        };
    }

    public static boolean isMounted(String code) {
        return switch (code) {
            case "El", "3Kn", "4Kn", "6Kn", "Kn", "HCh", "Cv", "6Cv", "LCh", "LH", "LCm", "SCh", "Cm", "Mtd-X" -> true;
            default -> false;
        };
    }

    public static boolean isCamel(String code) {
        return switch (code) {
            case "LCm", "Cm" -> true;
            default -> false;
        };
    }

    public static boolean isChariot(String code) {
        return switch (code) {
            case "HCh", "LCh", "SCh" -> true;
            default -> false;
        };
    }

    public static boolean isFoot(String code) {
        return switch (code) {
            case "El", "3Kn", "4Kn", "6Kn", "Kn", "HCh", "Cv", "6Cv", "LCh", "LH", "LCm", "SCh", "Cm", "Mtd-X" -> false;
            default -> true;
        };
    }

    /**
     * Need to check if non-LH cav can also have missiles.
     */
    public static boolean isMissile(String code) {
        return switch (code) {
            case "LH", "LCm", "3Bw", "3Cb", "3Lb", "4Bw", "4Cb", "4Lb", "8Bw", "8Cb", "8Lb", "Bw", "Cb", "Lb", "Ps",
                 "Art", "WWg" -> true;
            default -> false;
        };
    }
} // enum ElementType