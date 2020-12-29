package fullthrottle.gfx;

import java.util.ArrayList;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;

import fullthrottle.util.TimeManager;

public class Animation extends Sprite {

    private ArrayList<Sprite> sprites;
    private int framerate; //framerate in frames per second
    private int currentAnimationFrame;
    private float timeSinceLastChange;

    public Animation(SpriteSequence sprites, int framerate) {
        this.sprites = sprites.getSequence();
        this.framerate = framerate;
        this.currentAnimationFrame = 0;
        this.timeSinceLastChange = 0;
    }

    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        timeSinceLastChange += TimeManager.deltaTime();
        float timeUntilChange = 1f / framerate;
        if (timeSinceLastChange >= timeUntilChange) {
            timeSinceLastChange -= timeUntilChange;
            currentAnimationFrame++;
            currentAnimationFrame %= sprites.size();
        }

        sprites.get(currentAnimationFrame).draw(arg0, arg1);
    }
}
