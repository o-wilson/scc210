package fullthrottle.ui;

import fullthrottle.FullThrottle;

import java.lang.Class;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Observable;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.Sprite;

import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector2f;

import org.jsfml.window.event.*;
import org.jsfml.window.Mouse;

/**
 * UI Button that can display text and a sprite
 * Needs to be added as an observer to the ButtonManager
 */
@SuppressWarnings("deprecation")
public class Button implements Observer, Drawable {

    private Vector2f position;
    private Vector2i size;

    private Sprite sourceSprite;
    private Sprite sourceDisabledSprite;

    private Sprite enabledSprite;
    private Sprite disabledSprite;

    private Sprite activeSprite;
    private UI.SpriteFillMode fillMode;

    private Color defaultColor;
    private Color hoverColor;
    private Color heldColor;

    /**
     * Class containing all relevant data to invoke an action
     */
    private class ButtonAction {
        public Object actionObject;
        public Method actionMethod;
        public ActionType actionType;
        /**
         * true = only execute when button is enabled
         */
        public boolean enabled;

        public ButtonAction(
            Object o, Method m, ActionType t, boolean e
        ) {
            actionObject = o;
            actionMethod = m;
            actionType = t;
            enabled = e;
        }

        public void execute() {
            if (actionMethod != null && actionObject != null) {
                try {
                    actionMethod.invoke(actionObject);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean isType(ActionType t) {
            return t == actionType;
        }
    }

    private List<ButtonAction> actions;

    /**
     * Type of the mouse action
     */
    public enum ActionType {
        LEFT_CLICK,
        RIGHT_CLICK,
        ENTER,
        EXIT
    }

    private boolean hovered;
    private boolean lastHovered;
    private boolean heldLeft;
    private boolean heldRight;

    private boolean enabled;
    private boolean visible;
    
    /**
     * Basic button with default UI sprite
     * @param pos Button's position
     * @param size Button's size
     */
    public Button(Vector2f pos, Vector2i size) {
        this(
            pos, size,
            UI.DEFAULT_UI_SPRITE, UI.SpriteFillMode.STRETCH
        );
    }

    /**
     * Button with a specified background sprite
     * @param pos Button's position
     * @param size Button's size
     * @param sprite Button's sprite
     */
    public Button(Vector2f pos, Vector2i size, Sprite sprite) {
        this(pos, size, sprite, UI.SpriteFillMode.STRETCH);
    }

    /**
     * Button with text, background sprite and a specified SpriteFillMode
     * @param pos Button's position
     * @param size Button's size
     * @param sprite Button's sprite
     * @param fillMode Fill mode for sprite
     */
    public Button(Vector2f pos, Vector2i size, Sprite sprite, UI.SpriteFillMode fillMode) {
        this.position = pos;
        this.size = size;
        this.fillMode = fillMode;
        this.sourceSprite = sprite;
        this.sourceDisabledSprite = sprite;

        enabledSprite = UI.generateFillSprite(sprite, size, fillMode);
        disabledSprite = enabledSprite;
        this.activeSprite = enabledSprite;

        heldLeft = false;
        heldRight = false;
        hovered = false;
        lastHovered = false;

        defaultColor = Color.WHITE;
        hoverColor = Color.WHITE;
        heldColor = Color.WHITE;

        enabled = true;
        visible = true;

        actions = new ArrayList<ButtonAction>();
    }

    /**
     * Check whether a specified point is within the button
     * @param point The point to check
     * @return true if button contains point, false otherwise
     */
    public boolean containsPoint(Vector2i point) {
        Vector2f rPos = new Vector2f(
            point.x - position.x,
            point.y - position.y
        );

        if (rPos.x < 0 || rPos.x > size.x)
            return false;
        if (rPos.y < 0 || rPos.y > size.y)
            return false;

        return true;
    }

    /**
     * Attempts to add a new action to the button
     * Sets action as for enabled button
     * @param o Instance with the method
     * @param m Action to perform on trigger
     * @param t Type of the action (trigger)
     * @return true if successful, false otherwise
     */
    public boolean addAction(Object o, String m, ActionType t) {
        return addAction(o, m, t, true);
    }

    /**
     * Attempts to add a new action to the button
     * @param o Instance with the method
     * @param m Action to perform on trigger
     * @param t Type of the action (trigger)
     * @param e State of the button for action to trigger, t=enabled
     * @return true if successful, false otherwise
     */
    public boolean addAction(Object o, String m, ActionType t, boolean e) {
        Class<?> c = o.getClass();
        Method method = null;
        try {
            method = c.getMethod(m);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        if (method == null) return false;

        ButtonAction a = new ButtonAction(o, method, t, e);
        actions.add(a);

        return true;
    }

    public void setPosition(float x, float y) {
        setPosition(new Vector2f(x, y));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    public void setHeldColor(Color heldColor) {
        this.heldColor = heldColor;
    }

    /**
     * Change whether the button is enabled
     * @param b true = enable, false = disable
     */
    public void setEnabled(boolean b) {
        boolean changed = b != enabled;
        enabled = b;

        //if there's no change then no need to update anything
        if (!changed) return;
    }

    /**
     * Changes whether the button is visible
     * NB: user's responsibility to disable the button if needed
     * @param b true = visible
     */
    public void setVisible(boolean b) {
        this.visible = b;
    }

    /**
     * Sets the button to its opposite state and returns the new state
     * @return true = now enabled, false = now disabled
     */
    public boolean toggleEnabled() {
        setEnabled(!enabled);

        return enabled;
    }

    public void setPosition(Vector2f newPos) {
        this.position = newPos;
    }

    public Vector2f getPosition() {
        return position;
    }

    public int getWidth() {
        return size.x;
    }

    public int getHeight() {
        return size.y;
    }

    public Vector2i getSize() {
        return size;
    }

    public void setSprite(Sprite newSprite) {
        this.sourceSprite = newSprite;
        regenerateSprites();
    }

    public void setDisabledSprite(Sprite newSprite) {
        this.sourceDisabledSprite = newSprite;
        regenerateSprites();
    }

    public void setFillMode(UI.SpriteFillMode fillMode) {
        this.fillMode = fillMode;
        regenerateSprites();
    }

    /**
     * Called when the source sprite or fill mode is changed
     */
    private void regenerateSprites() {
        enabledSprite = UI.generateFillSprite(
            sourceSprite, size, fillMode
        );

        disabledSprite = UI.generateFillSprite(
            sourceDisabledSprite, size, fillMode
        );
    }

    @Override
    public void update(Observable o, Object event) {
        Event e = (Event)event;
        Vector2i mPos = Mouse.getPosition(FullThrottle.getWindow());
        MouseButtonEvent mBEvent;

        if (e.type == Event.Type.MOUSE_BUTTON_RELEASED) {
            mBEvent = e.asMouseButtonEvent();
            if (containsPoint(mPos) && (heldLeft || heldRight))
                if (mBEvent.button == Mouse.Button.LEFT && heldLeft) {
                    heldLeft = false;
                    actionTriggered(ActionType.LEFT_CLICK);
                } else if (mBEvent.button == Mouse.Button.RIGHT && heldRight) {
                    heldRight = false;
                    actionTriggered(ActionType.RIGHT_CLICK);
                }
        }

        if (e.type == Event.Type.MOUSE_MOVED) {
            if (containsPoint(mPos)) {
                hovered = true;
            } else {
                hovered = false;
            }

            if (hovered && !lastHovered)
                actionTriggered(ActionType.ENTER);

            if (!hovered && lastHovered)
                actionTriggered(ActionType.EXIT);

            lastHovered = hovered;
        }
        
        if (e.type == Event.Type.MOUSE_BUTTON_PRESSED) {
            mBEvent = e.asMouseButtonEvent();
            if (containsPoint(mPos))
                if (mBEvent.button == Mouse.Button.LEFT) {
                    heldLeft = true;
                } else if (mBEvent.button == Mouse.Button.RIGHT) {
                    heldRight = true;
                }
        }
    }

    /**
     * Called when the button is clicked (pressed and released)
     * Attempts to invoke the button's action
     */
    public void actionTriggered(ActionType t) {
        for (ButtonAction a : actions) {
            if (a.isType(t) && (enabled == a.enabled)) {
                a.execute();
            }
        }
    }

    @Override
    public void draw(RenderTarget target, RenderStates states) {
        if (!visible) return;

        this.activeSprite = enabled ? enabledSprite : disabledSprite;
        this.activeSprite.setPosition(this.position);
        
        if ((heldLeft || heldRight) && enabled) {
            this.activeSprite.setColor(heldColor);
        } else if (hovered && enabled) {
            this.activeSprite.setColor(hoverColor);
        } else {
            this.activeSprite.setColor(defaultColor);
        }

        this.activeSprite.draw(target, states);
    }
}