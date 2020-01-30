package io.github.agenttroll.ptr;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import io.github.agenttroll.ptr.comm.Remote;
import io.github.agenttroll.ptr.game.Game;
import io.github.agenttroll.ptr.game.GamePhase;
import io.github.agenttroll.ptr.scene.Scene;
import io.github.agenttroll.ptr.scene.StartScene;

public class ProtectTheResearchers extends ApplicationAdapter {
    private Game game;

    public ProtectTheResearchers(String leftPort, String rightPort) {
        Remote leftRemote = new Remote(leftPort);
        Remote rightRemote = new Remote(rightPort);

        this.game = new Game(leftRemote, rightRemote);
    }

    @Override
    public void create() {
        this.game.setPhase(GamePhase.START);
        this.game.setCurrentScene(new StartScene());
    }

    @Override
    public void resize(int width, int height) {
        Scene scene = this.game.getCurrentScene();
        scene.resize(width, height);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Scene scene = this.game.getCurrentScene();
        scene.render();
    }

    @Override
    public void dispose() {
        Scene scene = this.game.getCurrentScene();
        scene.dispose();

        this.game.getLeftRemote().dispose();
        this.game.getRightRemote().dispose();
    }
}
