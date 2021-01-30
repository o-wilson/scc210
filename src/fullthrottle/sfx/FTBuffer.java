package fullthrottle.sfx;

import java.io.IOException;
import java.nio.file.Path;
import org.jsfml.audio.SoundBuffer;

public class FTBuffer extends SoundBuffer {

    public FTBuffer(Path path) {
        super();
        
        try {
            loadFromFile(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
