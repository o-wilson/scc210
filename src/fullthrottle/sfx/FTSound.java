package fullthrottle.sfx;

import org.jsfml.audio.Sound;
import java.nio.file.Paths;

public class FTSound extends Sound {

    public void play_sound(String path) {
        setBuffer(new FTBuffer((Paths.get(path))));
        play();
    }
}

    
