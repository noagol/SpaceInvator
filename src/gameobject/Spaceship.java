package gameobject;

import biuoop.DrawSurface;
import biuoop.KeyboardSensor;
import game.Constants;
import game.GameLevel;
import geometry.Point;
import geometry.Rectangle;
import utilts.Velocity;

import java.awt.Color;

/**
 * A class to describe a paddle.
 * The gameobject.Paddle is the player in the game.
 * It is a rectangle that is controlled by the arrow keys,
 * and moves according to the player key presses.
 */
public class Spaceship implements Sprite, Collidable {
    // Members of the paddle.
    private biuoop.KeyboardSensor keyboard;
    private Rectangle spaceship;
    private int width;
    private int speed;
    private GameLevel gameLevel;
    private boolean isPressed = true;
    private Double shootInterval = 0.0;
    private boolean spaceshipLose = false;

    /**
     * A constructor, creates a new paddle as rectangle
     * controlled by the given keyboard sensor.
     *
     * @param keyboardSensor the keyboard sensor
     * @param width          the width
     * @param speed          the speed
     * @param game           the game
     */
    public Spaceship(KeyboardSensor keyboardSensor, int width, int speed, GameLevel game) {
        // Create a keyboard sensor for the current GUI.
        keyboard = keyboardSensor;
        // Create a rectangle - paddle.
        spaceship = new Rectangle(new Point((Constants.SCREEN_WIDTH - width) / 2,
                Constants.SCREEN_HEIGHT - Constants.BLOCK_HIGTH - 10),
                width, Constants.BLOCK_HIGTH);
        this.width = width;
        this.speed = speed;
        this.gameLevel = game;
    }

    /**
     * Move the paddle one step to the left (10 in each move),
     * the paddle will stop at the border.
     *
     * @param dt - the dt.
     */
    public void moveLeft(double dt) {
        // The new x after the paddle move left.
        double newX = Math.max(spaceship.getUpperLeft().getX()
                - (speed * dt), 0);
        // Move the paddle left.
        spaceship = new Rectangle(new Point(newX, spaceship.getUpperLeft().getY()),
                spaceship.getWidth(), spaceship.getHeight());
    }

    /**
     * Move the paddle one step to the right (10 in each move),
     * the paddle will stop at the border.
     *
     * @param dt - the dt.
     */
    public void moveRight(double dt) {
        // The new x after the paddle move right.
        double newX = Math.min(spaceship.getUpperLeft().getX() + (speed * dt),
                (Constants.SCREEN_WIDTH - this.spaceship.getWidth()));
        // Move the paddle right.
        spaceship = new Rectangle(new Point(newX, spaceship.getUpperLeft().getY()),
                spaceship.getWidth(), spaceship.getHeight());
    }

    /**
     * Check if the "left" or "right" keys are pressed,
     * and if so move it accordingly.
     *
     * @param dt - the dt.
     */
    public void timePassed(double dt) {
        // If the left key is pressed.
        if (keyboard.isPressed(KeyboardSensor.LEFT_KEY)) {
            // Move the paddle left.
            moveLeft(dt);
        }
        // If the right key is pressed.
        if (keyboard.isPressed(KeyboardSensor.RIGHT_KEY)) {
            // Move the paddle right.
            moveRight(dt);
        }
        // If the space key is pressed.
        if (keyboard.isPressed(KeyboardSensor.SPACE_KEY) && !isPressed) {
            this.isPressed = true;
        } else if (!keyboard.isPressed(KeyboardSensor.SPACE_KEY)) {
            this.isPressed = false;
        } else if (shootInterval >= 0.35) {
            if (isPressed) {
                // shoot ball
                shoot();
            }
            shootInterval = 0.0;
        }
        shootInterval += dt;
    }


    /**
     * Shoot ball from the spaceship by press space key.
     */
    private void shoot() {
        Ball ball = new Ball(spaceship.getUpperLeft().getX() + spaceship.getWidth() / 2,
                spaceship.getUpperLeft().getY() - Constants.RADIUS_SPACESHIP, Constants.RADIUS_SPACESHIP, Color.WHITE,
                this.gameLevel.getGameEnvironment());
        ball.setVelocity(0, -400);
        ball.addToGame(this.gameLevel);
    }

    /**
     * Draw the paddle (rectangle).
     *
     * @param d - a surface to draw on.
     */
    public void drawOn(DrawSurface d) {
        // Set the color of the paddle to orange.
        d.setColor(Color.decode("#F8BBD0"));
        // Draw the rectangle - paddle.
        d.fillRectangle((int) spaceship.getUpperLeft().getX(), (int) spaceship.getUpperLeft().getY()
                , (int) spaceship.getWidth(), (int) spaceship.getHeight());
        // Set the color of the frame of the paddle to black.
        d.setColor(Color.black);
        // Draw the frame of the rectangle.
        d.drawRectangle((int) spaceship.getUpperLeft().getX(), (int) spaceship.getUpperLeft().getY()
                , (int) spaceship.getWidth(), (int) spaceship.getHeight());
    }

    /**
     * @return the collidable object - paddle.
     */
    public Rectangle getCollisionRectangle() {
        return spaceship;
    }

    /**
     * Return the new velocity expected after the hit (based on
     * the force the object inflicted on us).
     * The paddle having 5 equally-spaced regions.
     * The behavior of the ball's bounce depends on where it hits the paddle.
     *
     * @param hitter          - hitter ball
     * @param collisionPoint  - the collision point between the
     *                        rectangle and the object.
     * @param currentVelocity - the object velocity.
     * @return the new velocity expected after the hit.
     */
    public Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity) {
        spaceshipLose = true;
        hitter.removeFromGame(gameLevel);
        return currentVelocity;
    }

    /**
     * Add this paddle to the game.
     *
     * @param g - a game.
     */
    public void addToGame(GameLevel g) {
        g.addSprite(this);
        g.addCollidable(this);
    }

    /**
     * Remove from game.
     *
     * @param g the g
     */
    public void removeFromGame(GameLevel g) {
        g.removeSprite(this);
        g.removeCollidable(this);
    }

    /**
     * Is spaceship lost boolean.
     *
     * @return the boolean
     */
    public boolean isSpaceshipLose() {
        return spaceshipLose;
    }

    /**
     * Sets spaceship lost.
     *
     * @param spaceshipLost the spaceship lost
     */
    public void setSpaceshipLost(boolean spaceshipLost) {
        this.spaceshipLose = spaceshipLost;
    }
}