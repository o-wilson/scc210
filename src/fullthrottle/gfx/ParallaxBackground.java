package fullthrottle.gfx;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Image;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector3f;

import fullthrottle.util.Updatable;

/**
 * Class for creating a parallaxed background Has internal ordering layers but
 * rendered on one layer by the main Renderer (could probably be changed in
 * future if needed)
 */
public class ParallaxBackground implements Drawable, Updatable {

    /**
     * Thrown if the user attempts to add an element to a layer <= 0 0 would cause a
     * div by 0, -ve would move elements in the opposite direction which isn't
     * handled by the rendering
     */
    public class InvalidIndexException extends RuntimeException {
        public InvalidIndexException(int index) {
            super("Invalid Z-Index: " + index);
        }
    }

    /**
     * All data needed for an element to be rendered
     */
    private class BackgroundElement {

        private int zIndex;
        private FloatRect bounds;

        private int globalTextureIndex;
        private FloatRect textureCoords;

        private float loopFrequency;

        VertexArray va;

        public BackgroundElement(int z, float freq, Vector2f startPos, Vector2f scale, int globalTextureIndex,
                FloatRect textureCoords, Drawable d) {
            this.zIndex = z;
            Vector2f size = new Vector2f(textureCoords.width, textureCoords.height);
            size = Vector2f.componentwiseMul(size, scale);

            this.bounds = new FloatRect(startPos, size);
            this.loopFrequency = freq;

            this.va = new VertexArray();

        }

        /**
         * Called from main class's update for each element, updates position
         * framerate-independently
         * 
         * @param view current viewrect of the render target - used to make sure edges
         *             are looped if visible
         */
        public void update(FloatRect view) {
            // float dX = speed * TimeManager.deltaTime() / zIndex;
            // sprite.move(dX * direction.directionMultiplier, 0);

            // bounds = sprite.getGlobalBounds();

            // boolean offL = bounds.left < view.left - loopFrequency;
            // boolean offR = bounds.left >= view.left + view.width;
            // boolean offscreen = offL || offR;

            // if (offscreen) {
            // int d = direction.directionMultiplier;
            // sprite.move(loopFrequency * -d, 0);
            // }
        }

        /**
         * Draw element to RenderTarget and any copies needed for looping/repeating
         * 
         * @param target RenderTarget to draw to
         * @param states RenderStates to use
         * @param view   current viewrect of the target
         */
        public void draw(RenderTarget target, RenderStates states, FloatRect view) {
            // sprite.draw(target, states);

            // int d = direction.directionMultiplier;
            // float viewRight = view.left + view.width;
            // float loopX = bounds.left;

            // /*
            // * if there is space (on the left or right) for another
            // * instance to be drawn
            // * only one of these is used, dependent on direction,
            // * hence &= direction
            // */
            // boolean spaceLeft = loopX <= viewRight - bounds.width;
            // spaceLeft &= direction == Direction.LEFT;
            // boolean spaceRight = loopX > view.left;
            // spaceRight &= direction == Direction.RIGHT;

            // /*
            // * if space on the relevant side (dependent on direction)
            // * continue drawing more instances until no more space
            // */
            // while (spaceLeft ^ spaceRight) {
            // float newX = loopX - d * loopFrequency;
            // loopSprite.setPosition(newX, bounds.top);
            // loopSprite.draw(target, states);
            // loopX = loopSprite.getGlobalBounds().left;
            // // update space flags, no need to recheck direction
            // spaceLeft &= loopX <= viewRight - bounds.width;
            // spaceRight &= loopX > view.left;
            // }
        }
    }

    /**
     * Controls the direction that the background scrolls in
     */
    public enum Direction {
        LEFT(-1), RIGHT(1);

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

    private ArrayList<Texture> globalTextures;
    private ArrayList<VertexArray> verticesToDraw;

    /**
     * Create a new parallax background with direction and speed
     * 
     * @param t RenderTarget for background to be drawn to
     * @param d Direction for background to move
     * @param s base speed multiplier for background elements to move relative to
     */
    public ParallaxBackground(RenderTarget t, Direction d, float s) {
        // if negative speed given, invert direction and speed
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

        this.globalTextures = new ArrayList<>();
        Texture tex = new Texture();
        try {
            tex.create(1, 1);
        } catch (TextureCreationException e) {
            e.printStackTrace();
        }
        globalTextures.add(tex);

        this.verticesToDraw = new ArrayList<>();
    }

    public void addElement(Sprite s, int zIndex, Vector2f startPos) {
        addElement(s, zIndex, startPos, s.getGlobalBounds().width);
    }

