package fullthrottle.gfx;

import fullthrottle.FullThrottle;
import fullthrottle.util.TimeManager;
import fullthrottle.util.Updatable;

import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.View;

import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;

public class ParallaxBackground implements Drawable, Updatable {
    
    private class BackgroundElement {

        private Sprite sprite;
        private Sprite loopSprite;
        private int zIndex;

        public BackgroundElement(Sprite s, int z) {
            this.sprite = s;
            this.zIndex = z;

            this.loopSprite = new Sprite(s.getTexture());
            loopSprite.setPosition(s.getPosition());
            loopSprite.setScale(s.getScale());
        }

        public void update() {
            float dX = speed * TimeManager.deltaTime() / zIndex;
            sprite.move(dX * direction.directionMultiplier, 0);

            FloatRect b = sprite.getGlobalBounds();

            if (b.left <= -b.width) {
                sprite.move(b.width, 0);
            }
        }

        public void draw(
            RenderTarget target, RenderStates states,
            FloatRect view
        ) {
            FloatRect sB = sprite.getGlobalBounds();
            if (sB.left > view.left - sB.width)
                sprite.draw(target, states);

            if (sB.left <= view.left + view.width - sB.width) {
                loopSprite.setPosition(sB.left + sB.width, sB.top);
                loopSprite.draw(target, states);
            }
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
        ConstView v = FullThrottle.getWindow().getView();
        Vector2f halfSize = Vector2f.div(v.getSize(), 2f);
        Vector2f vo = Vector2f.sub(v.getCenter(), halfSize);
        FloatRect vBounds = new FloatRect(vo, v.getSize());

        for (int i : zLayers)
            for (BackgroundElement e : elements.get(i))
                e.draw(target, states, vBounds);
    }
}
