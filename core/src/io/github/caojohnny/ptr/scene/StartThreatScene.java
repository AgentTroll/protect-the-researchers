package io.github.caojohnny.ptr.scene;

import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.caojohnny.ptr.actor.ImageActor;
import io.github.caojohnny.ptr.game.Threat;

public class StartThreatScene extends AnimatedSingleScene {
    public StartThreatScene(Threat threat) {
        super(threat.getAssetName(), threat.getAtlasPath());
        ImageActor foreground = new ImageActor(threat.getImagePath());
        foreground.setPosition(0, 0);

        Stage stage = this.getStage();
        foreground.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(foreground);
    }
}
