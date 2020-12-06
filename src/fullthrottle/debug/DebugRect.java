package fullthrottle.debug;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;

import org.jsfml.system.Vector2f;

import fullthrottle.FullThrottle;

/**
 * A class for debugging the position of objects
 * by highlighting their border
 */
public class DebugRect extends RectangleShape {
    public DebugRect(FloatRect bounds) {
        this(bounds.left, bounds.top, bounds.width, bounds.height);
    }

    public DebugRect(float x, float y, float w, float h) {
        super();
        setSize(new Vector2f(w, h));
        setPosition(x, y);
        setFillColor(Color.TRANSPARENT);
        setOutlineColor(Color.RED);
        setOutlineThickness(3);
    }

    @Override
    public void draw(RenderTarget target, RenderStates states) {
        super.draw(
            FullThrottle.getWindow(),
            new RenderStates(BlendMode.ALPHA)
        );
    }
}
