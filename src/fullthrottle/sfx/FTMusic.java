package fullthrottle.sfx;

import org.jsfml.audio.Music;
import java.io.IOException;
import java.nio.file.Paths;

public class FTMusic extends Music {

    
    
    public void play_music(String path)
    {
        
        try {
            openFromFile(Paths.get(path));

        } catch (IOException e) {
            e.printStackTrace();
        }

        setLoop(true);
      play();
    }

}

