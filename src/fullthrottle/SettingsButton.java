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

public final class SettingsButton extends Button {


    public SettingsButton(Vector2f pos, Vector2i size) {
        super(pos, size);
        createButton();
    }

    private void createButton() {
        Vector2i frameSize = new Vector2i(32, 32);
        Texture source = new FTTexture("./res/SettingsButton.png");
        Spritesheet sheet = new Spritesheet(source, frameSize);

        float SettingsButtonX = (FullThrottle.WINDOW_WIDTH - this.getWidth()) / 2;
        this.setPosition(SettingsButtonX, 600);


        this.addAction(this, "SettingsButtonLeftClick", ActionType.LEFT_CLICK);
        this.addAction(this, "SettingsButtonExit", ActionType.EXIT);
    }
    public void sButtonOpen() {
        System.out.println("SETTINGS");
    }

    }




