package fullthrottle;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;
import org.jsfml.window.event.Event;

import fullthrottle.gfx.Animation;
import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.SpriteSequence;
import fullthrottle.gfx.Spritesheet;
import fullthrottle.util.TimeManager;

public class AnimationPreview {
    public static void main(String[] args) {
        RenderWindow window = new RenderWindow(new VideoMode(512, 512), "Full Throttle",
                WindowStyle.TITLEBAR | WindowStyle.CLOSE);
        window.setKeyRepeatEnabled(false);



        String path = args[0];
        Texture texture = new FTTexture(path);

        Vector2i tileSize = new Vector2i(
            texture.getSize().y,
            texture.getSize().y
        );
        if (args.length > 1) {
            String[] dims = args[1].split("x");
            tileSize = new Vector2i(
                Integer.parseInt(dims[0]),
                Integer.parseInt(dims[1])
            );
        }
        Spritesheet sheet = new Spritesheet(texture, tileSize);
        SpriteSequence seq = new SpriteSequence(sheet);
        Animation anim = new Animation(seq, 10, true);

        float scale = Math.min(512/tileSize.x, 512/tileSize.y);

        anim.scale(new Vector2f(
            scale, scale
        ));

        while (window.isOpen()) {
            TimeManager.update();
            for (Event event : window.pollEvents()) {
                if (event.type == Event.Type.CLOSED) {
                    window.close();
                }
            }

            window.clear(Color.MAGENTA);

            anim.draw(window, new RenderStates(BlendMode.ALPHA));

            window.display();
        }
    }
}
