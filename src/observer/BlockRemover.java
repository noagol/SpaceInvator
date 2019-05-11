package observer;

import game.GameLevel;
import gameobject.Ball;
import gameobject.Block;
import utilts.Counter;

import java.util.List;

/**
 * The type blocks remover.
 * a BlockRemover is in charge of removing
 * blocks from the gameLevel, as well as keeping count
 * of the number of blocks that remain.
 */
public class BlockRemover implements HitListener {
    // Members of the class
    private GameLevel gameLevel;
    private Counter remainingBlocks = null;
    private List<List<Block>> blocksList = null;

    /**
     * Instantiates a new blocks remover.
     *
     * @param game      the game
     * @param remaining the remaining
     * @param block     the block
     */
    public BlockRemover(GameLevel game, Counter remaining, List<List<Block>> block) {
        this.gameLevel = game;
        this.remainingBlocks = remaining;
        this.blocksList = block;
    }

    /**
     * Instantiates a new Block remover.
     *
     * @param game the game
     */
    public BlockRemover(GameLevel game) {
        this.gameLevel = game;
    }

    /**
     * blocks that are hit and reach 0 hit-points should be removed
     * from the gameLevel, and remove this listener from the block
     * that is being removed from the gameLevel.
     *
     * @param beingHit the block being hit
     * @param hitter   the hitter
     */
    public void hitEvent(Block beingHit, Ball hitter) {
        if (beingHit.getHitPoints() == 0) {
            beingHit.removeFromGame();
            beingHit.removeHitListener(this);
            if (remainingBlocks != null) {
                remainingBlocks.decrease(1);
            }
        }
        if (blocksList != null) {
            for (List<Block> row : blocksList) {
                if (row.contains(beingHit)) {
                    row.remove(beingHit);
                    break;
                }
            }
        }
    }
}