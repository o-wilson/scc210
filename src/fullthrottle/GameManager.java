package fullthrottle;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Keyboard.Key;

import fullthrottle.gfx.Animation;
import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.Renderer;
import fullthrottle.gfx.SpriteSequence;
import fullthrottle.gfx.Spritesheet;
import fullthrottle.util.Input;
import fullthrottle.util.TimeManager;
import fullthrottle.util.Updatable;

public final class GameManager implements Updatable, Drawable {

    private Road road;

    private Player player;

    private boolean started;
    private boolean paused;
    private float playingTime;

    private Animation healthAlertAnim;

    public GameManager(Road road, Player player) {
        this.road = road;
        this.player = player;

        this.paused = false;

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
        if (!paused) {
            road.update();
            if (started) {
                playingTime += TimeManager.deltaTime();

                if (player.isActive()) {
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
                }

                if (road.isPlayerColliding(player.getBounds())) {
                    pause();
                    healthAlertAnim.restart();
                    healthAlertAnim.play();
                }
            }
            road.setSpeed(roadSpeedFunction(playingTime));
        }

        System.out.println(healthAlertAnim.getCurrentFrame());
    }

    public void startGame() {
        road.generateObstacles(true);
        playingTime = 0;
        player.setActive(true);
        this.started = true;
    }

    private float roadSpeedFunction(float t) {
        return Math.min(500, 150 + ((t / 2) * 5));
    }

    public boolean isPaused() {
        return paused;
    }

    public void pause() {
        if (started)
            this.paused = true;
    }

    public void play() {
        if (started)
            this.paused = false;
    }

    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        healthAlertAnim.draw(arg0, arg1);
    }
}
