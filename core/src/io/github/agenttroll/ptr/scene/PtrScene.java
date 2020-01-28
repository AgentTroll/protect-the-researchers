package io.github.agenttroll.ptr.scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.agenttroll.ptr.action.DescendAction;
import io.github.agenttroll.ptr.action.SpawnAction;
import io.github.agenttroll.ptr.actor.ArrowActor;
import io.github.agenttroll.ptr.comm.Remote;
import io.github.agenttroll.ptr.game.PtrListener;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class PtrScene extends SplitScene {
    private final Remote remote;

    public PtrScene(Remote remote) {
        this.remote = remote;
        this.remote.addListener(new PtrListener());

        this.addArrowToStage(this.getLeft());
        this.addArrowToStage(this.getRight());
    }

    private void addArrowToStage(Stage stage) {
        ArrowActor arrow = new ArrowActor();
        this.setupArrow(stage, arrow, 0.125F);
        stage.addActor(arrow);
    }

    private void setupArrow(Stage stage, Actor arrow, float pct) {
        float rX = stage.getWidth() * pct;
        arrow.addAction(new SpawnAction(rX, stage.getHeight()));
        arrow.addAction(sequence(new DescendAction(300F), run(() -> this.setupArrow(stage, arrow, pct))));
    }
}
