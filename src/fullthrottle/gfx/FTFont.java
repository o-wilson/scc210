package fullthrottle.gfx;

import java.io.IOException;
import java.nio.file.Paths;

import org.jsfml.graphics.Font;

public class FTFont extends Font {
    public FTFont(String path) {
        super();

        try {
            loadFromFile(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
