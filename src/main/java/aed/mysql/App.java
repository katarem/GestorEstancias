package aed.mysql;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application{

    @Override public void start(Stage stage) throws Exception {
        Controller c = new Controller();
        stage.setTitle("Gestor de Estancias");
        stage.setScene(new Scene(c.getView()));
        stage.show();
    }
    public static void main(String[] args){
        launch(args);
    }
}
