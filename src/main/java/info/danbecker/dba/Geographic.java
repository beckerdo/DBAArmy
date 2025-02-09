package info.danbecker.dba;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is a way to report on armies by Group (AFRICA,AMERICA,...)
 * or by region (Arabia,Black Sea,...)
 * <p>
 * getRegions will return the names of the top level regions in the world.
 * <p>
 * getArmies will return all ArmyRefs in a geographic group and all regions below.
 * <p>
 * getNames will return all names of group and regions below.
 * <p>
 * A static Geographic.WORLD provides the root of all Groups
 *
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public class Geographic implements Comparable<Geographic> {
    public final static Geographic WORLD = new Geographic( "World",
        List.of(
            new Geographic( "Africa",
                List.of (
                new Geographic( "Egypt" , "I/2,17,22,38,46,53,58,II/20,III/49,49,66,IV/20,45"),
                new Geographic( "Other Africa", "I/3,7,56,61,II/32,40,55,57,62,84,III/12,33,69,70,75")
                )
            ),
            new Geographic( "America", "III/22,41,59,IV/9,10,11,12,19,29,53,63,70,71,72,81,84"),
            new Geographic( "Asia",
                List.of (
                new Geographic( "Arabia" , "I/6,8,II/23,III/25a,25c,31,37,50,54a,54b,IV/46"),
                new Geographic( "Black Sea" , "I/16,24,31,39,40,43,50,II/6,25,48,65,67a,67b,III/14,27,47,51,71,80,IV/47,49"),
                new Geographic( "Byzantium" , "III/4,17,26,29,76,65,IV/1,2,31,32,33,34,50,51,55"),
                new Geographic( "Mesopotamia, Syria & Near East", """
                    I/1,4,9,11,12,15,19,20,21,25,27,29,34,35,37,44,45,51,62,
                    II/14,16,22,30,43,44,50,51,59,74,III/58,61,74,IV/6,7,17,26,56"""),
                new Geographic( "Persia" , "I/5,41,42,60,II/7,19,37,69,III/8a,8b,43,IV/24,42,67,77"),
                new Geographic( "Steppes", "I/43,II/24,26,58,80,III/11,13,14,16,42,44,47,IV/15,35,47,52,75")
                )
            ),
            new Geographic( "Europe",
                List.of (
                new Geographic( "British Isles" , "II/53,54,60,68,73,81,III/19,24,45,46,72,78,IV/3,16,21,23,58,62,83"),
                new Geographic( "France & Low Countries" , "I/14,II/11,70,72,III/5,18,28,52,53,IV/4,39,57,64,74,76,82,84"),
                new Geographic( "Germany & Eastern Europe" , "II/47,III/1,2,30,32,48,63,68,79,IV/13,30,43,44,65,66,80"),
                new Geographic( "Greece & Balkans, Syria & Near East",
             "I/18,26,28,60,47,48,52,54,63,II/5,12,15,17,18,31,34,35,52,IV/22,25,56,60,69"),
                new Geographic( "Italy & the Alps" ,
             "I/14,33,36,55a,55b,57,59,II/8b,8c,9,40,13,27,33,45,49,56,64,78,82,III/3,21,33,73,77,IV/5,61,41,79"),
                new Geographic( "Scandinavia & Baltic", "II/73,III/40,IV/38,60,68a,68e,74"),
                new Geographic( "Spain & Portugal", "II/39,66,83,III/34,35,IV/38,60a,68e,74")
                )
            ),
            new Geographic( "India", "III/22,41,59,IV/9,10,11,12,19,29,53,63,70,71,72,81,84"),
            new Geographic( "Orient",
                List.of (
                    new Geographic( "China" ,
                "I/13,32a,32b,43,II/4a,4b,4c,4d,4e,21,29,41,61,63a,63b,79,III/20,39,56,62,IV/14,48,73"),
                    new Geographic( "Chinese Borderlands" , "I/14,II/38,III/36,67"),
                    new Geographic( "Korea" , "II/75,76,77,III/57,IV/78"),
                    new Geographic( "Japan" , "I/64,III/6,7,55,IV/59"),
                    new Geographic( "Southeast Asia" , "I/49,III/9,23,60,IV/37,40"),
                    new Geographic( "Tibet" , "III/15")
                )
           )
        )
    );

    private Geographic parent;
    private final String name;
    private final List<Geographic> regions;
    private final List<ArmyRef> armyRefs;

    public Geographic( String name, List<Geographic> regions, List<ArmyRef> armies) {
        this.name = name;
        this.regions = regions; // children, may be null
        this.armyRefs = armies; // armies, may be null
        regions.forEach( g -> g.parent = this);
    }
    public Geographic( String name, List<Geographic> regions ) {
        this( name, regions, List.of() );
    }
    public Geographic( String name, String armies ) {
        this( name, List.of(), ArmyRef.parseList( armies ));
    }

    /** Get the parent region to which this region belongs */
    public Geographic getParent() {
        return parent;
    }

    /** Get the sub-regions of this region.* */
    public List<Geographic> getRegions() {
        return regions;
    }

    /** Get the name of this region */
    public String getName() {
        return name;
    }

    /** Get the names of the immediate sub-regions. */
    public List<String> getNames() {
        return regions.stream()
            .map( Geographic::getName )
            .toList();
    }

    /** Get all the region names of this region and below.
     * Use Geographic.WORLD.getAllNames to get all names in the entire tree. */
    public List<String> getAllNames() {
        return stream()
            .map( Geographic::getName )
            .toList();
    }

    /** Get the armies of this region.* */
    public List<ArmyRef> getArmies() {
        return armyRefs;
    }

    /** Get all the armies of this region and below
      * Use Geographic.WORLD.getAllArmies to get all armies in the entire tree.
      */
    public List<ArmyRef> getAllArmies() {
        return stream()
                .flatMap(g -> g.getArmies().stream())
                .collect( Collectors.toList());
    }

    @Override
    public int compareTo(Geographic that) {
        if (null == that) return -1;
        return this.name.compareTo( that.name);
    }

    @Override
    public boolean equals(Object other) {
        if ( other instanceof Geographic)
            return 0 == this.compareTo( (Geographic) other );
        return false;
    }

    /** Find a particular region by name. */
    // Consider a list that uses contains instead of equals/findFirst
    // List<ArmyRef> romans = findAll( "Rom" );
    public static Optional<Geographic> find(String geoName) {
        return Geographic.WORLD.stream()
                .filter( g -> geoName.equals( g.getName() ))
                .findFirst();
    }

    /**
     * A stream of Geographic nodes. Use map and forEach to manipulate.
     * @return a Stream of Geographic
     */
    public Stream<Geographic> stream() {
        return Stream.concat(
                Stream.of(this),
                regions.stream().flatMap(Geographic::stream)
        );
    }
}