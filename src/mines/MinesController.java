package mines;

import Ljavafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.util.function.UnaryOperator;

public class MinesController {
    private int height, width, mines; //variable for saving size, and mines count
    private GridPane grid; //grind for field
    private Mines game; //our game :)
    private fieldButton[][] buttonsGrid; //buttonsGrid array to be placed into grid

    class fieldButton { //my button class
        private Button button;
        private int x, y; //coordinates
        private boolean flag = false; //flag toggle

        private fieldButton(Button button, int x, int y) { //nested class button constructor
            this.button = button;
            this.x = x;
            this.y = y;
            this.button.setMinSize(48, 48); //minimal size of button
            this.button.setPrefSize(54, 54); //preferable size
            this.button.setMaxSize(54, 54); //maximum size
            /* get some style */
            this.button.setStyle("-fx-background-image: url('/resources/closed.jpg');" +
                    "-fx-text-fill: Black;" +
                    "-fx-border-color: Black;" +
                    "-fx-border-width: 1;" +
                    "-fx-font-size: 24;" +
                    "-fx-font-weight: bold ");
            /* and add mouse listener */
            this.button.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) { //check if right button was pressed
                    flag = game.toggleFlag(this.x, this.y); //toggle flag
                    if (flag) {
                        this.button.setText(""); //set text, and stylish
                        this.button.setStyle("-fx-background-image: url('/resources/flag.jpg');" +
                                "-fx-text-fill: Black;" +
                                "-fx-border-color: Black;" +
                                "-fx-border-width: 1;" +
                                "-fx-font-size: 24;" +
                                "-fx-font-weight: bold;");
                    } else {
                        this.button.setText(".");
                        this.button.setStyle("-fx-background-image: url('/resources/closed.jpg');" +
                                "-fx-text-fill: Black;" +
                                "-fx-border-color: Black;" +
                                "-fx-border-width: 1;" +
                                "-fx-font-size: 24;" +
                                "-fx-font-weight: bold;");
                    }
                } else { //if left button was pressed
                    if (!game.open(this.x, this.y)) { //trying to open, if false, we lost
                        shower("YOU DIED"); //using external function to open all cells, after set new bomb pic to one we pressed
                        this.button.setStyle("-fx-background-image: url('/resources/bomb_active.jpg');" +
                                "-fx-text-fill: Black;" +
                                "-fx-border-color: Black;" +
                                "-fx-border-width: 1;" +
                                "-fx-font-size: 24;" +
                                "-fx-font-weight: bold ");
                    } else if (game.isDone()) { //check if you won
                        shower("YOU WON");
                    } else {
                        this.button.setOnMouseClicked(null); //deactivate button we press and add styles
                        this.button.setStyle("-fx-background-image: url('/resources/opened.jpg');" +
                                "-fx-text-fill: Black;" +
                                "-fx-border-color: Black;" +
                                "-fx-border-width: 1;" +
                                "-fx-font-size: 24;" +
                                "-fx-font-weight: bold;");
                        this.button.setText(game.get(this.x, this.y)); //set number for button
                        checks(); //check neighbours
                    }
                }
            });
        }

        private void shower(String str) {
            Finish.setVisible(true); //showing message
            Finish.setText(str); //set string as label
            game.setShowAll(true); //toggle showAll flag
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    buttonsGrid[i][j].button.setOnMouseClicked(null); //deactivate button
                    String key = game.get(i, j); //get a key
                    if (key.equals("X")) { //if this cell is mine, set mine image
                        buttonsGrid[i][j].button.setStyle("-fx-background-image: url('/resources/bomb_not_active.jpg');" +
                                "-fx-text-fill: Black;" +
                                "-fx-border-color: Black;" +
                                "-fx-border-width: 1;" +
                                "-fx-font-size: 24;" +
                                "-fx-font-weight: bold ");
                    } else { //if this cell not mine, it's just an opened one
                        buttonsGrid[i][j].button.setStyle("-fx-background-image: url('/resources/opened.jpg');" +
                                "-fx-text-fill: Black;" +
                                "-fx-border-color: Black;" +
                                "-fx-border-width: 1;" +
                                "-fx-font-size: 24;" +
                                "-fx-font-weight: bold;");
                        buttonsGrid[i][j].button.setText(key); //probably with some text, or without, depends on key
                    }
                }
            }
        }

        private void checks() {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (game.isOpened(i, j)) { //if this cell is opened, set image of opened
                        buttonsGrid[i][j].button.setStyle("-fx-background-image: url('/resources/opened.jpg');" +
                                "-fx-text-fill: Black;" +
                                "-fx-border-color: Black;" +
                                "-fx-border-width: 1;" +
                                "-fx-font-size: 24;" +
                                "-fx-font-weight: bold; ");
                        buttonsGrid[i][j].button.setText(game.get(i, j)); //set text, if it has
                        buttonsGrid[i][j].button.setOnMouseClicked(null); //deactivate listener
                    }
                }
            }
        }
    }


    @FXML
    void initialize() {
        DropShadow ds = new DropShadow(); //create effect of shadow
        ds.setOffsetY(10.0f); //move it a bit by Y
        ds.setOffsetX(10.0f); //move it by X
        Finish.setStyle("-fx-text-fill: #A3001A;" + //set color and bold characters
                "-fx-font-weight: bold;");
        Finish.setEffect(ds); //set shadow for Finish label
        pane.setMaxSize(1770,1070);

        UnaryOperator<TextFormatter.Change> intFilter = change -> //unary operator to exclude invalid input
                change.getControlNewText().matches("\\d{0,9}") ? change : null;

        setHeight.setTextFormatter(new TextFormatter<String>(intFilter));
        setWidth.setTextFormatter(new TextFormatter<String>(intFilter));
        setMines.setTextFormatter(new TextFormatter<String>(intFilter));

        restart_button.setOnAction(event -> { //create listener for restart button
            /*get size of field, but I add some bounds, in case it won't get out of screen with resolution 1920x1080 */
            height = Integer.parseInt(setHeight.getText()) % 22;
            width = Integer.parseInt(setWidth.getText()) % 37;
            /* amount of mines are bounded by field, i.e. 10x10 = 100, so <=100 mines can be placed */
            mines = Integer.parseInt(setMines.getText()) % (height*width + 1);

            if (grid != null) { //if grid already has been created
                grid.getChildren().clear(); //clear this grid
                Finish.setVisible(false); //make finish label invisible
            }

            if (height > 0 && width > 0 && mines > 0) { //if variables are bigger then 0, we can go forward
                game = new Mines(height, width, mines); //create new game
                buttonsGrid = new fieldButton[height][width]; //create array of buttonsGrid
                grid = new GridPane(); //create new grid pane
                grid.setAlignment(Pos.CENTER); //set it position on middle
                grid.setPrefSize(930, 700); //preferable size
                grid.setMaxSize(1770, 1070); //maximum size

                for (int i = 0; i < height; i++) { //creating new buttonsGrid
                    for (int j = 0; j < width; j++) {
                        buttonsGrid[i][j] = new fieldButton(new Button(game.get(i, j)), i, j);
                        grid.add(buttonsGrid[i][j].button, j, i); //add this button to grid
                    }
                }
                pane.getChildren().add(grid); //add our grid to StackPane
            }
        });
    }

    @FXML
    private Button restart_button;

    @FXML
    private TextField setWidth;

    @FXML
    private TextField setHeight;

    @FXML
    private TextField setMines;

    @FXML
    private StackPane pane;

    @FXML
    private Label Finish;
}