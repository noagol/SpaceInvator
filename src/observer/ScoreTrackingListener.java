package observer;

import gameobject.Ball;
import gameobject.Block;
import utilts.Counter;

/**
 * The type Score tracking listener.
 */
public class ScoreTrackingListener implements HitListener {
    // Members of the class
    private Counter currentScore;

    /**
     * Instantiates a new Score tracking listener.
     *
     * @param score the score
     */
    public ScoreTrackingListener(Counter score) {
        this.currentScore = score;
    }

    /**
     * In case of hit event add 100 points to
     * the score.
     *
     * @param beingHit the block being hit
     * @param hitter   the hitter
     */
    public void hitEvent(Block beingHit, Ball hitter) {
        currentScore.increase(100);
    }
}