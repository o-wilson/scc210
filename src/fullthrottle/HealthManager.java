package fullthrottle;

import java.util.ArrayList;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import fullthrottle.gfx.Animation;
import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.SpriteSequence;
import fullthrottle.gfx.Spritesheet;

public final class HealthManager implements Drawable {
    
    private int maxHealth;
    private int currentHealth;

    private static class Heart implements Drawable {
        private static SpriteSequence HEART_SEQ = new SpriteSequence(
            new Spritesheet(
                new FTTexture("./res/Heart.png"),
                new Vector2i(32, 32)
            )
        );

        private Animation anim;

        private Vector2f position, size;

        private Vector2f scale;

        public Heart(Vector2f position, Vector2f size) {
            anim = new Animation(HEART_SEQ, 24, false);
            this.scale = Vector2f.componentwiseDiv(
                size,
                new Vector2f(
                    anim.getGlobalBounds().width,
                    anim.getGlobalBounds().height
                )
            );
            this.position = position;
            this.size = size;
            anim.setScale(scale);
            anim.setPosition(this.position);
            anim.jumpToEnd();
            anim.pause();
        }

        public void add() {
            anim.restart();
            anim.pause();
        }

        public void remove() {
            anim.play();
        }

        @Override
        public void draw(RenderTarget t, RenderStates s) {
            anim.setPosition(position);
            if (anim.getGlobalBounds().height != this.size.y) {
                anim.setScale(new Vector2f(1, 1));
                this.scale = Vector2f.componentwiseDiv(
                    size,
                    new Vector2f(
                        anim.getGlobalBounds().width,
                        anim.getGlobalBounds().height
                    )
                );
                anim.setScale(this.scale);
            }
            anim.draw(t, s);
        }
    }

    private Vector2f position;
    private float width, height;
    private ArrayList<Heart> hearts;

    public HealthManager(Vector2f position, float height) {
        maxHealth = 5;
        currentHealth = 5;

        this.position = position;
        this.width = 0;
        this.height = height;

        hearts = new ArrayList<>();
        Vector2f drawPos = position;
        Vector2f size = new Vector2f(height, height);
        for (int i = 0; i < maxHealth; i++) {
            Heart h = new Heart(drawPos, size);
            hearts.add(h);
            h.add();
            drawPos = new Vector2f(drawPos.x + size.x, drawPos.y);
            width += size.x;
        }
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void addMaxHealth() {
        Vector2f drawPos = new Vector2f(this.position.x + width, this.position.y);
        maxHealth++;
        currentHealth++;
        Heart h = new Heart(drawPos, new Vector2f(height, height));
        width += height;
        hearts.add(h);
        hearts.get(currentHealth - 1).add();
    }

    public int removeHealth() {
        currentHealth--;
        hearts.get(currentHealth).remove();
        return currentHealth;
    }

    public void addHealth() {
        if (currentHealth > hearts.size() - 1) return;
        hearts.get(currentHealth).add();
        currentHealth ++;
    }

    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        for (Heart h : hearts) {
            h.draw(arg0, arg1);
        }
    }

}
