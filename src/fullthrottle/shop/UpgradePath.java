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

/**
 * A class representing the stages and current progress of an upgrade
 */
public class UpgradePath implements Drawable {
    private String name;
    private Animation icon;

    private int currentStage;
    private int stages;
    private int[] prices;

    private Text cost;

    private Button up, down;

    private ArrayList<UpgradeMarker> markers;

    /**
     * Creates a new UpgradePath with as many stages
     * as prices are given in the prices array
     * @param name the title of the upgrade (shown as text)
     * @param icon the animated icon representing the upgrade
     * @param prices an array of prices needed to purchase each stage
     * @param pos the position on the screen
     * @param iconSize the dimensions of the icon
     */
    public UpgradePath(
        String name, Animation icon, int[] prices,
        Vector2f pos, Vector2f iconSize
    ) {
        // Can't have a path with no stages
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

        Vector2f markerSize = Vector2f.div(iconSize, 2);

        // calculate starting position and generate stage markers
        iconBounds = icon.getGlobalBounds();
        Vector2f markerPos = Vector2f.add(
            pos, new Vector2f(
                iconBounds.width,
                (iconBounds.height / 2) - markerSize.y / 2
            )
        );
        for (int i = 0; i < stages; i++) {
            UpgradeMarker uM = new UpgradeMarker(
                markerSize, markerPos
            );
            markerPos = Vector2f.add(
                markerPos, new Vector2f(markerSize.x, 0)
            );
            markers.add(uM);
        }
        markers.get(currentStage).unlock();

        cost = new Text(prices[0] + " C", UI.DEFAULT_UI_FONT, 10);

        // Add buttons to the end of the stage markers
        Vector2f bS = Vector2f.div(iconSize, 2f);
        Vector2f bP = new Vector2f(
            markerPos.x,
            pos.y + (iconBounds.height / 2) - bS.y / 2
        );
        down = new UpgradeButton(this, ButtonType.DOWN, bS);
        up = new UpgradeButton(this, ButtonType.UP, bS);
        down.setPosition(bP);
        bP = Vector2f.add(bP, new Vector2f(bS.x*1.5f, 0));
        up.setPosition(bP);

        icon.jumpToEnd();
    }

    /**
     * Buys the next stage of the upgrade
     * @return returns the price of the purchased stage, 0 if all purchased
     * IMPORTANT: caller is responsible for
     * 1) checking whether the stage can be afforded and
     * 2) removing the correct amount of money
     */
    public int buyNext() {
        //if all stages purchased
        if (currentStage >= stages) return 0;

        markers.get(currentStage).buy();
        icon.restart();
        icon.play();
        currentStage++;

        //if just purchased the last stage
        if (currentStage >= stages) return prices[currentStage - 1];
        markers.get(currentStage).unlock();

        return prices[currentStage - 1];
    }

    /**
     * Sells the latest stage of the upgrade
     * @return returns the original cost of the stage, 0 if none to sell
     * IMPORTANT: caller is responsible for refunding the correct amount
     */
    public int sellLast() {
        //if no stage to sell
        if (currentStage == 0) return 0;

        if (currentStage < stages)
            markers.get(currentStage).lock();

        //if just sold first (0th) stage
        if (currentStage == 0) return prices[currentStage];
        currentStage--;
        markers.get(currentStage).sell();

        return prices[currentStage + 1];
    }

    /**
     * Get the index of the furthest unlocked (not bought) stage
     * @return returns the index of the next stage to be bought
     */
    public int getCurrentStage() {
        return currentStage;
    }

    /**
     * Get the price of the next stage
     * @return returns the price of the next stage
     */
    public int getPrice() {
        return prices[currentStage];
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

    /**
     * Thrown when trying to create a path with
     * a 0 or negative number of stages
     */
    private class InvalidStageCountException extends RuntimeException {
        /**
         *
         */
        private static final long serialVersionUID = -1928867194158996212L;

        public InvalidStageCountException(int stages) {
            super("Invalid number of stages " + stages + ". Must be >0");
        }
    }
}
