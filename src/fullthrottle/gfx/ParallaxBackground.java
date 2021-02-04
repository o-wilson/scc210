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

import org.jsfml.system.Vector2f;

import java.lang.RuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;

/**
 * Class for creating a parallaxed background
 * Has internal ordering layers but rendered on one layer by 
 * the main Renderer (could probably be changed in future if needed)
 */
public class ParallaxBackground implements Drawable, Updatable {
    
    /**
     * Thrown if the user attempts to add an element to a layer <= 0
     * 0 would cause a div by 0, -ve would move elements in the
     * opposite direction which isn't handled by the rendering
     */
    public class InvalidIndexException extends RuntimeException {
        /**
         *
         */
        private static final long serialVersionUID = -6567041962550312019L;

        public InvalidIndexException(int index) {
            super("Invalid Z-Index: " + index);
        }
    }

    /**
     * All data needed for an element to be rendered
     */
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

        /**
         * Called from super's update for each element,
         * updates position framerate-independently
         * @param view current viewrect of the render target -
         * used to make sure edges are looped if visible
         */
        public void update(FloatRect view) {
            float dX = speed * TimeManager.deltaTime() / zIndex;
            sprite.move(dX * direction.directionMultiplier, 0);

            bounds = sprite.getGlobalBounds();

            boolean offL = bounds.left < view.left - loopFrequency;
            boolean offR = bounds.left >= view.left + view.width;
            boolean offscreen = offL || offR;

            if (offscreen) {
                int d = direction.directionMultiplier;
                sprite.move(loopFrequency * -d, 0);
            }
        }

        /**
         * Draw element to RenderTarget and any copies
         * needed for looping/repeating
         * @param target RenderTarget to draw to
         * @param states RenderStates to use
         * @param view current viewrect of the target
         */
        public void draw(
            RenderTarget target, RenderStates states,
            FloatRect view
        ) {
            sprite.draw(target, states);

            int d = direction.directionMultiplier;
            float viewRight = view.left + view.width;
            float loopX = bounds.left;

            /*
             * if there is space (on the left or right) for another
             * instance to be drawn, only one of these is used, 
             * dependent on direction, hence &= direction
            */
            boolean spaceLeft = loopX <= viewRight - bounds.width;
            spaceLeft &= direction == Direction.LEFT;
            boolean spaceRight = loopX > view.left;
            spaceRight &= direction == Direction.RIGHT;

            /*
             * if space on the relevant side (dependent on direction)
             * continue drawing more instances until no more space
             */
            while (spaceLeft ^ spaceRight) {
                float newX = loopX - d * loopFrequency;
                loopSprite.setPosition(newX, bounds.top);
                loopSprite.draw(target, states);
                loopX = loopSprite.getGlobalBounds().left;
                //update space flags, no need to recheck direction
                spaceLeft &= loopX <= viewRight - bounds.width;
                spaceRight &= loopX > view.left;
            }
        }
    }

    /**
     * Controls the direction that the background scrolls in
     */
    public enum Direction {
        LEFT (-1),
        RIGHT(1)
        ;

        /**
         * used internally for update and draw calculations
         */
        public final int directionMultiplier;

        private Direction(int d) {
            directionMultiplier = d;
        }
    }

    private HashMap<Integer, ArrayList<BackgroundElement>> elements;
    private List<Integer> zLayers;

    private Direction direction;
    private float speed;

    private RenderTarget target;

    /**
     * Create a new parallax background with direction and speed
     * @param t RenderTarget for background to be drawn to
     * @param d Direction for background to move
     * @param s base speed multiplier for background elements to move
     * relative to
     */
    public ParallaxBackground(RenderTarget t, Direction d, float s) {
        //if negative speed given, invert direction and speed
        if (s < 0) {
            if (d == Direction.LEFT)
                d = Direction.RIGHT;
            else
                d = Direction.LEFT;

            s = -s;
        }

        this.target = t;
        this.direction = d;
        this.speed = s;

        elements = new HashMap<>();
        zLayers = new ArrayList<>();
    }

    /**
     * add an element to the background with default (sprite width)
     * loop frequency
     * @param s sprite to be drawn
     * @param zIndex layer to be drawn on (higher = further back)
     * CANNOT BE <= 0, will throw InvalidIndexException
     * @param startPos position for sprite to start at
     * @throws InvalidIndexException if index <= 0 given
     */
    public void addElement(
        Sprite s, int zIndex, Vector2f startPos
    ) {
        if (zIndex <= 0)
            throw new InvalidIndexException(zIndex);
            
        addElement(s, zIndex, startPos, s.getGlobalBounds().width);
    }

    /**
     * add an element to the background with a specified frequency
     * @param s sprite to be drawn
     * @param zIndex layer to be drawn on (higher = further back)
     * CANNOT BE <= 0, will throw InvalidIndexException
     * @param startPos position for sprite to start at
     * @param loopFrequency distance (including sprite width) between
     * looping elements
     * @throws InvalidIndexException if index <= 0 given
     */
    public void addElement(
        Sprite s, int zIndex, Vector2f startPos,
        float loopFrequency
    ) {
        if (zIndex <= 0)
            throw new InvalidIndexException(zIndex);

        s.setPosition(startPos);
        BackgroundElement elem = new BackgroundElement(
            s, zIndex, loopFrequency
        );

        //create new layer if first time use
        //sort layers to ensure correct render order maintained
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
        FloatRect vBounds = FullThrottle.getViewRect();

        for (ArrayList<BackgroundElement> z : elements.values())
            for (BackgroundElement e : z)
                e.update(vBounds);
    }

    @Override
    public void draw(RenderTarget target, RenderStates states) {
        FloatRect vBounds = FullThrottle.getViewRect();
        
        for (int i : zLayers)
            for (BackgroundElement e : elements.get(i))
                e.draw(target, states, vBounds);
    }
}
