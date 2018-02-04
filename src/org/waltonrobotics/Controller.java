package org.waltonrobotics;

import java.awt.Toolkit;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Controller implements Initializable {

	private final SimpleDoubleProperty seconds = new SimpleDoubleProperty(0.0);
	private final long timeIncrement = 100;
	private final FieldElement[] fieldElements = new FieldElement[3];
	private final SimpleIntegerProperty blueScore = new SimpleIntegerProperty(0);
	private final SimpleIntegerProperty redScore = new SimpleIntegerProperty(0);
	@FXML
	private VBox blueSwitch;
	@FXML
	private VBox scale;
	@FXML
	private VBox redSwitch;
	@FXML
	private ToggleButton blueForce;
	@FXML
	private ToggleButton blueBoost;
	@FXML
	private ToggleButton blueLevitate;
	@FXML
	private Label bluePoints;
	@FXML
	private Label redPoints;
	@FXML
	private ToggleButton gameToggleButton;
	@FXML
	private ToggleButton redLevitate;
	@FXML
	private ToggleButton redBoost;
	@FXML
	private ToggleButton redForce;
	@FXML
	private Button redSideSwitch2;
	@FXML
	private Button redSideSwitch1;
	@FXML
	private Button leverButton1;
	@FXML
	private Button neutralLever;
	@FXML
	private Button leverButton2;
	@FXML
	private Button blueSideSwitch2;
	@FXML
	private Button blueNeutralSwitch;
	@FXML
	private Button blueSideSwitch1;
	@FXML
	private Button redNeutralSwitch;
	@FXML
	private Label gameTime;
	@FXML
	private Button resetButton;
	@FXML
	private Button redLevitateCounter;
	@FXML
	private Button redBoostCounter;
	@FXML
	private Button redForceCounter;
	@FXML
	private Button blueLevitateCounter;
	@FXML
	private Button blueBoostCounter;
	@FXML
	private Button blueForceCounter;

	private void randomizeElements() {
		for (FieldElement fieldElement : fieldElements) {
			fieldElement.randomizeSides();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		fieldElements[0] = new FieldElement(blueSideSwitch1, blueNeutralSwitch, blueSideSwitch2,
			Color.BLUE, blueScore);
		fieldElements[1] = new FieldElement(leverButton1, neutralLever, leverButton2, Color.BLUE,
			blueScore, redScore);
		fieldElements[2] = new FieldElement(redSideSwitch1, redNeutralSwitch, redSideSwitch2,
			Color.RED, redScore);

		bluePoints.textProperty().bind(Bindings.concat("Blue: ", blueScore));
		redPoints.textProperty().bind(Bindings.concat("Red: ", redScore));

		setScore(2);

		Timeline timeline = new Timeline(new KeyFrame(
			Duration.millis(timeIncrement),
			event -> {
				seconds.set(seconds.doubleValue() + timeIncrement / 1000.0);
				if (Math.round(seconds.get()) == 15.0) {
					Toolkit.getDefaultToolkit().beep();
					setScore(1);
				}

			}));
		timeline.setCycleCount(Animation.INDEFINITE);

		gameTime.textProperty().bind(Bindings.concat("Time: ", seconds.asString("%3.1f")));

		for (FieldElement fieldElement : fieldElements) {
			fieldElement.stop();
		}

		gameToggleButton.setOnAction(event ->
			{
				if (gameToggleButton.isSelected()) {
					gameToggleButton.setText("Stop");
					timeline.play();
					resetButton.setDisable(true);

					for (FieldElement fieldElement : fieldElements) {
						fieldElement.play();
					}
				} else {
					gameToggleButton.setText("Start");
					resetButton.setDisable(false);
					timeline.stop();

					for (FieldElement fieldElement : fieldElements) {
						fieldElement.stop();
					}
				}
			}
		);

		SimpleIntegerProperty redLevitateCount = new SimpleIntegerProperty(0);
		SimpleIntegerProperty redBoostCount = new SimpleIntegerProperty(0);
		SimpleIntegerProperty redForceCount = new SimpleIntegerProperty(0);

		SimpleIntegerProperty blueLevitateCount = new SimpleIntegerProperty(0);
		SimpleIntegerProperty blueBoostCount = new SimpleIntegerProperty(0);
		SimpleIntegerProperty blueForceCount = new SimpleIntegerProperty(0);

		resetButton.setOnAction(event -> {
			randomizeElements();
			seconds.set(0);
			blueScore.set(0);
			redScore.set(0);

			blueLevitate.setDisable(false);
			blueLevitateCounter.setDisable(false);

			blueBoost.setDisable(false);
			blueBoostCounter.setDisable(false);

			blueForce.setDisable(false);
			blueForceCounter.setDisable(false);

			redLevitate.setDisable(false);
			redLevitateCounter.setDisable(false);

			redBoost.setDisable(false);
			redBoostCounter.setDisable(false);

			redForce.setDisable(false);
			redForceCounter.setDisable(false);

			redLevitateCount.set(0);
			redBoostCount.set(0);
			redForceCount.set(0);

			blueLevitateCount.set(0);
			blueBoostCount.set(0);
			blueForceCount.set(0);

			redSwitch.setDisable(false);
			scale.setDisable(false);
			blueSwitch.setDisable(false);

			for (FieldElement fieldElement : fieldElements) {
				fieldElement.reset();
			}
		});

		randomizeElements();

		powerUp(redForce, redForceCounter, redForceCount, event -> {
			if (gameToggleButton.isSelected()) {
				Timeline timeline1 = new Timeline();

				int[] current = new int[3];

				for (int i = 0; i < fieldElements.length; i++) {
					current[i] = fieldElements[i].getPossessedSide();
				}

				switch (redForceCount.get()) {
					case 1:
						setRedSideTOPossessed(fieldElements[2]);
						redSwitch.setDisable(true);

						timeline1.getKeyFrames().add(new KeyFrame(Duration.seconds(10), event1 -> {
							fieldElements[2].setPossessedSide(current[2]);
							redSwitch.setDisable(false);
							timeline1.stop();
						}));
						break;
					case 2:

						setRedSideTOPossessed(fieldElements[1]);
						scale.setDisable(true);

						timeline1.getKeyFrames().add(new KeyFrame(Duration.seconds(10), event1 -> {
							fieldElements[1].setPossessedSide(current[1]);
							scale.setDisable(false);
							timeline1.stop();
						}));
						break;
					case 3:

						setRedSideTOPossessed(fieldElements[2]);
						redSwitch.setDisable(true);

						setRedSideTOPossessed(fieldElements[1]);
						scale.setDisable(true);

						timeline1.getKeyFrames().add(new KeyFrame(Duration.seconds(10), event1 -> {
							fieldElements[1].setPossessedSide(current[1]);
							scale.setDisable(false);

							fieldElements[2].setPossessedSide(current[2]);
							redSwitch.setDisable(false);

							timeline1.stop();
						}));
						break;
				}

				if (redForceCount.get() != 0) {
					timeline1.play();
					redForce.setDisable(true);
					redForceCounter.setDisable(true);
				}

			}
		});
		powerUp(blueForce, blueForceCounter, blueForceCount, event -> {
			if (gameToggleButton.isSelected()) {
				Timeline timeline1 = new Timeline(); // TODO put Timeline outside and inside the game start/stop event handler

				int[] current = new int[3];

				for (int i = 0; i < fieldElements.length; i++) {
					current[i] = fieldElements[i].getPossessedSide();
				}

				switch (blueForceCount.get()) {
					case 1:
						setBlueSideTOPossessed(fieldElements[0]);
						blueSwitch.setDisable(true);

						timeline1.getKeyFrames().add(new KeyFrame(Duration.seconds(10), event1 -> {
							fieldElements[0].setPossessedSide(current[0]);
							blueSwitch.setDisable(false);
							timeline1.stop();
						}));
						break;
					case 2:

						setBlueSideTOPossessed(fieldElements[1]);
						scale.setDisable(true);

						timeline1.getKeyFrames().add(new KeyFrame(Duration.seconds(10), event1 -> {
							fieldElements[1].setPossessedSide(current[1]);
							scale.setDisable(false);
							timeline1.stop();
						}));
						break;
					case 3:

						setBlueSideTOPossessed(fieldElements[0]);
						blueSwitch.setDisable(true);

						setBlueSideTOPossessed(fieldElements[1]);
						scale.setDisable(true);

						timeline1.getKeyFrames().add(new KeyFrame(Duration.seconds(10), event1 -> {
							fieldElements[1].setPossessedSide(current[1]);
							scale.setDisable(false);

							fieldElements[0].setPossessedSide(current[0]);
							blueSwitch.setDisable(false);
							timeline1.stop();
						}));
						break;
				}

				if (blueForceCount.get() != 0) {
					timeline1.play();
					blueForce.setDisable(true);
					blueForceCounter.setDisable(true);
				}

			}
		});

		powerUp(redLevitate, redLevitateCounter, redLevitateCount, event ->
		{
			if (gameToggleButton.isSelected() && redLevitateCount.get() == 3) {
				redLevitate.setDisable(true);
				redLevitateCounter.setDisable(true);
			}
		});
		powerUp(blueLevitate, blueLevitateCounter, blueLevitateCount, event ->
		{
			if (gameToggleButton.isSelected() && blueLevitateCount.get() == 3) {
				blueLevitate.setDisable(true);
				blueLevitateCounter.setDisable(true);
			}
		});


	}


	private void setBlueSideTOPossessed(FieldElement fieldElement) {

		if (fieldElement.getButtons()[0] == fieldElement.getBlueSide()) {
			fieldElement.setPossessedSide(0);
		} else {
			fieldElement.setPossessedSide(2);
		}
	}


	private void setRedSideTOPossessed(FieldElement fieldElement) {
		if (fieldElement.getButtons()[0] == fieldElement.getRedSide()) {
			fieldElement.setPossessedSide(0);
		} else {
			fieldElement.setPossessedSide(2);
		}
	}

	private void powerUp(ToggleButton toggleButton, Button counter, SimpleIntegerProperty count,
		EventHandler<ActionEvent> onPowerUp) {
		toggleButton.textProperty().bind(Bindings.concat(toggleButton.getText(), "(", count, ")"));
		counter.setOnAction(event -> {
			if (gameToggleButton.isSelected()) {
				count.set(Math.min(count.get() + 1, 3));
			}
		});

		toggleButton.setOnAction(onPowerUp);

	}

	private void setScore(int score) {
		for (int i = 0; i < fieldElements.length; i++) {
			setScore(i, score);
		}
	}

	private void setScore(int place, int score) {
		fieldElements[place].setScore(score);
	}
}
