package org.waltonrobotics;

import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.WritableIntegerValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

//FIXME This is horrible code please improve readability and all. This has no structure. Marius Juston the creator.
public class Controller implements Initializable {

	private static final long timeIncrement = 100;
	private final SimpleDoubleProperty seconds = new SimpleDoubleProperty(0.0);
	private final FieldElement[] fieldElements = new FieldElement[3];
	private final SimpleIntegerProperty blueScore = new SimpleIntegerProperty(0);
	private final SimpleIntegerProperty redScore = new SimpleIntegerProperty(0);
	private Map<ToggleButton, Button> buttonButtonHashMap = new HashMap<>();
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
	private ArrayList<Runnable> queue = new ArrayList<>();

	public Controller() {
	}

	private static void setBlueSideTOPossessed(FieldElement fieldElement) {

		if (fieldElement.getButtons()[0] == fieldElement.getBlueSide()) {
			fieldElement.setPossessedSide(0);
		} else {
			fieldElement.setPossessedSide(2);
		}
	}

	private static void setRedSideTOPossessed(FieldElement fieldElement) {
		if (fieldElement.getButtons()[0] == fieldElement.getRedSide()) {
			fieldElement.setPossessedSide(0);
		} else {
			fieldElement.setPossessedSide(2);
		}
	}

	private void randomizeElements() {
		for (FieldElement fieldElement : fieldElements) {
			fieldElement.randomizeSides();
		}
	}

