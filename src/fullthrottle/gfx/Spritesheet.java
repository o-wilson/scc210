package fullthrottle.gfx;

import java.util.HashMap;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2i;

public class Spritesheet {
    
    private Texture sheet;
    private Vector2i spriteDimensions;
    private Vector2i offset;
    private Vector2i padding;

    private Vector2i sheetDimensions;

    private HashMap<Integer, Sprite> storedSprites;

    /**
     * Initialises a spritesheet with the given dimensions and no
     * padding or offset
     * @param sheet the source texture
     * @param spriteDimensions the dimensions of each sprite
     */
    public Spritesheet(Texture sheet, Vector2i spriteDimensions) {
        this(sheet, spriteDimensions, Vector2i.ZERO, Vector2i.ZERO);
    }

    /**
     * Initialises a spritesheet with the given dimensions,
     * offset and padding
     * @param sheet the source texture
     * @param spriteDimensions the dimensions of each sprite
     * @param offset the x,y coordinates of the top left sprite
     * @param padding padding between sprites added on the
     * right and bottom
     * NB: padding must be consistent for all sprites INCLUDING EDGES
     */
    public Spritesheet(
        Texture sheet, Vector2i spriteDimensions,
        Vector2i offset, Vector2i padding
    ) {
        this.sheet = sheet;
        this.offset = offset;
        this.padding = padding;
        this.spriteDimensions = spriteDimensions;

        if (
            spriteDimensions.x * spriteDimensions.y == 0
            || spriteDimensions.x < 0
            || spriteDimensions.y < 0
        ) {
            throw new InvalidSpriteSizeException(spriteDimensions);
        }

        Vector2i textureSize = sheet.getSize();

        if (
            spriteDimensions.x > textureSize.x
            || spriteDimensions.y > textureSize.y
        ) {
            throw new InvalidSpriteSizeException(
                spriteDimensions, textureSize
            );
        }

        int width =
            (textureSize.x - offset.x) /
            (spriteDimensions.x + padding.x);
        int height = 
            (textureSize.y - offset.y) /
            (spriteDimensions.y + padding.y);
        this.sheetDimensions = new Vector2i(width, height);

        storedSprites = new HashMap<>();
    }
    
    public Sprite getSprite(int x, int y) {
        if (x >= sheetDimensions.x || y >= sheetDimensions.y)
            throw new InvalidSpriteIndexException(new Vector2i(x, y));

        int index = (y * sheetDimensions.x) + x;
        return getSprite(index);
    }

    public Sprite getSprite(Vector2i pos) {
        return getSprite(pos.x, pos.y);
    }

    public Sprite getSprite(int index) {
        if (index >= getLength())
            throw new InvalidSpriteIndexException(
                index, getLength()
            );

        if (storedSprites.keySet().contains(index))
            return storedSprites.get(index);

        int x = (index % sheetDimensions.x);
        int y = (index / sheetDimensions.x);
        Vector2i rawPosition = new Vector2i(x, y);
        Vector2i spriteOffset = Vector2i.componentwiseMul(
            rawPosition, spriteDimensions
        );
        spriteOffset = Vector2i.add(
            spriteOffset, offset
        );
        Vector2i cumulPadding = Vector2i.componentwiseMul(
            padding, rawPosition
        );
        spriteOffset = Vector2i.add(spriteOffset, cumulPadding);

        IntRect spriteRect = new IntRect(
            spriteOffset, spriteDimensions
        );
        Sprite s = new Sprite(sheet, spriteRect);

        storedSprites.put(index, s);
        return s;
    }

    public int getLength() {
        return sheetDimensions.x * sheetDimensions.y;
    }

    public Vector2i getSheetDimensions() {
        return sheetDimensions;
    }

    private class InvalidSpriteSizeException extends RuntimeException {
        /**
         *
         */
        private static final long serialVersionUID = -1135456920797388134L;

        public InvalidSpriteSizeException(
            Vector2i size
        ) {
            super(
                "Invalid sprite size [" + size.x + "," + size.y + "]"
            );
        }
        
        public InvalidSpriteSizeException(
            Vector2i size, Vector2i textureSize
        ) {
            super(
                "Sprite size [" + size.x + "," + size.y +
                "] is larger than sheet size [" +
                textureSize.x + "," + textureSize.y + "]"
            );
        }
    }

    private class InvalidSpriteIndexException extends RuntimeException {
        /**
         *
         */
        private static final long serialVersionUID = 3631380075106135529L;

        public InvalidSpriteIndexException(int index, int max) {
            super(
                "Index " + index +
                " is invalid for Spritesheet with " +
                max + " sprites"
            );
        }

        public InvalidSpriteIndexException(Vector2i pos) {
            super(
                "No sprite at [" + pos.x + "," + pos.y +
                "] because the Spritesheet has dimensions [" +
                sheetDimensions.x + "," + sheetDimensions.y + "]"
            );
        }
    }
}
