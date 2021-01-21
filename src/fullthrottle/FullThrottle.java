package fullthrottle;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

//Exprimental don't touch
import org.jsfml.audio.Music;
import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Image;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;
import org.jsfml.window.event.Event;

import fullthrottle.gfx.Animation;
import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.ParallaxBackground;
import fullthrottle.gfx.ParallaxBackground.Direction;
import fullthrottle.gfx.Renderer;
import fullthrottle.gfx.SpriteSequence;
import fullthrottle.sfx.FTMusic;
import fullthrottle.ui.Button;
import fullthrottle.ui.Button.ActionType;
import fullthrottle.ui.ButtonManager;
import fullthrottle.ui.UI;
import fullthrottle.util.Input;
import fullthrottle.util.TimeManager;
import fullthrottle.util.Updatable;


public class FullThrottle {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;

    private static RenderWindow window;

    private ButtonManager buttonManager;
    //Music stuff don't touch for now pls Java garbage collection is making a problems 
    Music menu_music = new FTMusic("./res/Music/scorched_earth_original.ogg");
    //

    // private ArrayList<Drawable> drawables;
    private ArrayList<Updatable> updatables;

    private Text fpsCount;
    private boolean showFps = false;

    public FullThrottle() {
        start();

        fpsCount = new Text("60 FPS", UI.DEFAULT_UI_FONT);
        fpsCount.setColor(Color.RED);
        fpsCount.setPosition(1150, 10);
        double avgFps = 60;
        
        while(window.isOpen()) {
            TimeManager.update();

            /*
             * first few frames are insanely fast and make
             * the framerate seem unnaturally high, so we ignore them
             */
            if(TimeManager.deltaTime() >= 1f/144)
                avgFps = 0.9 * avgFps + (1 - 0.9) * (1 / TimeManager.deltaTime());

            fpsCount.setString((int)avgFps + " FPS");

            //Handle events
            Input.clearFlags();
            for(Event event : window.pollEvents()) {
                if(event.type == Event.Type.CLOSED) {
                    //The user pressed the close button
                    window.close();
                }
                
                if (event.asMouseEvent() != null) {
                    buttonManager.mouseEvent(event);

                    if (event.asMouseButtonEvent() != null) {
                        Input.mouseEvent(event);
                    }
                }

                if (event.asKeyEvent() != null) {
                    Input.keyEvent(event);
                }
            }
            
            update();

            window.clear(Color.BLACK);

            Renderer.render(window);

            if (Input.getKeyDown(Key.F3))
                showFps = !showFps;
            if (showFps)
                fpsCount.draw(window, new RenderStates(BlendMode.ALPHA));
            
            window.display();
        }
    }

    private void start() {

        window = new RenderWindow(
            new VideoMode(WINDOW_WIDTH, WINDOW_HEIGHT),
            "Full Throttle",
            WindowStyle.TITLEBAR | WindowStyle.CLOSE
        );
        window.setKeyRepeatEnabled(false);

        Image icon = new Image();
        try {
            icon.loadFromFile(Paths.get("./res/Icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        window.setIcon(icon);

        buttonManager = ButtonManager.getInstance();

        // drawables = new ArrayList<>();
        updatables = new ArrayList<>();

        Texture titleT = new FTTexture("./res/Title.png");
        Texture settingsT = new FTTexture("./res/Settings.png");

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

        //Play Button test
        Button playButton = new PlayButton(
            Vector2f.ZERO, new Vector2i(192, 96)
        );
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
        ParallaxBackground bg = new ParallaxBackground(window, Direction.LEFT, 3000);

        /*Texture sky = new FTTexture("./res/BackgroundTest/Sky.png");
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
        bg.addElement(bushS, 5, new Vector2f(1000, 506.25f), 500);*/
        Texture longRoad = new FTTexture("./res/RoadFull.png");
        Sprite fullRoadS = new Sprite(longRoad);
        fullRoadS.scale(2.8125f, 2.8125f);
        bg.addElement(fullRoadS, 5, Vector2f.ZERO);

        //Added stuff for the audio
        menu_music.play();
        //END of added stuff for the audio


        updatables.add(bg);
        Renderer.addDrawable(bg, 1000);



        //Sequence/Animation Test
        Texture carSprites = new FTTexture("./res/AnimationTest.png");
        Vector2i fS = new Vector2i(32, 32);
        SpriteSequence carSeq = new SpriteSequence(carSprites, fS);
        Animation carAnim = new Animation(carSeq, 8, true);
        Renderer.addDrawable(carAnim);
        carAnim.setScale(new Vector2f(5, 5));
        carAnim.setPosition(new Vector2f(900, 450));
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

    public void playEnter() {
        
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
