package io.github.agenttroll.ptr.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

// Abstract view class used to control a vertically
// split screen with two stages
// This class was designed to be built on top of, not
// to be used directly
public class SplitScene implements Scene {
    private SingleScene left;
    private SingleScene right;

    public SplitScene() {
        this.left = new SingleScene();
        this.right = new SingleScene();

        this.left.getStage().setViewport(createFitViewport());
        this.right.getStage().setViewport(createFitViewport());
        this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private static FitViewport createFitViewport() {
        float windowWidth = Gdx.graphics.getWidth();
        int windowHeight = Gdx.graphics.getHeight();

        // Vertically split; each window is half the full width of the window
        float halfWidth = windowWidth / 2;
        return new FitViewport(halfWidth, windowHeight);
    }

    @Override
    public void resize(int width, int height) {
        int halfWidth = width / 2;

        // Update the viewport and then change its dimensions
        // and offset from the bottom of the screen to prevent
        // the viewport from shifting around
        this.left.resize(halfWidth, height);
        Viewport leftViewport = this.left.getStage().getViewport();
        leftViewport.setScreenX(0);

        this.right.resize(halfWidth, height);
        Viewport rightViewport = this.right.getStage().getViewport();
        rightViewport.setScreenX(halfWidth);
    }

    @Override
    public void render() {
        this.left.render();
        this.right.render();
    }

    @Override
    public void dispose() {
        this.left.dispose();
        this.right.dispose();
    }

    public void setLeft(SingleScene left) {
        if (this.left != null) {
            this.left.dispose();
        }

        this.left = left;
        this.left.getStage().setViewport(createFitViewport());
        this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void setRight(SingleScene right) {
        if (this.right != null) {
            this.right.dispose();
        }

        this.right = right;
        this.right.getStage().setViewport(createFitViewport());
        this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public SingleScene getLeft() {
        return this.left;
    }

    public SingleScene getRight() {
        return this.right;
    }
}
