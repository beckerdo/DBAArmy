package info.danbecker.dba;

import java.util.List;
import java.util.logging.Logger;

/**
 * An ArmyVariant encapsulates the information for each flavor or army
 * for a particular Section/Number/Variant
 * It includes
 * <ul>
 * <li>the ArmyRef with section, army number and variant number (1=a, 2=b, etc.)</li>
 * <li>the variant name</li>
 * <li>the year range</li>
 * <li>the troop definition</li>
 * <li>the home terrain</li>
 * <li>aggression value</li>
 * <li>enemies list</li>
 * <li>allies list</li>
 * </ul>
 * <p>
 * For army name, historical information, and references
 * @see ArmyHeader
 * <p>
 * An @see Army encapsulates an ArmyHeader and ArmyVariant list.
 * <p>
 * An @link ArmyList encapsulates an Army list and Section lists.s
 *
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 * @version DBA30
 */
public class ArmyVariant implements Comparable<ArmyVariant> {
    static Logger LOGGER = Logger.getLogger(ArmyVariant.class.getName());

    final ArmyRef armyRef;
    final String variantName;
    final YearRange years;
    final TroopDef troopDef;
    final String terrain;
    final int aggression;
    final List<ArmyRef> enemies;
    final List<ArmyRef> allies;

    /**
     * Construct header from ArmyHeaderBean information.
     * Synthesize and fill in other fields.
     *
     * @param armyRef
     */
    public ArmyVariant(ArmyRef armyRef, String variantName, String troopDef,
        String terrain, int aggression, String enemies, String allies) {
        if (null == armyRef) throw new IllegalArgumentException("armyRef is null");
        this.armyRef = armyRef;
        if (null == variantName) throw new IllegalArgumentException("army variant name");
        this.variantName = variantName;
        if ( variantName.contains (YearType.Era.BC.name()) || variantName.contains (YearType.Era.AD.name() ))
            this.years = YearRange.parse( variantName ); // not all variants have year
        else
            this.years = null;
        if (null == troopDef) throw new IllegalArgumentException("army troop definition for " + armyRef );
        try {
            this.troopDef = new TroopDef( troopDef );
        } catch ( IllegalArgumentException e ) {
            // Add armyRef to message
            throw new IllegalArgumentException( e.getMessage() + ", armyRef=" + armyRef);
        }
        if (null == terrain || terrain.isEmpty()) throw new IllegalArgumentException("empty terrain for " + armyRef );
        this.terrain = terrain;
        if (0 > aggression || 6 < aggression) throw new IllegalArgumentException("illegal aggression of " + aggression);
        this.aggression = aggression;
        this.enemies = ArmyRef.parseList( enemies ); // can handle nulls or empties
        this.allies = ArmyRef.parseList( allies );
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (!(obj instanceof ArmyVariant)) return false;
        return this.armyRef.equals(((ArmyVariant) obj).armyRef);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public int compareTo(ArmyVariant that) {
        if (null == that) return -1;
        return this.armyRef.compareTo(that.armyRef);
    }

    /**
     * Returns the ArmyRef
     * @return the section and number (ArmyRef) of this army
     */
    public ArmyRef getArmyRef() {
        return this.armyRef;
    }

    /**
     * Returns the variant name
     * @return the name of this army
     */
    public String getVariantName() {
        return this.variantName;
    }

    /**
     * Returns the variant years
     * @return the years of this army
     */
    public YearRange getYears() {
        return this.years;
    }

    /**
     * Returns the troop definition
     * @return the troop definition of this army
     */
    public TroopDef getTroopDef() {
        return this.troopDef;
    }

    /**
     * Returns the terrain
     * @return the terrain definition of this army
     */
    public String getTerrain() {
        return this.terrain;
    }

    /**
     * Returns the enemies
     * @return the enemies of this army
     */
    public List<ArmyRef> getEnemies() {
        return this.enemies;
    }

    /**
     * Returns the allies
     * @return the allies of this army
     */
    public List<ArmyRef> getAllies() {
        return this.allies;
    }

    /**
     * Convert an element to a string.
     * The parse and toString APIs are reciprocal.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( armyRef.toString() );
        sb.append( " " );
        sb.append( variantName );
        sb.append( " " );
        sb.append( years );
        return sb.toString();
    }
}