package io.github.caojohnny.ptr.scene;

import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.caojohnny.ptr.action.DescendAction;
import io.github.caojohnny.ptr.actor.ImageActor;
import io.github.caojohnny.ptr.game.PlayerData;
import io.github.caojohnny.ptr.protocol.in.StartRoundMsg;

public class InputScene extends SingleScene {
    private final ImageActor shapeActor;

    public InputScene(PlayerData data, StartRoundMsg msg) {
        Stage stage = this.getStage();
        float stageHeight = stage.getHeight();

        this.shapeActor = new ImageActor(msg.getShapeId().getImagePath());
        this.shapeActor.setY(stageHeight);

        long durationSec = msg.getRoundDurationMillis() / 1000;
        this.shapeActor.addAction(new DescendAction(stageHeight / durationSec));

        stage.addActor(this.shapeActor);

        drawHealth(stage, data);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        Stage stage = this.getStage();
        this.shapeActor.setX((stage.getWidth() / 2) - (this.shapeActor.getWidth() / 2));
    }

    public static void drawHealth(Stage stage, PlayerData data) {
        ImageActor health = new ImageActor("GameRunning/Health/Health" + data.getLivesRemaining() + ".png");
        health.setPosition(-70, stage.getHeight() - health.getHeight() + 70);
        stage.addActor(health);
    }
}
