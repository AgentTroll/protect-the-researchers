package io.github.agenttroll.ptr.scene;

import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.agenttroll.ptr.actor.ImageActor;

// Performs the setup needed to display the initial
// start screen on the window
public class MenuScene extends SingleScene {
    public MenuScene() {
        Stage stage = this.getStage();

        ImageActor actor = new ImageActor("start.png");
        actor.setPosition(0, 0);
        actor.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(actor);
    }
}
