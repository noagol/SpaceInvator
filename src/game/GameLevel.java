package game;

import gameobject.Aliens;
import gameobject.Background;
import animation.AnimationRunner;
import animation.CountdownAnimation;
import animation.PauseScreen;
import animation.Animation;
import biuoop.DrawSurface;
import biuoop.KeyboardSensor;

import gameobject.Ball;
import gameobject.Block;
import gameobject.Collidable;
import gameobject.LivesIndicator;
import gameobject.ScoreIndicator;
import gameobject.Sprite;
import gameobject.LevelIndicator;
import gameobject.Spaceship;
import geometry.Point;
import observer.BallRemover;
import observer.BlockRemover;
import utilts.Counter;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;


/**
 * A class to create a new game.
 * The class is in charge of initializing and running the game.
 */
public class GameLevel implements Animation {
    // Members of the class.
    private SpriteCollection sprites;
    private GameEnvironment environment;
    private Counter remainingAliens;
    private Counter score;
    private Counter lives;
    private AnimationRunner runner;
    private boolean running;
    private KeyboardSensor keyboardSensor;
    private double move;
    private Aliens aliens;
    private List<Ball> aliensBalls = new ArrayList<>();
    private int levelCount;

    /**
     * A constructor, create a new game.
     *
     * @param keyboardSensor  the keyboard sensor
     * @param animationRunner the animation runner
     * @param score           the score
     * @param lives           the lives
     * @param move            the move
     * @param count           the count levels
     */
    public GameLevel(KeyboardSensor keyboardSensor,
                     AnimationRunner animationRunner, Counter score,
                     Counter lives, double move, int count) {
        sprites = new SpriteCollection();
        environment = new GameEnvironment();
        remainingAliens = new Counter();
        this.lives = lives;
        this.score = score;
        this.keyboardSensor = keyboardSensor;
        this.runner = animationRunner;
        this.move = move;
        this.levelCount = count;
    }


    /**
     * Add a new collidable to the the list
     * of the collidables.
     *
     * @param c - a new collidable to add.
     */
    public void addCollidable(Collidable c) {
        environment.addCollidable(c);
    }

    /**
     * Add a new sprite to the sprute list.
     *
     * @param s - a new sprite to add.
     */
    public void addSprite(Sprite s) {
        sprites.addSprite(s);
    }

    /**
     * Remove collidable.
     *
     * @param c a collidable to remove.
     */
    public void removeCollidable(Collidable c) {
        environment.removeCollidable(c);
    }

    /**
     * Remove sprite.
     *
     * @param s a sprite to remove.
     */
    public void removeSprite(Sprite s) {
        sprites.removeSprite(s);
    }

    /**
     * Initialize a new game: create the blocks,
     * gameobject,Ball and gameobject, Paddle
     * and add them to the game.
     */
    public void initialize() {
        //information.getBackground().addToGame(this);
        addSprite(new Background(Color.black));
        ScoreIndicator sc = new ScoreIndicator(score);
        sc.addToGame(this);
        LivesIndicator live = new LivesIndicator(lives);
        live.addToGame(this);
        LevelIndicator level = new LevelIndicator("Battle no." + levelCount);
        level.addToGame(this);
        // create the aliens
        List<List<Block>> block1 = getShield(100, Constants.SHIELDS_HEIGHT);
        List<List<Block>> block2 = getShield(350, Constants.SHIELDS_HEIGHT);
        List<List<Block>> block3 = getShield(600, Constants.SHIELDS_HEIGHT);
        aliens = new Aliens(this, move, this.score,
                this.remainingAliens);
        aliens.addToGame(this);
        // create death region
        deathRegion();
    }

    /**
     * Run the game - start the animation loop.
     */
    public void playOneTurn() {
        // Create a new paddle.
        Spaceship p = new Spaceship(keyboardSensor, Constants.PADDLE_WIDTH,
                Constants.PADDLE_SPEED, this);
        // Add the paddle to the game.
        p.addToGame(this);
        environment.setPaddle(p);
        this.runner.run(new CountdownAnimation(2, 3, sprites));
        this.running = true;
        this.runner.run(this);
        p.removeFromGame(this);
    }

