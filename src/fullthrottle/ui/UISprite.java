package fullthrottle.ui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

import fullthrottle.util.TimeManager;

public class UISprite extends Sprite {
    private boolean visible;

    private float currentOpacity;

    private float fadeLength;
    private float currentFadeLength;
    private int fadeDirection;

    public UISprite(Texture t) {
        super(t);
        visible = true;
        currentOpacity = 255;

        fadeDirection = 0;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (visible)
            currentOpacity = 255;
    }

    public void fadeOut(float length) {
        fadeDirection = -1;
        fadeLength = length;
        currentFadeLength = 0;
    }

    public void fadeIn(float length) {
        setVisible(true);
        currentOpacity = 0;
        fadeDirection = 1;
        fadeLength = length;
        currentFadeLength = 0;
    }

    @Override
    public void draw(RenderTarget target, RenderStates rs) {
        if (!visible) return;

        if (fadeDirection != 0) {
            currentFadeLength += TimeManager.deltaTime();
            if (currentFadeLength >= fadeLength)
                currentFadeLength = fadeLength;

            currentOpacity = 255 * (currentFadeLength / fadeLength);
            if (fadeDirection < 0) {
                currentOpacity = 255 - currentOpacity;
            }

            if (currentFadeLength >= fadeLength)
                fadeDirection = 0;
        }

        if (currentOpacity == 0) {
            setVisible(false);
        }

        super.setColor(new Color(super.getColor(), (int)currentOpacity));

        super.draw(target, rs);
    }
}
