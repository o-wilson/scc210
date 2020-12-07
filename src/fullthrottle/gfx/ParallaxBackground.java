package fullthrottle.gfx;

import fullthrottle.util.Updatable;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.Sprite;

import java.util.ArrayList;
import java.util.HashMap;

public class ParallaxBackground implements Drawable, Updatable {
    
    private class BackgroundElement implements Drawable {

        private Sprite sprite;
        private int zIndex;

        public BackgroundElement(Sprite s, int z) {
            this.sprite = s;
            this.zIndex = z;
        }

        public void update(float dTime) {
            // TODO: move element
        }

        public void draw(RenderTarget target, RenderStates states) {
            sprite.draw(target, states);
        }
    }

    public enum Direction {
        LEFT,
        RIGHT
    }

    private HashMap<Integer, ArrayList<BackgroundElement>> elements;

    private Direction direction;
    private float speed;

    public ParallaxBackground(Direction d, float s) {
        this.direction = d;
        this.speed = s;

        elements = new HashMap<>();
    }

    public void addElement(Sprite s, int zIndex) {
        BackgroundElement elem = new BackgroundElement(s, zIndex);
        if (!elements.containsKey(zIndex))
            elements.put(zIndex, new ArrayList<>());
        elements.get(zIndex).add(elem);
    }

    @Override
    public void update(float dTime) {
        for (ArrayList<BackgroundElement> z : elements.values())
            for (BackgroundElement e : elements.get(z))
                e.update(dTime);
    }

    @Override
    public void draw(RenderTarget target, RenderStates states) {
        for (Integer i : elements.keySet()) {
            for (BackgroundElement e : elements.get(i)) {
                e.draw(target, states);
            }
        }
    }
}