    public void addElement(Sprite s, int zIndex, Vector2f startPos, float loopFrequency) {
        // get texture + rect

        Texture t = (Texture) s.getTexture();
        IntRect texRect = s.getTextureRect();

        // add texture to global (returns Vector3f - (x,y,i))

        Vector3f texPos = addToGlobal(t);
        // System.out.println(texPos);

        // get sprite bounds

        FloatRect bounds = s.getGlobalBounds();

        // create BackgroundElement with bounds, textureID, texture Coords, zIndex,
        // loopFrequency
    }

    public void addElement(VertexArray v, int zIndex) {
        addElement(v, zIndex, v.getBounds().width);
    }

    public void addElement(VertexArray v, int zIndex, float loopFrequency) {
        // create BackgroundElement with VertexArray, zIndex, loopFrequency
    }

    public void addElement(VertexArray v, int zIndex, Texture t) {
        Vector2f size = new Vector2f(t.getSize());
        addElement(v, zIndex, t, new FloatRect(Vector2f.ZERO, size));
    }

    public void addElement(VertexArray v, int zIndex, Texture t, FloatRect texRect) {
        addElement(v, zIndex, v.getBounds().width, t, texRect);
    }

    public void addElement(VertexArray v, int zIndex, float loopFrequency, Texture t, FloatRect texRect) {
        // add texture to global (returns Vector3f - (x,y,i))

        // create BackgroundElement with VertexArray, textureID, texture Coords, zIndex,
        // loopFrequency
    }

    private Vector3f addToGlobal(Texture t) {
        int currentGlobal = globalTextures.size() - 1;
        Vector2i gSize = globalTextures.get(currentGlobal).getSize();
        Vector2i tSize = t.getSize();

        // Calculate if current global has enough space for new texture
        // (assumes placed naively horizontally adjacent)
        if (gSize.x + tSize.x > Texture.getMaximumSize()) {
            Texture nextGlobal = new Texture();
            try {
                nextGlobal.create(1, 1);
                globalTextures.add(nextGlobal);
                currentGlobal++;
            } catch (TextureCreationException e) {
                e.printStackTrace();
                return null;
            }
        }

        Texture globalTexture = globalTextures.get(currentGlobal);
        Image gImage = globalTexture.copyToImage();
        BufferedImage buffGImage = gImage.toBufferedImage();

        BufferedImage newGlobal = addTextureToBufferedImage(
            buffGImage, t
        );

        try {
            ImageIO.write(newGlobal, "png", new File("./test/globalTexture_" + currentGlobal + ".png"));
            // System.out.println("updated global texture file");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Texture newGlobalTexture = new Texture();
        Image newGlobalImage = new Image();
        newGlobalImage.create(newGlobal);
        try {
            newGlobalTexture.loadFromImage(newGlobalImage);
        } catch (TextureCreationException e) {
            e.printStackTrace();
        }
        globalTextures.set(currentGlobal, newGlobalTexture);

        float startX = buffGImage.getWidth();
        float startY = 0;
        return new Vector3f(startX, startY, currentGlobal);
    }

    private BufferedImage addTextureToBufferedImage(
        BufferedImage source, Texture next
    ) {
        Image nImg = next.copyToImage();
        BufferedImage nextImage = nImg.toBufferedImage();
        int totalWidth = source.getWidth();
        int totalHeight = source.getHeight();
        
        totalWidth += nextImage.getWidth();
        totalHeight = Math.max(totalHeight, nextImage.getHeight());

        BufferedImage newImage = new BufferedImage(
            totalWidth, totalHeight,
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(source, 0, 0, null);
        g2d.drawImage(nextImage, source.getWidth(), 0, null);
        g2d.dispose();

        return newImage;
    }

    @Override
    public void update() {
        FloatRect vBounds = getViewRect(this.target);

        for (ArrayList<BackgroundElement> z : elements.values())
            for (BackgroundElement e : z)
                e.update(vBounds);
    }

    @Override
    public void draw(RenderTarget target, RenderStates states) {
        FloatRect vBounds = getViewRect(this.target);
        
        for (int i : zLayers)
            for (BackgroundElement e : elements.get(i))
                e.draw(target, states, vBounds);
    }

    /**
     * Get the coordinates and size of the viewport
     * for the given RenderTarget
     * @param target RenderTarget to get viewport of
     * @return viewport as a FloatRect
     */
    private FloatRect getViewRect(RenderTarget target) {
        ConstView v = target.getView();
        Vector2f halfSize = Vector2f.div(v.getSize(), 2f);
        Vector2f vo = Vector2f.sub(v.getCenter(), halfSize);
        return new FloatRect(vo, v.getSize());
    }
}
