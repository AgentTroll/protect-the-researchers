package io.github.agenttroll.ptr;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import io.github.agenttroll.ptr.comm.Remote;
import io.github.agenttroll.ptr.scene.PtrScene;

public class ProtectTheResearchers extends ApplicationAdapter {
	private PtrScene scene;

	@Override
	public void create() {
		this.scene = new PtrScene();

		Remote remote = new Remote();
	}

	@Override
	public void resize(int width, int height) {
		this.scene.resize(width, height);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		this.scene.render();
	}
	
	@Override
	public void dispose() {
	    this.scene.dispose();
	}
}
