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

public final class PlayButton extends Button {
    private Animator playButtonAnimator;

    public PlayButton(Vector2f pos, Vector2i size) {
        super(pos, size);
        createButton();
    }

    private void createButton() {
        Vector2i frameSize = new Vector2i(64, 32);
        Texture source = new FTTexture("./res/PlayButton.png");
        Spritesheet sheet = new Spritesheet(source, frameSize);

        SpriteSequence idleSequence = new SpriteSequence(sheet, 0, 0);
        SpriteSequence enterSequence = new SpriteSequence(sheet, 7, 13);
        SpriteSequence exitSequence = new SpriteSequence(sheet, 14, 20);
        Animation playEnterAnim = new Animation(enterSequence, 15, false);
        Animation playExitAnim = new Animation(exitSequence, 15, false);
        Animation playIdleAnim = new Animation(idleSequence, 1, false);
        playExitAnim.jumpToEnd();

        playButtonAnimator = new Animator();
        playButtonAnimator.addAnimation("Enter", playEnterAnim);
        playButtonAnimator.addAnimation("Exit", playExitAnim);
        playButtonAnimator.addAnimation("Idle", playIdleAnim);

        playButtonAnimator.setCurrentAnimation("Idle");
        Vector2i playButtonSize = this.getSize();
        playButtonAnimator.setScale(new Vector2f(
            Vector2i.componentwiseDiv(playButtonSize, frameSize)
        ));

        float playButtonX = (FullThrottle.WINDOW_WIDTH - this.getWidth()) / 2;
        this.setPosition(playButtonX, 400);

        this.setSprite(playButtonAnimator);

        this.addAction(this, "playButtonLeftClick", ActionType.LEFT_CLICK);
        this.addAction(this, "playButtonEnter", ActionType.ENTER);
        this.addAction(this, "playButtonExit", ActionType.EXIT);
    }

    public void playButtonEnter() {
        Animation exit = playButtonAnimator.getAnimation("Exit");
        int startFrame = exit.getLength() - exit.getCurrentFrame() - 1;
        playButtonAnimator.getAnimation("Enter").setCurrentFrame(startFrame);
        playButtonAnimator.setCurrentAnimation("Enter");
    }

    public void playButtonExit() {
        Animation enter = playButtonAnimator.getAnimation("Enter");
        int startFrame = enter.getLength() - enter.getCurrentFrame() - 1;
        playButtonAnimator.getAnimation("Exit").setCurrentFrame(startFrame);
        playButtonAnimator.setCurrentAnimation("Exit");
    }

    public void playButtonLeftClick() {
        // System.out.println("Play Button pressed");
    }
}
