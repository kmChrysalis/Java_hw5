package mines;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MinesFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Board.fxml")); //receive our scene from fxml
        Scene scene = new Scene(root); //create new scene from root
        primaryStage.setTitle("Adventure Mine-Time Sweeper"); //set title
        /* set minimal size of windows */
        primaryStage.setMinHeight(650);
        primaryStage.setMinWidth(800);
        /* set maximal size of windows */
        primaryStage.setMaxHeight(1070);
        primaryStage.setMaxWidth(1920);
        /* set background image*/
        root.setStyle("-fx-background-image: url('/resources/background.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-position: center");
        /* make scene primary stage and show*/
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}