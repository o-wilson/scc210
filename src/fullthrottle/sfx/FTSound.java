package fullthrottle.sfx;

import org.jsfml.audio.Sound;
import java.nio.file.Paths;

public class FTSound extends Sound {

    public FTSound(String path) {
        super();
        setBuffer(new FTBuffer((Paths.get(path))));
    }
}

    
