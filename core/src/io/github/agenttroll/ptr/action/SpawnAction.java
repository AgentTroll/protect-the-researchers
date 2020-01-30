package io.github.agenttroll.ptr.action;

import com.badlogic.gdx.scenes.scene2d.Action;

// Action that moves the action to the given "spawn"
// position, assuming that it is visible
public class SpawnAction extends Action {
    private final float spawnX;
    private final float spawnY;

    public SpawnAction(float spawnX, float spawnY) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    @Override
    public boolean act(float delta) {
        this.getActor().setPosition(this.spawnX, this.spawnY);
        return true;
    }
}
