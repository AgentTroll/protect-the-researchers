package io.github.agenttroll.ptr.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.agenttroll.ptr.action.DescendAction;
import io.github.agenttroll.ptr.action.SpawnAction;
import io.github.agenttroll.ptr.actor.ArrowActor;
import io.github.agenttroll.ptr.game.Game;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class SinglePlayerScene extends SingleScene {
    private final Game game;

    public SinglePlayerScene(Game game) {
        this.game = game;

        this.addArrowToStage(this.getStage());
    }

    @Override
    public void render() {
        super.render();

        this.drawBox(this.getStage());
    }

    private void drawBox(Stage stage) {
        float lineH = stage.getHeight() * 0.1F;
        stage.getViewport().apply();

        Gdx.gl.glLineWidth(1);
        Gdx.gl.glEnable(GL30.GL_BLEND);

        ShapeRenderer renderer = new ShapeRenderer();
        renderer.setProjectionMatrix(stage.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.WHITE.cpy().sub(0, 0, 0, 0.4F));
        renderer.line(1, 0, 1, lineH);
        renderer.line(1, lineH, stage.getWidth(), lineH);
        renderer.line(stage.getWidth(), lineH, stage.getWidth(), 0);
        renderer.end();

        Gdx.gl.glLineWidth(1);
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
