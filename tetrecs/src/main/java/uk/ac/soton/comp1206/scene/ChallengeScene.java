package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        //display of current board state
        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);

        //display of next piece
        var pieceBoard = new PieceBoard(game.getPieceGrid(), gameWindow.getWidth()/6, gameWindow.getWidth()/6);
        game.pieceBoard = pieceBoard;
        mainPane.setRight(pieceBoard);

        //display of following piece
        var followingPieceBoard = new PieceBoard(game.getFollowingPieceGrid(), gameWindow.getWidth()/8, gameWindow.getWidth()/8);
        game.followingPieceBoard = followingPieceBoard;
        mainPane.setLeft(followingPieceBoard);


        //displays score
        var scoreLabel = new Text("Score: ");
        var score = new Text();
        score.getStyleClass().add("score");
        scoreLabel.getStyleClass().add("score");
        score.textProperty().bind(game.getScore().asString());

        //displays level
        var levelLabel = new Text("\nLevel: ");
        var level = new Text();
        level.getStyleClass().add("level");
        levelLabel.getStyleClass().add("level");
        level.textProperty().bind(game.getLevel().asString());

        //displays lives
        var livesLabel = new Text("\nLives: ");
        var lives = new Text();
        lives.getStyleClass().add("lives");
        livesLabel.getStyleClass().add("lives");
        lives.textProperty().bind(game.getLives().asString());
        //if lives reaches zero, goes to the score screen
        game.getLives().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(0)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        gameWindow.startScore();
                    }
                });
            }
        });

        //displays multiplier
        var multiplierLabel = new Text("\nMultiplier: ");
        var multiplier = new Text();
        multiplierLabel.setFill(Color.PINK);
        multiplier.setFill(Color.PINK);
        multiplier.textProperty().bind(game.getMultiplier().asString());

        //displays time
        var timeLabel = new Text("\nTime: ");
        var time = new Text();
        timeLabel.setFill(Color.WHITE);
        time.setFill(Color.WHITE);
        time.textProperty().bind((game.getTimeLeft().asString()));
        //if time drops below 2, changes colour
        game.getTimeLeft().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(2.0) || newValue.equals(1.5) || newValue.equals(1.0) || newValue.equals(0.5)) {
                time.setFill(Color.PURPLE);
                timeLabel.setFill(Color.PURPLE);
            } else {
                time.setFill(Color.WHITE);
                timeLabel.setFill(Color.WHITE);
            }
        });

        //textflow in which score, level, lives, multiplier, and time are contained
        var textFlow = new TextFlow(scoreLabel, score, levelLabel, level, livesLabel, lives, multiplierLabel, multiplier, timeLabel, time);
        mainPane.setTop(textFlow);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
    }


    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
        //controls leave scene events
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case ESCAPE: game.multimedia.endMusic(); gameWindow.startMenu(); game.cancelTimer();
                    case R: game.rotateCurrentPiece(); break;
                    case SPACE: game.swapCurrentPieces(); break;
                }
            }
        });
    }
}