	@Override
	public final void initialize(URL location, ResourceBundle resources) {
		buttonButtonHashMap.put(redBoost, redBoostCounter);
		buttonButtonHashMap.put(redForce, redForceCounter);
		buttonButtonHashMap.put(redLevitate, redLevitateCounter);
		buttonButtonHashMap.put(blueBoost, blueBoostCounter);
		buttonButtonHashMap.put(blueForce, blueForceCounter);
		buttonButtonHashMap.put(blueLevitate, blueLevitateCounter);

		FieldElement.setBlueScore(blueScore);
		FieldElement.setRedScore(redScore);

		fieldElements[0] = new FieldElement(blueSideSwitch1, blueNeutralSwitch, blueSideSwitch2,
			Color.BLUE, blueScore);
		fieldElements[1] = new FieldElement(leverButton1, neutralLever, leverButton2, Color.BLUE,
			blueScore, redScore);
		fieldElements[2] = new FieldElement(redSideSwitch1, redNeutralSwitch, redSideSwitch2,
			Color.RED, redScore);

		bluePoints.textProperty().bind(Bindings.concat("Blue: ", blueScore));
		redPoints.textProperty().bind(Bindings.concat("Red: ", redScore));

		setScore(2);

		AtomicBoolean alreadyRun = new AtomicBoolean(false);
		AtomicBoolean alreadyDisplayedWarning = new AtomicBoolean(false);
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(
			Duration.millis(timeIncrement),
			event -> {
				seconds.set(seconds.doubleValue() + timeIncrement / 1000.0);
				if (!alreadyRun.get() && Math.round(seconds.get()) == 15.0) {
					System.out.println("Print");
					Toolkit.getDefaultToolkit().beep();
					setScore(1);
					alreadyRun.set(true);
				} else if (!alreadyDisplayedWarning.get() && Math.round(seconds.get()) == 150.0) {
					Alert alert = new Alert(AlertType.WARNING, "Game ended");
					stop(timeline);
					gameToggleButton.setSelected(false);
					alert.show();
					alreadyDisplayedWarning.set(true);
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

					FieldElement.play();
				} else {
					stop(timeline);
				}
			}
		);

		SimpleIntegerProperty redLevitateCount = new SimpleIntegerProperty(0);
		WritableIntegerValue redBoostCount = new SimpleIntegerProperty(0);
		SimpleIntegerProperty redForceCount = new SimpleIntegerProperty(0);

		SimpleIntegerProperty blueLevitateCount = new SimpleIntegerProperty(0);
		WritableIntegerValue blueBoostCount = new SimpleIntegerProperty(0);
		SimpleIntegerProperty blueForceCount = new SimpleIntegerProperty(0);

		resetButton.setOnAction(event -> {
			randomizeElements();
			seconds.set(0);
			blueScore.set(0);
			redScore.set(0);
			queue.clear();

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

		powerUp(redScore, redBoost, redBoostCounter, redBoostCount, () -> {
			if (gameToggleButton.isSelected()) {
				System.out.println("RED BOOST");
				Timeline timeline1 = new Timeline();

				switch (redBoostCount.get()) {
					case 1:
						setRedSideSwitch(2);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								setRedSideSwitch(1);
								nextPower();
							}));
						break;
					case 2:

						setScaleScore(2);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								setScaleScore(1);
								timeline1.stop();
								nextPower();
							}));
						break;
					case 3:

						setRedSideSwitch(2);
						setSwitchesScore(2);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								setScaleScore(1);
								setRedSideSwitch(1);
								timeline1.stop();

								nextPower();
							}));
						break;
				}

				if (redBoostCount.get() != 0) {
					timeline1.play();

				}
			}
		});
		powerUp(blueScore, blueBoost, blueBoostCounter, blueBoostCount, () -> {

			if (gameToggleButton.isSelected()) {
				System.out.println("BLUE BOOST");
				Timeline timeline1 = new Timeline();

				switch (blueBoostCount.get()) {
					case 1:
						setBlueSideSwitch(2);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								setBlueSideSwitch(1);
								timeline1.stop();

								nextPower();
							}));
						break;
					case 2:

						setScaleScore(2);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								setScaleScore(1);
								timeline1.stop();

								nextPower();
							}));
						break;
					case 3:

						setBlueSideSwitch(2);
						setSwitchesScore(2);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								setBlueSideSwitch(1);
								setSwitchesScore(1);
								timeline1.stop();

								nextPower();
							}));
						break;
				}

				if (blueBoostCount.get() != 0) {
					timeline1.play();
				}
			}
		});

		powerUp(redScore, redForce, redForceCounter, redForceCount, () -> {

			if (gameToggleButton.isSelected()) {
				System.out.println("RED FORCE");
				Timeline timeline1 = new Timeline();

				int[] current = new int[3];

				for (int i = 0; i < fieldElements.length; i++) {
					current[i] = fieldElements[i].getPossessedSide();
				}

				switch (redForceCount.get()) {
					case 1:
						setRedSideTOPossessed(fieldElements[2]);
						redSwitch.setDisable(true);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								fieldElements[2].setPossessedSide(current[2]);
								redSwitch.setDisable(false);
								timeline1.stop();

								nextPower();
							}));
						break;
					case 2:

						setRedSideTOPossessed(fieldElements[1]);
						scale.setDisable(true);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								fieldElements[1].setPossessedSide(current[1]);
								scale.setDisable(false);
								timeline1.stop();
								nextPower();
							}));
						break;
					case 3:

						setRedSideTOPossessed(fieldElements[2]);
						redSwitch.setDisable(true);

						setRedSideTOPossessed(fieldElements[1]);
						scale.setDisable(true);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								fieldElements[1].setPossessedSide(current[1]);
								scale.setDisable(false);

								fieldElements[2].setPossessedSide(current[2]);
								redSwitch.setDisable(false);

								timeline1.stop();
								nextPower();
							}));
						break;
				}

				if (redForceCount.get() != 0) {
					timeline1.play();
				}

			}
		});
		powerUp(blueScore, blueForce, blueForceCounter, blueForceCount, () -> {

			if (gameToggleButton.isSelected()) {
				System.out.println("BLUE FORCE");
				Timeline timeline1 = new Timeline(); // TODO put Timeline outside and inside the game start/stop event handler

				int[] current = new int[3];

				for (int i = 0; i < fieldElements.length; i++) {
					current[i] = fieldElements[i].getPossessedSide();
				}

				switch (blueForceCount.get()) {
					case 1:
						setBlueSideTOPossessed(fieldElements[0]);
						blueSwitch.setDisable(true);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								fieldElements[0].setPossessedSide(current[0]);
								blueSwitch.setDisable(false);
								timeline1.stop();

								nextPower();
							}));
						break;
					case 2:

						setBlueSideTOPossessed(fieldElements[1]);
						scale.setDisable(true);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								fieldElements[1].setPossessedSide(current[1]);
								scale.setDisable(false);
								timeline1.stop();

								nextPower();
							}));
						break;
					case 3:

						setBlueSideTOPossessed(fieldElements[0]);
						blueSwitch.setDisable(true);

						setBlueSideTOPossessed(fieldElements[1]);
						scale.setDisable(true);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								fieldElements[1].setPossessedSide(current[1]);
								scale.setDisable(false);

								fieldElements[0].setPossessedSide(current[0]);
								blueSwitch.setDisable(false);
								timeline1.stop();
								nextPower();
							}));
						break;
				}

				if (blueForceCount.get() != 0) {
					timeline1.play();
				}

			}
		});

		powerUp(redScore, redLevitate, redLevitateCounter, redLevitateCount, () -> {

			System.out.println("RED LEVITATE");

			if (gameToggleButton.isSelected()) {
				nextPower();
				if (redLevitateCount.get() == 3) {
					redLevitate.setDisable(true);
					redLevitateCounter.setDisable(true);
				}
			}
		});
		powerUp(blueScore, blueLevitate, blueLevitateCounter, blueLevitateCount, () -> {
			System.out.println("BLUE LEVITATE");
			if (gameToggleButton.isSelected()) {
				nextPower();

				if (blueLevitateCount.get() == 3) {
					blueLevitate.setDisable(true);
					blueLevitateCounter.setDisable(true);
				}
			}
		});


	}

	private void powerUp(SimpleIntegerProperty score, ToggleButton toggleButton, Button counter,
		WritableIntegerValue count,
		Runnable onPowerUp) {
		toggleButton.textProperty().bind(Bindings.concat(toggleButton.getText(), "(", count, ")"));
		counter.setOnAction(event -> {
			if (gameToggleButton.isSelected() && count.get() < 3) {
				count.set(count.get() + 1);
				score.set(score.get() + 5);
			}
		});

		EventHandler<ActionEvent> actionEventEventHandler = event -> {
			ToggleButton power = ((ToggleButton) event.getSource());

			int c = Integer.parseInt(power.getText().replaceAll("\\D", ""));

			if (c >= 1) {
				if (!(power.equals(redLevitate) || power.equals(redLevitate))) {
					power.setDisable(true);
					buttonButtonHashMap.get(power).setDisable(true);
				}
				queue.add(onPowerUp);
				if (queue.size() == 1) {
					onPowerUp.run();
				}
			}
		};

		toggleButton.setOnAction(actionEventEventHandler);
	}

	private void setScore(int score) {
		for (int i = 0; i < fieldElements.length; i++) {
			setScore(i, score);
		}
	}

	private void setScaleScore(int score) {
		setScore(1, score);
	}

	public void setBlueSideSwitch(int score) {
		setScore(0, score);
	}


	public void setRedSideSwitch(int score) {
		setScore(2, score);
	}


	private void setSwitchesScore(int score) {
		setScore(0, score);
		setScore(2, score);
	}

	private void setScore(int place, int score) {
		fieldElements[place].setScore(score);
	}

	private void nextPower() {
		if (!queue.isEmpty()) {
			queue.remove(0);
		}

		if (!queue.isEmpty()) {
			queue.get(0).run();
		}
	}

	private void stop(Timeline timeline) {
		gameToggleButton.setText("Start");
		resetButton.setDisable(false);
		timeline.stop();

		for (FieldElement fieldElement : fieldElements) {
			fieldElement.stop();
		}
	}
}
