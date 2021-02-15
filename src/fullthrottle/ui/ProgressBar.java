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

    private Vector2f[] positionOffsets;
    private Vector2f[] textureOffsets;

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
        
        positionOffsets = new Vector2f[] {
            Vector2f.ZERO,
            new Vector2f(this.size.y, 0),
            new Vector2f(this.size.y, this.size.y),
            new Vector2f(0, this.size.y)
        };
        
        textureOffsets = new Vector2f[] {
            Vector2f.ZERO,
            new Vector2f(spriteSize.x, 0),
            this.spriteSize,
            new Vector2f(0, spriteSize.y)
        };

        this.border = createBar(0, max);
    }

    private VertexArray createBar(int stage, float value) {
        VertexArray va = new VertexArray(PrimitiveType.QUADS);
        if (value == 0) return va;
        float sections = (size.x / size.y) * (value / maxValue);
        Vector2f currentPosition = this.position;
        //Start of bar
        float amount = 1;
        if (sections < 2) {
            amount = sections - 1;
        }
        if (amount < 0) amount = 0;

        for (int i = 0 ; i < 4; i++) {
            if (amount <= 0) break;
            Vertex v = new Vertex(
                Vector2f.add(
                    currentPosition,
                    Vector2f.componentwiseMul(
                        positionOffsets[i],
                        new Vector2f(amount, 1)
                    )
                ),
                Vector2f.add(
                    new Vector2f(0, stage * spriteSize.y),
                    Vector2f.componentwiseMul(
                        textureOffsets[i],
                        new Vector2f(amount, 1)
                    )
                )
            );
            va.add(v);
        }
        currentPosition = Vector2f.add(
            currentPosition,
            new Vector2f(this.size.y * amount, 0)
        );
        //Middle of bar
        int middleSections = (int)sections - 2;
        if (middleSections < 0) middleSections = 0;
        for (int i = 0; i < middleSections; i++) {
            for (int j = 0; j < 4; j++) {
                Vertex v = new Vertex(
                    Vector2f.add(
                        currentPosition,
                        positionOffsets[j]
                    ),
                    Vector2f.add(
                        new Vector2f(
                            spriteSize.x,
                            stage * spriteSize.y
                        ),
                        textureOffsets[j]
                    )
                );
                va.add(v);
            }
            currentPosition = Vector2f.add(
                currentPosition,
                new Vector2f(this.size.y, 0)
            );
        }
        //Remainder
        float remainder = sections - middleSections - 2;
        if (remainder != 0 && sections > 2) {
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
                        new Vector2f(
                            spriteSize.x,
                            stage * spriteSize.y
                        ),
                        Vector2f.componentwiseMul(
                            textureOffsets[i],
                            new Vector2f(remainder, 1)
                        )
                    )
                );
                va.add(v);
            }
            currentPosition = Vector2f.add(
                currentPosition,
                new Vector2f(this.size.y * remainder, 0)
            );
        }

        //End
        float endAmount = 1;
        if (sections < 1) {
            endAmount = sections;
            if (endAmount <= 0) return va; 
        }
        for (int i = 0 ; i < 4; i++) {
            Vertex v = new Vertex(
                Vector2f.add(
                    currentPosition,
                    Vector2f.componentwiseMul(
                        positionOffsets[i],
                        new Vector2f(endAmount, 1)
                    )
                ),
                Vector2f.add(
                    new Vector2f(
                        spriteSize.x * 2,
                        stage * spriteSize.y
                    ),
                    new Vector2f(
                        (textureOffsets[i].x == 0)
                            ? 16 * (1 - endAmount)
                            : textureOffsets[i].x,
                        textureOffsets[i].y
                    )
                )
            );
            va.add(v);
        }

        return va;
    }

    public void setValue(float newValue) {
        if (newValue < 0 || newValue > maxValue)
            throw new IllegalArgumentException();

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