package fullthrottle;

import fullthrottle.gfx.Spritesheet;
import fullthrottle.gfx.FTTexture;

import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Drawable;

import fullthrottle.util.TimeManager;

public class Player implements Drawable {
    private Vector2f vPosition;
    private Sprite sSprite;
    private float moveSpeed;
    public Player(){
        vPosition = new Vector2f(900, 450);
        Spritesheet carSheet = new Spritesheet(
            new  FTTexture("./res/AnimationTest.png"),
            new Vector2i(32, 32)
        );
        sSprite = carSheet.getSprite(0);
        moveSpeed = 75;
    }
    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        sSprite.setPosition(vPosition);
        sSprite.draw(arg0, arg1);
    }
    public void move(Vector2f moveDirection){
        moveDirection = Vector2f.mul(moveDirection, moveSpeed * TimeManager.deltaTime());
        vPosition = Vector2f.add(vPosition, moveDirection);
    }
}
