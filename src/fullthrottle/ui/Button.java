package fullthrottle.ui;

import fullthrottle.FullThrottle;
import fullthrottle.gfx.FTTexture;
import fullthrottle.ui.UI;

import java.lang.Class;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Observer;
import java.util.Observable;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector2f;

import org.jsfml.window.event.*;
import org.jsfml.window.Mouse;

/**
 * UI Button that can display text and a sprite
 */
@SuppressWarnings("deprecation")
public class Button implements Observer, Drawable {

    private Vector2f position;
    private Vector2i size;
    private String text;
    private Sprite sourceSprite;
    private Sprite drawnSprite;
    private UI.SpriteFillMode fillMode;

    private Object actionObject;
    private Method clickAction;

    private boolean hovered;
    private boolean held;
    
    /**
     * Basic button taking vector2i for position and size
     * @param pos Button's position
     * @param size Button's size
     */
    public Button(Vector2f pos, Vector2i size) {
        this(pos, size, "", new Sprite(), UI.SpriteFillMode.STRETCH);
    }

    /**
     * Button with text
     * @param pos Button's position
     * @param size Button's size
     * @param text Button's text
     */
    public Button(Vector2f pos, Vector2i size, String text) {
        this(pos, size, text, new Sprite(), UI.SpriteFillMode.STRETCH);
    }

    /**
     * Button with text and a background sprite
     * @param pos Button's position
     * @param size Button's size
     * @param text Button's text
     * @param sprite Button's sprite
     */
    public Button(Vector2f pos, Vector2i size, String text, Sprite sprite) {
        this(pos, size, text, sprite, UI.SpriteFillMode.STRETCH);
    }

    /**
     * Button with text, background sprite and a specified SpriteFillMode
     * @param pos Button's position
     * @param size Button's size
     * @param text Button's text
     * @param sprite Button's sprite
     * @param fillMode Fill mode for sprite, see {@link UI#SpriteFillMode}
     */
    public Button(Vector2f pos, Vector2i size, String text, Sprite sprite, UI.SpriteFillMode fillMode) {
        this.position = pos;
        this.size = size;
        this.text = text;
        this.fillMode = fillMode;
        this.sourceSprite = sprite;
        this.drawnSprite = UI.generateFillSprite(sprite, this.size, this.fillMode);

        held = false;
        hovered = false;
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
     * Attempts to set the button's action to a method on an object
     * @param object Instance with the method
     * @param method Action to perform when button clicked
     * @return true if successful, false otherwise
     */
    public boolean setAction(Object object, String method) {
        Class<?> c = object.getClass();
        this.actionObject = object;
        Method m = null;
        try {
            m = c.getMethod(method);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        this.clickAction = m;

        return (m != null);
    }

    @Override
    public void update(Observable o, Object event) {
        Event e = (Event)event;
        Vector2i mPos = Mouse.getPosition(FullThrottle.getWindow());

        if (e.type == Event.Type.MOUSE_BUTTON_RELEASED) {
            if (held && containsPoint(mPos)) {
                actionTriggered();
            }

            held = false;
        }

        if (e.type == Event.Type.MOUSE_MOVED) {
            if (containsPoint(mPos)) {
                hovered = true;
            } else {
                hovered = false;
            }
        }
        
        if (e.type == Event.Type.MOUSE_BUTTON_PRESSED) {
            MouseButtonEvent mouseEvent = e.asMouseButtonEvent();
            if (mouseEvent.button == Mouse.Button.LEFT) {
                if (containsPoint(mPos))
                    held = true;
            }
        }
    }

    /**
     * Called when the button is clicked (pressed and released)
     * Attempts to invoke the button's action
     */
    public void actionTriggered() {
        if (clickAction != null && actionObject != null) {
            try {
                clickAction.invoke(actionObject);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void draw(RenderTarget target, RenderStates states) {
        this.drawnSprite.setPosition(this.position);
        
        if (held) {
            this.drawnSprite.setColor(new Color(100, 100, 100));
        } else if (hovered) {
            this.drawnSprite.setColor(new Color(200, 200, 200));
        } else {
            this.drawnSprite.setColor(Color.WHITE);
        }

        this.drawnSprite.draw(target, states);
    }
}