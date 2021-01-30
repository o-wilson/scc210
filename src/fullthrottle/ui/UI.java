package fullthrottle.ui;

import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.FTFont;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Image;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;

import org.jsfml.system.Vector2i;

public final class UI {
    
    public static Sprite DEFAULT_UI_SPRITE = new Sprite(
        new FTTexture("./res/Button.png")
    );

    public static Font DEFAULT_UI_FONT = new FTFont(
        "./res/FreeSans.ttf"
    );

    /**
     * Modes for displaying the button's sprite
     */
    public static enum SpriteFillMode {
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

    /**
     * Creates a scaled version of a sprite,
     * using a specified fill mode
     * @param source the original sprite
     * @param size the size to scale/fill to
     * @param fillMode the method to fill by
     * @return Returns a new, scaled instance of the original sprite
     * @see UI#generateScaledImage(Image, Vector2i, SpriteFillMode)
     */
    public static Sprite generateFillSprite
    (
        Sprite source,
        Vector2i size,
        SpriteFillMode fillMode
    ) {
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

    /**
     * Generates a scaled version of an image, which is at least
     * as large as specified by size.
     * NB: not intended for external use, be aware that the returned
     * Image can be larger than size
     * @param source the original image
     * @param size the size to scale/fill to
     * @param fillMode the method to fill by
     * @return Returns an Image at least as large as size
     */
    private static Image generateScaledImage
    (
        Image source,
        Vector2i size,
        SpriteFillMode fillMode
    ) {
        Vector2i sSize = source.getSize();
        
        if (fillMode == SpriteFillMode.NONE) return source;

        boolean stretch = fillMode == SpriteFillMode.STRETCH;
        boolean fill = fillMode == SpriteFillMode.FILL;
        if (stretch || fill) {
            double scaleX = (double)size.x / (double)sSize.x;
            double scaleY = (double)size.y / (double)sSize.y;
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
            int tileX = (int)Math.ceil((double)size.x / sSize.x);
            int tileY = (int)Math.ceil((double)size.y / sSize.y);
            // System.out.println("TILE mode not implemented yet :)");
            BufferedImage original = source.toBufferedImage();
            BufferedImage tiledImage = new BufferedImage(
                tileX * sSize.x, tileY * sSize.y,
                BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g = tiledImage.createGraphics();
            AffineTransform at = new AffineTransform();
            AffineTransformOp op = new AffineTransformOp(
                at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR
            );
            g.drawImage(original, op, 0, 0);
            g.drawImage(original, op, 32, 0);
            for (int y = 0; y < tileY; y++) {
                for (int x = 0; x < tileX; x++) {
                    g.drawImage(
                        original, op,
                        x * sSize.x, y * sSize.y
                    );
                }
            }
            g.dispose();

            Image finishedImage = new Image();
            finishedImage.create(tiledImage);
            return finishedImage;
        }

        return new Image();
    }
}