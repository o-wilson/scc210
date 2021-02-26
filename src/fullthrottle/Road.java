package fullthrottle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import fullthrottle.Obstacle.ObstacleType;
import fullthrottle.Pickup.PickupType;
import fullthrottle.gfx.Animation;
import fullthrottle.gfx.FTTexture;
import fullthrottle.util.TimeManager;
import fullthrottle.util.Updatable;

/**
 * A class that creates a dynamically generating road that scrolls
 * at a variable speed, road type can be changed while running so 
 * only one should be needed
 */
public final class Road implements Drawable, Updatable {
    private boolean bVisible;
    private boolean generateObstacles;
    /**
     * Dimensions of each tile in the spritesheet
     */
    public static Vector2i ROAD_TILE_DIMENSIONS = new Vector2i(16, 16);
    /**
     * The multiplier used to ensure the road is the specified height
     */
    public static float ROAD_TILE_SCALE = 1;
    /**
     * The texture containing the road tiles spritesheet
     */
    public static Texture ROAD_TEXTURE = new FTTexture("./res/Road.png");

    /**
     * Used if no RoadSection is specified on creation
     */
    public static RoadSection DEFAULT_ROAD_SECTION = RoadSection.WHITE;

    public static int MAX_OBSTACLES = 15;

    /**
     * Used to calculate texture coordinates based on
     * the top left coordinate; vertex order: TL TR BR BL
     */
    private static Vector2f[] VERTEX_OFFSETS = new Vector2f[] {
        Vector2f.ZERO,
        new Vector2f(ROAD_TILE_DIMENSIONS.x, 0),
        new Vector2f(ROAD_TILE_DIMENSIONS.x, ROAD_TILE_DIMENSIONS.y),
        new Vector2f(0, ROAD_TILE_DIMENSIONS.y)
    };

    private ArrayList<TileTexCoords[]> columns;
    private int lanes;

    private float speed;

    private RoadSection lastRoadSection;
    private RoadSection roadSection;

    private Random rand;

    private Vector2f origin;

    private HashMap<Integer, ArrayList<Obstacle>> obstacles;
    private HashMap<Integer, ArrayList<Pickup>> pickups;

    private Animation explosion;

    private ArrayList<ObstacleType> currentAllowedObstacles;

    private ArrayList<PickupType> pickupTypes;

    /**
     * Holds information about the structure and sections of the
     * road tile spritesheet
     */
    public enum RoadSection {
        WHITE(0, 1, 2, 30, 5, 6),
        YELLOW(7, 8, 9, 20, 12, 13),
        DIRT(14, 15, 16, 40, 23, 24)
        ;

        public final int transitionColumn;
        public final int startColumn;
        public final int mainColumn;
        public final int variation;
        public final int endColumn;
        public final int baseColumn;

        private RoadSection(int t, int s, int m, int v, int e, int b) {
            transitionColumn = t;
            startColumn = s;
            mainColumn = m;
            variation = v;
            endColumn = e;
            baseColumn = b;
        }
    }

    /**
     * Stores 4 texture coordinates corresponding to each
     * corner of a tile
     */
    private class TileTexCoords {
        private Vector2f[] points = new Vector2f[4];

        private TileTexCoords (Vector2f topLeft) {
            for (int i = 0; i < 4; i++) {
                points[i] = Vector2f.add(topLeft, VERTEX_OFFSETS[i]);
            }
        }

        public Vector2f getVertex(int index) {
            return points[index];
        }
    }

    /**
     * Creates a road using the default RoadSection
     * @param lanes number of lanes to create the road with
     * @param height height in pixels of the road
     */
    public Road(int lanes, float height) {
        this(lanes, height, DEFAULT_ROAD_SECTION);
    }

    /**
     * Creates a road using a specified RoadSection
     * @param lanes number of lanes to create the road with
     * @param height height in pixels of the road
     * @param rS RoadSection to use when generating the first screen
     */
    public Road(int lanes, float height, RoadSection rS) {
        if (lanes < 2) throw new InvalidLaneCountException(lanes);

        if (height <= 0) throw new IllegalArgumentException(
            "Invalid height " + height + "; must be >0"
        );

        rand = new Random();

        this.lanes = lanes;
        this.speed = 0;
        this.lastRoadSection = rS;
        this.roadSection = rS;

        ROAD_TILE_SCALE = (height / (lanes + 2)) / ROAD_TILE_DIMENSIONS.y;

        this.origin = new Vector2f(
            0, FullThrottle.WINDOW_HEIGHT - height
        );

        columns = new ArrayList<>();
        obstacles = new HashMap<>();
        pickups = new HashMap<>();
        for (int i = 0; i < lanes; i++) {
            obstacles.put(i, new ArrayList<Obstacle>());
            pickups.put(i, new ArrayList<Pickup>());
        }
        currentAllowedObstacles = ObstacleType.getObstaclesForSection(rS);

        explosion = new Animation(Obstacle.OBSTACLE_EXPLOSION_SEQUENCE, 16, false);
        explosion.scale(new Vector2f(ROAD_TILE_SCALE, ROAD_TILE_SCALE));
        explosion.restart();
        explosion.pause();

        pickupTypes = PickupType.getAll();
    }

