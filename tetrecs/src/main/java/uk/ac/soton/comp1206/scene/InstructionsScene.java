package uk.ac.soton.comp1206.scene;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;

public class InstructionsScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Instructions Scene");
    }

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var instructionPane = new StackPane();
        instructionPane.setMaxWidth(gameWindow.getWidth());
        instructionPane.setMaxHeight(gameWindow.getHeight());
        instructionPane.getStyleClass().add("menu-background");
        root.getChildren().add(instructionPane);

        var mainPane = new BorderPane();
        instructionPane.getChildren().add(mainPane);

        //where the pieceboards are held
        var examplePane = new GridPane();
        mainPane.setBottom(examplePane);
        var pieceBoardList = new ArrayList<PieceBoard>();
        //adds pieceboards displaying each different piece
        for (int i = 0; i < 15; i++) {
            pieceBoardList.add(new PieceBoard(new Grid(3, 3), gameWindow.getWidth()/12, gameWindow.getWidth()/12));
            GamePiece piece = GamePiece.createPiece(i);
            pieceBoardList.get(i).displayPiece(piece);
            examplePane.add(pieceBoardList.get(i), i % 8, Math.floorDiv(i, 8));
        }

        //title
        var title = new Text("Instructions");
        title.getStyleClass().add("title");
        mainPane.setTop(title);

        //instructions
        var instructions = new Text("TetrECS is a fast-paced gravity-free block placement game, " +
                "where you must survive by clearing rows through careful placement of the upcoming " +
                "blocks before the time runs out. Lose all 3 lives and you're destroyed!\n" +
                "Create complete rows or columns to clear them. The more cleared at the same time, " +
                "the more points you earn! The bonus will multiply as you clear more in a row!\n" +
                "To go back to a previous screen, press Escape\n" +
                "Hit Enter to drop a piece\n" +
                "You can use your mouse to click to place the tiles\n" +
                "You can rotate left and right with Q and E\n" +
                "Hit R to swap the upcoming tiles");
        instructions.getStyleClass().add("instructions");
        mainPane.setCenter(instructions);
        mainPane.setAlignment(instructions, Pos.TOP_CENTER);

    }


    @Override
    public void initialise() {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    gameWindow.startMenu();
                }
            }
        });
    }
}
