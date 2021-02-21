package fullthrottle;

import fullthrottle.gfx.Spritesheet;
import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.SpriteSequence;
import fullthrottle.gfx.Animation;

import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Drawable;

import fullthrottle.util.TimeManager;

public class Player implements Drawable {
    private Vector2f vPosition;
    //private Sprite sSprite;
    private Animation carAnim;
    private float moveSpeed;
    private float moveSpeedNS;
    private float moveSpeedEW;
    public Player(){
        vPosition = new Vector2f(40, 490);
        Spritesheet carSheet = new Spritesheet(
            new  FTTexture("./res/AnimationTest.png"),
            new Vector2i(32, 32)
        );
        //Sprite sSprite = carSheet.getSprite(0);
        moveSpeed = 75;
        moveSpeedEW = 75;
        moveSpeedNS = 100;
        //sSprite.setScale(new Vector2f(2, 2));

        SpriteSequence carSeq = new SpriteSequence(carSheet);
        carAnim = new Animation(carSeq, 8, true);
        carAnim.setScale(new Vector2f(2, 2));
        //carAnim.setPosition(new Vector2f(900, 450));
    }
    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        carAnim.setPosition(vPosition);
        carAnim.draw(arg0, arg1);
    }
    public void move(Vector2f moveDirection){
        moveDirection = Vector2f.mul(moveDirection, moveSpeed * TimeManager.deltaTime());
        vPosition = Vector2f.add(vPosition, moveDirection);
    }
}