    public float getTopEdge() {
        return FullThrottle.WINDOW_HEIGHT - (ROAD_TILE_SCALE * ROAD_TILE_DIMENSIONS.y * (lanes + 1));
    }

    public float getBottomEdge() {
        return FullThrottle.WINDOW_HEIGHT - (ROAD_TILE_SCALE * ROAD_TILE_DIMENSIONS.y);
    }

    public float getLanePos(int lane) {
        return getTopEdge() + (lane * ROAD_TILE_SCALE * ROAD_TILE_DIMENSIONS.y);
    }
    
    /**
     * Generates the transition between road types
     * Called from setRoadSection
     */
    private void generateTransitionColumns() {
        for (int i = 1; i < 4; i++) {
            generateColumn(i);
        }
    }

    /**
     * Generate a new column (not transitional)
     */
    private void generateColumn() {
        generateColumn(0);
    }

    /**
     * Generates a new column of road tiles
     * @param transition used when transitioning:
     *      0 = no transition,
     *      1 = end current,
     *      2 = transition,
     *      3 = start next
     */
    private void generateColumn(int transition) {
        int size = lanes + 2;
        if (transition == 2)
            size *= 2;
        TileTexCoords[] nextColumn = new TileTexCoords[size];

        for (int i = 0; i < (lanes + 2) * (transition == 2 ? 2 : 1); i++) {
            float tileX = roadSection.mainColumn, tileY;

            // Set y component of texture coordinate
            int j = i % (lanes + 2);
            if (j == 0) {
                tileY = 0;
            } else if (j == 1) {
                tileY = 1;
            } else if (j == lanes) {
                tileY = 3;
            } else if (j == lanes + 1) {
                tileY = 4;
            } else {
                tileY = 2;
            }

            // Set x component of texture coordinate
            if (transition == 0) {
                //random 1-100
                int variation = rand.nextInt(100) + 1;
                tileX = roadSection.mainColumn;
                if (variation <= roadSection.variation) {
                    int variants = roadSection.endColumn - (int)tileX;
                    tileX += 1 + rand.nextInt(variants - 1);
                }
            } else if (transition == 1) {
                tileX = lastRoadSection.endColumn;
            } else if (transition == 2) {
                if (i < lanes + 2) {
                    tileX = lastRoadSection.baseColumn;
                } else {
                    tileX = roadSection.transitionColumn;
                }
            } else if (transition == 3) {
                tileX = roadSection.startColumn;
            }

            Vector2f topLeft = Vector2f.componentwiseMul(
                new Vector2f(tileX, tileY),
                new Vector2f(ROAD_TILE_DIMENSIONS)
            );

            nextColumn[i] = new TileTexCoords(topLeft);
        }

        columns.add(nextColumn);
    }

    private void generateObstacle(int column) {
        ObstacleType type = currentAllowedObstacles.get(rand.nextInt(currentAllowedObstacles.size()));

        float scaleFactor = (ROAD_TILE_SCALE * ROAD_TILE_DIMENSIONS.y) / Obstacle.OBSTACLE_SPRITE_SIZE.y;
        int lane = rand.nextInt(lanes);
        Obstacle o = new Obstacle(type, new Vector2f(column * ROAD_TILE_DIMENSIONS.x * ROAD_TILE_SCALE, getLanePos(lane)), scaleFactor);
        obstacles.get(lane).add(o);
    }

    private int numObstacles() {
        int count = 0;
        for (ArrayList<Obstacle> l : obstacles.values())
            count += l.size();

        return count;

    }
    private void generatePickup(int column) {
        PickupType type = pickupTypes.get(rand.nextInt(pickupTypes.size()));

        float scaleFactor = (ROAD_TILE_SCALE * ROAD_TILE_DIMENSIONS.y) / Pickup.PICKUP_SPRITE_SIZE.y;
        int lane = rand.nextInt(lanes);
        Pickup p = new Pickup(type, new Vector2f(column * ROAD_TILE_DIMENSIONS.x * ROAD_TILE_SCALE, getLanePos(lane)), scaleFactor);
        pickups.get(lane).add(p);
    }

    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        if (!bVisible) return;
        Vector2f drawPos = origin;
        VertexArray va = new VertexArray(PrimitiveType.QUADS);
        
