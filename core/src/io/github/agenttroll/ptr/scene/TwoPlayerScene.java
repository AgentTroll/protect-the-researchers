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

// Represents the game screen for 2 players
// playing against each other
public class TwoPlayerScene extends SplitScene {
    private final Game game;

    public TwoPlayerScene(Game game) {
        this.game = game;

        this.addArrowToStage(this.getLeft());
        this.addArrowToStage(this.getRight());
    }

    @Override
    public void render() {
        super.render();

        this.drawBox(this.getLeft());
        this.drawBox(this.getRight());
    }

    // Draws the little box at the bottom of the screen
    // representing the area where the input is supposed
    // to take place
    private void drawBox(Stage stage) {
        // Top of the box should be 10% of the screen above the bottom
        float lineH = stage.getHeight() * 0.1F;

        // Selects the viewport to draw the box on
        stage.getViewport().apply();

        Gdx.gl.glLineWidth(1); // Set the rendered line width
        Gdx.gl.glEnable(GL30.GL_BLEND); // Allow opacity changes

        // TODO: Possibly extract `renderer` to its own field
        // This performs the actual drawing of the box
        ShapeRenderer renderer = new ShapeRenderer();
        renderer.setProjectionMatrix(stage.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.WHITE.cpy().sub(0, 0, 0, 0.4F));
        renderer.line(1, 0, 1, lineH);
        renderer.line(1, lineH, stage.getWidth(), lineH);
        renderer.line(stage.getWidth(), lineH, stage.getWidth(), 0);
        renderer.end();

        Gdx.gl.glLineWidth(1); // Reset line width for other tasks
    }

    // Adds the arrow actor and adds the appropriate tasks
    // (actions) that control its movement and behavior
    private void addArrowToStage(Stage stage) {
        ArrowActor arrow = new ArrowActor();
        this.setupArrow(stage, arrow, 0.125F);
        stage.addActor(arrow);
    }

    // Adds the controller tasks (actions) to the given action
    // on the given stage
    private void setupArrow(Stage stage, Actor arrow, float pct) {
        float rX = stage.getWidth() * pct;
        arrow.addAction(new SpawnAction(rX, stage.getHeight()));
        arrow.addAction(sequence(new DescendAction(300F), run(() -> this.setupArrow(stage, arrow, pct))));
    }
}
