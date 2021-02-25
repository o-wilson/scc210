package fullthrottle;

import org.jsfml.system.Vector2f;

import fullthrottle.util.TimeManager;
import fullthrottle.util.Updatable;

public final class GameManager implements Updatable {

    private Road road;

    private Player player;

    private boolean started;
    private boolean paused;
    private float playingTime;

    public GameManager(Road road, Player player) {
        this.road = road;
        this.player = player;

        this.paused = false;
    }

    public void movePlayer(Vector2f moveDirection) {
        if (paused) return;
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
            }
            road.setSpeed(roadSpeedFunction(playingTime));
        }
	}

    public void startGame() {
        road.generateObstacles(true);
        playingTime = 0;
        player.setActive(true);
        this.started = true;
    }

    private float roadSpeedFunction(float t) {
        return Math.min(500, 150 + ((t/2) * 5));
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
}
