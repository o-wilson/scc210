package fullthrottle.gfx;

import java.util.ArrayList;
import java.util.Collections;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2i;

/**
 * A class for holding a sequence of related sprites,
 * for example in an animation
 */
public class SpriteSequence {
    private ArrayList<Sprite> sprites;
    
    /**
     * Default constructor for a sequence that is one entire row
     * @param spritesheet The texture containing the sequence
     * @param fS The dimensions in pixels of each frame
     */
    public SpriteSequence(Texture spritesheet, Vector2i fS) {
        this(
            spritesheet, fS,
            1, spritesheet.getSize().x / fS.x
        );
    }

    /**
     * Constructor for a sequence across multiple rows
     * @param spritesheet The texture containing the sequence
     * @param fS The dimensions in pixels of each frame
     * @param rows The number of rows that the sequence spans
     * @param length The total number of frames in the sequence
     */
    public SpriteSequence(
        Texture spritesheet, Vector2i fS,
        int rows, int length
    ) {
        int addedFrames = 0;
        sprites = new ArrayList<>();
        int sheetW = spritesheet.getSize().x;
        Vector2i framePos = Vector2i.ZERO;
        IntRect rect = new IntRect(framePos, fS);

        for (int r = 0; r < rows; r++) {
            for (int f = 0; f < sheetW / fS.x; f++) {
                if (addedFrames >= length) break;
                framePos = new Vector2i(f * fS.x, r * fS.y);
                rect = new IntRect(framePos, fS);
                sprites.add(new Sprite(spritesheet, rect));
                addedFrames++;
            }
        }
    }

    public ArrayList<Sprite> getSequence() {
        return new ArrayList<>(sprites);
    }
}
