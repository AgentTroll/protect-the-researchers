package io.github.agenttroll.ptr;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.agenttroll.ptr.action.DescendAction;
import io.github.agenttroll.ptr.action.SpawnAction;
import io.github.agenttroll.ptr.actor.ArrowActor;

import java.util.concurrent.ThreadLocalRandom;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class ProtectTheResearchers extends ApplicationAdapter {
 	private Stage stage;

	@Override
	public void create() {
		this.stage = new Stage(new ScreenViewport());

		for (int i = 0; i < 3; i++) {
			ArrowActor arrow = new ArrowActor();
			this.setupArrow(arrow);
			stage.addActor(arrow);
		}
	}

	@Override
	public void resize(int width, int height) {
	    this.stage.getViewport().update(width, height, true);
	}

	@Override
	public void render() {
		// Gdx.gl.glClearColor(1, 0, 0, 1);

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float deltaTime = Gdx.graphics.getDeltaTime();
		this.stage.act(deltaTime);

		this.stage.draw();
	}
	
	@Override
	public void dispose() {
	    this.stage.dispose();
	}

	private void setupArrow(Actor arrow) {
		float rX = ThreadLocalRandom.current().nextFloat() * this.stage.getWidth();
		arrow.addAction(new SpawnAction(rX, this.stage.getHeight() - 1));
		arrow.addAction(sequence(new DescendAction(300F), run(() -> this.setupArrow(arrow))));
	}
}