    /**
     * Do one frame in the animation.
     *
     * @param d  the surface
     * @param dt the dt
     */
    @Override
    public void doOneFrame(DrawSurface d, double dt) {
        //draws all the sprites on the surface.
        this.sprites.drawAllOn(d);
        this.sprites.notifyAllTimePassed();
        // Check if there is no more alien in the game.
        if (remainingAliens.getValue() == 0) {
            this.running = false;
        }
        if (environment.getSpaceship().isSpaceshipLose()) {
            environment.getSpaceship().setSpaceshipLost(false);
            removeAlienBalls();
            lives.decrease(1);
            if (lives.getValue() > 0) {
                aliens.backToStartPoint();
                this.runner.run(new CountdownAnimation(2, 3, sprites));
            } else {
                this.running = false;
            }
        }
        if (aliens.getWin()) {
            lives.decrease(1);
            if (lives.getValue() > 0) {
                aliens.backToStartPoint();
                this.runner.run(new CountdownAnimation(2, 3, sprites));
            } else {
                this.running = false;
            }
        }
        // Check if "p" is pressed
        if (keyboardSensor.isPressed("p")) {
            this.runner.run(new PauseScreen(keyboardSensor));
        }
    }

    /**
     * Check if the animation should stop.
     *
     * @return true if the animation need to stop,
     * else return false.
     */
    @Override
    public boolean shouldStop() {
        return !this.running;
    }

    /**
     * Stop the animation.
     */
    @Override
    public void stop() {
        this.running = true;
    }


    /**
     * Gets alien counter.
     *
     * @return the alien counter
     */
    public int getAlienCounter() {
        return this.remainingAliens.getValue();
    }

    /**
     * Gets game environment.
     *
     * @return the game environment
     */
    public GameEnvironment getGameEnvironment() {
        return this.environment;
    }

    /**
     * Gets shield blocks.
     *
     * @param x the x
     * @param y the y
     * @return the shield
     */
    private List<List<Block>> getShield(int x, int y) {
        List<List<Block>> blocks = new ArrayList<>();
        List<Block> row;
        BlockRemover b = new BlockRemover(this);
        // create shield blocks.
        for (int i = 0; i < 3; i++) {
            row = new ArrayList<>();
            for (int j = 0; j < 20; j++) {
                Block block = new Block(new Point(x + j * 7, y + i * 7),
                        7.0, 7.0, this, Color.decode("#CE93D8"), false);
                block.addToGame(this);
                block.addHitListener(b);
                row.add(block);
            }
            blocks.add(row);
        }
        return blocks;
    }

    /**
     * Death region.
     */
    public void deathRegion() {
        BallRemover ballRemover = new BallRemover(this);
        // Set the upper block.
        Block topBlock = new Block(new Point(0, 0),
                (double) Constants.SCREEN_WIDTH,
                (double) Constants.BLOCK_HIGTH, this, Color.BLACK, false);
        // Add the block to the game.
        addCollidable(topBlock);
        topBlock.addHitListener(ballRemover);
        // Set the bottom block.
        Block bottomblock = new Block(new Point(0, Constants.SCREEN_HEIGHT),
                (double) Constants.SCREEN_WIDTH,
                (double) Constants.BLOCK_HIGTH, this, Color.black, false);
        // Add the block to the game.
        addCollidable(bottomblock);
        bottomblock.addHitListener(ballRemover);
    }

    /**
     * Add ball to the list
     * of the balls.
     *
     * @param ball the ball
     */
    public void addBall(Ball ball) {
        this.aliensBalls.add(ball);
    }

    /**
     * Remove alien balls.
     */
    private void removeAlienBalls() {
       // remove all balls from thr game.
        for (Ball ball : aliensBalls) {
            sprites.removeSprite(ball);
        }
        aliensBalls.removeAll(aliensBalls);
    }
}

