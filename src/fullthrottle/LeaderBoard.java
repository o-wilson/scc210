package fullthrottle;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import fullthrottle.gfx.FTTexture;
import fullthrottle.ui.Button;
import fullthrottle.ui.Button.ActionType;
import fullthrottle.ui.ButtonManager;
import fullthrottle.ui.UI;
import fullthrottle.ui.UI.SpriteFillMode;
import fullthrottle.ui.UISprite;
import fullthrottle.util.HighScoreManager;
import fullthrottle.util.HighScoreManager.HighScore;

public final class LeaderBoard extends UISprite {
    private static Texture LEADERBOARD_TEXTURE = new FTTexture(
        "./res/Leaderboard.png"
    );

    public static final int LEADERBOARD_LENGTH = 5;

    private List<HighScore> scores;

    private Button closeButton;

    private VertexArray overlay;

    private Texture crowns;

    private Color textColor;
    private ArrayList<Text> texts;

    private float scale;

    private Vector2f[] textPositions;

    public LeaderBoard () {
        super(LEADERBOARD_TEXTURE);

        texts = new ArrayList<>();
        textPositions = new Vector2f[] {
            new Vector2f(43, 65),
            new Vector2f(43, 102),
            new Vector2f(43, 139),
            new Vector2f(43, 176),
            new Vector2f(43, 213)
        };

        scale = 2;
        setScale(scale, scale);
        float width = getGlobalBounds().width;
        float height = getGlobalBounds().height;
        Vector2f pos = new Vector2f(
            (FullThrottle.WINDOW_WIDTH - width) / 2,
            (FullThrottle.WINDOW_HEIGHT - height) / 2
        );
        setPosition(pos);

        for (int i = 0; i < textPositions.length; i++) {
            textPositions[i] = Vector2f.mul(textPositions[i], scale);
            textPositions[i] = Vector2f.add(textPositions[i], pos);
        }

        textColor = new Color(0xac, 0x32, 0x32);

        closeButton = new Button(
            Vector2f.sub(pos, new Vector2f(-(width - 24), 24)),
            new Vector2i(48, 48),
            new Sprite(new FTTexture("./res/CloseButton.png")),
            SpriteFillMode.STRETCH
        );
        closeButton.addAction(this, "closeLeaderBoard", ActionType.LEFT_CLICK);
        ButtonManager.getInstance().addObserver(closeButton);

        setVisible(false);

        overlay = new VertexArray(PrimitiveType.QUADS);
        Color overlayColor1 = new Color(0xac, 0x32, 0x32, 100);
        Color overlayColor2 = new Color(0xfb, 0xf2, 0x36, 100);
        overlay.add(new Vertex(Vector2f.ZERO, overlayColor2));
        overlay.add(new Vertex(new Vector2f(FullThrottle.WINDOW_WIDTH, 0), overlayColor2));
        overlay.add(new Vertex(new Vector2f(FullThrottle.WINDOW_WIDTH, FullThrottle.WINDOW_HEIGHT), overlayColor1));
        overlay.add(new Vertex(new Vector2f(0, FullThrottle.WINDOW_HEIGHT), overlayColor1));

        crowns = new FTTexture("./res/Crowns.png");
    }

    public void loadScores() {
        texts.clear();
        scores = HighScoreManager.getHighScores(LEADERBOARD_LENGTH);

        for (int i = 0; i < scores.size(); i++) {
            int boxHeight = (int)(32 * scale);
            String string = scores.get(i).name + ": " + scores.get(i).score;
            Text t = new Text(string, UI.DEFAULT_UI_FONT, boxHeight);
            t.setColor(textColor);
            Vector2f offset = new Vector2f(0, 0.5f * (boxHeight - t.getGlobalBounds().height));
            Vector2f drawPos = Vector2f.sub(textPositions[i], offset);
            t.setPosition(drawPos);
            texts.add(t);
        }
    }

    public void addCloseCallback(Object o, String m, ActionType t) {
        closeButton.addAction(o, m, t);
    }

    public void disableCloseButton() {
        closeButton.setEnabled(false);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        closeButton.setVisible(visible);
        if (visible) {
            closeButton.setEnabled(true);
            loadScores();
        }
    }

    @Override
    public void draw(RenderTarget target, RenderStates rs) {
        if (!isVisible()) return;
        if (scores == null) loadScores();

        overlay.draw(target, rs);
        super.draw(target, rs);
        closeButton.draw(target, rs);

        for (Text t : texts)
            t.draw(target, rs);

        if (scores.size() == 0) return;

        int[] topScores = new int[] {0, 0, 0};
        int p = 0;
        for (int i = 0; i < 3; i++) {
            topScores[i] = scores.get(p).score;
            while(p < scores.size() && scores.get(p).score == topScores[i])
                p++;
            if (p >= scores.size()) break;
        }
        
        for (int i = 0; i < scores.size(); i++) {
            HighScore s = scores.get(i);
            int position = 0;
            if (s.score == topScores[0])
                position = 1;
            else if (s.score == topScores[1])
                position = 2;
            else if (s.score == topScores[2])
                position = 3;
            else continue;

            Sprite crown = new Sprite(crowns, new IntRect(new Vector2i(0, 32 * (position - 1)), new Vector2i(32, 32)));
            crown.setPosition(Vector2f.sub(textPositions[i], new Vector2f(32 * scale, 0)));
            crown.scale(scale, scale);
            crown.draw(target, rs);
        }
    }

    public void closeLeaderBoard() {
        this.setVisible(false);
    }
}
