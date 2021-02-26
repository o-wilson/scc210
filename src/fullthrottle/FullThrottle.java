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
import fullthrottle.gfx.Animation;
import fullthrottle.gfx.Animator;
import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.ParallaxBackground;
import fullthrottle.gfx.ParallaxBackground.Direction;
import fullthrottle.sfx.FTMusic;
import fullthrottle.gfx.Renderer;
import fullthrottle.gfx.SpriteSequence;
import fullthrottle.gfx.Spritesheet;
import fullthrottle.ui.Button;
import fullthrottle.ui.Button.ActionType;
import fullthrottle.ui.ButtonManager;
import fullthrottle.ui.ProgressBar;
import fullthrottle.ui.ReelInput;
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
    private int score = 0;
    private double score_mult = 1;
    private static GameManager gameManager;

    // Loading screen
    public ProgressBar loadingBar;

    // Menu
    private FTMusic music= new FTMusic();
    public UISprite title;
    public Button settingsButton, playButton, highScoreButton;
    public ParallaxBackground background;
    public LeaderBoard leaderBoard;

    // Gameplay
    public Player player;
    public Road road;
    public ProgressBar fuelBar;

    // Shop
    

    // Game Over
    public UISprite gameOverText;
    public ReelInput nameInput;
    public Text gameOverScoreText;
    public Button mainMenuButton, submitScoreButton, playAgainButton;

    public Animator mmbAnimator, ssbAnimator, pagAnimator;

    public FullThrottle() {
        init();

        Texture titleT = new FTTexture("./res/Title.png");
        title = new UISprite(titleT);
        float titleW = title.getGlobalBounds().width;
        float titleH = title.getGlobalBounds().height;
        title.setPosition((WINDOW_WIDTH - titleW) / 2, (WINDOW_HEIGHT - titleH) / 2);
        title.fadeIn(2);

        Vector2f loadingBarSize = new Vector2f(WINDOW_WIDTH + 100, 32);
        Vector2f loadingBarPos = new Vector2f(0, WINDOW_HEIGHT * (4 / 5f));
        loadingBar = new ProgressBar(loadingBarPos, loadingBarSize, 100, new FTTexture("./res/LoadingBar.png"),
                new Vector2i(16, 16), 10, new float[] { 100 });
        loadingBar.lerpUpdate(false);

        for (int i = 120; i >= 0; i--) {
            TimeManager.update();
            window.clear();

            float titleY = title.getGlobalBounds().top;
            titleY = (50 - titleY) * .03f;
            title.move(0, titleY);
            title.draw(window, new RenderStates(BlendMode.ALPHA));

            loadingBar.setValue((120 - i) / 6f);
            for (int u = 0; u < 100; u++)
                loadingBar.update();
            loadingBar.draw(window, new RenderStates(BlendMode.ALPHA));

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
        music.play_music("./res/Music/Theme_3.ogg");
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

    /**
     * Initialisation of key utilities like
     * Window, TimeManager, ButtonManager etc
     */
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

    /**
     * Load images, create instances etc.
     */
    private void load() {
        // Menu
        Texture settingsT = new FTTexture("./res/Settings.png");
        Sprite settingsS = new Sprite(settingsT);
        settingsButton = new Button(
            new Vector2f(10, 10), new Vector2i(64, 64),
            settingsS, UI.SpriteFillMode.STRETCH
        );
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
        leaderBoard.addCloseCallback(this, "hideLeaderBoard", ActionType.LEFT_CLICK);

        background = new ParallaxBackground(window, Direction.LEFT, 3000);

        Texture sky = new FTTexture("./res/BackgroundTest/Sky.png");
        Texture buildings = new FTTexture("./res/BackgroundTest/Buildings.png");

        Sprite skyS = new Sprite(sky);
        skyS.scale(2.8125f, 2.8125f);
        Sprite buildingsS = new Sprite(buildings);
        buildingsS.scale(2.8125f, 2.8125f);

        background.addElement(skyS, 30, Vector2f.ZERO);
        background.addElement(buildingsS, 15, Vector2f.ZERO);
        
        updatables.add(background);



        // Gameplay
        road = new Road(4, 320);
        road.setSpeed(50);

        fuelBar = new ProgressBar(
            new Vector2f(10, 10), new Vector2f(256, 64), 100,
            new FTTexture("./res/FuelBar.png"), new Vector2i(16, 16),
            80, new float[] {25, 50, 100}
        );
        updatables.add(fuelBar);

        player = new Player();



        // Upgrades




        // Game Over
        gameOverText = new UISprite(new FTTexture("./res/GameOver.png"));
        gameOverText.setPosition(new Vector2f(
            (WINDOW_WIDTH - gameOverText.getGlobalBounds().width) / 2,
            50
        ));

        nameInput = new ReelInput(5, new Vector2f((WINDOW_WIDTH - 480) / 2, 384), new Vector2f(480, 192));

        gameOverScoreText = new Text("Score: 0000", UI.DEFAULT_UI_FONT, 48);

        Vector2i gameOverButtonSize = new Vector2i(192, 96);
        Vector2f gameOverButtonScale = Vector2f.componentwiseDiv(new Vector2f(gameOverButtonSize), new Vector2f(64, 32));

        Texture mmbTexture = new FTTexture("./res/MainMenuButton.png");
        Spritesheet mmbSheet = new Spritesheet(mmbTexture, new Vector2i(64, 32));
        SpriteSequence mmbIdleSeq = new SpriteSequence(mmbSheet, 0, 0);
        SpriteSequence mmbEnterSeq = new SpriteSequence(mmbSheet, 7, 13);
        SpriteSequence mmbExitSeq = new SpriteSequence(mmbSheet, 14, 20);
        Animation mmbIdleAnim = new Animation(mmbIdleSeq, 14, false);
        Animation mmbEnterAnim = new Animation(mmbEnterSeq, 14, false);
        Animation mmbExitAnim = new Animation(mmbExitSeq, 14, false);
        mmbAnimator = new Animator();
        mmbAnimator.addAnimation("IDLE", mmbIdleAnim);
        mmbAnimator.addAnimation("ENTER", mmbEnterAnim);
        mmbAnimator.addAnimation("EXIT", mmbExitAnim);
        mmbAnimator.setCurrentAnimation("IDLE");
        mmbAnimator.setScale(gameOverButtonScale);
        Vector2f mmbPos = new Vector2f((WINDOW_WIDTH - (gameOverButtonSize.x * 2 + 48)) / 2, 592);
        mainMenuButton = new Button(mmbPos, gameOverButtonSize, mmbAnimator);
        mainMenuButton.addAction(this, "mmbEnter", ActionType.ENTER);
        mainMenuButton.addAction(this, "mmbExit", ActionType.EXIT);
        mainMenuButton.addAction(this, "mmbClick", ActionType.LEFT_CLICK);
        buttonManager.addObserver(mainMenuButton);

        Texture ssbTexture = new FTTexture("./res/SubmitButton.png");
        Spritesheet ssbSheet = new Spritesheet(ssbTexture, new Vector2i(64, 32));
        SpriteSequence ssbIdleSeq = new SpriteSequence(ssbSheet, 0, 0);
        SpriteSequence ssbEnterSeq = new SpriteSequence(ssbSheet, 7, 13);
        SpriteSequence ssbExitSeq = new SpriteSequence(ssbSheet, 14, 20);
        Animation ssbIdleAnim = new Animation(ssbIdleSeq, 14, false);
        Animation ssbEnterAnim = new Animation(ssbEnterSeq, 14, false);
        Animation ssbExitAnim = new Animation(ssbExitSeq, 14, false);
        ssbAnimator = new Animator();
        ssbAnimator.addAnimation("IDLE", ssbIdleAnim);
        ssbAnimator.addAnimation("ENTER", ssbEnterAnim);
        ssbAnimator.addAnimation("EXIT", ssbExitAnim);
        ssbAnimator.setCurrentAnimation("IDLE");
        ssbAnimator.setScale(gameOverButtonScale);
        Vector2f ssbPos = new Vector2f(500, 500);
        submitScoreButton = new Button(ssbPos, gameOverButtonSize, ssbAnimator);
        submitScoreButton.setHeldColor(new Color(50, 50, 50));
        submitScoreButton.addAction(this, "ssbEnter", ActionType.ENTER, true);
        submitScoreButton.addAction(this, "ssbExit", ActionType.EXIT, true);
        submitScoreButton.addAction(this, "ssbClick", ActionType.LEFT_CLICK, true);
        buttonManager.addObserver(submitScoreButton);

        Texture pagTexture = new FTTexture("./res/PlayAgainButton.png");
        Spritesheet pagSheet = new Spritesheet(pagTexture, new Vector2i(64, 32));
        SpriteSequence pagIdleSeq = new SpriteSequence(pagSheet, 0, 0);
        SpriteSequence pagEnterSeq = new SpriteSequence(pagSheet, 7, 13);
        SpriteSequence pagExitSeq = new SpriteSequence(pagSheet, 14, 20);
        Animation pagIdleAnim = new Animation(pagIdleSeq, 14, false);
        Animation pagEnterAnim = new Animation(pagEnterSeq, 14, false);
        Animation pagExitAnim = new Animation(pagExitSeq, 14, false);
        pagAnimator = new Animator();
        pagAnimator.addAnimation("IDLE", pagIdleAnim);
        pagAnimator.addAnimation("ENTER", pagEnterAnim);
        pagAnimator.addAnimation("EXIT", pagExitAnim);
        pagAnimator.setCurrentAnimation("IDLE");
        pagAnimator.setScale(gameOverButtonScale);
        Vector2f pagPos = new Vector2f((WINDOW_WIDTH - (gameOverButtonSize.x * 2 + 48)) / 2 + 48 + gameOverButtonSize.x, 592);
        playAgainButton = new Button(pagPos, gameOverButtonSize, pagAnimator);
        playAgainButton.addAction(this, "pagEnter", ActionType.ENTER);
        playAgainButton.addAction(this, "pagExit", ActionType.EXIT);
        playAgainButton.addAction(this, "pagClick", ActionType.LEFT_CLICK);
        buttonManager.addObserver(playAgainButton);
    }

    /**
     * Start the game
     */
    private void start() {
        Renderer.clear();

        // Menu
        title.setPosition(title.getGlobalBounds().left, 50);
        Renderer.addDrawable(title);
        Renderer.addDrawable(settingsButton, -50);
        Renderer.addDrawable(playButton, -50);
        Renderer.addDrawable(highScoreButton, -50);
        Renderer.addDrawable(background, 1000);
        Renderer.addDrawable(leaderBoard, -100);

        // Gameplay
        Renderer.addDrawable(road);
        Renderer.addDrawable(fuelBar);
        Renderer.addDrawable(player, 0);

        // Upgrades

        // Game Over
        Renderer.addDrawable(gameOverText);
        Renderer.addDrawable(nameInput, -100);
        Renderer.addDrawable(mainMenuButton, -100);    
        // Start the game manager
        gameManager = new GameManager(this);
        updatables.add(gameManager);
        Renderer.addDrawable(gameManager, -60);
    }

    private void update() {
        for (Updatable u : updatables) {
            u.update();
        }

        score += 5*score_mult;
        if (score==3000*score_mult*score_mult){
            road.setRoadSection(RoadSection.WHITE);
        }
        if (score==6000*score_mult*score_mult){
            road.setRoadSection(RoadSection.YELLOW);
        }
        if (score==9000*score_mult*score_mult){
            road.setRoadSection(RoadSection.DIRT);
            score_mult = score_mult *3;
        }

        if (Input.getKeyDown(Key.NUM1))
            road.setRoadSection(RoadSection.WHITE);
        if (Input.getKeyDown(Key.NUM2))
            road.setRoadSection(RoadSection.YELLOW);
        if (Input.getKeyDown(Key.NUM3))
            road.setRoadSection(RoadSection.DIRT);
        if (Input.getKey(Key.UP))
        if (Input.getKey(Key.DOWN))
        if (Input.getKey(Key.LEFT))
        if (Input.getKey(Key.RIGHT))
            
        if (Input.getKeyDown(Key.P))
            if (gameManager.isPaused())
                gameManager.play();
            else
                gameManager.pause();
    }

    public static RenderWindow getWindow() {
        if (window != null)
            return window;

        return null;
    }

    public static GameManager getGameManager() {
        if (gameManager != null)
            return gameManager;

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
        gameManager.startGame();
    }

    public void showLeaderBoard() {
        playButton.setEnabled(false);
        highScoreButton.setEnabled(false);
        settingsButton.setEnabled(false);
        leaderBoard.setVisible(true);
    }

    public void hideLeaderBoard() {
        leaderBoard.disableCloseButton();
        playButton.setEnabled(true);
        highScoreButton.setEnabled(true);
        settingsButton.setEnabled(true);
    }

    public void mmbEnter() {
        Animation exit = mmbAnimator.getAnimation("EXIT");
        int startFrame = exit.getLength() - exit.getCurrentFrame() - 1;
        mmbAnimator.getAnimation("ENTER").setCurrentFrame(startFrame);
        mmbAnimator.setCurrentAnimation("ENTER");
    }

    public void mmbExit() {
        Animation enter = mmbAnimator.getAnimation("ENTER");
        int startFrame = enter.getLength() - enter.getCurrentFrame() - 1;
        mmbAnimator.getAnimation("EXIT").setCurrentFrame(startFrame);
        mmbAnimator.setCurrentAnimation("EXIT");
    }

    public void mmbClick() {
        gameManager.mainMenu();
    }

    public void ssbEnter() {
        Animation exit = ssbAnimator.getAnimation("EXIT");
        int startFrame = exit.getLength() - exit.getCurrentFrame() - 1;
        ssbAnimator.getAnimation("ENTER").setCurrentFrame(startFrame);
        ssbAnimator.setCurrentAnimation("ENTER");
    }

    public void ssbExit() {
        Animation enter = ssbAnimator.getAnimation("ENTER");
        int startFrame = enter.getLength() - enter.getCurrentFrame() - 1;
        ssbAnimator.getAnimation("EXIT").setCurrentFrame(startFrame);
        ssbAnimator.setCurrentAnimation("EXIT");
    }

    public void ssbClick() {
        gameManager.submitScore();
    }

    public void pagEnter() {
        Animation exit = pagAnimator.getAnimation("EXIT");
        int startFrame = exit.getLength() - exit.getCurrentFrame() - 1;
        pagAnimator.getAnimation("ENTER").setCurrentFrame(startFrame);
        pagAnimator.setCurrentAnimation("ENTER");
    }

    public void pagExit() {
        Animation enter = pagAnimator.getAnimation("ENTER");
        int startFrame = enter.getLength() - enter.getCurrentFrame() - 1;
        pagAnimator.getAnimation("EXIT").setCurrentFrame(startFrame);
        pagAnimator.setCurrentAnimation("EXIT");
    }

    public void pagClick() {
        gameManager.playAgain();
    }

    public static void main(String[] args) {
        new FullThrottle();
    }
}
