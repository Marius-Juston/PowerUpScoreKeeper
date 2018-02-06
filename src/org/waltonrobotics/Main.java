package org.waltonrobotics;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public Main() {
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public final void start(Stage primaryStage) throws java.io.IOException {
		Parent root = FXMLLoader.load(getClass().getResource("field.fxml"));
		primaryStage.setTitle("Point simulation");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
}
