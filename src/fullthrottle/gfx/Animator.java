package fullthrottle.gfx;

import java.util.HashMap;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

public class Animator extends Sprite {
    private HashMap<String, Animation> animations;

    private Animation currentAnimation;

    public class NoSuchAnimationException extends RuntimeException {
        public NoSuchAnimationException() {
            super("This animator has no animations yet");
        }
        
        public NoSuchAnimationException(String name) {
            super("The animation " + name + " does not exist for Animator");
        }
    }

    public Animator() {
        animations = new HashMap<>();
    }
    
    public void addAnimation(String name, Animation anim) {
        animations.put(name, anim);
    }

    public void setCurrentAnimation(String name) {
        if (!animations.containsKey(name))
            throw new NoSuchAnimationException(name);
        else
            currentAnimation = animations.get(name);
    }

    public Animation getCurrentAnimation() {
        if (currentAnimation == null)
            throw new NoSuchAnimationException();
        else
            return currentAnimation;
    }

    public Animation getAnimation(String name) {
        if (!animations.containsKey(name))
            throw new NoSuchAnimationException(name);
        else
            return animations.get(name);
    }

    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        currentAnimation.draw(arg0, arg1);
    }



    @Override
    public void setColor(Color arg0) {
        for (Animation a : animations.values())
            a.setColor(arg0);
        super.setColor(arg0);
    }

    @Override
    public void setOrigin(Vector2f arg0) {
        for (Animation a : animations.values())
            a.setOrigin(arg0);
        super.setOrigin(arg0);
    }

    @Override
    public void setPosition(Vector2f arg0) {
        for (Animation a : animations.values())
            a.setPosition(arg0);
        super.setPosition(arg0);
    }

    @Override
    public void setRotation(float arg0) {
        for (Animation a : animations.values())
            a.setRotation(arg0);
        super.setRotation(arg0);
    }

    @Override
    public void setScale(Vector2f arg0) {
        for (Animation a : animations.values())
            a.setScale(arg0);
        super.setScale(arg0);
    }

    @Override
    public FloatRect getGlobalBounds() {
        if (currentAnimation == null)
            throw new NoSuchAnimationException();
        return currentAnimation.getGlobalBounds();
    }
}
