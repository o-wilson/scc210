package fullthrottle;

import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import fullthrottle.gfx.Animation;
import fullthrottle.gfx.Animator;
import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.Renderer;
import fullthrottle.gfx.SpriteSequence;
import fullthrottle.ui.Button;

public class PlayButton extends Button {
    private Animator playButtonAnimator;

    private boolean created;

    public PlayButton(Vector2f pos, Vector2i size) {
        super(pos, size);
        created = false;
        createButton();
    }

    private void createButton() {
        if (created) return;
        created = true;

        Texture pEnter = new FTTexture("./res/PlayButtonEnter.png");
        Texture pExit = new FTTexture("./res/PlayButtonExit.png");
        Texture pIdle = new FTTexture("./res/PlayButtonIdle.png");
        Vector2i frameSize = new Vector2i(64, 32);
        SpriteSequence enterSequence = new SpriteSequence(pEnter, frameSize);
        SpriteSequence exitSequence = new SpriteSequence(pExit, frameSize);
        SpriteSequence idleSequence = new SpriteSequence(pIdle, frameSize);
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

        float playButtonX = (FullThrottle.WINDOW_WIDTH - this.getWidth()) /2;
        this.setPosition(playButtonX, 400);

        this.setSprite(playButtonAnimator);

        this.addAction(this, "playButtonLeftClick", ActionType.LEFT_CLICK);
        this.addAction(this, "playButtonEnter", ActionType.ENTER);
        this.addAction(this, "playButtonExit", ActionType.EXIT);
        Renderer.addDrawable(this);
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
        System.out.println("Play Button pressed");
    }
}
