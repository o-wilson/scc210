package fullthrottle;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Keyboard.Key;

import fullthrottle.Pickup.PickupType;
import fullthrottle.gfx.Animation;
import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.ParallaxBackground;
import fullthrottle.gfx.SpriteSequence;
import fullthrottle.gfx.Spritesheet;
import fullthrottle.ui.Button;
import fullthrottle.ui.ProgressBar;
import fullthrottle.ui.ReelInput;
import fullthrottle.ui.UISprite;
import fullthrottle.util.HighScoreManager;
import fullthrottle.util.Input;
import fullthrottle.util.TimeManager;
import fullthrottle.util.Updatable;

public final class GameManager implements Updatable, Drawable {

    // MENU
    private UISprite title;
    private Button playButton, highScoreButton, settingsButton;
    private LeaderBoard leaderBoard;
    private ParallaxBackground background;
    // MENU END

    // GAMEPLAY
    private Animation healthAlertAnim;
    private Road road;
    private Player player;
    private HealthManager healthManager;
    private ProgressBar fuelBar;
    // GAMEPLAY END

    // UPGRADES
    // UPGRADES END

    // GAME OVER
    private boolean startGameOver;
    private UISprite gameOverText;
    private ReelInput nameInput;
    private Text gameOverScoreText;
    private Button mainMenuButton, submitScoreButton, playAgainButton;
    // GAME OVER END

    private boolean paused;
    private float playingTime;
    private float score;
    private GameState currentGameState;

    private enum GameState {
        MAIN_MENU,
        GAMEPLAY,
        UPGRADE,
        GAME_OVER
    }

    public GameManager(FullThrottle game) {
        // Menu
        this.title = game.title;
        this.playButton = game.playButton;
        this.highScoreButton = game.highScoreButton;
        this.settingsButton = game.settingsButton;
        this.leaderBoard = game.leaderBoard;
        this.background = game.background;
        // Gameplay
        this.road = game.road;
        this.player = game.player;
        this.fuelBar = game.fuelBar;

        Texture hATexture = new FTTexture("./res/HealthAlert.png");
        Spritesheet hASheet = new Spritesheet(hATexture, new Vector2i(256, 144));
        SpriteSequence hASeq = new SpriteSequence(hASheet);
        healthAlertAnim = new Animation(hASeq, 16, false);
        healthAlertAnim.scale(
            Vector2f.componentwiseDiv(
                new Vector2f(
                    FullThrottle.WINDOW_WIDTH,
                    FullThrottle.WINDOW_HEIGHT
                ),
                new Vector2f(256, 144)
            )
        );
        healthAlertAnim.setPosition(Vector2f.ZERO);
        healthAlertAnim.pause();
        healthAlertAnim.restart();

        healthManager = new HealthManager(new Vector2f(288, 10), 64);
        this.paused = false;
        this.score = 0;
        // Upgrades

        // Game Over
        this.gameOverText = game.gameOverText;
        this.nameInput = game.nameInput;
        this.gameOverScoreText = game.gameOverScoreText;
        this.mainMenuButton = game.mainMenuButton;
        this.submitScoreButton = game.submitScoreButton;
        this.playAgainButton = game.playAgainButton;

        currentGameState = GameState.MAIN_MENU;

        mainMenu();
    }

    public void movePlayer(Vector2f moveDirection) {
        if (paused)
            return;
        player.move(moveDirection);
        Vector2f playerPos = player.getPosition();

        float pX = playerPos.x;
        float pY = playerPos.y;
                if (playerPos.x < 0)
            pX = 0;
        else if (playerPos.x > FullThrottle.WINDOW_WIDTH - player.getSize().x)
            pX = FullThrottle.WINDOW_WIDTH - player.getSize().x;

        if (playerPos.y < road.getTopEdge())
            pY = road.getTopEdge();
        else if (playerPos.y > road.getBottomEdge() - player.getSize().y)
            pY = road.getBottomEdge() - player.getSize().y;
        player.setPosition(new Vector2f(pX, pY));
    }

