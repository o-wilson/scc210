package fullthrottle;

import fullthrottle.gfx.*;
import fullthrottle.ui.*;

import org.jsfml.graphics.*;

import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector2f;

import org.jsfml.window.*;
import org.jsfml.window.event.*;

import java.util.ArrayList;

public class FullThrottle {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;

    private static RenderWindow window;

    private ButtonManager buttonManager;

    private ArrayList<Drawable> drawables;

    public FullThrottle() {
        start();
        
        while(window.isOpen()) {
            update();

            //Handle events
            for(Event event : window.pollEvents()) {
                if(event.type == Event.Type.CLOSED) {
                    //The user pressed the close button
                    window.close();
                }
                
                if (event.asMouseEvent() != null) {
                    buttonManager.mouseEvent(event);
                }
            }
        }
    }

    private void start() {
        window = new RenderWindow(
            new VideoMode(WINDOW_WIDTH, WINDOW_HEIGHT),
            "Full Throttle",
            WindowStyle.TITLEBAR | WindowStyle.CLOSE
        );

        buttonManager = ButtonManager.getInstance();

        drawables = new ArrayList<>();

        Texture titleT = new FTTexture("./res/Title.png");
        Texture settingsT = new FTTexture("./res/Settings.png");
        Texture buttonT = new FTTexture("./res/Button.png");

        Sprite titleS = new Sprite(titleT);
        drawables.add(titleS);
        float titleW = titleS.getGlobalBounds().width;
        titleS.setPosition((WINDOW_WIDTH - titleW) / 2, 50);

        // Sprite settingsS = new Sprite(settingsT);
        // uiSprites.add(settingsS);
        // // settingsS.setScale(2, 2);
        // settingsS.setPosition(10, 10);

        Sprite settingsS = new Sprite(settingsT);
        Button settingsButton = new Button(new Vector2f(10, 10), new Vector2i(64, 64), "", settingsS, Button.SpriteFillMode.STRETCH);
        settingsButton.setAction(this, "settings");
        drawables.add(settingsButton);
        buttonManager.addObserver(settingsButton);
    }

    private void update() {
        window.clear(Color.BLACK);

        RenderStates rs = new RenderStates(BlendMode.ADD);
        for (Drawable d : drawables) {
            d.draw(window, rs);
        }

        window.display();
    }

    public static RenderWindow getWindow() {
        if (window != null)
            return window;

        return null;
    }

    public void settings() {
        System.out.println("Settings clicked");
    }

    public static void main(String[] args) {
        new FullThrottle();
    }
}