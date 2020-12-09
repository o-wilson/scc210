package fullthrottle.util;

/**
 * Interface to allow aggregating all game objects
 * that need updating each frame
 */
public interface Updatable {
    /**
     * Intended to be called each frame/update period
     */
    public void update();

}
