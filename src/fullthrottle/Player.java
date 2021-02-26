package fullthrottle;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.FloatRect;
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
    private boolean shifted;
    private boolean slam;

    private FloatRect bounds;

    private float scale;
    private float shiftStart;

    public Player() {
        this.scale = 2;

        resetPosition();
        bVisible = true;
        shifted = false;
        slam = false;
        shiftStart = 0;
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
        carAnim.setScale(new Vector2f(scale, scale));
        carAnim.setPosition(new Vector2f(900, 450));

        bounds = new FloatRect(
            1 * scale,
            6 * scale,
            30 * scale,
            20 * scale
        );
    }

    public void resetPosition() {
        position = new Vector2f(32, 480);
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

    public void setShifted(boolean shift) {
        this.shifted = shift;
    }

    public boolean getShifted() {
        return shifted;
    }

    public void setSlam(boolean slm) {
        this.slam = slm;
    }

    public boolean getSlam() {
        return slam;
    }

    public void setShiftStart(float shift) {
        this.shiftStart = shift;
    }

    public float getShiftStart() {
        return shiftStart;
    }

    public Vector2f getSize() {
        Vector2f size = new Vector2f(
            carAnim.getGlobalBounds().width,
            carAnim.getGlobalBounds().height
        );
        return size;
    }

    public FloatRect getBounds() {
        FloatRect globalBounds = new FloatRect(
            bounds.left + position.x,
            bounds.top + position.y,
            bounds.width,
            bounds.height
        );
        return globalBounds;
    }
}
