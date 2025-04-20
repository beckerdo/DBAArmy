package info.danbecker.dba;

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
}
