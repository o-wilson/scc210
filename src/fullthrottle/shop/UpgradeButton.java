package fullthrottle.shop;

import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import fullthrottle.gfx.Animation;
import fullthrottle.gfx.Animator;
import fullthrottle.gfx.SpriteSequence;
import fullthrottle.gfx.Spritesheet;
import fullthrottle.ui.Button;
import fullthrottle.ui.ButtonManager;
import fullthrottle.ui.UI;
import fullthrottle.ui.UI.SpriteFillMode;

public class UpgradeButton extends Button {
    public enum ButtonType {
        UP(0, "buyNext"),
        DOWN(1, "sellLast")
        ;

        public final int spriteIndex;
        public final String methodName;

        private ButtonType(int spriteIndex, String method) {
            this.spriteIndex = spriteIndex;
            this.methodName = method;
        }
    }

    public UpgradeButton(
        UpgradePath path, ButtonType type,
        Vector2f size
    ) {
        super(Vector2f.ZERO, new Vector2i(size));

        Spritesheet sheet = new Spritesheet(
            UI.UPGRADE_BUTTONS_TEXTURE, new Vector2i(32, 32)
        );

        SpriteSequence ss = new SpriteSequence(sheet, type.spriteIndex, type.spriteIndex);
        Animation a = new Animation(ss, 1, false);
        Animator anim = new Animator();
        anim.addAnimation("Default", a);
        anim.setCurrentAnimation("Default");
        this.setSprite(anim);

        // this.setSprite(sheet.getSprite(type.spriteIndex));
        this.addAction(path, type.methodName, ActionType.LEFT_CLICK);
        this.setFillMode(SpriteFillMode.FILL);

        ButtonManager.getInstance().addObserver(this);
    }
}
