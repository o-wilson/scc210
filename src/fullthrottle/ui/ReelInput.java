package fullthrottle.ui;

import java.util.ArrayList;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import fullthrottle.gfx.FTTexture;
import fullthrottle.ui.Button.ActionType;

public class ReelInput implements Drawable {

    public static class Reel implements Drawable {
        public static Texture REEL_TEXTURE = new FTTexture(
            "./res/LetterReel.png"
        );

        private int currentValue;
        private int minAscii, maxAscii;

        private Button up, down;

        private Vector2f position, size;

        public Reel(
            Vector2f position, Vector2f size,
            int min, int max
        ) {
            this.minAscii = min;
            this.maxAscii = max;
            this.position = position;
            this.size = size;
            currentValue = min;

            Vector2f scale = Vector2f.componentwiseDiv(
                size,
                new Vector2f(REEL_TEXTURE.getSize())
            );
            Vector2i buttonSize = new Vector2i(
                Vector2f.componentwiseMul(
                    new Vector2f(16, 11),
                    scale
                )
            );
            Vector2f upPos = Vector2f.componentwiseMul(
                new Vector2f(24, 20),
                scale
            );
            Vector2f downPos = Vector2f.componentwiseMul(
                new Vector2f(24, 97),
                scale
            );

            up = new Button(Vector2f.add(upPos, position), buttonSize);
            up.addAction(this, "up", ActionType.LEFT_CLICK);
            ButtonManager.getInstance().addObserver(up);

            down = new Button(Vector2f.add(downPos, position), buttonSize);
            down.addAction(this, "down", ActionType.LEFT_CLICK);
            ButtonManager.getInstance().addObserver(down);
        }

        public void up() {
            currentValue--;
            if (currentValue < minAscii)
                currentValue = maxAscii;

            System.out.print((char)currentValue);
        }

        public void down() {
            currentValue++;
            if (currentValue > maxAscii)
                currentValue = minAscii;
            System.out.print((char)currentValue);
        }

        public char getValue() {
            return (char)currentValue;
        }

        @Override
        public void draw(RenderTarget arg0, RenderStates arg1) {
            Sprite s = new Sprite(REEL_TEXTURE);
            s.setPosition(position);
            s.setScale(
                Vector2f.componentwiseDiv(
                    size,
                    new Vector2f(
                        s.getGlobalBounds().width,
                        s.getGlobalBounds().height
                    )
                )
            );
            s.draw(arg0, arg1);
        }
    }

    private int length;

    private ArrayList<Reel> reels;

    private Vector2f position, size;

    private boolean visible;

    public ReelInput(int length, Vector2f position, Vector2f size) {
        this.length = length;
        reels = new ArrayList<>();
        Vector2f reelSize = new Vector2f(Reel.REEL_TEXTURE.getSize());
        reelSize = Vector2f.componentwiseMul(
            reelSize,
            Vector2f.componentwiseDiv(
                size,
                Vector2f.componentwiseMul(
                    reelSize,
                    new Vector2f(length, 1)
                )
            )
        );
        System.out.println(reelSize.x + "," + reelSize.y);
        for (int i = 0; i < length; i++) {
            Reel r = new Reel(
                new Vector2f(
                    position.x + (reelSize.x * i),
                    position.y
                ), reelSize,
                'A', 'Z'
                
            );
            reels.add(r);
        }
    }

    public String getString() {
        String result = "";
        for (Reel r : reels) {
            result += r.getValue();
        }
        return result;
    }

    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        if (!visible) return;
        for (Reel r : reels)
            r.draw(arg0, arg1);
    }

    public void setVisible(boolean b) {
        this.visible = b;
    }

}
