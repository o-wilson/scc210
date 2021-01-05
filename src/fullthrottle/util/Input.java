package fullthrottle.util;

import java.util.ArrayList;

import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse.Button;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;
import org.jsfml.window.event.MouseButtonEvent;

/**
 * This is a class to store currently held keys, as well as
 * keys that have been pressed or released during the current frame
 * This is a static class and shouldn't be instantiated
 */
public final class Input {

    private static ArrayList<Key> keysHeld = new ArrayList<>();
    private static ArrayList<Key> keysPressed = new ArrayList<>();
    private static ArrayList<Key> keysReleased = new ArrayList<>();

    private static ArrayList<Button> buttonsHeld = new ArrayList<>();
    private static ArrayList<Button> buttonsPressed = new ArrayList<>();
    private static ArrayList<Button> buttonsReleased = new ArrayList<>();

    private Input() {

    }

    /**
     * At the start of each frame, the "pressed" and "released"
     * flages of each key and button is reset
     */
    public static void clearFlags() {
        keysPressed.clear();
        keysReleased.clear();

        buttonsPressed.clear();
        buttonsReleased.clear();
    }

    /**
     * Debug method to generate string of pressed keys
     * @return String of keys in the order they were pressed
     */
    private static String currentKeys() {
        String keys = "";
        for (Key k : keysHeld) {
            keys += k.toString();
        }
        return keys;
    }

    private static String currentButtons() {
        String buttons = "";
        for (Button b : buttonsHeld) {
            buttons += b.toString();
        }
        return buttons;
    }

    //Key Events

    /**
     * Called from the main game loop to update
     * the current state of the keys
     * @param e the event polled from the window
     */
    public static void keyEvent(Event e) {
        KeyEvent event = e.asKeyEvent();
        if (event == null) return;

        if (event.type == Event.Type.KEY_PRESSED) {
            keysHeld.add(event.key);
            keysPressed.add(event.key);
        } else if (event.type == Event.Type.KEY_RELEASED) {
            keysHeld.remove(event.key);
            keysReleased.add(event.key);
        }

        // System.out.println(currentKeys());
    }

    /**
     * Check whether a specified key is currently held
     * @param key the key to check
     * @return true if held, false otherwise
     */
    public static boolean getKey(Key key) {
        return keysHeld.contains(key);
    }

    /**
     * Check whether a specified key has been pressed
     * during the current frame
     * @param key the key to check
     * @return true if just pressed, false otherwise
     */
    public static boolean getKeyDown(Key key) {
        return keysPressed.contains(key);
    }

    /**
     * Check whether a specified key has been released
     * during the current frame
     * @param key the key to check
     * @return true if just released, false otherwise
     */
    public static boolean getKeyUp(Key key) {
        return keysReleased.contains(key);
    }


    
    //Mouse Events

    /**
     * Called from the main game loop to update
     * the current state of the mouse
     * @param e the event polled from the window
     */
    public static void mouseEvent(Event e) {
        MouseButtonEvent event = e.asMouseButtonEvent();
        if (event == null) return;

        if (event.type == Event.Type.MOUSE_BUTTON_PRESSED) {
            buttonsHeld.add(event.button);
            buttonsPressed.add(event.button);
        } else if (event.type == Event.Type.MOUSE_BUTTON_RELEASED) {
            buttonsHeld.remove(event.button);
            buttonsReleased.add(event.button);
        }

        // System.out.println(currentButtons());
    }

    /**
     * Check whether a specified button is currently held
     * @param button the button to check
     * @return true if held, false otherwise
     */
    public static boolean getMouseButton(Button button) {
        return buttonsHeld.contains(button);
    }

    /**
     * Check whether a specified button has been pressed
     * during the current frame
     * @param button the button to check
     * @return true if just pressed, false otherwise
     */
    public static boolean getMouseButtonDown(Button button) {
        return buttonsPressed.contains(button);
    }

    /**
     * Check whether a specified button has been released
     * during the current frame
     * @param button the button to check
     * @return true if just released, false otherwise
     */
    public static boolean getMouseButtonUp(Button button) {
        return buttonsReleased.contains(button);
    }
}
