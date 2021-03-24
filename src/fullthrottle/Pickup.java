package fullthrottle;

import java.util.ArrayList;

import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;

import fullthrottle.gfx.FTTexture;

public class Pickup {
    public static Texture PICKUP_SPRITE_SHEET = new FTTexture("./res/Pickups.png");
    public static Vector2f PICKUP_SPRITE_SIZE = new Vector2f(32, 32);

    private static Vector2f[] PICKUP_QUAD_OFFSETS = new Vector2f[] {
        Vector2f.ZERO,
        new Vector2f(PICKUP_SPRITE_SIZE.x, 0),
        PICKUP_SPRITE_SIZE,
        new Vector2f(0, PICKUP_SPRITE_SIZE.y)
    };

    public enum PickupType {
        COIN(0, new FloatRect(0, 0, 32, 32)),
        FUEL(1, new FloatRect(0, 0, 32, 32))
        ;

        public final int index;
        public final FloatRect hitBox;

        private PickupType(int i, FloatRect h) {
            this.index = i;
            this.hitBox = h;
        }

        public static ArrayList<PickupType> getAll() {
            ArrayList<PickupType> types = new ArrayList<>();
            for (PickupType p : PickupType.values()) {
                types.add(p);
            }
            return types;
        }
    }

    private PickupType type;

    private Vector2f position;

    private float scale;

    public Pickup(PickupType type, Vector2f pos, float scale) {
        this.type = type;
        this.position = pos;
        this.scale = scale;
    }

    public Vector2f move(float dXPos) {
        position = Vector2f.sub(position, new Vector2f(dXPos, 0));
        
        // return position.x + (OBSTACLE_SPRITE_SIZE.x * scale) > 0;
        return position;
    }

    public PickupType getType() {
        return this.type;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public Vector2f getSize() {
        return Vector2f.mul(PICKUP_SPRITE_SIZE, scale);
    }

    public boolean isOnScreen() {
        return getPosition().x + getSize().x > 0;
    }

    public VertexArray getVertexArray() {
        Vector2f spritePos = new Vector2f(
            0,
            PICKUP_SPRITE_SIZE.y * type.index
        );

        VertexArray va = new VertexArray(PrimitiveType.QUADS);
        
        for (int i = 0; i < 4; i++) {
            Vertex v = new Vertex(
                Vector2f.add(
                    position,
                    Vector2f.mul(PICKUP_QUAD_OFFSETS[i], scale)
                ),
                Vector2f.add(spritePos, PICKUP_QUAD_OFFSETS[i])
            );
            va.add(v);
        }

        return va;
    }

    public boolean intersects(FloatRect other) {
        FloatRect hitbox = new FloatRect(
            type.hitBox.left * scale + position.x,
            type.hitBox.top * scale + position.y,
            type.hitBox.width * scale,
            type.hitBox.height * scale
        );
        boolean intersects = hitbox.intersection(other) != null;

        return intersects;
    }
}
