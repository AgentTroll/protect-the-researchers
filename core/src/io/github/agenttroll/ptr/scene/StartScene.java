package io.github.agenttroll.ptr.scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.agenttroll.ptr.actor.ImageActor;

public class StartScene extends SingleScene {
    public StartScene() {
        Stage stage = this.getStage();

        ImageActor actor = new ImageActor("start.png");
        actor.setPosition(0, 0);
        stage.addActor(actor);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        Stage stage = this.getStage();
        for (Actor actor : stage.getActors()) {
            actor.setSize(stage.getWidth(), stage.getHeight());
        }
    }
}
