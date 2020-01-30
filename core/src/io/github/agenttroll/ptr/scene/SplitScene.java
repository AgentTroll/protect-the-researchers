package io.github.agenttroll.ptr.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

// Abstract view class used to control a vertically
// split screen with two stages
// This class was designed to be built on top of, not
// to be used directly
public class SplitScene implements Scene {
    private final Stage left;
    private final Stage right;

    public SplitScene() {
        float windowWidth = Gdx.graphics.getWidth();
        int windowHeight = Gdx.graphics.getHeight();

        // Vertically split; each window is half the full width of the window
        float halfWidth = windowWidth / 2;
        this.left = new Stage(new FitViewport(halfWidth, windowHeight));
        this.right = new Stage(new FitViewport(halfWidth, windowHeight));
    }

    @Override
    public void resize(int width, int height) {
        int halfWidth = width / 2;

        // Update the viewport and then change its dimensions
        // and offset from the bottom of the screen to prevent
        // the viewport from shifting around

        Viewport leftViewport = this.left.getViewport();
        leftViewport.update(halfWidth, height, true);
        leftViewport.setScreenSize(halfWidth, height);
        leftViewport.setScreenX(0);

        Viewport rightViewport = this.right.getViewport();
        rightViewport.update(halfWidth, height, true);
        rightViewport.setScreenSize(halfWidth, height);
        rightViewport.setScreenX(halfWidth);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Switch into the context of both stage viewports
        // and perform the necessary actions and draw the
        // actors on screen

        this.left.getViewport().apply();
        this.left.act(deltaTime);
        this.left.draw();

        this.right.getViewport().apply();
        this.right.act(deltaTime);
        this.right.draw();
    }

    @Override
    public void dispose() {
        this.left.dispose();
        this.right.dispose();
    }

    public Stage getLeft() {
        return this.left;
    }

    public Stage getRight() {
        return this.right;
    }
}
