package info.danbecker.dba;

import java.util.List;
import java.util.logging.Logger;

/**
 * An Army holds the ArmyHeader data for the given ArmyRef
 * and also a list of all the ArmyVariants.
 * <p>
 * For army history, references, etc.
 * @see ArmyHeader
 * <p>
 * For troop definitions, terrain, aggression, etc.
 * @see ArmyVariant
 *
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 * @version DBA30
 */
public class Army implements Comparable<Army> {
    static Logger LOGGER = Logger.getLogger(Army.class.getName());

    final ArmyHeader header;
    final List<ArmyVariant> variants;

    /**
     * Construct Army from ArmyHeader and list of ArmyVariant.
     *
     * @param header with group information ArmyHeader
     * @param variants a list of ArmyVariant
     */
    public Army(ArmyHeader header, List<ArmyVariant> variants) {
        if (null == header) throw new IllegalArgumentException("army header is null");
        this.header = header;
        if (null == variants) throw new IllegalArgumentException("army variants is null");
        this.variants = variants;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (!(obj instanceof Army)) return false;
        return this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public int compareTo(Army that) {
        if (null == that) return -1;
        return this.header.armyRef.compareTo(that.header.armyRef);
    }

    /**
     * Returns the ArmyRef
     * @return the section and number (ArmyRef) of this army
     */
    public ArmyRef getArmyRef() {
        return this.header.armyRef;
    }

    /**
     * Returns the ArmyRef
     * @return the section and number (ArmyRef) of this army
     */
    public List<ArmyVariant> getVariants() {
        return this.variants;
    }


    /**
     * Convert an element to a string.
     * The parse and toString APIs are reciprocal.
     */
    @Override
    public String toString() {
        return header.toString();
    }
}