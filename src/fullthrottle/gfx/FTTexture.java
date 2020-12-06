package fullthrottle.gfx;

import org.jsfml.graphics.Texture;

import java.nio.file.Paths;
import java.io.IOException;

/**
 * Implementation of <a href="https://jsfml.sfmlprojects.org/javadoc/org/jsfml/graphics/Texture.html">org.jsfml.graphics.Texture</a> that can take a String path to a source image
 */
public class FTTexture extends Texture {
    /**
     * Tries to load an image from the path given
     * @param path String representation of path to source image
     */
    public FTTexture(String path) {
        super();
        try {
            loadFromFile(Paths.get(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
