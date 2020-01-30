package io.github.agenttroll.ptr.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.agenttroll.ptr.actor.StaticTextActor;

public class EndScene extends SingleScene {
    public EndScene(boolean win) {
        String text;
        Color color;
        if (win) {
            text = "You won!";
            color = Color.GREEN;
        } else {
            text = "You lost! :))";
            color = Color.RED;
        }

        Stage stage = this.getStage();
        StaticTextActor textActor = new StaticTextActor(text);

        float halfWidth = stage.getWidth() / 2;
        float halfHeight = stage.getHeight() / 2;
        textActor.setPosition(halfWidth, halfHeight);
        textActor.setColor(color);

        stage.addActor(textActor);
    }
}
