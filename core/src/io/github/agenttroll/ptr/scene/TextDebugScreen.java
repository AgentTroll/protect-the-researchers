package io.github.agenttroll.ptr.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.agenttroll.ptr.actor.StaticTextActor;

public class TextDebugScreen extends SingleScene {
    public TextDebugScreen(String input) {
        StaticTextActor actor = new StaticTextActor(input);

        Stage stage = this.getStage();
        float x = stage.getWidth() / 2;
        float y = stage.getHeight() / 2;
        actor.setPosition(x, y);
        actor.setColor(Color.WHITE);
        stage.addActor(actor);
    }
}
