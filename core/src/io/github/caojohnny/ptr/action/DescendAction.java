package io.github.caojohnny.ptr.action;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

// Action that simply moves a sprite downwards at a
// fixed speed
public class DescendAction extends Action {
    private final float speed;

    public DescendAction(float speed) {
        this.speed = speed;
    }

    @Override
    public boolean act(float delta) {
        Actor actor = this.getActor();
        actor.setY(actor.getY() - (delta * speed));

        return actor.getY() <= 0;
    }
}
