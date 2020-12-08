package fullthrottle.util;

import org.jsfml.system.Clock;
import org.jsfml.system.Time;

/**
 * Class to help with timings, specifically time between frames
 */
public final class TimeManager {
    private static Clock clock = new Clock();
    private static Time deltaTime;
    private static int frameCount = 0;

    /**
     * Should be called with each frame, updates the deltaTime
     */
    public static void update() {
        deltaTime = clock.restart();
        frameCount++;
    }

    /**
     * Number of frames since the clock started
     * @return
     */
    public static int frameCount() {
        return frameCount;
    }

    /**
     * Get time elapsed since last frame
     * @return float value of time in seconds since last frame
     */
    public static float deltaTime() {
        return deltaTime.asSeconds();
    }

    /**
     * Get time elapsed since last frame
     * @return long value of time in milliseconds since last frame
     */
    public static long deltaTimeAsMilliseconds() {
        return deltaTime.asMilliseconds();
    }

    /**
     * Get time elapsed since last frame
     * @return Time object representing time since last frame
     */
    public static Time deltaTimeRaw() {
        return deltaTime;
    }
}
