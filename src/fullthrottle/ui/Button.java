package fullthrottle.ui;

import fullthrottle.FullThrottle;
import fullthrottle.gfx.FTTexture;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

import java.util.Observer;
import java.util.Observable;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Image;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;

import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector2f;

import org.jsfml.window.event.*;
import org.jsfml.window.Mouse;

/**
 * UI Button that can display text and a sprite
 */
@SuppressWarnings("deprecation")
public class Button implements Observer, Drawable {
    /**
     * Modes for displaying the button's sprite
     */
    public enum SpriteFillMode {
        /**
         * Don't scale the sprite at all - Means that the
         * bounds of the button will not necessarily be visible
         */
        NONE,

        /**
         * Stretch/compress the sprite's width and height
         * to match the button
         */
        STRETCH,

        /**
         * Tile the sprite from the top left to fill the button's area
         */
        TILE,

        /**
         * Scale the sprite proportionally to fill the button's area, 
         * If the aspect ratios are different,
         * some of the sprite will be cropped out
         * (scaled from the top left)
         */
        FILL
    }

    private Vector2f position;
    private Vector2i size;
    private String text;
    private Sprite sourceSprite;
    private Sprite drawnSprite;
    private SpriteFillMode fillMode;

    private boolean hovered;
    private boolean held;
    
    /**
     * Basic button taking vector2i for position and size
     * @param pos Button's position
     * @param size Button's size
     */
    public Button(Vector2f pos, Vector2i size) {
        this(pos, size, "", new Sprite(), SpriteFillMode.STRETCH);
    }

    /**
     * Button with text
     * @param pos Button's position
     * @param size Button's size
     * @param text Button's text
     */
    public Button(Vector2f pos, Vector2i size, String text) {
        this(pos, size, text, new Sprite(), SpriteFillMode.STRETCH);
    }

    /**
     * Button with text and a background sprite
     * @param pos Button's position
     * @param size Button's size
     * @param text Button's text
     * @param sprite Button's sprite
     */
    public Button(Vector2f pos, Vector2i size, String text, Sprite sprite) {
        this(pos, size, text, sprite, SpriteFillMode.STRETCH);
    }

    /**
     * Button with text, background sprite and a specified SpriteFillMode
     * @param pos Button's position
     * @param size Button's size
     * @param text Button's text
     * @param sprite Button's sprite
     * @param fillMode Fill mode for sprite, see {@link SpriteFillMode}
     */
    public Button(Vector2f pos, Vector2i size, String text, Sprite sprite, SpriteFillMode fillMode) {
        this.position = pos;
        this.size = size;
        this.text = text;
        this.fillMode = fillMode;
        this.sourceSprite = sprite;
        this.drawnSprite = generateFillSprite(sprite, this.size, this.fillMode);

        held = false;
        hovered = false;
    }

    public static Sprite generateFillSprite(Sprite source, Vector2i size, SpriteFillMode fillMode) {
        if (source.equals(new Sprite())) return source;
        FloatRect oBounds = source.getLocalBounds();
        if (oBounds.width * oBounds.height == 0) return source;
        
        Image image = source.getTexture().copyToImage();

        //An image at least as big as the button, ready to crop
        Image newImage = generateScaledImage(image, size, fillMode);

        IntRect cropArea = new IntRect(Vector2i.ZERO, size);
        Texture croppedTexture = new Texture();
        
        //Create texture from image, cropping to button size
        try {
            croppedTexture.loadFromImage(newImage, cropArea);
        } catch (TextureCreationException e) {
            e.printStackTrace();
        }

        //Create and return sprite
        Sprite scaledSprite = new Sprite(croppedTexture);
        return scaledSprite;
    }

    private static Image generateScaledImage(Image source, Vector2i size, SpriteFillMode fillMode) {
        Vector2i imageSize = source.getSize();
        
        if (fillMode == SpriteFillMode.NONE) return source;

        if (fillMode == SpriteFillMode.STRETCH || fillMode == SpriteFillMode.FILL) {
            double scaleX = (double)size.x / (double)imageSize.x;
            double scaleY = (double)size.y / (double)imageSize.y;
            if (fillMode == SpriteFillMode.FILL) {
                scaleX = Math.max(scaleX, scaleY);
                scaleY = scaleX;
            }

            BufferedImage bImage = source.toBufferedImage();
            BufferedImage scaledBImage = new BufferedImage(
                size.x, size.y,
                BufferedImage.TYPE_INT_ARGB
            );

            AffineTransform at = new AffineTransform();
            at.scale(scaleX, scaleY);
            AffineTransformOp scaleOp = new AffineTransformOp(
                at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR
            );
            scaledBImage = scaleOp.filter(bImage, scaledBImage);

            Image scaledImage = new Image();
            scaledImage.create(scaledBImage);
            return scaledImage;
        }
    
        if (fillMode == SpriteFillMode.TILE) {
            int tileX = size.x / imageSize.x;
            int tileY = size.y / imageSize.y;
            System.out.println("TILE mode not implemented yet :)");
            return generateScaledImage(source, size, SpriteFillMode.STRETCH);
        }

        return new Image();
    }

    /**
     * Check whether a specified point is within the button
     * @param point The point to check
     * @return true if button contains point, false otherwise
     */
    public boolean containsPoint(Vector2i point) {
        Vector2f rPos = new Vector2f(
            point.x - position.x,
            point.y - position.y
        );

        if (rPos.x < 0 || rPos.x > size.x)
            return false;
        if (rPos.y < 0 || rPos.y > size.y)
            return false;

        return true;
    }

    @Override
    public void update(Observable o, Object event) {
        Event e = (Event)event;
        Vector2i mPos = Mouse.getPosition(FullThrottle.getWindow());

        if (e.type == Event.Type.MOUSE_BUTTON_RELEASED) {
            if (held && containsPoint(mPos)) {
                actionTriggered();
            }

            held = false;
        }

        if (e.type == Event.Type.MOUSE_MOVED) {
            if (containsPoint(mPos)) {
                hovered = true;
            } else {
                hovered = false;
            }
        }
        
        if (e.type == Event.Type.MOUSE_BUTTON_PRESSED) {
            MouseButtonEvent mouseEvent = e.asMouseButtonEvent();
            if (mouseEvent.button == Mouse.Button.LEFT) {
                if (containsPoint(mPos))
                    held = true;
            }
        }
    }

    public void actionTriggered() {
        System.out.println("Action triggered");
    }

    @Override
    public void draw(RenderTarget target, RenderStates states) {
        this.drawnSprite.setPosition(this.position);
        
        if (held) {
            this.drawnSprite.setColor(new Color(100, 100, 100));
        } else if (hovered) {
            this.drawnSprite.setColor(new Color(200, 200, 200));
        } else {
            this.drawnSprite.setColor(Color.WHITE);
        }

        this.drawnSprite.draw(target, states);
    }
}