        //For each column
        for (TileTexCoords[] c : columns) {
            //For each tile in the column
            for (int t = 0; t < c.length; t++) {
                if (t == lanes + 2)
                    drawPos = new Vector2f(drawPos.x, origin.y);
                //For each point in the tile
                for (int p = 0; p < 4; p++) {
                    //Create and add a vertex
                    Vertex v = new Vertex(
                        Vector2f.add(drawPos, Vector2f.mul(
                            VERTEX_OFFSETS[p], ROAD_TILE_SCALE
                        )),
                        c[t].getVertex(p)
                    );
                    va.add(v);
                }
                //Update drawPos to next tile down
                drawPos = Vector2f.add(
                    drawPos,
                    new Vector2f(
                        0, ROAD_TILE_DIMENSIONS.y * ROAD_TILE_SCALE
                    )
                );
            }
            //Update drawPos to top of next column
            drawPos = new Vector2f(
                drawPos.x + ROAD_TILE_SCALE * ROAD_TILE_DIMENSIONS.x,
                origin.y
            );
        }

        //Add texture to given RenderStates
        RenderStates rs = new RenderStates(arg1, ROAD_TEXTURE);
        //Draw VertexArray
        va.draw(arg0, rs);
        // System.out.println("Start obst draw");
        VertexArray obVA = new VertexArray(PrimitiveType.QUADS);
        RenderStates obRS = new RenderStates(arg1, Obstacle.OBSTACLE_SPRITE_SHEET);
        for (ArrayList<Obstacle> l : obstacles.values()) {
            for (Obstacle o : l) {
                obVA.addAll(o.getVertexArray());
            }
        }
        obVA.draw(arg0, obRS);

        VertexArray pVA = new VertexArray(PrimitiveType.QUADS);
        RenderStates pRS = new RenderStates(arg1, Pickup.PICKUP_SPRITE_SHEET);
        for (ArrayList<Pickup> l : pickups.values())
            for (Pickup p : l)
                pVA.addAll(p.getVertexArray());
        pVA.draw(arg0, pRS);

