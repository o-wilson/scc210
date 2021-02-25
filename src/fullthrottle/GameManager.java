package fullthrottle;

import org.jsfml.system.Vector2f;

public final class GameManager {

    private Road road;

    private Player player;

    public GameManager(Road road, Player player) {
        this.road = road;
        this.player = player;
    }

    public void movePlayer(Vector2f moveDirection) {
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
}
