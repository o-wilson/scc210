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

        private FloatRect bounds;

        private float loopFrequency;

        public BackgroundElement(Sprite s, int z, float freq) {
            this.sprite = s;
            this.zIndex = z;
            this.bounds = sprite.getGlobalBounds();
            this.loopFrequency = freq;

            this.loopSprite = new Sprite(s.getTexture());
            loopSprite.setPosition(s.getPosition());
            loopSprite.setScale(s.getScale());
        }

        public void update(FloatRect view) {
            float dX = speed * TimeManager.deltaTime() / zIndex;
            sprite.move(dX * direction.directionMultiplier, 0);

            bounds = sprite.getGlobalBounds();

            boolean offscreenL = bounds.left < view.left - loopFrequency;
            boolean offscreenR = bounds.left >= view.left + view.width;
            boolean offscreen = offscreenL || offscreenR;

            if (offscreen) {
                sprite.move(loopFrequency * -direction.directionMultiplier, 0);
            }
        }

        public void draw(
            RenderTarget target, RenderStates states,
            FloatRect view
        ) {
            sprite.draw(target, states);

            float loopX = bounds.left;
            while (loopX <= view.left + view.width - loopFrequency) {
                loopSprite.setPosition(loopX + loopFrequency, bounds.top);
                loopSprite.draw(target, states);
                loopX = loopSprite.getGlobalBounds().left;
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
        addElement(s, zIndex, startPos, s.getGlobalBounds().width);
    }

    public void addElement(Sprite s, int zIndex, Vector2f startPos, float loopFrequency) {
        s.setPosition(startPos);
        BackgroundElement elem = new BackgroundElement(s, zIndex, loopFrequency);

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
        FloatRect vBounds = getViewRect();

        for (ArrayList<BackgroundElement> z : elements.values())
            for (BackgroundElement e : z)
                e.update(vBounds);
    }

    @Override
    public void draw(RenderTarget target, RenderStates states) {
        FloatRect vBounds = getViewRect();

        for (int i : zLayers)
            for (BackgroundElement e : elements.get(i))
                e.draw(target, states, vBounds);
    }

    private FloatRect getViewRect() {
        ConstView v = FullThrottle.getWindow().getView();
        Vector2f halfSize = Vector2f.div(v.getSize(), 2f);
        Vector2f vo = Vector2f.sub(v.getCenter(), halfSize);
        return new FloatRect(vo, v.getSize());
    }
}
