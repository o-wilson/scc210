package fullthrottle;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import fullthrottle.gfx.Animation;
import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.SpriteSequence;
import fullthrottle.gfx.Spritesheet;
import fullthrottle.util.TimeManager;

public class Player implements Drawable {

    private Vector2f position;

    private Animation carAnim;

    private Vector2f moveSpeed;

    private boolean bVisible;
    private boolean active;

    public Player() {
        position = new Vector2f(40, 490);
        bVisible = true;
        Spritesheet carSheet = new Spritesheet(
            new FTTexture("./res/Car.png"),
            new Vector2i(32, 32)
        );
        //Sprite sSprite = carSheet.getSprite(0);
        moveSpeed = new Vector2f(120, 150);
        // moveSpeedEW = 150;
        // moveSpeedNS = 150;
        //sSprite.setScale(new Vector2f(2, 2));

        SpriteSequence carSeq = new SpriteSequence(carSheet);
        carAnim = new Animation(carSeq, 8, true);
        carAnim.setScale(new Vector2f(2, 2));
        carAnim.setPosition(new Vector2f(900, 450));
    }

    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        if (!bVisible) return;
        carAnim.setPosition(position);
        carAnim.draw(arg0, arg1);
    }

    public void move(Vector2f moveDirection) {
        if (!active) return;
        
        moveDirection = Vector2f.componentwiseMul(moveDirection, moveSpeed);
        moveDirection = Vector2f.mul(moveDirection, TimeManager.deltaTime());
        
        position = Vector2f.add(position, moveDirection);
    }

    public void setVisible(boolean b) {
        this.bVisible = b;
    }
    
    public void setActive(boolean b) {
        this.active = b;
    }

    public boolean isActive() {
        return active;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getSize() {
        Vector2f size = new Vector2f(
            carAnim.getGlobalBounds().width,
            carAnim.getGlobalBounds().height
        );
        return size;
    }
}
