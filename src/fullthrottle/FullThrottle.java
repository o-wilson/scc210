package fullthrottle;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.FloatRect;
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

import fullthrottle.Road.RoadSection;
import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.ParallaxBackground;
import fullthrottle.gfx.ParallaxBackground.Direction;
import fullthrottle.gfx.Renderer;
import fullthrottle.ui.ButtonManager;
import fullthrottle.ui.ProgressBar;
import fullthrottle.ui.UI;
import fullthrottle.util.Input;
import fullthrottle.util.TimeManager;
import fullthrottle.util.Updatable;


public class FullThrottle {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;

    private static RenderWindow window;

    private ButtonManager buttonManager;

    private ArrayList<Updatable> updatables;

    private Text fpsCount;
    private boolean showFps = false;

    //Road demo
    public Road actualRoad;
    private ProgressBar pb;

    //Movement test
    public Player pPlayer;

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
        //Initialisation
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

        updatables = new ArrayList<>();



        //Title
        // Texture titleT = new FTTexture("./res/Title.png");
        // Sprite titleS = new Sprite(titleT);
        // Renderer.addDrawable(titleS);
        // float titleW = titleS.getGlobalBounds().width;
        // titleS.setPosition((WINDOW_WIDTH - titleW) / 2, 50);



        //Buttons
        // Texture settingsT = new FTTexture("./res/Settings.png");
        // Sprite settingsS = new Sprite(settingsT);
        // Button settingsButton = new Button(
        //     new Vector2f(10, 10), new Vector2i(64, 64),
        //     settingsS, UI.SpriteFillMode.STRETCH
        // );
        // settingsButton.addAction(this, "settings", ActionType.LEFT_CLICK);
        // Renderer.addDrawable(settingsButton);
        // buttonManager.addObserver(settingsButton);

        // Button playButton = new PlayButton(
        //     Vector2f.ZERO, new Vector2i(192, 96)
        // );
        // buttonManager.addObserver(playButton);
        // playButton.addAction(this, "play_button_test_sounds", ActionType.LEFT_CLICK);

        // Button highScoresButton = new HighScoresButton(
        //     Vector2f.ZERO, new Vector2i(192, 96)
        // );
        // buttonManager.addObserver(highScoresButton);



        // BACKGROUND TEST
        ParallaxBackground bg = new ParallaxBackground(window, Direction.LEFT, 3000);

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
        // bg.addElement(roadS, 5, Vector2f.ZERO);
        // bg.addElement(bushS, 5, new Vector2f(1000, 506.25f), 500);
        
        updatables.add(bg);
        Renderer.addDrawable(bg, 1000);
        

        //ROAD
        actualRoad = new Road(4, 320);
        Renderer.addDrawable(actualRoad);
        updatables.add(actualRoad);
        actualRoad.setSpeed(50);

        //FUEL BAR
        Texture fuelBar = new FTTexture("./res/FuelBar.png");
        pb = new ProgressBar(
            new Vector2f(100, 400), new Vector2f(900, 64), 100,
            fuelBar, new Vector2i(16, 16),
            80, new float[] {25, 50, 100}
        );
        Renderer.addDrawable(pb);
        updatables.add(pb);
        actualRoad.setSpeed(90);

        //PLAYER
        pPlayer = new Player();
        Renderer.addDrawable(pPlayer, 0);
    }

    private void update() {
        for (Updatable u : updatables) {
            u.update();
        }

        if (Input.getKeyDown(Key.NUM1))
            actualRoad.setRoadSection(RoadSection.WHITE);
        if (Input.getKeyDown(Key.NUM2))
            actualRoad.setRoadSection(RoadSection.YELLOW);
        if (Input.getKeyDown(Key.NUM3))
            actualRoad.setRoadSection(RoadSection.DIRT);

        if (Input.getKeyDown(Key.UP))
            actualRoad.increaseSpeed(5);
        else if (Input.getKeyDown(Key.DOWN))
            actualRoad.increaseSpeed(-5);

        if (Input.getKeyDown(Key.LEFT))
            System.out.println(pb.addToValue(-10));
        if (Input.getKeyDown(Key.RIGHT))
            System.out.println(pb.addToValue(10));
            
        Vector2f moveDirection = Vector2f.ZERO;
        if (Input.getKey(Key.W))
            moveDirection = Vector2f.add(moveDirection, new Vector2f(0, -1));
        else if (Input.getKey(Key.S))
            moveDirection = Vector2f.add(moveDirection, new Vector2f(0, 1));

        if (Input.getKey(Key.A))
            moveDirection = Vector2f.add(moveDirection, new Vector2f(-1, 0));
        else if (Input.getKey(Key.D))
            moveDirection = Vector2f.add(moveDirection, new Vector2f(1, 0));
        pPlayer.move(moveDirection);
    }

    public static RenderWindow getWindow() {
        if (window != null)
            return window;

        return null;
    }

    /**
     * Get the coordinates and size of the viewport of the window
     * @return viewport as a FloatRect
     */
    public static FloatRect getViewRect() {
        ConstView v = FullThrottle.getWindow().getView();
        Vector2f halfSize = Vector2f.div(v.getSize(), 2f);
        Vector2f vo = Vector2f.sub(v.getCenter(), halfSize);
        return new FloatRect(vo, v.getSize());
    }

    // public void settings() {
    //     System.out.println("Settings clicked");
    // }

    public static void main(String[] args) {
        new FullThrottle();
    }
}
