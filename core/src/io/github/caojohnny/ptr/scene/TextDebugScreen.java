package io.github.caojohnny.ptr.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.caojohnny.ptr.actor.StaticTextActor;

public class TextDebugScreen extends SingleScene {
    private final StaticTextActor actor;

    public TextDebugScreen(String input) {
        this.actor = new StaticTextActor(input);

        Stage stage = this.getStage();
        float x = stage.getWidth() / 2;
        float y = stage.getHeight() / 2;
        this.actor.setPosition(x, y);
        this.actor.setColor(Color.WHITE);
        stage.addActor(actor);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        Stage stage = this.getStage();
        float x = stage.getWidth() / 2;
        float y = stage.getHeight() / 2;
        actor.setPosition(x, y);
    }
}
