package fullthrottle;

import fullthrottle.gfx.*;
import fullthrottle.gfx.ParallaxBackground.Direction;
import fullthrottle.util.TimeManager;
import fullthrottle.util.Updatable;
import fullthrottle.ui.*;
import fullthrottle.ui.Button.ActionType;

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

    // private ArrayList<Drawable> drawables;
    private ArrayList<Updatable> updatables;

    public FullThrottle() {
        start();
        
        while(window.isOpen()) {
            TimeManager.update();
            update();

            // System.out.println(1 / TimeManager.deltaTime());

            window.clear(Color.BLACK);

            Renderer.render(window);
            
            window.display();

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

        // drawables = new ArrayList<>();
        updatables = new ArrayList<>();

        Texture titleT = new FTTexture("./res/Title.png");
        Texture settingsT = new FTTexture("./res/Settings.png");
        Texture buttonT = new FTTexture("./res/Button.png");

        Texture squareButton = new FTTexture("./res/SquareButton.png");
        Texture squareButtonDisabled = new FTTexture("./res/SquareButtonDisabled.png");
        
        Sprite tButton = new Sprite(squareButton);
        Sprite tButtonDisabled = new Sprite(squareButtonDisabled);

        Sprite titleS = new Sprite(titleT);
        Renderer.addDrawable(titleS);
        float titleW = titleS.getGlobalBounds().width;
        titleS.setPosition((WINDOW_WIDTH - titleW) / 2, 50);

        Sprite settingsS = new Sprite(settingsT);
        Button settingsButton = new Button(
            new Vector2f(10, 10), new Vector2i(64, 64),
            settingsS, UI.SpriteFillMode.STRETCH
        );
        settingsButton.addAction(this, "settings", ActionType.LEFT_CLICK);
        Renderer.addDrawable(settingsButton);
        buttonManager.addObserver(settingsButton);

        Button playButton = new Button(
            Vector2f.ZERO, new Vector2i(196, 96)
        );
        float playButtonX = (WINDOW_WIDTH - playButton.getWidth()) /2;
        playButton.setPosition(playButtonX, 400);
        playButton.addAction(this, "play", ActionType.LEFT_CLICK);
        Renderer.addDrawable(playButton);
        buttonManager.addObserver(playButton);

        Button testButton = new Button(
            new Vector2f(400, 400), new Vector2i(128, 128),
            tButton, UI.SpriteFillMode.STRETCH
        );
        testButton.setDisabledSprite(tButtonDisabled);
        testButton.addAction(this, "testEnabled", ActionType.LEFT_CLICK);
        testButton.addAction(this, "testDisabled", ActionType.LEFT_CLICK, false);
        buttonManager.addObserver(testButton);
        Renderer.addDrawable(testButton);

        playButton.addAction(testButton, "toggleEnabled", ActionType.RIGHT_CLICK);


        // BACKGROUND TEST
        ParallaxBackground bg = new ParallaxBackground(window, Direction.LEFT, 700);

        Texture sky = new FTTexture("./res/BackgroundTest/Sky.png");
        Texture buildings = new FTTexture("./res/BackgroundTest/Buildings.png");
        Texture road = new FTTexture("./res/BackgroundTest/Road.png");
        Texture bush = new FTTexture("./res/BackgroundTest/Bush.png");

        Sprite skyS = new Sprite(sky);
        skyS.scale(2.8125f, 2.8125f);
        Sprite buildingsS = new Sprite(buildings);
        buildingsS.scale(2.8125f, 2.8125f);
        Sprite roadS = new Sprite(road);
        roadS.scale(2.8125f, 2.8125f);
        Sprite bushS = new Sprite(bush);
        bushS.scale(2.8125f, 2.8125f);

        bg.addElement(skyS, 30, Vector2f.ZERO);
        bg.addElement(buildingsS, 15, Vector2f.ZERO);
        bg.addElement(roadS, 5, Vector2f.ZERO);
        bg.addElement(bushS, 5, new Vector2f(1000, 506.25f), 500);

        updatables.add(bg);
        Renderer.addDrawable(bg, 1000);
    }

    private void update() {
        for (Updatable u : updatables) {
            u.update();
        }
    }

    public static RenderWindow getWindow() {
        if (window != null)
            return window;

        return null;
    }

    public void settings() {
        System.out.println("Settings clicked");
    }

    public void play() {
        System.out.println("Play game");
    }

    public void testEnabled() {
        System.out.println("Button is enabled");
    }

    public void testDisabled() {
        System.out.println("Button is disabled");
    }

    public static void main(String[] args) {
        new FullThrottle();
    }
}
