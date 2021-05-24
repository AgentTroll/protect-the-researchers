package io.github.caojohnny.ptr.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

// Superclass for static image sprites bundled in the jar
public class ImageActor extends Actor {
    private final TextureRegion tr;

    public ImageActor(String texturePath) {
        Texture tex = new Texture(texturePath);
        this.tr = new TextureRegion(tex);

        this.setBounds(this.tr.getRegionX(), this.tr.getRegionY(),
                this.tr.getRegionWidth(), this.tr.getRegionHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = this.getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(this.tr, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(),
                this.getWidth(), this.getHeight(), this.getScaleX(), this.getScaleY(), this.getRotation());
    }
}
