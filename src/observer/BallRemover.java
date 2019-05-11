package observer;

import game.GameLevel;
import gameobject.Ball;
import gameobject.Block;

/**
 * The type Ball remover.
 */
public class BallRemover implements HitListener {
    // Members of the class
    private GameLevel gameLevel;

    /**
     * Instantiates a new Ball remover.
     *
     * @param gameLevel      the game level
     *
     */
    public BallRemover(GameLevel gameLevel) {
        this.gameLevel = gameLevel;
    }

    /**
     * Charge of removing balls, and updating
     * an availabe-balls counter.
     *
     * @param beingHit the block being hit
     * @param hitter   the hitter
     */
    @Override
    public void hitEvent(Block beingHit, Ball hitter) {
        hitter.removeFromGame(gameLevel);
    }
}