    @Override
    public void update() {
        movePlayer(new Vector2f(-0.2222f,0));
        if (currentGameState == GameState.MAIN_MENU) {
            road.setSpeed(roadSpeedFunction(playingTime));
        } else if (currentGameState == GameState.GAMEPLAY) {
            fuelBar.addToValue(-5 * TimeManager.deltaTime());
            if(fuelBar.getCurrentValue() <= 0)
                gameOver();
            playingTime += TimeManager.deltaTime();
            if (!paused) {
                road.update();
                playingTime += TimeManager.deltaTime();
                if (playingTime - player.getShiftStart() >= 3){
                    player.setShifted(false);
                }
                if (player.isActive()) {
                    Vector2f moveDirection = Vector2f.ZERO;

                    if (Input.getKey(Key.SPACE) && player.getShifted() == false){
                        player.setSlam(true);
                        if (Input.getKey(Key.A))
                            moveDirection = Vector2f.add(moveDirection, new Vector2f(-20, 0));
                        else if (Input.getKey(Key.D))
                            moveDirection = Vector2f.add(moveDirection, new Vector2f(20, 0));
                        if (Input.getKey(Key.W))
                            moveDirection = Vector2f.add(moveDirection, new Vector2f(0, -20));
                        else if (Input.getKey(Key.S))
                            moveDirection = Vector2f.add(moveDirection, new Vector2f(0, 20));
                        player.setShifted(true);
                        player.setShiftStart(playingTime);
                    }
                    else{
                        player.setSlam(false);
                        if (Input.getKey(Key.A))
                            moveDirection = Vector2f.add(moveDirection, new Vector2f(-1, 0));
                        else if (Input.getKey(Key.D))
                            moveDirection = Vector2f.add(moveDirection, new Vector2f(1, 0));
                        if (Input.getKey(Key.W))
                            moveDirection = Vector2f.add(moveDirection, new Vector2f(0, -1));
                        else if (Input.getKey(Key.S))
                            moveDirection = Vector2f.add(moveDirection, new Vector2f(0, 1));
                    }
                    

                    movePlayer(moveDirection);
                }
            
            }


            Vector2f moveDirection = Vector2f.ZERO;

            if (Input.getKey(Key.A))
                moveDirection = Vector2f.add(moveDirection, new Vector2f(-1, 0));
            else if (Input.getKey(Key.D))
                moveDirection = Vector2f.add(moveDirection, new Vector2f(1, 0));
            if (Input.getKey(Key.W))
                moveDirection = Vector2f.add(moveDirection, new Vector2f(0, -1));
            else if (Input.getKey(Key.S))
                moveDirection = Vector2f.add(moveDirection, new Vector2f(0, 1));

            movePlayer(moveDirection);



            if (road.isPlayerColliding(player.getBounds())) {
                if (!player.getSlam())
                {
                    pause();
                    healthAlertAnim.restart();
                    healthAlertAnim.play();
                    int health = healthManager.removeHealth();
                    if (health == 0) {
                        gameOver();
                        return;
                    }
                }
                
            }

            Pickup p = road.isPlayerOnPickup(player.getBounds());
            if (p != null) {
                if (p.getType() == PickupType.FUEL) {
                    fuelBar.addToValue(10);
                }
            }


            road.setSpeed(roadSpeedFunction(playingTime));
            score += road.getSpeed() * road.getSpeed() * TimeManager.deltaTime() / 10000f;
        } else if (currentGameState == GameState.UPGRADE) {

        } else if (currentGameState == GameState.GAME_OVER) {
            playingTime += TimeManager.deltaTime();
            if (playingTime > 2 && startGameOver) {
                startGameOver = false;
                nameInput.setVisible(true);

                mainMenuButton.setVisible(true);
                mainMenuButton.setEnabled(true);
                submitScoreButton.setVisible(true);
                submitScoreButton.setEnabled(true);
                playAgainButton.setVisible(true);
                playAgainButton.setEnabled(true);
                mainMenuButton.setDefaultColor(Color.WHITE);
                submitScoreButton.setDefaultColor(Color.WHITE);
                playAgainButton.setDefaultColor(Color.WHITE);
            }
        }

        if (!paused) {
            road.update();
        }
    }

    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        if (currentGameState == GameState.MAIN_MENU) {

        } else if (currentGameState == GameState.GAMEPLAY) {
            healthManager.draw(arg0, arg1);
            healthAlertAnim.draw(arg0, arg1);
        } else if (currentGameState == GameState.UPGRADE) {
            healthManager.draw(arg0, arg1);
            healthAlertAnim.draw(arg0, arg1);
        } else if (currentGameState == GameState.GAME_OVER) {
            healthAlertAnim.draw(arg0, arg1);
        }
    }

    public void mainMenu() {
        currentGameState = GameState.MAIN_MENU;

        playButton.setVisible(true);
        playButton.setEnabled(true);
        highScoreButton.setVisible(true);
        highScoreButton.setEnabled(true);
        settingsButton.setVisible(true);
        settingsButton.setEnabled(true);
        leaderBoard.setVisible(false);
        title.setVisible(true);
        road.setVisible(true);
        road.generateObstacles(false);
        gameOverText.setVisible(false);
        nameInput.setVisible(false);
        fuelBar.setVisible(false);

        mainMenuButton.setVisible(false);
        mainMenuButton.setEnabled(false);
        submitScoreButton.setVisible(false);
        submitScoreButton.setEnabled(false);
        playAgainButton.setVisible(false);
        playAgainButton.setEnabled(false);

        road.clearObstacles();
        healthManager.reset();
    }

    public void playAgain() {
        road.clearObstacles();
        score = 0;
        healthManager.reset();
        gameOverText.fadeOut(.6f);
        nameInput.setVisible(false);
        mainMenuButton.setVisible(false);
        submitScoreButton.setVisible(false);
        playAgainButton.setVisible(false);
        mainMenuButton.setEnabled(false);
        submitScoreButton.setEnabled(false);
        playAgainButton.setEnabled(false);

        fuelBar.setToMax();

        startGame();
    }

    public void startGame() {
        if (currentGameState == GameState.MAIN_MENU) {
            title.fadeOut(.6f);
            fuelBar.setToMax();
        }
        fuelBar.setVisible(true);
        playButton.setVisible(false);
        playButton.setEnabled(false);
        highScoreButton.setVisible(false);
        highScoreButton.setEnabled(false);
        settingsButton.setVisible(false);
        settingsButton.setEnabled(false);
        leaderBoard.setVisible(false);
        currentGameState = GameState.GAMEPLAY;
        player.resetPosition();
        player.setVisible(true);
        

        road.generateObstacles(true);
        playingTime = 0;
        player.setActive(true);
    }

    public void startUpgrades() {
        currentGameState = GameState.UPGRADE;
        
        road.generateObstacles(false);
        player.setActive(false);
    }

    public void gameOver() {
        currentGameState = GameState.GAME_OVER;

        player.setVisible(false);
        fuelBar.setVisible(false);
        startGameOver = true;

        gameOverText.fadeIn(2);
        playingTime = 0;
    }

    private float roadSpeedFunction(float t) {
        return Math.min(500, 150 + ((t / 2) * 5));
    }

    public boolean isPaused() {
        return paused;
    }

    public void pause() {
        this.paused = true;
    }

    public void play() {
        this.paused = false;
    }

    public void submitScore() {
        HighScoreManager.addHighScore(nameInput.getString(), (int)score);
        submitScoreButton.setEnabled(false);
        submitScoreButton.setDefaultColor(new Color(120, 120, 120));
    }
}
