package io.github.caojohnny.ptr.scene;

import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.caojohnny.ptr.actor.ImageActor;

public class StaticImageScene extends SingleScene {
    private final ImageActor actor;

    public StaticImageScene(String imagePath) {
        this.actor = new ImageActor(imagePath);
        this.actor.setPosition(0, 0);

        Stage stage = this.getStage();
        this.actor.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(this.actor);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.actor.setSize(width, height);
    }

    public ImageActor getActor() {
        return this.actor;
    }
}
