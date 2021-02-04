package fullthrottle;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import fullthrottle.gfx.FTTexture;
import fullthrottle.gfx.Spritesheet;
import fullthrottle.util.Updatable;

public final class Road implements Drawable, Updatable {
    public static Vector2i ROAD_TILE_DIMENSIONS = new Vector2i(16, 16);
    public static Texture ROAD_TEXTURE = new FTTexture("./res/Road.png");
    public static Spritesheet ROAD_SHEET = new Spritesheet(ROAD_TEXTURE, ROAD_TILE_DIMENSIONS);

    public static RoadSection DEFAULT_ROAD_SECTION = RoadSection.WHITE;

    private static Vector2f[] VERTEX_OFFSETS = new Vector2f[] {
        Vector2f.ZERO,
        new Vector2f(ROAD_TILE_DIMENSIONS.x, 0),
        new Vector2f(ROAD_TILE_DIMENSIONS.x, ROAD_TILE_DIMENSIONS.y),
        new Vector2f(0, ROAD_TILE_DIMENSIONS.y)
    };

    private ArrayList<TileTexCoords[]> columns;
    private int lanes;

    private float speed;

    private RoadSection roadSection;

    private Random rand;

    public enum RoadSection {
        BLANK(0, 0, 0, 0),
        WHITE(1, 2, 40, 4),
        YELLOW(5, 6, 10, 8)
        ;

        public final int startColumn;
        public final int mainColumn;
        public final int variation;
        public final int endColumn;

        private RoadSection(int s, int m, int v, int e) {
            startColumn = s;
            mainColumn = m;
            variation = v;
            endColumn = e;
        }
    }

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

    public Road(int lanes, float speed) {
        this(lanes, speed, DEFAULT_ROAD_SECTION);
    }

    public Road(int lanes, float speed, RoadSection rS) {
        if (lanes < 2) throw new InvalidLaneCountException(lanes);

        rand = new Random();

        this.lanes = lanes;
        this.speed = speed;
        this.roadSection = rS;

        columns = new ArrayList<>();

        for (int i = 0; i < 5; i++)
            generateColumn();
        // System.out.println(roadSection);
        // System.out.println(columns.get(0)[0].getVertex(0));
    }

    private void generateColumn() {
        TileTexCoords[] nextColumn = new TileTexCoords[lanes];

        for (int i = 0; i < lanes; i++) {
            float tileX, tileY;
            if (i == 0) {
                tileY = 0;
            } else if (i == lanes - 1) {
                tileY = 2;
            } else {
                tileY = 1;
            }

            //random 1-100
            int variation = rand.nextInt(100) + 1;
            if (variation > roadSection.variation) {
                tileX = roadSection.mainColumn;
            } else {
                if (roadSection.endColumn - (roadSection.mainColumn + 1) == 1) {
                    tileX = roadSection.mainColumn + 1;
                } else {
                    //needs actually doing
                    tileX = 0;
                }
            }

            Vector2f topLeft = Vector2f.componentwiseMul(
                new Vector2f(tileX, tileY),
                new Vector2f(ROAD_TILE_DIMENSIONS)
            );

            nextColumn[i] = new TileTexCoords(topLeft);
        }

        columns.add(nextColumn);
    }

    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        Vector2f drawOrigin = new Vector2f(400, 400);
        Vector2f drawPos = drawOrigin;
        VertexArray va = new VertexArray(PrimitiveType.QUADS);
        
        //For each column
        for (TileTexCoords[] c : columns) {
            //For each tile in the column
            for (int t = 0; t < c.length; t++) {
                //For each point in the tile
                for (int p = 0; p < 4; p++) {
                    //Create and add a vertex
                    Vertex v = new Vertex(
                        Vector2f.add(drawPos, VERTEX_OFFSETS[p]),
                        c[t].getVertex(p)
                    );
                    va.add(v);
                }
                //Update drawPos to next tile down
                drawPos = Vector2f.add(
                    drawPos,
                    new Vector2f(0, ROAD_TILE_DIMENSIONS.y)
                );
            }
            //Update drawPos to top of next column
            drawPos = Vector2f.add(
                drawPos,
                new Vector2f(
                    ROAD_TILE_DIMENSIONS.x,
                    -ROAD_TILE_DIMENSIONS.y * c.length
                )  
            );
        }

        //Add texture to given RenderStates
        RenderStates rs = new RenderStates(arg1, ROAD_TEXTURE);
        //Draw VertexArray
        va.draw(arg0, rs);
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    private class InvalidLaneCountException extends RuntimeException {
        public InvalidLaneCountException(int count) {
            super(
                "Invalid number of lanes " + count +
                "; must be >= 2"
            );
        }
    }
}
