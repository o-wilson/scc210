package fullthrottle.gfx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;

public final class Renderer {
    
    private static RenderStates DEFAULT_RENDER_STATES = new RenderStates(BlendMode.ALPHA);
    private static int DEFAULT_RENDER_LAYER = 1;

    private static class RenderObject {
        public Drawable drawable;
        public RenderStates rs;

        public RenderObject(Drawable d, RenderStates rs) {
            this.drawable = d;
            this.rs = rs;
        }

        public void draw(RenderTarget target) {
            drawable.draw(target, rs);
        }
    }

    private static HashMap<Integer, ArrayList<RenderObject>> objects = new HashMap<>();
    private static List<Integer> renderLayers = new ArrayList<>();

    public static void render(RenderTarget target) {
        for (int i : renderLayers)
            for (RenderObject o : objects.get(i))
                o.draw(target);
    }

    public static void addDrawable(Drawable d) {
        addDrawable(d, DEFAULT_RENDER_LAYER, DEFAULT_RENDER_STATES);
    }

    public static void addDrawable(Drawable d, int rL) {
        addDrawable(d, rL, DEFAULT_RENDER_STATES);
    }

    public static void addDrawable(Drawable d, int rL, RenderStates rs) {
        RenderObject o = new RenderObject(d, rs);

        if (!objects.containsKey(rL)) {
            objects.put(rL, new ArrayList<>());

            renderLayers.add(rL);
            Collections.sort(renderLayers);
            Collections.reverse(renderLayers);
        }

        objects.get(rL).add(o);
    }
}
