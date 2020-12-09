package fullthrottle.gfx;

import fullthrottle.util.TimeManager;
import fullthrottle.util.Updatable;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.Sprite;

import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;

public class ParallaxBackground implements Drawable, Updatable {
    
    private class BackgroundElement implements Drawable {

        private Sprite sprite;
        private int zIndex;

        public BackgroundElement(Sprite s, int z) {
            this.sprite = s;
            this.zIndex = z;
        }

        public void update() {
            float dX = (1f / zIndex) * speed * TimeManager.deltaTime();
            sprite.move(dX * direction.directionMultiplier, 0);
        }

        public void draw(RenderTarget target, RenderStates states) {
            sprite.draw(target, states);
        }
    }

    public enum Direction {
        LEFT (-1),
        RIGHT(1)
        ;

        public final int directionMultiplier;

        private Direction(int d) {
            directionMultiplier = d;
        }
    }

    private HashMap<Integer, ArrayList<BackgroundElement>> elements;
    private List<Integer> zLayers;

    private Direction direction;
    private float speed;

    public ParallaxBackground(Direction d, float s) {
        this.direction = d;
        this.speed = s;

        elements = new HashMap<>();
        zLayers = new ArrayList<>();
    }

    public void addElement(Sprite s, int zIndex, Vector2f startPos) {
        s.setPosition(startPos);
        BackgroundElement elem = new BackgroundElement(s, zIndex);

        if (!elements.containsKey(zIndex)) {
            elements.put(zIndex, new ArrayList<>());

            zLayers.add(zIndex);
            Collections.sort(zLayers);
            Collections.reverse(zLayers);
        }
        
        elements.get(zIndex).add(elem);
    }

    @Override
    public void update() {
        for (ArrayList<BackgroundElement> z : elements.values())
            for (BackgroundElement e : z)
                e.update();
    }

    @Override
    public void draw(RenderTarget target, RenderStates states) {
        for (int i : zLayers)
            for (BackgroundElement e : elements.get(i))
                e.draw(target, states);
    }
}
