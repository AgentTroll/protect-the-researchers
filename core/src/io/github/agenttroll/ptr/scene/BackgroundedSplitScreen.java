package io.github.agenttroll.ptr.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BackgroundedSplitScreen extends SplitScene {
    private SingleScene background;

    public SingleScene getBackground() {
        return this.background;
    }

    public void setBackground(SingleScene scene) {
        if (this.background != null) {
            this.background.dispose();
        }

        this.background = scene;
        this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        if (this.background != null) {
            this.background.resize(width, height);

            Stage stage = this.background.getStage();
            Viewport viewport = stage.getViewport();
            viewport.setScreenX(0);
        }

        super.resize(width, height);
    }

    @Override
    public void render() {
        if (this.background != null) {
            this.background.render();
        }

        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();

        if (this.background != null) {
            this.background.dispose();
        }
    }
}
