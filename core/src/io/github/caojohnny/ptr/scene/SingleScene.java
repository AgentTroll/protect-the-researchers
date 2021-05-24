package io.github.caojohnny.ptr.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SingleScene implements Scene {
    private final Stage stage;

    public SingleScene() {
        this.stage = new Stage();
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = this.stage.getViewport();
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        this.stage.getViewport().apply();
        this.stage.act(deltaTime);
        this.stage.draw();
    }

    @Override
    public void dispose() {
        this.stage.dispose();
    }

    public Stage getStage() {
        return this.stage;
    }
}
