package io.github.caojohnny.ptr.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.caojohnny.ptr.platform.Platform;

public class AnimatedSingleScene extends SingleScene {
    private final TextureAtlas atlas;
    private final Array<TextureAtlas.AtlasRegion> regionArr;

    private final Animation<TextureRegion> animation;
    private final SpriteBatch spriteBatch = new SpriteBatch();
    private float curTime;

    public AnimatedSingleScene(String regionName, String atlasPath) {
        this.atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        this.regionArr = atlas.findRegions(regionName);
        this.animation = new Animation<>(Platform.ANIMATION_INTERVAL_SEC,
                regionArr,
                Animation.PlayMode.LOOP);
    }

    @Override
    public void render() {
        Stage stage = this.getStage();
        float deltaTime = Gdx.graphics.getDeltaTime();
        this.curTime += deltaTime;
        TextureRegion menuFrame = this.animation.getKeyFrame(this.curTime);

        Viewport viewport = stage.getViewport();
        viewport.apply();

        stage.act(deltaTime);
        this.spriteBatch.begin();
        this.spriteBatch.draw(menuFrame, 0, 0, stage.getWidth(), stage.getHeight());
        this.spriteBatch.end();
        stage.draw();

        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        this.atlas.dispose();
    }
}
