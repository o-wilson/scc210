package fullthrottle.gfx;

import java.util.ArrayList;

import org.jsfml.graphics.Sprite;

/**
 * A class for holding a sequence of related sprites,
 * for example in an animation
 */
public class SpriteSequence {
    private ArrayList<Sprite> sprites;
    
    /**
     * Creates a new sprite sequence using the whole spritesheet
     * @param spritesheet source spritesheet
     */
    public SpriteSequence(Spritesheet spritesheet) {
        this(spritesheet, 0, spritesheet.getLength() - 1);
    }

    /**
     * Creates a new sprite sequence from the given sprite sheet using
     * sprites between the start and end indices (inclusive)
     * @param spritesheet source spritesheet
     * @param start the index of the first sprite in the sequence
     * @param end the index of the last sprite in the sequence
     */
    public SpriteSequence(
        Spritesheet spritesheet,
        int start, int end
    ) {
        sprites = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            sprites.add(spritesheet.getSprite(i));
        }
    }

    /**
     * Get the sequence of sprites
     * @return a copy of the ArrayList of the sprites in the sequence
     */
    public ArrayList<Sprite> getSequence() {
        return new ArrayList<>(sprites);
    }
}
