package io.github.caojohnny.ptr.actor;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

// An actor representing static text that doesn't change over
// the lifetime of the actor
public class StaticTextActor extends TextActor {
    private final String text;
    private GlyphLayout cachedGlyphs;

    public StaticTextActor(String text) {
        this.text = text;
    }

    @Override
    protected GlyphLayout getCurrentText(BitmapFont font) {
        if (this.cachedGlyphs == null) {
            this.cachedGlyphs = new GlyphLayout(font, this.text);
        }

        return this.cachedGlyphs;
    }
}
