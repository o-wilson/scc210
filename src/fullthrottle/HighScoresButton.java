package fullthrottle;

import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import fullthrottle.gfx.Animation;
import fullthrottle.gfx.Animator;
import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.Renderer;
import fullthrottle.gfx.SpriteSequence;
import fullthrottle.gfx.Spritesheet;
import fullthrottle.ui.Button;

public final class HighScoresButton extends Button {
    private Animator animator;

    public HighScoresButton(Vector2f pos, Vector2i size) {
        super(pos, size);
        createButton();
    }

    private void createButton() {
        Vector2i frameSize = new Vector2i(64, 32);
        Texture source = new FTTexture("./res/HighScoresButton.png");
        Spritesheet sheet = new Spritesheet(source, frameSize);

        SpriteSequence idleSequence = new SpriteSequence(sheet, 0, 0);
        SpriteSequence enterSequence = new SpriteSequence(sheet, 7, 13);
        SpriteSequence exitSequence = new SpriteSequence(sheet, 14, 20);
        Animation enterAnim = new Animation(enterSequence, 15, false);
        Animation exitAnim = new Animation(exitSequence, 15, false);
        Animation idleAnim = new Animation(idleSequence, 1, false);
        exitAnim.jumpToEnd();

        animator = new Animator();
        animator.addAnimation("Enter", enterAnim);
        animator.addAnimation("Exit", exitAnim);
        animator.addAnimation("Idle", idleAnim);

        animator.setCurrentAnimation("Idle");
        Vector2i buttonSize = this.getSize();
        animator.setScale(new Vector2f(Vector2i.componentwiseDiv(buttonSize, frameSize)));

        float buttonX = (FullThrottle.WINDOW_WIDTH - this.getWidth()) / 2;
        this.setPosition(buttonX, 512);

        this.setSprite(animator);

        this.addAction(this, "hsButtonLeftClick", ActionType.LEFT_CLICK);
        this.addAction(this, "hsButtonEnter", ActionType.ENTER);
        this.addAction(this, "hsButtonExit", ActionType.EXIT);
    }

    public void hsButtonEnter() {
        Animation exit = animator.getAnimation("Exit");
        int startFrame = exit.getLength() - exit.getCurrentFrame() - 1;
        animator.getAnimation("Enter").setCurrentFrame(startFrame);
        animator.setCurrentAnimation("Enter");
    }

    public void hsButtonExit() {
        Animation enter = animator.getAnimation("Enter");
        int startFrame = enter.getLength() - enter.getCurrentFrame() - 1;
        animator.getAnimation("Exit").setCurrentFrame(startFrame);
        animator.setCurrentAnimation("Exit");
    }

    public void hsButtonLeftClick() {
        // System.out.println("High Scores Button pressed");
    }
}
