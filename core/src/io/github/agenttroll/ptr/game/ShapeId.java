package io.github.agenttroll.ptr.game;

public enum ShapeId {
    SQUARE("Square"),
    C("C"),
    TRIANGLE("Triangle"),
    T("T");

    private static final String ASSET_PREFIX = "GameRunning/RoundRunning/Shapes/";
    private static final String ASSET_SUFFIX = ".png";

    private final String assetName;
    private final String imagePath;

    ShapeId(String assetName) {
        this.assetName = assetName;
        this.imagePath = ASSET_PREFIX + assetName + ASSET_SUFFIX;
    }

    public String getAssetName() {
        return this.assetName;
    }

    public String getImagePath() {
        return this.imagePath;
    }
}
