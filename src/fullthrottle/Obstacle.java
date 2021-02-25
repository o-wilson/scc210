package fullthrottle;

import java.util.ArrayList;
import java.util.Arrays;

import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import fullthrottle.Road.RoadSection;
import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.SpriteSequence;
import fullthrottle.gfx.Spritesheet;
import fullthrottle.util.TimeManager;

public class Obstacle {
    public static Texture OBSTACLE_SPRITE_SHEET = new FTTexture("./res/Obstacles.png");
    public static Vector2f OBSTACLE_SPRITE_SIZE = new Vector2f(32, 32);
    public static Texture OBSTACLE_EXPLOSION_TEXTURE = new FTTexture("./res/Explosion.png");
    public static Spritesheet OBSTACLE_EXPLOSION_SHEET = new Spritesheet(OBSTACLE_EXPLOSION_TEXTURE, new Vector2i(32, 32));
    public static SpriteSequence OBSTACLE_EXPLOSION_SEQUENCE = new SpriteSequence(OBSTACLE_EXPLOSION_SHEET);

    private static Vector2f[] OBSTACLE_QUAD_OFFSETS = new Vector2f[] {
        Vector2f.ZERO,
        new Vector2f(OBSTACLE_SPRITE_SIZE.x, 0),
        OBSTACLE_SPRITE_SIZE,
        new Vector2f(0, OBSTACLE_SPRITE_SIZE.y)
    };

    public enum ObstacleType {
        CAR_1(0, 2, 10, 120, new FloatRect(1, 11, 30, 13), new RoadSection[] {RoadSection.YELLOW}),
        CAR_2(1, 2, 10, 110, new FloatRect(1, 11, 30, 13), new RoadSection[] {RoadSection.YELLOW}),
        CAR_3(2, 2, 10, 125, new FloatRect(2, 11, 27, 13), new RoadSection[] {RoadSection.YELLOW}),
        CAR_4(3, 2, 7, 80, new FloatRect(1, 9, 30, 16), new RoadSection[] {RoadSection.YELLOW}),
        CAR_5(4, 2, 8, 100, new FloatRect(1, 10, 30, 15), new RoadSection[] {RoadSection.YELLOW}),
        CAR_6(5, 2, 10, 110, new FloatRect(1, 11, 30, 13), new RoadSection[] {RoadSection.WHITE}),
        CAR_7(6, 2, 8, 100, new FloatRect(1, 11, 30, 12), new RoadSection[] {RoadSection.WHITE}),
        CAR_8(7, 2, 8, 90, new FloatRect(1, 10, 30, 14), new RoadSection[] {RoadSection.WHITE}),
        CAR_9(8, 2, 10, 110, new FloatRect(1, 10, 30, 13), new RoadSection[] {RoadSection.WHITE}),
        CAR_10(9, 2, 13, 140, new FloatRect(1, 12, 30, 10), new RoadSection[] {RoadSection.YELLOW, RoadSection.WHITE, RoadSection.DIRT}),
        CAR_11(10, 2, 13, 180, new FloatRect(1, 12, 30, 11), new RoadSection[] {RoadSection.YELLOW, RoadSection.WHITE, RoadSection.DIRT})
        ;
        
        public final int obstacleIndex;
        public final int frames;
        public final int fps;
        public final float moveSpeed;
        public final FloatRect hitBox;
        public final RoadSection[] sections;

        private ObstacleType(
            int i, int f, int fps, float s,
            FloatRect b, RoadSection[] sections
        ) {
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

    public Obstacle(ObstacleType t, Vector2f pos, float scale) {
        this.type = t;
        
        this.frameCount = t.frames;
        this.currentFrame = 0;
        this.timeToNextFrame = 1f / ((type.fps != 0) ? type.fps : 1);
        this.currentTimeToNextFrame = timeToNextFrame;

        this.position = pos;
        this.scale = scale;
    }
    
    public Vector2f move(float dXPos) {
        dXPos += type.moveSpeed * TimeManager.deltaTime();
        position = Vector2f.sub(position, new Vector2f(dXPos, 0));
        
        // return position.x + (OBSTACLE_SPRITE_SIZE.x * scale) > 0;
        return position;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public Vector2f getSize() {
        return Vector2f.mul(OBSTACLE_SPRITE_SIZE, scale);
    }

    public boolean isOnScreen() {
        return getPosition().x + getSize().x > 0;
    }

    public VertexArray getVertexArray() {
        Vector2f spritePos = new Vector2f(
            0,
            OBSTACLE_SPRITE_SIZE.y * type.obstacleIndex
        );
        if (frameCount != 1) {
            currentTimeToNextFrame -= TimeManager.deltaTime();
            if (currentTimeToNextFrame <= 0) {
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

    public boolean intersects(FloatRect other) {
        FloatRect obstacleHitBox = new FloatRect(
            type.hitBox.left * scale + position.x,
            type.hitBox.top * scale + position.y,
            type.hitBox.width * scale,
            type.hitBox.height * scale
        );
        boolean intersects = obstacleHitBox.intersection(other) != null;

        return intersects;
    }
}
