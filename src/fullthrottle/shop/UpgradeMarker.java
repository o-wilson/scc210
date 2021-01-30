package fullthrottle.shop;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import fullthrottle.gfx.Animation;
import fullthrottle.gfx.Animator;
import fullthrottle.gfx.SpriteSequence;
import fullthrottle.gfx.Spritesheet;
import fullthrottle.ui.UI;

/**
 * Class representing a stage marker of an UpgradePath
 */
public class UpgradeMarker implements Drawable {
    private Animator anim;

    /**
     * Creates a new marker at the specified size and position
     * @param size size of the marker sprite
     * @param pos position of the marker
     */
    public UpgradeMarker(Vector2f size, Vector2f pos) {
        // Initialise animations
        anim = new Animator();

        Vector2i frameSize = new Vector2i(32, 32);
        Spritesheet sheet = new Spritesheet(
            UI.UPGRADE_MARKER_TEXTURE, frameSize
        );

        SpriteSequence lockedS = new SpriteSequence(sheet, 0, 0);
        SpriteSequence unlockS = new SpriteSequence(sheet, 14, 27);
        SpriteSequence buyS = new SpriteSequence(sheet, 28, 41);

        Animation lockedA = new Animation(lockedS, 15, false);
        Animation unlockA = new Animation(unlockS, 15, false);
        Animation buyA = new Animation(buyS, 15, false);

        anim.addAnimation("Locked", lockedA);
        anim.addAnimation("Unlock", unlockA);
        anim.addAnimation("Buy", buyA);

        anim.setCurrentAnimation("Locked");

        anim.setPosition(pos);
        Vector2f scale = Vector2f.componentwiseDiv(
            size, new Vector2f(frameSize)
        );

        anim.setScale(scale);
    }

    /**
     * Plays the unlock animation
     */
    public void unlock() {
        anim.getAnimation("Unlock").restart();
        anim.setCurrentAnimation("Unlock");
    }

    /**
     * Plays the buy animation
     */
    public void buy() {
        anim.getAnimation("Buy").restart();
        anim.setCurrentAnimation("Buy");
    }

    /**
     * Sets the current animation to unlocked
     */
    public void sell() {
        anim.getAnimation("Unlock").jumpToEnd();
        anim.setCurrentAnimation("Unlock");
    }

    /**
     * Sets the current animation to locked
     */
    public void lock() {
        anim.setCurrentAnimation("Locked");
    }

    /*
     * room for potential optimisation combining all
     * points to a VertexArray?
    */
    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        anim.draw(arg0, arg1);
    }
}
