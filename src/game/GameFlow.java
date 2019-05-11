package game;

import animation.AnimationRunner;
import animation.EndScreen;
import animation.HighScoresAnimation;
import animation.MenuAnimation;
import animation.KeyPressStoppableAnimation;
import animation.Task;
import biuoop.DialogManager;
import biuoop.GUI;
import gameobject.Background;

import biuoop.KeyboardSensor;

import utilts.Counter;
import utilts.HighScoresTable;
import utilts.ScoreInfo;

import java.awt.Color;
import java.io.File;
import java.io.IOException;


/**
 * The type Game flow.
 */
public class GameFlow {
    // Members of the class.
    private KeyboardSensor keyboardSensor;
    private AnimationRunner animationRunner;
    private Counter lives;
    private Counter score;
    private HighScoresTable highScoresTable;



    /**
     * Instantiates a new Game flow.
     */
    public GameFlow() {
        // Create a new Gui
        GUI gui = new GUI("Space Invaders", Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        // Create a new animation runner
        AnimationRunner animation = new AnimationRunner(Constants.SECOND, gui);
        this.animationRunner = animation;
        this.keyboardSensor = gui.getKeyboardSensor();
        this.highScoresTable = getHighScoresTable();
    }

    /**
     * Run levels.
     */
    public void runLevels() {
        // counter for the levels
        int count = 1;
        // creates a live and score counter.
        this.lives = new Counter();
        this.score = new Counter();
        this.lives.increase(Constants.LIVE);
        // the move game
        double move = 0.03;
        while (lives.getValue() > 0) {
            GameLevel level = new GameLevel(this.keyboardSensor,
                    this.animationRunner,
                    score,
                    lives,
                    move,
                    count);

            level.initialize();

            while ((lives.getValue() > 0) && (level.getAlienCounter() > 0)) {
                level.playOneTurn();
            }
            move = move * 0.8;
            count++;
        }

        // give the space key ro the end screen
        this.animationRunner.run(new KeyPressStoppableAnimation(keyboardSensor,
                KeyboardSensor.SPACE_KEY,
                new EndScreen(this.score, this.lives,
                        this.animationRunner.getGui())));
        // If we should update the table
        if (highScoresTable.getRank(score.getValue()) >= 0) {
            DialogManager dialog = animationRunner.getGui().getDialogManager();
            String name = dialog.showQuestionDialog("Name", "What is your name?", "");
            highScoresTable.add(new ScoreInfo(name, score.getValue()));
            try {
                highScoresTable.save(new File(Constants.HIGH_SCORE_TABLE_FILE_NAME));
            } catch (IOException e) {
                System.out.println("Fail to save highScores");
            }
        }
       // stop the high scores screen by press space key.
        animationRunner.run(new KeyPressStoppableAnimation(keyboardSensor,
                KeyboardSensor.SPACE_KEY,
                new HighScoresAnimation(highScoresTable)));
    }

    /**
     * Create a new HighScores Table and return it.
     *
     * @return the HighScores Table
     */
    private HighScoresTable getHighScoresTable() {
        File file = new File(Constants.HIGH_SCORE_TABLE_FILE_NAME);
        // Check if the HighScoresTable exits
        if (file.exists()) {
            // If so return it.
            return HighScoresTable.loadFromFile(file);
        } else {
            // Else create a new HighScores Table.
            HighScoresTable highScores = new HighScoresTable(5);
            try {
                highScores.save(file);
            } catch (IOException e) {
                System.out.println("Fail to save highScores");
            }
            // return the HighScores Table.
            return highScores;
        }
    }

    /**
     * Start the game.
     */
    public void startTheGame() {
        // Run the game.
        while (true) {
            MenuAnimation<Task> menuAnimation = getMenu();
            this.animationRunner.run(menuAnimation);
            Task task = menuAnimation.getStatus();
            task.run();
        }
    }

    /**
     * Gets menu.
     *
     * @return the menu
     */
    public MenuAnimation<Task> getMenu() {
        // Create a new menu animation
        MenuAnimation<Task> menuAnimation = new MenuAnimation<>("Space Invaders",
                keyboardSensor, new Background(Color.decode("#FCE4EC")));
        // Task for highScore
        Task<Void> startGame = new Task<Void>() {
            @Override
            // Create run to highScore
            public Void run() {
                runLevels();
                return null;
            }
        };
        Task<Void> highScore = new Task<Void>() {
            @Override
            // Create run to highScore
            public Void run() {
                animationRunner.run(new KeyPressStoppableAnimation(keyboardSensor,
                        KeyboardSensor.SPACE_KEY,
                        new HighScoresAnimation(highScoresTable)));
                return null;
            }
        };

        // Task for exitGame
        Task<Void> exitGame = new Task<Void>() {
            // exit the game.
            @Override
            public Void run() {
                System.exit(0);
                return null;
            }
        };
        // add new selections
        menuAnimation.addSelection("s", "Start Game", startGame);
        menuAnimation.addSelection("h", "High Scores", highScore);
        menuAnimation.addSelection("q", "Exit", exitGame);
        return menuAnimation;
    }
}