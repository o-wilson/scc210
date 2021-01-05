package fullthrottle.ui;

import java.util.Observable;

import org.jsfml.window.event.*;

/**
 * This is an observable class that notifies buttons of relevant mouse inputs such as movement and clicking
 * This is a singleton class so only one instance can exist at a time
 */
@SuppressWarnings("deprecation")
public final class ButtonManager extends Observable {
    private static ButtonManager INSTANCE;

    private ButtonManager() {

    }

    public static ButtonManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ButtonManager();
        }

        return INSTANCE;
    }
    
    public void mouseEvent(Event event) {
        setChanged();
        notifyObservers(event);
    }
}
