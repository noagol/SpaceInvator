package gameobject;

import biuoop.DrawSurface;
import game.Constants;
import game.GameLevel;
import geometry.Line;
import geometry.Point;
import geometry.Rectangle;
import utilts.Velocity;
import observer.HitListener;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A class to describe a block.
 * A block is a rectangle with color and number of hits by the ball.
 */
public class Block implements Collidable, Sprite, HitNotifier, Cloneable {
    // Members of the class.
    private Rectangle rect;
    private int numberOfHits;
    private List<HitListener> hitListeners = new ArrayList<>();
    private GameLevel gameLevel;
    private Map<Integer, List<DrawBlock>> drawers = new HashMap<>();
    private Boolean iaAlien;
    private Point startUpperleft;

    /**
     * A constructor of the block.
     * get a new block sizes and the number of the start hits.
     *
     * @param upperLeft - the point of the left side of the block.
     * @param width     - the width of the block.
     * @param height    - the height of the block.
     * @param game      the game
     * @param c         the color
     * @param alien     the alien
     */
    public Block(Point upperLeft, Double width, Double height,
                 GameLevel game, Color c, Boolean alien) {
        this.rect = new Rectangle(upperLeft, width, height);
        this.numberOfHits = 1;
        this.hitListeners = new ArrayList<>();
        this.gameLevel = game;
        List<DrawBlock> draw = new ArrayList<>();
        draw.add(new BlockFill(c));
        this.drawers.put(-1, draw);
        this.iaAlien = alien;
        this.startUpperleft = upperLeft;
    }

    /**
     * A constructor of the block.
     * get a new block sizes and the number of the start hits.
     *
     * @param upperLeft - the point of the left side of the block.
     * @param width     - the width of the block.
     * @param height    - the height of the block.
     * @param game      the game
     * @param image     the image
     * @param alien     the alien
     */
    public Block(Point upperLeft, Double width, Double height,
                 GameLevel game, Image image, Boolean alien) {
        this.rect = new Rectangle(upperLeft, width, height);
        this.numberOfHits = 1;
        this.hitListeners = new ArrayList<>();
        this.gameLevel = game;
        List<DrawBlock> draw = new ArrayList<>();
        draw.add(new BlockImage(image));
        this.drawers.put(-1, draw);
        this.iaAlien = alien;
        this.startUpperleft = upperLeft;
    }

    /**
     * @return - the "collision shape" of the object (rectangle).
     */
    public Rectangle getCollisionRectangle() {
        return this.rect;
    }

    /**
     * Notify the object that we collided with it at collisionPoint
     * with a given velocity.
     * Return the new velocity expected after the hit (based on
     * the force the object inflicted on us).
     * If it is hit from the sides, it changes its horizontal
     * direction, otherwise changes the the vertical direction.
     *
     * @param hitter          - hitter ball
     * @param collisionPoint  - the collision point between the
     *                        rectangle and the object.
     * @param currentVelocity - the object velocity.
     * @return the new velocity of the ball.
     */
    public Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity) {
        /* If the block is an alien and the ball
         come from another alien remove the ball.
          */
        if (this.iaAlien && currentVelocity.getDy() > 0) {
            hitter.removeFromGame(gameLevel);
            return currentVelocity;
        } else {
            List<Line> frame = this.rect.getListRectangle();
            numberOfHits--;
            hitter.removeFromGame(gameLevel);
            this.notifyHit(hitter);
            // Check if the ball hit the upper or bottom of the block.
            if (frame.get(0).pointExists(collisionPoint) || frame.get(1).pointExists(collisionPoint)) {
                // Change horizontal direction.
                return new Velocity(currentVelocity.getDx(), -currentVelocity.getDy());
                // Check if the ball hit the left or right side of the block.
            } else if (frame.get(2).pointExists(collisionPoint) || frame.get(3).pointExists(collisionPoint)) {
                // Change vertical direction.
                return new Velocity(-currentVelocity.getDx(), currentVelocity.getDy());
            } else {
                throw new RuntimeException("error");
            }
        }
    }


    /**
     * Draw a new block to the screen.
     * Draw the number of hits in the middle of the block.
     *
     * @param surface - a surface to draw on.
     */
    public void drawOn(DrawSurface surface) {
        List<DrawBlock> drawBlocks;
        if (this.drawers.containsKey(numberOfHits)) {
            drawBlocks = this.drawers.get(numberOfHits);
        } else {
            drawBlocks = this.drawers.get(-1);
        }
        if (drawBlocks != null) {
            for (DrawBlock block : drawBlocks) {
                block.drawOn(surface, rect);
            }
        }
    }

    /**
     * Currently we do nothing.
     *
     * @param dt - the dt.
     */
    public void timePassed(double dt) {
    }

    /**
     * Adding the block to the game.
     *
     * @param g - the game.
     */
    public void addToGame(GameLevel g) {
        g.addCollidable(this);
        g.addSprite(this);
    }

    /**
     * Remove block from game.
     */
    public void removeFromGame() {
        gameLevel.removeSprite(this);
        gameLevel.removeCollidable(this);
    }

    /**
     * Notify hit.
     *
     * @param hitter - hitter ball
     */
    private void notifyHit(Ball hitter) {
        // Make a copy of the hitListeners.
        List<HitListener> listeners = new ArrayList<HitListener>(this.hitListeners);
        // Notify all listeners about a hit event:
        for (HitListener hl : listeners) {
            hl.hitEvent(this, hitter);
        }
    }

    /**
     * Add hl as a listener to hit events.
     *
     * @param hl - listener to add.
     */
    @Override
    public void addHitListener(HitListener hl) {
        this.hitListeners.add(hl);
    }

    /**
     * Remove hl from the list of listeners to hit events.
     *
     * @param hl - listener to remove.
     */
    @Override
    public void removeHitListener(HitListener hl) {
        this.hitListeners.remove(hl);
    }

    /**
     * Gets hit points.
     *
     * @return the hit points
     */
    public int getHitPoints() {
        return this.numberOfHits;
    }


    /**
     * @return a string representation
     * of the object block
     */
    public Block clone() {
        try {
            return (Block) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    /**
     * Move the block to left
     * or right side by change x.
     *
     * @param speed the speed
     */
    public void moveSide(int speed) {
        rect.setUpperLeft(new Point(rect.getUpperLeft().getX() + speed,
                rect.getUpperLeft().getY()));
    }

    /**
     * Move the block up and down.
     *
     * @param speed the speed
     */
    public void moveUpDown(int speed) {
        rect.setUpperLeft(new Point(rect.getUpperLeft().getX(),
                rect.getUpperLeft().getY() + speed));
    }

    /**
     * Shoot ball from the alien.
     */
    public void shoot() {
        Ball ball = new Ball(rect.getUpperLeft().getX() + rect.getWidth() / 2,
                rect.getUpperLeft().getY() + Constants.RADIUS_ALIENS + rect.getHeight(),
                Constants.RADIUS_ALIENS, Color.decode("#F48FB1"),
                this.gameLevel.getGameEnvironment());
        ball.setVelocity(0, 400);
        ball.addToGame(this.gameLevel);
    }

    /**
     * Gets start upper left of a block.
     *
     * @return the start upperleft
     */
    public Point getStartUpperLeft() {
        return startUpperleft;
    }
}