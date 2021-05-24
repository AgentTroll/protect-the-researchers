package io.github.caojohnny.ptr.scene;

import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.caojohnny.ptr.actor.ImageActor;
import io.github.caojohnny.ptr.game.PlayerData;

public class InputResponseScene extends StaticImageScene {
    public InputResponseScene(PlayerData data, boolean correct) {
        super(correct ? "GameRunning/RoundEnd/Success.png" : "GameRunning/RoundEnd/Failure.png");

        InputScene.drawHealth(this.getStage(), data);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        ImageActor actor = this.getActor();
        actor.setSize(width, height / 2);

        Stage stage = this.getStage();
        actor.setPosition(0, (stage.getHeight() / 2) - (actor.getHeight() / 2));
    }
}
