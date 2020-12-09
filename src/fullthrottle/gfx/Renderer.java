package fullthrottle.gfx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;

<<<<<<< HEAD
=======
/**
 * Class for handling rendering, including ordering of sprites
 * No need to instantiate - all methods static
 */
>>>>>>> parallax
public final class Renderer {
    
    private static RenderStates DEFAULT_RENDER_STATES = new RenderStates(BlendMode.ALPHA);
    private static int DEFAULT_RENDER_LAYER = 1;

<<<<<<< HEAD
    private static class RenderObject {
        public Drawable drawable;
        public RenderStates rs;

=======
    /**
     * Holds all required data for an object to be rendered
     */
    private static class RenderObject {
        /**
         * Element to be drawn
         */
        public Drawable drawable;
        /**
         * Render states to be passed to Drawable.draw
         */
        public RenderStates rs;

        /**
         * Called from addDrawable
         * @param d object implementing JSFML's Drawable
         * @param rs RenderStates for drawing
         */
>>>>>>> parallax
        public RenderObject(Drawable d, RenderStates rs) {
            this.drawable = d;
            this.rs = rs;
        }

<<<<<<< HEAD
=======
        /**
         * Invokes the drawables draw method
         * @param target RenderTarget to be drawn to (e.g. RenderWindow)
         */
>>>>>>> parallax
        public void draw(RenderTarget target) {
            drawable.draw(target, rs);
        }
    }

<<<<<<< HEAD
    private static HashMap<Integer, ArrayList<RenderObject>> objects = new HashMap<>();
    private static List<Integer> renderLayers = new ArrayList<>();

=======
    /**
     * Stores ArrayLists of objects to render,
     * indexed by order to be rendered (higher index = further "back")
     */
    private static HashMap<Integer, ArrayList<RenderObject>> objects = new HashMap<>();

    /**
     * List of render layers to ensure layers are drawn in order.
     * Sorted whenever a new layer is added/used
     */
    private static List<Integer> renderLayers = new ArrayList<>();

    /**
     * Loops through the HashMap, ordered by the renderLayers List
     * Draws from back to front
     * @param target RenderTarget to draw to
     */
>>>>>>> parallax
    public static void render(RenderTarget target) {
        for (int i : renderLayers)
            for (RenderObject o : objects.get(i))
                o.draw(target);
    }

<<<<<<< HEAD
=======
    /**
     * Add a Drawable object to be rendered,
     * uses default render layer (1) and render states (alpha)
     * @param d object to be drawn
     */
>>>>>>> parallax
    public static void addDrawable(Drawable d) {
        addDrawable(d, DEFAULT_RENDER_LAYER, DEFAULT_RENDER_STATES);
    }

<<<<<<< HEAD
=======
    /**
     * Add a Drawable object to be rendered,
     * uses default render states (alpha)
     * @param d object to be drawn
     * @param rL layer to assign object to (higher = further back)
     */
>>>>>>> parallax
    public static void addDrawable(Drawable d, int rL) {
        addDrawable(d, rL, DEFAULT_RENDER_STATES);
    }

<<<<<<< HEAD
    public static void addDrawable(Drawable d, int rL, RenderStates rs) {
        RenderObject o = new RenderObject(d, rs);

=======
    /**
     * Add a Drawable object to be rendered
     * @param d object to be drawn
     * @param rL layer to assign object to (higher = further back)
     * @param rs RenderStates to use when drawing the object
     */
    public static void addDrawable(Drawable d, int rL, RenderStates rs) {
        RenderObject o = new RenderObject(d, rs);

        //add empty list to hashmap when a new index is used
        //sort list of indices to ensure rendering order is maintained
>>>>>>> parallax
        if (!objects.containsKey(rL)) {
            objects.put(rL, new ArrayList<>());

            renderLayers.add(rL);
            Collections.sort(renderLayers);
            Collections.reverse(renderLayers);
        }

        objects.get(rL).add(o);
    }
}
