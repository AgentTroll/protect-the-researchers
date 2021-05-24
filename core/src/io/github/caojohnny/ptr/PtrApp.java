package io.github.caojohnny.ptr;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import io.github.caojohnny.ptr.comm.ArduinoRemote;
import io.github.caojohnny.ptr.comm.DummyRemote;
import io.github.caojohnny.ptr.comm.Remote;
import io.github.caojohnny.ptr.game.Game;
import io.github.caojohnny.ptr.scene.Scene;

public class PtrApp extends ApplicationAdapter {
    private Game game;

    public PtrApp(String leftPort, String rightPort) {
        Remote leftRemote = initRemote(leftPort);
        Remote rightRemote = initRemote(rightPort);

        this.game = new Game(leftRemote, rightRemote);
    }

    private static Remote initRemote(String port) {
        if (port.contains("dummy")) {
            return new DummyRemote(port);
        } else {
            return new ArduinoRemote(port);
        }
    }

    @Override
    public void create() {
        // Initialize with the starting game state
        this.game.initGame();

        // Set the cursor to invisible
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
        pm.dispose();
    }

    @Override
    public void resize(int width, int height) {
        Scene scene = this.game.getCurrentScene();
        scene.resize(width, height);
    }

    @Override
    public void render() {
        // Clear the screen to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render the scene on top of the window
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
