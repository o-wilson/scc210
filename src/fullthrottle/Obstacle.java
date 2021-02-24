package fullthrottle;

import java.util.ArrayList;
import java.util.Arrays;

import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;

import fullthrottle.Road.RoadSection;
import fullthrottle.gfx.FTTexture;
import fullthrottle.util.TimeManager;

public class Obstacle {
    public static Texture OBSTACLE_SPRITE_SHEET = new FTTexture("./res/Obstacles.png");
    public static Vector2f OBSTACLE_SPRITE_SIZE = new Vector2f(32, 32);

    private static Vector2f[] OBSTACLE_QUAD_OFFSETS = new Vector2f[] {
        Vector2f.ZERO,
        new Vector2f(OBSTACLE_SPRITE_SIZE.x, 0),
        OBSTACLE_SPRITE_SIZE,
        new Vector2f(0, OBSTACLE_SPRITE_SIZE.y)
    };

    public enum ObstacleType {
        CAR_1(0, 2, 12, 50, new FloatRect(0, 0, 16, 16), new RoadSection[] {RoadSection.YELLOW}),
        CAR_2(1, 2, 12, 80, new FloatRect(0, 0, 16, 16), new RoadSection[] {RoadSection.WHITE, RoadSection.DIRT})
        ;
        
        public final int obstacleIndex;
        public final int frames;
        public final int fps;
        public final float moveSpeed;
        public final FloatRect hitBox;
        public final RoadSection[] sections;

        private ObstacleType(int i, int f, int fps, float s, FloatRect b, RoadSection[] sections) {
            this.obstacleIndex = i;
            this.frames = f;
            this.fps = fps;
            this.moveSpeed = s;
            this.hitBox = b;
            this.sections = sections;
        }

        public static ArrayList<ObstacleType> getObstaclesForSection(RoadSection rs) {
            ArrayList<ObstacleType> list = new ArrayList<>();

            for (ObstacleType o : ObstacleType.values())
                if (Arrays.asList(o.sections).contains(rs))
                    list.add(o);
            
            return list;
        }
    }

    private ObstacleType type;

    private Vector2f position;

    private float scale;

    private int frameCount;
    private int currentFrame;
    private float timeToNextFrame;
    private float currentTimeToNextFrame;

    public Obstacle(ObstacleType t) {
        this.type = t;
        
        this.frameCount = t.frames;
        this.currentFrame = 0;
        this.timeToNextFrame = 1f / ((type.fps != 0) ? type.fps : 1);
        this.currentTimeToNextFrame = timeToNextFrame;

        this.position = new Vector2f(900, 400);
        this.scale = 3;
    }
    
    public boolean move(float dXPos) {
        dXPos -= type.moveSpeed * TimeManager.deltaTime();
        position = Vector2f.add(position, new Vector2f(dXPos, 0));
        
        return position.x + (OBSTACLE_SPRITE_SIZE.x * scale) > 0;
    }

    public VertexArray getVertexArray() {
        Vector2f spritePos = new Vector2f(
            0,
            OBSTACLE_SPRITE_SIZE.y * type.obstacleIndex
        );
        if (frameCount != 1) {
            currentTimeToNextFrame -= TimeManager.deltaTime();
            while (currentTimeToNextFrame <= 0) {
                currentFrame++;
                currentTimeToNextFrame += timeToNextFrame;
            }
            currentFrame %= frameCount;
            spritePos = Vector2f.componentwiseMul(
                OBSTACLE_SPRITE_SIZE,
                new Vector2f(currentFrame, type.obstacleIndex)
            );
        }

        VertexArray va = new VertexArray(PrimitiveType.QUADS);
        
        for (int i = 0; i < 4; i++) {
            Vertex v = new Vertex(
                Vector2f.add(
                    position,
                    Vector2f.mul(OBSTACLE_QUAD_OFFSETS[i], scale)
                ),
                Vector2f.add(spritePos, OBSTACLE_QUAD_OFFSETS[i])
            );
            va.add(v);
        }

        return va;
    }
}
