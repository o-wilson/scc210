package fullthrottle.shop;

import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import fullthrottle.gfx.FTTexture;
import fullthrottle.ui.Button;
import fullthrottle.ui.ButtonManager;
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

        Texture tex = new FTTexture(
            "./res/shop/UpgradeButton" + type.spriteIndex + ".png"
        );

        Sprite bSprite = new Sprite(tex);
        FloatRect sB = bSprite.getGlobalBounds();
        Vector2f sSize = new Vector2f(sB.left, sB.top);
        Vector2f scale = Vector2f.componentwiseDiv(size, sSize);
        bSprite.scale(scale);
        this.setSprite(bSprite);

        // this.setSprite(sheet.getSprite(type.spriteIndex));
        this.addAction(path, type.methodName, ActionType.LEFT_CLICK);
        this.setFillMode(SpriteFillMode.FILL);

        ButtonManager.getInstance().addObserver(this);
    }
}
