package io.github.agenttroll.ptr.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;

// Abstract actor superclass for text elements to be shown
// on the stage using the font bundled in the jar
public abstract class TextActor extends Actor {
    private static final BitmapFont FONT;

    static {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/SWTxt.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 24;

        FONT = generator.generateFont(param);
        generator.dispose();

        // https://stackoverflow.com/questions/33633395/how-set-libgdx-bitmap-font-size
        FONT.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = this.getColor();
        FONT.setColor(color.r, color.g, color.b, parentAlpha);

        GlyphLayout text = this.getCurrentText(FONT);
        int xOff = this.isCentered() ? (int) text.width / 2 : 0;
        int yOff = this.isCentered() ? (int) text.height / 2 : 0;
        FONT.draw(batch, text, this.getX() - xOff, this.getY() - yOff);
    }

    protected abstract GlyphLayout getCurrentText(BitmapFont font);

    protected boolean isCentered() {
        return true;
    }
}
