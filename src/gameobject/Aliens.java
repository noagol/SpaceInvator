package gameobject;

import biuoop.DrawSurface;
import game.Constants;
import game.GameLevel;
import geometry.Point;
import io.ImageParser;
import observer.BlockRemover;
import observer.ScoreTrackingListener;
import utilts.Counter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The type Aliens.
 */
public class Aliens implements Sprite {
    // Members of the class.
    private List<List<Block>> alien = new ArrayList<>();
    private double moveTime;
    private double startMoveTime;
    private static final double SHOOT_TIME = 0.5;
    private double timePassedMove = 0.0;
    private double timePassedShoot = 0.0;
    private int speed = 5;
    private Random random = new Random();
    private Boolean isWin = false;


    /**
     * Instantiates a new Alien.
     *
     * @param gameLevel    the game level
     * @param move         the move time
     * @param score        the score
     * @param remainAliens the remain aliens
     */
    public Aliens(GameLevel gameLevel, double move,
                  Counter score, Counter remainAliens) {
        this.moveTime = move;
        this.startMoveTime = move;
        // create block remover
        BlockRemover b = new BlockRemover(gameLevel, remainAliens, this.alien);
        // create score tracking
        ScoreTrackingListener scoreTrackingListener = new ScoreTrackingListener(score);
        List<Block> row;
        // create the aliens formation
        for (int i = 0; i < 10; i++) {
            row = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                Block block = new Block(new Point(40 + 50 * i, 30 + j * 40),
                        40.0, 30.0, gameLevel,
                        ImageParser.getImage("resources/enemy.png"), true);
                row.add(block);
                block.addHitListener(b);
                block.addHitListener(scoreTrackingListener);
                remainAliens.increase(1);
            }
            alien.add(row);
        }
    }

    /**
     * Draw the sprite to the screen.
     *
     * @param d - a surface to draw on.
     */
    @Override
    public void drawOn(DrawSurface d) {

    }

    /**
     * Notify the sprite that time has passed.
     *
     * @param dt the dt
     */
    @Override
    public void timePassed(double dt) {
        Block topLeft = getLeft();
        Block topRight = getRight();
        /*if the aliens in the formation hits the
         left or right side of the screen*/
        if ((topLeft != null && topRight != null) && ((topRight.getCollisionRectangle()
                .getUpperLeft().getX()
                + topRight.getCollisionRectangle().getWidth() + 10
                > Constants.SCREEN_WIDTH && speed > 0)
                || (topLeft.getCollisionRectangle().getUpperLeft().getX() - 10 < 0
                && speed < 0))) {
            // change the direction
            speed = speed * (-1);
            // increase their speed by 10%
            moveTime = moveTime * 0.9;
            // move the blocks down
            for (List<Block> list : this.alien) {
                for (Block block : list) {
                    block.moveUpDown(20);
                }
            }
            Block bottomAlien = getBottom();
            // check if the alien get to the shields
            if ((bottomAlien != null) && (bottomAlien.getCollisionRectangle().getUpperLeft().getY()
                    + bottomAlien.getCollisionRectangle().getHeight() > Constants.SHIELDS_HEIGHT)) {
                isWin = true;
            }
        }

        this.timePassedMove += dt;
        if (timePassedMove > moveTime) {
            for (List<Block> list : this.alien) {
                for (Block block : list) {
                    block.moveSide(speed);
                }
            }
            this.timePassedMove = 0;
        }

        this.timePassedShoot += dt;
        /* Every 0.5 seconds a random column of aliens
         is chosen to shoot, the lowest alien on that
          column will release a shot.*/
        if (timePassedShoot > SHOOT_TIME) {
            Integer rand = random.nextInt(alien.size());
            Block randBlock = getTheLowest(alien.get(rand));
            if (randBlock != null) {
                randBlock.shoot();
                this.timePassedShoot = 0;
            }
        }
    }

    /**
     * Add an alien to the game.
     *
     * @param g - the game.
     */
    @Override
    public void addToGame(GameLevel g) {
        for (List<Block> list : this.alien) {
            for (Block block : list) {
                block.addToGame(g);
            }
        }
        g.addSprite(this);
    }

    /**
     * Gets the lowest alien in a column.
     *
     * @param blocks the blocks
     * @return the the lowest
     */
    private Block getTheLowest(List<Block> blocks) {
        if (blocks.size() > 0) {
            return blocks.get(blocks.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * Gets right alien from the
     * aliens formation.
     *
     * @return the right
     */
    private Block getRight() {
        for (int i = this.alien.size() - 1; i >= 0; i--) {
            Block block = getTheLowest(this.alien.get(i));
            if (block != null) {
                return block;
            }
        }
        return null;
    }

    /**
     * Gets left alien from the
     * aliens formation.
     *
     * @return the left
     */
    private Block getLeft() {
        for (int i = 0; i < this.alien.size(); i++) {
            Block block = getTheLowest(this.alien.get(i));
            if (block != null) {
                return block;
            }
        }
        return null;
    }

    /**
     * Gets bottom alien.
     *
     * @return the bottom
     */
    private Block getBottom() {
        Block lowest = null;
        for (int i = 0; i < this.alien.size(); i++) {
            Block b = getTheLowest(this.alien.get(i));
            if (lowest == null) {
                lowest = b;
            } else if (b != null && lowest.getCollisionRectangle().getUpperLeft().getY()
                    < b.getCollisionRectangle().getUpperLeft().getY()) {
                lowest = b;
            }
        }
        return lowest;
    }

    /**
     * Gets win.
     *
     * @return the win
     */
    public Boolean getWin() {
        return isWin;
    }

    /**
     * Back to start point,
     * make the aliens formation to
     * go back to the start point.
     */
    public void backToStartPoint() {
        Boolean emptyColumn = false;
        int leftEmptyColumn = 0;
        // goes all the aliens
        for (List<Block> list : this.alien) {
            if (list.size() == 0 && !emptyColumn) {
                leftEmptyColumn++;
            } else {
                emptyColumn = true;
            }
            // change their location
            for (Block block : list) {
                Point point = block.getStartUpperLeft().clone();
                point.setX(point.getX() - leftEmptyColumn * 50);
                block.getCollisionRectangle().setUpperLeft(point);
            }
        }
        isWin = false;
        moveTime = startMoveTime;
        speed = Math.abs(speed);
    }
}
