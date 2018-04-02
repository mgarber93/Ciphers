package com.mattgarb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../../view/MainView.fxml"));
        primaryStage.setTitle("Ciphers");
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


    public static String onlyLowerLetters(final String strText) {
        return strText.replaceAll("[^a-zA-Z]", "").toLowerCase();
    }
}
