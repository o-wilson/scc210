package fullthrottle;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Image;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;
import org.jsfml.window.event.Event;

import fullthrottle.Obstacle.ObstacleType;
import fullthrottle.Road.RoadSection;
import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.ParallaxBackground;
import fullthrottle.gfx.ParallaxBackground.Direction;
import fullthrottle.gfx.Renderer;
import fullthrottle.ui.Button;
import fullthrottle.ui.Button.ActionType;
import fullthrottle.ui.ButtonManager;
import fullthrottle.ui.ProgressBar;
import fullthrottle.ui.UI;
import fullthrottle.ui.UISprite;
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

    // Loading screen
    public ProgressBar loadingBar;

    // Menu
    public UISprite title;
    public Button settingsButton, playButton, highScoreButton;
    public ParallaxBackground background;
    public LeaderBoard leaderBoard;

    // Gameplay
    public Road gameRoad;
    private ProgressBar fuelBar;

    // Shop

    // Road demo

    // Movement test
    public Player pPlayer;

    // Misc Testing

    public FullThrottle() {
        init();

        Texture titleT = new FTTexture("./res/Title.png");
        title = new UISprite(titleT);
        float titleW = title.getGlobalBounds().width;
        float titleH = title.getGlobalBounds().height;
        title.setPosition((WINDOW_WIDTH - titleW) / 2, (WINDOW_HEIGHT - titleH) / 2);

        Vector2f loadingBarSize = new Vector2f(WINDOW_WIDTH + 100, 32);
        Vector2f loadingBarPos = new Vector2f(0, WINDOW_HEIGHT * (4 / 5f));
        loadingBar = new ProgressBar(loadingBarPos, loadingBarSize, 100, new FTTexture("./res/LoadingBar.png"),
                new Vector2i(16, 16), 10, new float[] { 100 });
        loadingBar.lerpUpdate(false);

        for (int i = 120; i >= 0; i--) {
            window.clear();

            float titleY = title.getGlobalBounds().top;
            titleY = (50 - titleY) * .03f;
            title.move(0, titleY);
            title.draw(window, new RenderStates(BlendMode.ALPHA));

            loadingBar.setValue((120 - i) / 6f);
            for (int u = 0; u < 100; u++)
                loadingBar.update();
            loadingBar.draw(window, new RenderStates(BlendMode.ALPHA));

            VertexArray va = new VertexArray(PrimitiveType.QUADS);
            Color fadeAmount = new Color(0, 0, 0, (255/120) * i);
            va.add(new Vertex(Vector2f.ZERO, fadeAmount));
            va.add(new Vertex(new Vector2f(WINDOW_WIDTH, 0), fadeAmount));
            va.add(new Vertex(new Vector2f(WINDOW_WIDTH, WINDOW_HEIGHT), fadeAmount));
            va.add(new Vertex(new Vector2f(0, WINDOW_HEIGHT), fadeAmount));
            va.draw(window, new RenderStates(BlendMode.ALPHA));

            window.display();
        }

        load();

        loadingBar.lerpUpdate(true);
        for (int i = 20; i < 120; i++) {
            if (i > 30 && i < 80) loadingBar.setValue(i);
            else if (i >= 80)
                loadingBar.setValue(((i / 2f) + 40 <= 100) ? (i / 2f) + 40 : 100);
            else
                loadingBar.setValue((i / 2f) + 10);
            
            TimeManager.update();
            loadingBar.update();
            window.clear();
            RenderStates rs = new RenderStates(BlendMode.ALPHA);
            loadingBar.draw(window, rs);
            title.draw(window, rs);
            window.display();
        }

        start();

        fpsCount = new Text("60 FPS", UI.DEFAULT_UI_FONT);
        fpsCount.setColor(Color.RED);
        fpsCount.setPosition(1150, 10);
        double avgFps = 60;
        window.setFramerateLimit(0);

        while (window.isOpen()) {
            //cap fps to 60
            if (TimeManager.elapsedTime() < 1/61f) continue;
            TimeManager.update();

            avgFps = 0.9 * avgFps + (1 - 0.9) * (1 / TimeManager.deltaTime());

            fpsCount.setString((int) avgFps + " FPS");

            // Handle events
            Input.clearFlags();
            for (Event event : window.pollEvents()) {
                if (event.type == Event.Type.CLOSED) {
                    // The user pressed the close button
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

    private void init() {
        // Initialisation
        window = new RenderWindow(new VideoMode(WINDOW_WIDTH, WINDOW_HEIGHT), "Full Throttle",
                WindowStyle.TITLEBAR | WindowStyle.CLOSE);
        window.setKeyRepeatEnabled(false);
        window.setFramerateLimit(120);

        Image icon = new Image();
        try {
            icon.loadFromFile(Paths.get("./res/Icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        window.setIcon(icon);

        buttonManager = ButtonManager.getInstance();

        updatables = new ArrayList<>();

        TimeManager.update();
    }

    private void load() {
        // Load menu elements
        Texture settingsT = new FTTexture("./res/Settings.png");
        Sprite settingsS = new Sprite(settingsT);
        settingsButton = new Button(
            new Vector2f(10, 10), new Vector2i(64, 64),
            settingsS, UI.SpriteFillMode.STRETCH
        );
        // settingsButton.addAction(this, "settings", ActionType.LEFT_CLICK);
        buttonManager.addObserver(settingsButton);
        
        playButton = new PlayButton(
            Vector2f.ZERO, new Vector2i(192, 96)
        );
        playButton.addAction(this, "startGame", ActionType.LEFT_CLICK);
        buttonManager.addObserver(playButton);

        highScoreButton = new HighScoresButton(
            Vector2f.ZERO, new Vector2i(192, 96)
        );
        highScoreButton.addAction(this, "showLeaderBoard", ActionType.LEFT_CLICK);
        buttonManager.addObserver(highScoreButton);

        leaderBoard = new LeaderBoard();



        background = new ParallaxBackground(window, Direction.LEFT, 3000);

        Texture sky = new FTTexture("./res/BackgroundTest/Sky.png");
        Texture buildings = new FTTexture("./res/BackgroundTest/Buildings.png");
        // Texture road = new FTTexture("./res/BackgroundTest/Road.png");
        // Texture bush = new FTTexture("./res/BackgroundTest/Bush.png");

        Sprite skyS = new Sprite(sky);
        skyS.scale(2.8125f, 2.8125f);
        Sprite buildingsS = new Sprite(buildings);
        buildingsS.scale(2.8125f, 2.8125f);
        // Sprite roadS = new Sprite(road);
        // roadS.scale(2.8125f, 2.8125f);
        // Sprite bushS = new Sprite(bush);
        // bushS.scale(2.8125f, 2.8125f);

        background.addElement(skyS, 30, Vector2f.ZERO);
        background.addElement(buildingsS, 15, Vector2f.ZERO);
        // background.addElement(roadS, 5, Vector2f.ZERO);
        // background.addElement(bushS, 5, new Vector2f(1000, 506.25f), 500);
        
        updatables.add(background);



        // Load game elements
        gameRoad = new Road(4, 320);
        updatables.add(gameRoad);
        gameRoad.setSpeed(50);

        fuelBar = new ProgressBar(
            new Vector2f(10, 10), new Vector2f(256, 64), 100,
            new FTTexture("./res/FuelBar.png"), new Vector2i(16, 16),
            80, new float[] {25, 50, 100}
        );
        updatables.add(fuelBar);

        pPlayer = new Player();
    }

    private void start() {
        Renderer.clear();

        // Add menu elements
        title.setPosition(title.getGlobalBounds().left, 50);
        Renderer.addDrawable(title);

        Renderer.addDrawable(settingsButton, -50);
        Renderer.addDrawable(playButton, -50);
        Renderer.addDrawable(highScoreButton, -50);
        
        Renderer.addDrawable(background, 1000);

        Renderer.addDrawable(leaderBoard);
        leaderBoard.setVisible(false);
        


        Renderer.addDrawable(gameRoad);
        gameRoad.setVisible(true);
        gameRoad.generateObstacles(false);
        Renderer.addDrawable(fuelBar);
        fuelBar.setVisible(false);

        Renderer.addDrawable(pPlayer, 0);
        pPlayer.setVisible(true);
    }

    private void update() {
        for (Updatable u : updatables) {
            u.update();
        }

        if (Input.getKeyDown(Key.NUM1))
            gameRoad.setRoadSection(RoadSection.WHITE);
        if (Input.getKeyDown(Key.NUM2))
            gameRoad.setRoadSection(RoadSection.YELLOW);
        if (Input.getKeyDown(Key.NUM3))
            gameRoad.setRoadSection(RoadSection.DIRT);

        if (Input.getKeyDown(Key.UP))
            gameRoad.increaseSpeed(5);
        else if (Input.getKeyDown(Key.DOWN))
            gameRoad.increaseSpeed(-5);

        if (Input.getKeyDown(Key.LEFT))
            fuelBar.addToValue(-10);
        if (Input.getKeyDown(Key.RIGHT))
            fuelBar.addToValue(10);
            

        
        Vector2f moveDirection = Vector2f.ZERO;
    
        if (Input.getKey(Key.A))
            moveDirection = Vector2f.add(moveDirection, new Vector2f(-1, 0));
        else if (Input.getKey(Key.D))
            moveDirection = Vector2f.add(moveDirection, new Vector2f(1, 0));
        if (Input.getKey(Key.W))
            moveDirection = Vector2f.add(moveDirection, new Vector2f(0, -1));
        else if (Input.getKey(Key.S))
            moveDirection = Vector2f.add(moveDirection, new Vector2f(0, 1));

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

    public void settings() {
        System.out.println("Settings clicked");
    }

    public void startGame() {
        fuelBar.setVisible(true);
        playButton.setVisible(false);
        playButton.setEnabled(false);
        highScoreButton.setVisible(false);
        highScoreButton.setEnabled(false);
        settingsButton.setVisible(false);
        settingsButton.setEnabled(false);
        title.setVisible(false);
        leaderBoard.setVisible(false);
        gameRoad.setVisible(true);
        gameRoad.generateObstacles(true);
        pPlayer.setVisible(true);
        pPlayer.setActive(true);
    }

    public void showLeaderBoard() {
        leaderBoard.setVisible(true);
    }

    public static void main(String[] args) {
        new FullThrottle();
    }
}
