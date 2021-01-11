package fullthrottle.gfx;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Image;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector3f;

import fullthrottle.util.TimeManager;
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

        private Vector2f position;
        private Vector2f size;

        private int zIndex;
        private float loopFrequency;

        private int globalTextureIndex;
        private Vector2f textureStart;
        private Vector2f textureDimensions;

        private VertexArray va;

        private boolean usesTexture;

        private Vector2f[] vertexOrder;

        public BackgroundElement(
            FloatRect bounds, int zIndex, float loopFrequency,
            int globalTextureIndex, FloatRect textureCoords
        ) {
            this(bounds, zIndex, loopFrequency);

            this.usesTexture = true;
            this.globalTextureIndex = globalTextureIndex;
            this.textureStart = new Vector2f(
                textureCoords.left, textureCoords.top
            );
            this.textureDimensions = new Vector2f(
                textureCoords.width, textureCoords.height
            );
        }

        public BackgroundElement(
            FloatRect bounds, int zIndex, float loopFrequency
        ) {
            this.usesTexture = false;

            this.position = new Vector2f(
                bounds.left, bounds.top
            );
            this.size = new Vector2f(
                bounds.width, bounds.height
            );

            this.zIndex = zIndex;
            this.loopFrequency = loopFrequency;

            vertexOrder = new Vector2f[4];
            vertexOrder[0] = new Vector2f(0, 0); //TL
            vertexOrder[1] = new Vector2f(1, 0); //TR
            vertexOrder[2] = new Vector2f(1, 1); //BR
            vertexOrder[3] = new Vector2f(0, 1); //BL
        }

        /**
         * Called from main class's update for each element, updates position
         * framerate-independently
         * 
         * @param view current viewrect of the render target - used to make sure edges
         *             are looped if visible
         */
        public void update(FloatRect view) {
            int d = direction.directionMultiplier;
            float dX = speed * TimeManager.deltaTime() / zIndex;

            position = Vector2f.add(
                position, new Vector2f(dX * d, 0)
            );

            boolean offL = position.x < view.left - loopFrequency;
            boolean offR = position.x >= view.left + view.width;
            boolean offscreen = offL || offR;

            if (offscreen) {
                position = Vector2f.add(
                    position, new Vector2f(loopFrequency * -d, 0)
                );
            }
        }

        /**
         * Draw element to RenderTarget and any copies needed for looping/repeating
         * 
         * @param target RenderTarget to draw to
         * @param states RenderStates to use
         * @param view   current viewrect of the target
         */
        public void draw(RenderTarget target, RenderStates states, FloatRect view) {
            va = new VertexArray(PrimitiveType.QUADS);

            addToVertexArray();

            int d = direction.directionMultiplier;
            float viewRight = view.left + view.width;
            float loopX = position.x;

            /*
            * if there is space (on the left or right) for another
            * instance to be drawn
            * only one of these is used, dependent on direction,
            * hence &= direction
            */
            boolean spaceLeft = loopX <= viewRight - size.x;
            spaceLeft &= direction == Direction.LEFT;
            boolean spaceRight = loopX > view.left;
            spaceRight &= direction == Direction.RIGHT;

            /*
            * if space on the relevant side (dependent on direction)
            * continue drawing more instances until no more space
            */
            while (spaceLeft ^ spaceRight) {
                loopX -= d * loopFrequency;
                Vector2f drawOffset = new Vector2f(loopX, position.y);
                addToVertexArray(drawOffset);
                // update space flags, no need to recheck direction
                spaceLeft &= loopX <= viewRight - size.x;
                spaceRight &= loopX > view.left;
            }

            addToVertexMap();

            // VertexArray va = new VertexArray(PrimitiveType.QUADS);

            // for (int i = 0; i < 4; i++) {
            //     Vector2f positionOffset = Vector2f.componentwiseMul(size, vertexOrder[i]);
            //     Vector2f vPos = Vector2f.add(position, positionOffset);

            //     Vector2f textureOffset = Vector2f.componentwiseMul(textureDimensions, vertexOrder[i]);
            //     Vector2f vTex = Vector2f.add(textureStart, textureOffset);

            //     Vertex v = new Vertex(vPos, vTex);
            //     va.add(v);
            // }

            // RenderStates rs = new RenderStates(BlendMode.ALPHA);
            // rs = new RenderStates(rs, globalTextures.get(globalTextureIndex));
            // va.draw(target, rs);
            // System.out.println("*****");
        }

        private void addToVertexArray() {
            addToVertexArray(Vector2f.ZERO);
        }

        private void addToVertexArray(Vector2f offset) {
            for (int i = 0; i < 4; i++) {
                Vector2f positionOffset = Vector2f.componentwiseMul(size, vertexOrder[i]);
                positionOffset = Vector2f.add(positionOffset, offset);
                Vector2f vPos = Vector2f.add(position, positionOffset);

                Vector2f textureOffset = Vector2f.componentwiseMul(textureDimensions, vertexOrder[i]);
                Vector2f vTex = Vector2f.add(textureStart, textureOffset);

                Vertex v = new Vertex(vPos, vTex);
                va.add(v);
                if (va.size() == 28)
                    System.out.println(va.get(27).position);
            }
            // System.out.println(va);
            // System.out.println("***");
        }

        private void addToVertexMap() {
            if (!verticesToDraw.containsKey(globalTextureIndex))
                verticesToDraw.put(globalTextureIndex, new VertexArray());
            
            verticesToDraw.get(globalTextureIndex).addAll(va);
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
    private HashMap<Integer, VertexArray> verticesToDraw;

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

        verticesToDraw = new HashMap<>();
    }

    /**
     * Put a background element into the hashmap used for drawing
     * Ensure layer order is maintained if a new layer is added
     * @param e element to put
     * @param zIndex z index of the element
     */
    private void putElement(BackgroundElement e, int zIndex) {
        if (!elements.containsKey(zIndex)) {
            elements.put(zIndex, new ArrayList<>());

            zLayers.add(zIndex);
            Collections.sort(zLayers);
            Collections.reverse(zLayers);
        }

        elements.get(zIndex).add(e);
    }

    public void addElement(Sprite s, int zIndex, Vector2f startPos) {
        addElement(s, zIndex, startPos, s.getGlobalBounds().width);
    }

    public void addElement(Sprite s, int zIndex, Vector2f startPos, float loopFrequency) {
        if (zIndex <= 0) throw new InvalidIndexException(zIndex);
        
        // get texture + rect

        Texture t = (Texture) s.getTexture();
        IntRect texRect = s.getTextureRect();

        // add texture to global (returns Vector3f - (x,y,i))

        Vector3f texPos = addToGlobal(t);
        
        // get sprite bounds

        s.setPosition(startPos);
        FloatRect bounds = s.getGlobalBounds();

        // create BackgroundElement with bounds, textureID, texture Coords, zIndex, loopFrequency

        Vector2f textureStart = new Vector2f(texPos.x, texPos.y);
        textureStart = Vector2f.add(textureStart, new Vector2f(texRect.left, texRect.top));
        Vector2f textureDim = new Vector2f(texRect.width, texRect.height);
        if (textureDim.equals(Vector2f.ZERO))
            textureDim = new Vector2f(s.getLocalBounds().width, s.getLocalBounds().height);
        FloatRect textureCoords = new FloatRect(textureStart, textureDim);
        BackgroundElement element = new BackgroundElement(
            bounds, zIndex, loopFrequency,
            (int)texPos.z, textureCoords
        );
        
        putElement(element, zIndex);
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
        verticesToDraw.clear();
        
        for (int i : zLayers)
            for (BackgroundElement e : elements.get(i))
                e.draw(target, states, vBounds);

        for (int i : verticesToDraw.keySet()) {
            RenderStates rs = new RenderStates(BlendMode.ALPHA);
            rs = new RenderStates(rs, globalTextures.get(i));
            verticesToDraw.get(i).draw(target, rs);
        }

        System.out.println(verticesToDraw);
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
