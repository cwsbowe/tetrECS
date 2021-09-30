package uk.ac.soton.comp1206.game;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.scene.media.Media;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;
    protected final Grid pieceGrid;
    protected final Grid followingPieceGrid;

    //two upcoming pieces
    private GamePiece currentPiece;
    private GamePiece followingPiece;

    //game variables
    private SimpleIntegerProperty score = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty level = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty lives = new SimpleIntegerProperty(3);
    private SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);

    //score holder
    private SimpleListProperty localScores = new SimpleListProperty();

    //media variables
    public Multimedia multimedia;
    private Media placeSound;
    private Media rotateSound;
    private Media swapSound;

    //boards holding upcoming pieces
    public PieceBoard pieceBoard;
    public PieceBoard followingPieceBoard;

    //time controls
    public Timer timer;
    public Timer shownTimer;
    public TimerTask timerTask;
    public SimpleDoubleProperty timeLeft = new SimpleDoubleProperty(12);
    public TimerTask reduceTimeLeft;


    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
        this.pieceGrid = new Grid(3, 3);
        this.followingPieceGrid = new Grid(3, 3);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        Media music = new Media(getClass().getResource("/music/game.wav").toExternalForm());
        placeSound = new Media(getClass().getResource("/sounds/place.wav").toExternalForm());
        rotateSound = new Media(getClass().getResource("/sounds/rotate.wav").toExternalForm());
        swapSound = new Media(getClass().getResource("/sounds/transition.wav").toExternalForm());
        logger.info("Game media established");
        multimedia = new Multimedia();
        multimedia.playMusic(music);
        currentPiece = spawnPiece();
        followingPiece = spawnPiece();
        pieceBoard.displayPiece(currentPiece);
        followingPieceBoard.displayPiece(followingPiece);
        logger.info("Timers created");
        timer = new Timer();
        shownTimer = new Timer();
        //recreating timers and timer tasks
        timerTask = new TimerTask() {
            @Override
            public void run() {
                gameLoop();
            }
        };
        reduceTimeLeft = new TimerTask() {
            @Override
            public void run() {
                setTimeLeft(getTimeLeft().get() - 0.5);
            }
        };
        shownTimer.scheduleAtFixedRate(reduceTimeLeft, 500, 500);
        timer.schedule(timerTask, getTimerDelay());
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {

        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        if (grid.canPlayPiece(currentPiece, x, y)) {
            timer.cancel();
            shownTimer.cancel();
            grid.playPiece(currentPiece, x, y);
            multimedia.playAudio(placeSound);
            afterPiece();
            nextPiece();
            //recreating timers and timer tasks
            timer = new Timer();
            shownTimer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    gameLoop();
                }
            };
            reduceTimeLeft = new TimerTask() {
                @Override
                public void run() {
                    setTimeLeft(getTimeLeft().get() - 0.5);
                }
            };
            setTimeLeft(getTimerDelay() / 1000.0);
            shownTimer.scheduleAtFixedRate(reduceTimeLeft, 500, 500);
            timer.schedule(timerTask, getTimerDelay());

        }
    }



    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    public Grid getPieceGrid() { return pieceGrid; }

    public Grid getFollowingPieceGrid() { return followingPieceGrid; }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    //Getting game variables
    public SimpleIntegerProperty getScore() {
        return score;
    }

    public SimpleIntegerProperty getLevel() {
        return level;
    }

    public SimpleIntegerProperty getLives() {
        return lives;
    }

    public SimpleIntegerProperty getMultiplier() {
        return multiplier;
    }

    //Getting remaining time on timer
    public SimpleDoubleProperty getTimeLeft() { return timeLeft; }

    //Setting game variables
    public void setScore(int value) {
        score.set(value);
    }

    public void setLevel(int value) {
        level.set(value);
    }

    public void setLives(int value) {
        lives.set(value);
    }

    public void setMultiplier(int value) {
        multiplier.set(value);
    }

    //Setting remaining time on timer
    public void setTimeLeft(double value) {timeLeft.set(value);}

    public void cancelTimer() {
        timer.cancel();
    }

    public GamePiece spawnPiece() {
        logger.info("Spawning piece");
        Random rand = new Random();
        int piece = rand.nextInt(15);
        return GamePiece.createPiece(piece);
    }

    public void nextPiece() {
        logger.info("Next piece");
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        pieceBoard.displayPiece(currentPiece);
        followingPieceBoard.displayPiece(followingPiece);
    }

    public void afterPiece() {
        logger.info("After piece");
        boolean current = true;
        var rowsToClear = new ArrayList<Integer>();
        var colsToClear = new ArrayList<Integer>();
        int numOfRows = this.getRows();
        int numOfCols = this.getCols();
        //checks if any rows need to be cleared
        for (int rowNum = 0; rowNum < numOfRows; rowNum++) {
            for (int colNum = 0; colNum < numOfCols; colNum++) {
                if (grid.get(colNum, rowNum) == 0) {
                    current = false;
                }
            }
            if (current) {
                rowsToClear.add(rowNum);
            }
            current = true;
        }
        //checks if any columns need to be cleared
        for (int colNum = 0; colNum < numOfCols; colNum++) {
            for (int rowNum = 0; rowNum < numOfRows; rowNum++) {
                if (grid.get(colNum, rowNum) == 0) {
                    current = false;
                }
            }
            if (current) {
                colsToClear.add(colNum);
            }
            current = true;
        }
        //clearing rows
        for (int rowNum : rowsToClear) {
            for (int colNum = 0; colNum < numOfCols; colNum++) {
                grid.set(colNum, rowNum, 0);
            }
        }
        //clearing columns
        for (int colNum : colsToClear) {
            for (int rowNum = 0; rowNum < numOfCols; rowNum++) {
                grid.set(colNum, rowNum, 0);
            }
        }
        var numOfClearedRows = rowsToClear.size();
        var numOfClearedCols = colsToClear.size();
        var numOfClearedLines = numOfClearedRows + numOfClearedCols;
        if (numOfClearedLines > 0) {
            //works out the number of cleared blocks, accounting for duplicates
            int blocks = numOfRows * numOfClearedCols + numOfCols * numOfClearedRows - numOfClearedRows * numOfClearedCols;
            afterPiece(numOfClearedLines, blocks);
            setMultiplier(getMultiplier().get() + 1);
        } else {
            setMultiplier(1);
        }

    }

    //updates score and level
    public void afterPiece(int lines, int blocks){
        var previousScore = getScore();
        var multiplier = getMultiplier();
        var newScore = previousScore.get() + (lines * blocks * 10 * multiplier.get());
        setLevel(Math.floorDiv(newScore, 1000));
        setScore(newScore);
    }

    //rotates the current piece
    public void rotateCurrentPiece() {
        currentPiece.rotate();
        pieceBoard.displayPiece(currentPiece);
        multimedia.playAudio(rotateSound);
    }

    //swaps the current piece with the following piece
    public void swapCurrentPieces() {
        GamePiece temp = currentPiece;
        currentPiece = followingPiece;
        followingPiece = temp;
        pieceBoard.displayPiece(currentPiece);
        followingPieceBoard.displayPiece(followingPiece);
        multimedia.playAudio(swapSound);
    }

    public int getTimerDelay() {
        int level = getLevel().get();
        if (level < 19) {
            return 12000 - 500 * level;
        } else {
            return 2500;
        }
    }

    public void gameLoop() {
        //time runs out
        setLives(getLives().get() - 1);
        setMultiplier(1);

        //reset timer
        timer.cancel();
        shownTimer.cancel();
        setTimeLeft(getTimeLeft().get() / 1000.0);

        //change piece
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        pieceBoard.displayPiece(currentPiece);
        followingPieceBoard.displayPiece(followingPiece);

        //set new timer
        timer = new Timer();
        shownTimer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                gameLoop();
            }
        };
        reduceTimeLeft = new TimerTask() {
            @Override
            public void run() {
                setTimeLeft(getTimeLeft().get() - 0.5);
            }
        };
        setTimeLeft(getTimerDelay() / 1000.0);
        timer.schedule(timerTask, getTimerDelay());
        shownTimer.scheduleAtFixedRate(reduceTimeLeft, 500, 500);
    }

}
