package fullthrottle.ui;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import fullthrottle.util.TimeManager;
import fullthrottle.util.Updatable;

public class ProgressBar implements Drawable, Updatable {

    public static final float DEFAULT_FILL_SPEED = 10;

    private float maxValue;
    private float currentValue;
    private float displayValue;

    private float stages[];

    private Texture spriteSheet;
    private Vector2f spriteSize;

    private float fillSpeed;

    private Vector2f position, size;

    private VertexArray border;

    public ProgressBar(
        Vector2f position, Vector2f size, float max,
        Texture sheet, Vector2i spriteSize
    ) {
        this(position, size, max, sheet, spriteSize, max, new float[] {max});
    }

    public ProgressBar(
        Vector2f position, Vector2f size, float max,
        Texture sheet, Vector2i spriteSize,
        float startValue, float[] stages    
    ) {
        this.position = position;
        this.size = size;

        this.maxValue = max;
        this.currentValue = startValue;
        this.displayValue = 0;
        this.stages = stages;

        this.spriteSheet = sheet;
        this.spriteSize = new Vector2f(spriteSize);

        this.fillSpeed = DEFAULT_FILL_SPEED;

        this.border = createBar(0, max);
    }

    private VertexArray createBar(int stage, float value) {
        VertexArray va = new VertexArray(PrimitiveType.QUADS);
        // quick and dirty assuming tiles are square
        Vector2f[] positionOffsets = new Vector2f[] {
            Vector2f.ZERO,
            new Vector2f(this.size.y, 0),
            new Vector2f(this.size.y, this.size.y),
            new Vector2f(0, this.size.y)
        };
        Vector2f[] textureOffsets = new Vector2f[] {
            Vector2f.ZERO,
            new Vector2f(spriteSize.x, 0),
            this.spriteSize,
            new Vector2f(0, spriteSize.y)
        };
        Vector2f currentPosition = this.position;
        //Start of bar
        for (int i = 0 ; i < 4; i++) {
            Vertex v = new Vertex(
                Vector2f.add(
                    currentPosition,
                    positionOffsets[i]
                ),
                Vector2f.add(
                    new Vector2f(0, stage * spriteSize.y),
                    textureOffsets[i]
                )
            );
            va.add(v);
        }
        //Middle of bar
        float sections = (this.size.x / this.size.y) * (value / maxValue) - 2;
        for (int i = 0; i < (int)sections; i++) {
            currentPosition = Vector2f.add(currentPosition, new Vector2f(this.size.y, 0));
            for (int j = 0; j < 4; j++) {
                Vertex v = new Vertex(
                    Vector2f.add(
                        currentPosition,
                        positionOffsets[j]
                    ),
                    Vector2f.add(
                        new Vector2f(spriteSize.x, stage * spriteSize.y),
                        textureOffsets[j]
                    )
                );
                va.add(v);
            }
        }
        currentPosition = Vector2f.add(currentPosition, new Vector2f(this.size.y, 0));
        //Remainder
        float remainder = sections - (int)sections;
        if (remainder != 0) {
            for (int i = 0; i < 4; i++) {
                Vertex v = new Vertex(
                    Vector2f.add(
                        currentPosition,
                        Vector2f.componentwiseMul(
                            positionOffsets[i],
                            new Vector2f(remainder, 1)
                        )
                    ),
                    Vector2f.add(
                        new Vector2f(spriteSize.x, stage * spriteSize.y),
                        Vector2f.componentwiseMul(
                            textureOffsets[i],
                            new Vector2f(remainder, 1)
                        )
                    )
                );
                va.add(v);
            }
            currentPosition = Vector2f.add(currentPosition, new Vector2f(this.size.y * remainder, 0));
        }

        //End
        for (int i = 0 ; i < 4; i++) {
            Vertex v = new Vertex(
                Vector2f.add(
                    currentPosition,
                    positionOffsets[i]
                ),
                Vector2f.add(
                    new Vector2f(spriteSize.x * 2, stage * spriteSize.y),
                    textureOffsets[i]
                )
            );
            va.add(v);
        }

        return va;
    }

    public void setValue(float newValue) {
        if (newValue < 0 || newValue > maxValue) throw new IllegalArgumentException();

        currentValue = newValue;        
    }

    public float addToValue(float dValue) {
        currentValue += dValue;

        if (currentValue < 0) currentValue = 0;
        if (currentValue > maxValue) currentValue = maxValue;

        return currentValue;
    }

    public float getCurrentValue() {
        return currentValue;
    }

    @Override
    public void update() {
        displayValue += (currentValue - displayValue) * fillSpeed * TimeManager.deltaTime();
        if (Math.abs(currentValue - displayValue) < 0.1f) displayValue = currentValue;
    }

    @Override
    public void draw(RenderTarget arg0, RenderStates arg1) {
        arg1 = new RenderStates(arg1, spriteSheet);
        VertexArray va = new VertexArray(PrimitiveType.QUADS);
        
        int stage = 1;
        while(displayValue > stages[stage - 1]) {
            stage++;
            if (stage > stages.length) {
                stage = stages.length;
                break;
            }
        }
        VertexArray bar = createBar(stage, displayValue);
        va.addAll(bar);

        va.addAll(border);
        va.draw(arg0, arg1);
    }

}