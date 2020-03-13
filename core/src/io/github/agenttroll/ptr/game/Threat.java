package io.github.agenttroll.ptr.game;

public enum Threat {
    DIAMOND_RAIN("DiamondRain"),
    ROUGH_SEA("RoughSea"),
    WIND_STORM("WindStorm");

    private static final String ROUND_START_PREFIX = "GameRunning/RoundStart/";
    private static final String ROUND_START_SUFFIX = ".png";
    private static final String ATLAS_PREFIX = "Output/";
    private static final String ATLAS_SUFFIX = ".atlas";

    private final String assetName;
    private final String imagePath;
    private final String atlasPath;

    Threat(String assetName) {
        this.assetName = assetName;
        this.imagePath = ROUND_START_PREFIX + assetName + ROUND_START_SUFFIX;
        this.atlasPath = ATLAS_PREFIX + assetName + ATLAS_SUFFIX;
    }

    public String getAssetName() {
        return this.assetName;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public String getAtlasPath() {
        return this.atlasPath;
    }
}
