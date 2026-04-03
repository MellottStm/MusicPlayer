package com.smt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // 加载 SearchView.fxml
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/View/SearchView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 850, 550);   // 窗口大小可调整
        try {
            Image icon = new Image(Main.class.getResourceAsStream("/Img/icon.jpg"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("loading fail");
        }
        // 加载全局样式（推荐方式）
        scene.getStylesheets().add(Main.class.getResource("/styles.css").toExternalForm());
        stage.setResizable(false);
        stage.setTitle("MusicPlayer");
        stage.setScene(scene);
        stage.show();
    }
}