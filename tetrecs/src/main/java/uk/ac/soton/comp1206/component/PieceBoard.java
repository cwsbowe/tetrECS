package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

public class PieceBoard extends GameBoard {

    private static final Logger logger = LogManager.getLogger(PieceBoard.class);

    public PieceBoard(Grid grid, double width, double height) {
        super(grid, width, height);
    }


    public void displayPiece(GamePiece currentPiece) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                grid.set(x, y, 0);
            }
        }
        grid.playPiece(currentPiece, 1, 1);
        logger.info("Current piece updated");
    }
}
