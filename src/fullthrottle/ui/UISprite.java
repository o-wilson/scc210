package fullthrottle.ui;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

public class UISprite extends Sprite {
    private boolean visible;

    public UISprite(Texture t) {
        super(t);
        visible = true;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void draw(RenderTarget target, RenderStates rs) {
        if (!visible) return;

        super.draw(target, rs);
    }
}
