package fullthrottle.shop;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import fullthrottle.gfx.Animation;
import fullthrottle.gfx.Animator;
import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.SpriteSequence;
import fullthrottle.gfx.Spritesheet;
import fullthrottle.ui.UI;

public class UpgradeMarker implements Drawable {
    private Animator anim;

    public UpgradeMarker(Vector2f size, Vector2f pos) {
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
        Vector2f scale = Vector2f.componentwiseDiv(size, new Vector2f(frameSize));

        anim.setScale(scale);
    }

    public void unlock() {
        anim.getAnimation("Unlock").restart();
        anim.setCurrentAnimation("Unlock");
    }

    public void buy() {
        anim.getAnimation("Buy").restart();
        anim.setCurrentAnimation("Buy");
    }

    public void sell() {
        anim.getAnimation("Unlock").jumpToEnd();
        anim.setCurrentAnimation("Unlock");
    }

    public void lock() {
        anim.setCurrentAnimation("Locked");
    }

    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        // TODO Auto-generated method stub
        anim.draw(arg0, arg1);
    }
}
