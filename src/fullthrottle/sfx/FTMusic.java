package fullthrottle.sfx;

import org.jsfml.audio.Music;
import java.io.IOException;
import java.nio.file.Paths;

public class FTMusic extends Music {


    public FTMusic(String path) {
        super();

        try {
            openFromFile(Paths.get(path));

        } catch (IOException e) {
            e.printStackTrace();
        }
        setLoop(true);
        play();

    }

    

}

