package info.danbecker.dba;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public enum TerrainType {
    ARABLE(),
    FOREST(),
    HILLY(),
    STEPPE(),
    DRY(),
    TROPICAL(),
    LITTORAL();

    public String initCap() {
        String name = name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
    public String abbr() {
        return name().charAt(0) + name().substring(1,2).toLowerCase();
    }

    /**
     * A slightly less restrictive valueOf that converts to upper case
     * or throws IllegalArgumentException
     * @param terrainType a case-insensitve string (may have lower case)
     * @return a TerrainType or throw an illegal argument exception
     */
    public static TerrainType fromString( String terrainType ) {
        return TerrainType.valueOf( terrainType.toUpperCase() );
    }

    /**
     * Support list of TerrainType from "/" delimited String
     * such as "Arable/Littoral/Forest"
     * or throws IllegalArgumentException
     * @param terrainTypes "/" delimited Strings
     * @return list of TerrainType or throw an illegal argument exception
     */
    public static List<TerrainType> listFromString(String terrainTypesStr ) {
        List<TerrainType> terrainTypes = new ArrayList<>();
        return Stream.of( terrainTypesStr.split("/"))
                .map ( TerrainType::fromString )
                .toList();
    }
}
