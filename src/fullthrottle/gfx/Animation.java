package fullthrottle.gfx;

import java.util.ArrayList;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

import fullthrottle.util.TimeManager;

public class Animation extends Sprite {

    private ArrayList<Sprite> sprites;
    private int framerate; //framerate in frames per second
    private int currentAnimationFrame;
    private float timeSinceLastChange;

    private boolean paused;

    public Animation(SpriteSequence sprites, int framerate) {
        super(sprites.getSequence().get(0).getTexture());
        
        this.sprites = sprites.getSequence();
        this.framerate = framerate;
        this.currentAnimationFrame = 0;
        this.timeSinceLastChange = 0;

        this.paused = false;
    }

    public void setFramerate(int framerate) {
        this.framerate = framerate;
    }

    public void setSprites(ArrayList<Sprite> sprites) {
        this.sprites = sprites;
    }

    public void pause() {
        this.paused = true;
    }

    public void play() {
        this.paused = false;
    }

    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        if (!paused) {
            timeSinceLastChange += TimeManager.deltaTime();
            float timeUntilChange = 1f / framerate;
            if (timeSinceLastChange >= timeUntilChange) {
                timeSinceLastChange -= timeUntilChange;
                currentAnimationFrame++;
                currentAnimationFrame %= sprites.size();
            }
        }

        sprites.get(currentAnimationFrame).draw(arg0, arg1);
    }

    @Override
    public void setColor(Color arg0) {
        for (Sprite s : sprites)
            s.setColor(arg0);
        super.setColor(arg0);
    }

    @Override
    public void setOrigin(Vector2f arg0) {
        for (Sprite s : sprites)
            s.setOrigin(arg0);
        super.setOrigin(arg0);
    }

    @Override
    public void setPosition(Vector2f arg0) {
        for (Sprite s : sprites)
            s.setPosition(arg0);
        super.setPosition(arg0);
    }

    @Override
    public void setRotation(float arg0) {
        for (Sprite s : sprites)
            s.setRotation(arg0);
        super.setRotation(arg0);
    }

    @Override
    public void setScale(Vector2f arg0) {
        for (Sprite s : sprites)
            s.setScale(arg0);
        super.setScale(arg0);
    }
}
