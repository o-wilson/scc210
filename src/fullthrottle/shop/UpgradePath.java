package fullthrottle.shop;

import java.util.ArrayList;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import fullthrottle.gfx.Animation;
import fullthrottle.shop.UpgradeButton.ButtonType;
import fullthrottle.ui.Button;
import fullthrottle.ui.UI;

public class UpgradePath implements Drawable {
    private String name;
    private Animation icon;

    private int currentStage;
    private int stages;
    private int[] prices;

    private Text cost;

    private Button up, down;

    private ArrayList<UpgradeMarker> markers;

    public UpgradePath(
        String name, Animation icon, int[] prices,
        Vector2f pos, Vector2f iconSize
    ) {
        if (prices.length <= 0)
            throw new InvalidStageCountException(prices.length);

        markers = new ArrayList<>();

        this.currentStage = 0;
        this.prices = prices;
        this.stages = prices.length;
        this.name = name;
        this.icon = icon;

        FloatRect iconBounds = icon.getGlobalBounds();
        icon.setPosition(pos);
        Vector2f cSize = new Vector2f(
            iconBounds.width, iconBounds.height
        );
        // Vector2f scale = Vector2f.componentwiseDiv(iconSize, cSize);
        // icon.setScale(scale);

        Vector2f markerSize = Vector2f.div(iconSize, 2);

        iconBounds = icon.getGlobalBounds();
        System.out.println(name + ": " + iconBounds);
        Vector2f markerPos = Vector2f.add(
            pos, new Vector2f(iconBounds.width, (iconBounds.height / 2) - markerSize.y / 2)
        );
        for (int i = 0; i < stages; i++) {
            UpgradeMarker uM = new UpgradeMarker(
                markerSize, markerPos
            );
            markerPos = Vector2f.add(markerPos, new Vector2f(markerSize.x, 0));
            markers.add(uM);
        }
        markers.get(currentStage).unlock();

        cost = new Text(prices[0] + " C", UI.DEFAULT_UI_FONT, 10);

        Vector2f bS = Vector2f.div(iconSize, 2f);
        Vector2f bP = new Vector2f(markerPos.x, pos.y + (iconBounds.height / 2) - bS.y / 2);
        down = new UpgradeButton(this, ButtonType.DOWN, bS);
        up = new UpgradeButton(this, ButtonType.UP, bS);
        down.setPosition(bP);
        bP = Vector2f.add(bP, new Vector2f(bS.x*1.5f, 0));
        up.setPosition(bP);

        icon.jumpToEnd();

        // up = new Button(new Vector2f(markerPos.x, pos.y), new Vector2i(iconSize), sheet.getSprite(0), SpriteFillMode.FILL);
        // ButtonManager.getInstance().addObserver(up);
        // up.addAction(this, "sellLast", ActionType.LEFT_CLICK);
    }

    public void setPosition(Vector2f position) {
        
    }

    private class InvalidStageCountException extends RuntimeException {
        public InvalidStageCountException(int stages) {
            super("Invalid number of stages " + stages + ". Must be >0");
        }
    }

    public void buyNext() {
        if (currentStage >= stages) return;

        System.out.println("Bought next stage of " + name + " for " + prices[currentStage] + " coins!");
        markers.get(currentStage).buy();
        icon.restart();
        icon.play();
        currentStage++;
        if (currentStage >= stages) return;
        markers.get(currentStage).unlock();
    }

    public void sellLast() {
        if (currentStage == 0) return;

        System.out.println("Sold previous stage of " + name);
        if (currentStage < stages)
            markers.get(currentStage).lock();
        if (currentStage == 0) return;
        currentStage--;
        markers.get(currentStage).sell();
    }

    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        icon.draw(arg0, arg1);

        for (UpgradeMarker u : markers) {
            u.draw(arg0, arg1);
        }

        down.draw(arg0, arg1);
        up.draw(arg0, arg1);
    }
}