        explosion.draw(arg0, arg1);
        if (explosion.getCurrentFrame() == explosion.getLength() - 1)
            FullThrottle.getGameManager().play();
    }

    @Override
    public void update() {
        FloatRect vBounds = FullThrottle.getViewRect();
        /* 
         * For testing (simulates screen boundaries to
         * see what's happening "offscreen"): 
         */
        // FloatRect vBounds = new FloatRect(
        //     400, 0, 600, FullThrottle.WINDOW_HEIGHT
        // );

        float dX = speed * TimeManager.deltaTime();
        
        origin = Vector2f.sub(origin, new Vector2f(dX, 0));

        float tileWidth = ROAD_TILE_DIMENSIONS.x * ROAD_TILE_SCALE;
        while ((columns.size() - 1) * tileWidth < vBounds.width) {
            generateColumn();
            if (rand.nextInt(3) == 1 && generateObstacles && numObstacles() < MAX_OBSTACLES){
                generateObstacle(columns.size());
            }

            if (rand.nextInt(5) == 1 && generateObstacles) {
                generatePickup(columns.size());
            } 
        }

        if (origin.x < vBounds.left - tileWidth) {
            origin = Vector2f.add(
                origin, new Vector2f(
                    tileWidth, 0
                )
            );
            if (columns.size() != 0)
                columns.remove(0);
        }

        for (ArrayList<Obstacle> l : obstacles.values()) {
            ArrayList<Obstacle> offscreen = new ArrayList<>();
            float currentX = Float.NEGATIVE_INFINITY;
            for (Obstacle o : l) {
                Vector2f newPos = o.move(dX);
                if (newPos.x < currentX + (o.getSize().x / 2))
                    o.setPosition(
                        new Vector2f(
                            currentX + (o.getSize().x / 2),
                            newPos.y
                        )
                    );
                currentX = newPos.x + o.getSize().x;

                if (!o.isOnScreen())
                    offscreen.add(o);
            }
            l.removeAll(offscreen);
        }

        for (ArrayList<Pickup> l : pickups.values()) {
            ArrayList<Pickup> offscreen = new ArrayList<>();
            for (Pickup p : l) {
                p.move(dX);
                if (!p.isOnScreen())
                    offscreen.add(p);
            }
            l.removeAll(offscreen);
        }
    }

    /**
     * Get current speed of the road
     * @return float value of speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Set speed to specific value
     * @param speed new speed for road
     */
    public void setSpeed(float speed) {
        if (speed < 0) throw new IllegalArgumentException(
            "Illegal speed " + speed + "; must be >= 0"
        );
        this.speed = speed;
    }

    /**
     * Change speed by specified amount
     * @param dSpeed amount to change speed by
     */
    public void increaseSpeed(float dSpeed) {
        this.speed += dSpeed;
        if (speed < 0) speed = 0;
    }

    /**
     * Change the type of road that will be generated
     * for the next column
     * @param roadSection RoadSection to change to
     */
    public void setRoadSection(RoadSection roadSection) {
        this.lastRoadSection = this.roadSection;
        this.roadSection = roadSection;
        currentAllowedObstacles = ObstacleType.getObstaclesForSection(roadSection);
        generateTransitionColumns();
    }
    
    public RoadSection getRoadSection() {
        return roadSection;
    }

    public void setVisible(boolean b) {
        this.bVisible = b;
    }

    public void generateObstacles(boolean b) {
        this.generateObstacles = b;
    }

    public void clearObstacles() {
        for (ArrayList<Obstacle> l : obstacles.values())
            l.clear();
    }

    public boolean isPlayerColliding(FloatRect playerBounds) {
        ArrayList<Integer> playerLanes = new ArrayList<>();
        for (int i = 0; i < lanes; i++) {
            float top = getTopEdge() + (i * ROAD_TILE_DIMENSIONS.y * ROAD_TILE_SCALE);
            float bottom = getTopEdge() + ((i + 1) * ROAD_TILE_DIMENSIONS.y * ROAD_TILE_SCALE);
            if (playerBounds.top >= top && playerBounds.top <= bottom) {
                playerLanes.add(i);
                continue;
            }
            float pBottom = playerBounds.top + playerBounds.height;
            if (pBottom >= top && pBottom <= bottom) {
                playerLanes.add(i);
                continue;
            }

            if (playerLanes.size() != 0)
                if (i > playerLanes.get(playerLanes.size() - 1) + 1)
                    break;
        }
        if (playerLanes.size() == 2 && playerLanes.get(0) + 1 != playerLanes.get(1))
            playerLanes.add(1, playerLanes.get(0) + 1);

        boolean collision = false;
        Obstacle collider = null;
        for (int i : playerLanes) {
            for (Obstacle o : obstacles.get(i)) {
                collision = o.intersects(playerBounds);
                if (collision) {
                    obstacles.get(i).remove(o);
                    collider = o;
                    break;
                }
            }
            if (collision) break;
        }

        if (collision) {
            float eX = collider.getPosition().x - (explosion.getGlobalBounds().width - collider.getSize().x) / 2;
            float eY = collider.getPosition().y - (explosion.getGlobalBounds().height - collider.getSize().y);
            explosion.setPosition(new Vector2f(eX, eY));
            explosion.restart();
            explosion.play();
        }

        return collision;
    }

    public Pickup isPlayerOnPickup(FloatRect playerBounds) {
        ArrayList<Integer> playerLanes = new ArrayList<>();
        for (int i = 0; i < lanes; i++) {
            float top = getTopEdge() + (i * ROAD_TILE_DIMENSIONS.y * ROAD_TILE_SCALE);
            float bottom = getTopEdge() + ((i + 1) * ROAD_TILE_DIMENSIONS.y * ROAD_TILE_SCALE);
            if (playerBounds.top >= top && playerBounds.top <= bottom) {
                playerLanes.add(i);
                continue;
            }
            float pBottom = playerBounds.top + playerBounds.height;
            if (pBottom >= top && pBottom <= bottom) {
                playerLanes.add(i);
                continue;
            }

            if (playerLanes.size() != 0)
                if (i > playerLanes.get(playerLanes.size() - 1) + 1)
                    break;
        }
        if (playerLanes.size() == 2 && playerLanes.get(0) + 1 != playerLanes.get(1))
            playerLanes.add(1, playerLanes.get(0) + 1);

        boolean collision = false;
        Pickup collider = null;
        for (int i : playerLanes) {
            for (Pickup p : pickups.get(i)) {
                collision = p.intersects(playerBounds);
                if (collision) {
                    pickups.get(i).remove(p);
                    collider = p;
                    break;
                }
            }
            if (collision) break;
        }

        return collider;
    }

    private class InvalidLaneCountException extends RuntimeException {
        /**
         *
         */
        private static final long serialVersionUID = 4358193149770933936L;

        public InvalidLaneCountException(int count) {
            super(
                "Invalid number of lanes " + count +
                "; must be >= 2"
            );
        }
    }
}
