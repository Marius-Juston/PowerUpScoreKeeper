package org.waltonrobotics;

import java.awt.Toolkit;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

//FIXME This is horrible code please improve readability and all. This has no structure. Marius Juston the creator.
public class Controller implements Initializable {

	private static final long timeIncrement = 100;
	private static final SecureRandom secureRandom = new SecureRandom();
	private final SimpleDoubleProperty seconds = new SimpleDoubleProperty(0.0);
	private final FieldElement[] fieldElements = new FieldElement[3];
	private final SimpleIntegerProperty blueScore = new SimpleIntegerProperty(0);
	private final SimpleIntegerProperty redScore = new SimpleIntegerProperty(0);
	private final Map<ToggleButton, Button> buttonButtonHashMap = new HashMap<>();
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
	private final List<Runnable> queue = new ArrayList<>();

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
		boolean rightIsBlue = Controller.secureRandom.nextBoolean();

		this.fieldElements[0].randomizeSides(rightIsBlue);
		this.fieldElements[1].randomizeSides(Controller.secureRandom.nextBoolean());
		this.fieldElements[2].randomizeSides(rightIsBlue);
	}

	@Override
	public final void initialize(URL location, ResourceBundle resources) {
		this.buttonButtonHashMap.put(this.redBoost, this.redBoostCounter);
		this.buttonButtonHashMap.put(this.redForce, this.redForceCounter);
		this.buttonButtonHashMap.put(this.redLevitate, this.redLevitateCounter);
		this.buttonButtonHashMap.put(this.blueBoost, this.blueBoostCounter);
		this.buttonButtonHashMap.put(this.blueForce, this.blueForceCounter);
		this.buttonButtonHashMap.put(this.blueLevitate, this.blueLevitateCounter);

		FieldElement.setBlueScore(this.blueScore);
		FieldElement.setRedScore(this.redScore);

		this.fieldElements[0] = new FieldElement(this.blueSideSwitch1, this.blueNeutralSwitch,
			this.blueSideSwitch2,
			Color.BLUE, this.blueScore);
		this.fieldElements[1] = new FieldElement(this.leverButton1, this.neutralLever, this.leverButton2, Color.BLUE,
			this.blueScore, this.redScore);
		this.fieldElements[2] = new FieldElement(this.redSideSwitch1, this.redNeutralSwitch, this.redSideSwitch2,
			Color.RED, this.redScore);

		this.bluePoints.textProperty().bind(Bindings.concat("Blue: ", this.blueScore));
		this.redPoints.textProperty().bind(Bindings.concat("Red: ", this.redScore));

		this.setScore(2);

		AtomicBoolean alreadyRun = new AtomicBoolean(false);
		AtomicBoolean alreadyDisplayedWarning = new AtomicBoolean(false);
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(
			Duration.millis(Controller.timeIncrement),
			event -> {
				this.seconds.set(this.seconds.doubleValue() + (Controller.timeIncrement / 1000.0));
				if (!alreadyRun.get() && (Math.round(this.seconds.get()) == 15.0)) {
					Toolkit.getDefaultToolkit().beep();
					this.setScore(1);
					alreadyRun.set(true);
				} else if (!alreadyDisplayedWarning.get() && (Math.round(this.seconds.get()) == 150.0)) {
					Alert alert = new Alert(Alert.AlertType.WARNING, "Game ended");
					this.stop(timeline);
					this.gameToggleButton.setSelected(false);
					alert.show();
					alreadyDisplayedWarning.set(true);
				}
			}));
		timeline.setCycleCount(Animation.INDEFINITE);

		this.gameTime.textProperty().bind(Bindings.concat("Time: ", this.seconds.asString("%3.1f")));

		for (FieldElement fieldElement : this.fieldElements) {
			fieldElement.stop();
		}

		this.gameToggleButton.setOnAction(event ->
			{
				if (this.gameToggleButton.isSelected()) {
					this.gameToggleButton.setText("Stop");
					timeline.play();
					this.resetButton.setDisable(true);

					FieldElement.play();
				} else {
					this.stop(timeline);
				}
			}
		);

		WritableIntegerValue redLevitateCount = new SimpleIntegerProperty(0);
		WritableIntegerValue redBoostCount = new SimpleIntegerProperty(0);
		WritableIntegerValue redForceCount = new SimpleIntegerProperty(0);

		WritableIntegerValue blueLevitateCount = new SimpleIntegerProperty(0);
		WritableIntegerValue blueBoostCount = new SimpleIntegerProperty(0);
		WritableIntegerValue blueForceCount = new SimpleIntegerProperty(0);

		this.resetButton.setOnAction(event -> {
			this.randomizeElements();
			this.seconds.set(0);
			this.blueScore.set(0);
			this.redScore.set(0);
			this.queue.clear();

			this.blueLevitate.setDisable(false);
			this.blueLevitateCounter.setDisable(false);

			this.blueBoost.setDisable(false);
			this.blueBoostCounter.setDisable(false);

			this.blueForce.setDisable(false);
			this.blueForceCounter.setDisable(false);

			this.redLevitate.setDisable(false);
			this.redLevitateCounter.setDisable(false);

			this.redBoost.setDisable(false);
			this.redBoostCounter.setDisable(false);

			this.redForce.setDisable(false);
			this.redForceCounter.setDisable(false);

			redLevitateCount.set(0);
			redBoostCount.set(0);
			redForceCount.set(0);

			blueLevitateCount.set(0);
			blueBoostCount.set(0);
			blueForceCount.set(0);

			this.redSwitch.setDisable(false);
			this.scale.setDisable(false);
			this.blueSwitch.setDisable(false);

			for (FieldElement fieldElement : this.fieldElements) {
				fieldElement.reset();
			}
		});

		this.randomizeElements();

		this.powerUp(this.redScore, this.redBoost, this.redBoostCounter, redBoostCount, () -> {
			if (this.gameToggleButton.isSelected()) {
//				System.out.println("RED BOOST");
				Timeline timeline1 = new Timeline();

				switch (redBoostCount.get()) {
					case 1:
						this.setRedSideSwitch(2);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								this.setRedSideSwitch(1);
								this.nextPower();
							}));
						break;
					case 2:

						this.setScaleScore(2);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								this.setScaleScore(1);
								timeline1.stop();
								this.nextPower();
							}));
						break;
					case 3:

						this.setRedSideSwitch(2);
						this.setSwitchesScore(2);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								this.setScaleScore(1);
								this.setRedSideSwitch(1);
								timeline1.stop();

								this.nextPower();
							}));
						break;
				}

				if (redBoostCount.get() != 0) {
					timeline1.play();

				}
			}
		});
		this.powerUp(this.blueScore, this.blueBoost, this.blueBoostCounter, blueBoostCount, () -> {

			if (this.gameToggleButton.isSelected()) {
//				System.out.println("BLUE BOOST");
				Timeline timeline1 = new Timeline();

				switch (blueBoostCount.get()) {
					case 1:
						this.setBlueSideSwitch(2);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								this.setBlueSideSwitch(1);
								timeline1.stop();

								this.nextPower();
							}));
						break;
					case 2:

						this.setScaleScore(2);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								this.setScaleScore(1);
								timeline1.stop();

								this.nextPower();
							}));
						break;
					case 3:

						this.setBlueSideSwitch(2);
						this.setSwitchesScore(2);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								this.setBlueSideSwitch(1);
								this.setSwitchesScore(1);
								timeline1.stop();

								this.nextPower();
							}));
						break;
				}

				if (blueBoostCount.get() != 0) {
					timeline1.play();
				}
			}
		});

		this.powerUp(this.redScore, this.redForce, this.redForceCounter, redForceCount, () -> {

			if (this.gameToggleButton.isSelected()) {
//				System.out.println("RED FORCE");
				Timeline timeline1 = new Timeline();

				int[] current = new int[3];

				for (int i = 0; i < this.fieldElements.length; i++) {
					current[i] = this.fieldElements[i].getPossessedSide();
				}

				switch (redForceCount.get()) {
					case 1:
						Controller.setRedSideTOPossessed(this.fieldElements[2]);
						this.redSwitch.setDisable(true);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								this.fieldElements[2].setPossessedSide(current[2]);
								this.redSwitch.setDisable(false);
								timeline1.stop();

								this.nextPower();
							}));
						break;
					case 2:

						Controller.setRedSideTOPossessed(this.fieldElements[1]);
						this.scale.setDisable(true);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								this.fieldElements[1].setPossessedSide(current[1]);
								this.scale.setDisable(false);
								timeline1.stop();
								this.nextPower();
							}));
						break;
					case 3:

						Controller.setRedSideTOPossessed(this.fieldElements[2]);
						this.redSwitch.setDisable(true);

						Controller.setRedSideTOPossessed(this.fieldElements[1]);
						this.scale.setDisable(true);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								this.fieldElements[1].setPossessedSide(current[1]);
								this.scale.setDisable(false);

								this.fieldElements[2].setPossessedSide(current[2]);
								this.redSwitch.setDisable(false);

								timeline1.stop();
								this.nextPower();
							}));
						break;
				}

				if (redForceCount.get() != 0) {
					timeline1.play();
				}

			}
		});
		this.powerUp(this.blueScore, this.blueForce, this.blueForceCounter, blueForceCount, () -> {

			if (this.gameToggleButton.isSelected()) {
//				System.out.println("BLUE FORCE");
				Timeline timeline1 = new Timeline(); // TODO put Timeline outside and inside the game start/stop event handler

				int[] current = new int[3];

				for (int i = 0; i < this.fieldElements.length; i++) {
					current[i] = this.fieldElements[i].getPossessedSide();
				}

				switch (blueForceCount.get()) {
					case 1:
						Controller.setBlueSideTOPossessed(this.fieldElements[0]);
						this.blueSwitch.setDisable(true);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								this.fieldElements[0].setPossessedSide(current[0]);
								this.blueSwitch.setDisable(false);
								timeline1.stop();

								this.nextPower();
							}));
						break;
					case 2:

						Controller.setBlueSideTOPossessed(this.fieldElements[1]);
						this.scale.setDisable(true);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								this.fieldElements[1].setPossessedSide(current[1]);
								this.scale.setDisable(false);
								timeline1.stop();

								this.nextPower();
							}));
						break;
					case 3:

						Controller.setBlueSideTOPossessed(this.fieldElements[0]);
						this.blueSwitch.setDisable(true);

						Controller.setBlueSideTOPossessed(this.fieldElements[1]);
						this.scale.setDisable(true);

						timeline1.getKeyFrames()
							.add(new KeyFrame(Duration.seconds(10), event1 -> {
								this.fieldElements[1].setPossessedSide(current[1]);
								this.scale.setDisable(false);

								this.fieldElements[0].setPossessedSide(current[0]);
								this.blueSwitch.setDisable(false);
								timeline1.stop();
								this.nextPower();
							}));
						break;
				}

				if (blueForceCount.get() != 0) {
					timeline1.play();
				}

			}
		});

		this.powerUp(this.redScore, this.redLevitate, this.redLevitateCounter, redLevitateCount, () -> {

//			System.out.println("RED LEVITATE");

			if (this.gameToggleButton.isSelected()) {
				this.nextPower();
				if (redLevitateCount.get() == 3) {
					this.redLevitate.setDisable(true);
					this.redLevitateCounter.setDisable(true);
				}
			}
		});
		this.powerUp(this.blueScore, this.blueLevitate, this.blueLevitateCounter, blueLevitateCount, () -> {
//			System.out.println("BLUE LEVITATE");
			if (this.gameToggleButton.isSelected()) {
				this.nextPower();

				if (blueLevitateCount.get() == 3) {
					this.blueLevitate.setDisable(true);
					this.blueLevitateCounter.setDisable(true);
				}
			}
		});


	}

	private void powerUp(WritableIntegerValue score, ToggleButton toggleButton, Button counter,
		WritableIntegerValue count,
		Runnable onPowerUp) {
		toggleButton.textProperty().bind(Bindings.concat(toggleButton.getText(), "(", count, ")"));
		counter.setOnAction(event -> {
			if (this.gameToggleButton.isSelected() && (count.get() < 3)) {
				count.set(count.get() + 1);
				score.set(score.get() + 5);
			}
		});

		EventHandler<ActionEvent> actionEventEventHandler = event -> {
			ToggleButton power = ((ToggleButton) event.getSource());

			int c = Integer.parseInt(power.getText().replaceAll("\\D", ""));

			if (c >= 1) {
				if (!(power.equals(this.redLevitate) || power.equals(this.redLevitate))) {
					power.setDisable(true);
					this.buttonButtonHashMap.get(power).setDisable(true);
				}
				this.queue.add(onPowerUp);
				if (this.queue.size() == 1) {
					onPowerUp.run();
				}
			}
		};

		toggleButton.setOnAction(actionEventEventHandler);
	}

	private void setScore(int score) {
		for (int i = 0; i < this.fieldElements.length; i++) {
			this.setScore(i, score);
		}
	}

	private void setScaleScore(int score) {
		this.setScore(1, score);
	}

	private void setBlueSideSwitch(int score) {
		this.setScore(0, score);
	}


	private void setRedSideSwitch(int score) {
		this.setScore(2, score);
	}


	private void setSwitchesScore(int score) {
		this.setScore(0, score);
		this.setScore(2, score);
	}

	private void setScore(int place, int score) {
		this.fieldElements[place].setScore(score);
	}

	private void nextPower() {
		if (!this.queue.isEmpty()) {
			this.queue.remove(0);
		}

		if (!this.queue.isEmpty()) {
			this.queue.get(0).run();
		}
	}

	private void stop(Timeline timeline) {
		this.gameToggleButton.setText("Start");
		this.resetButton.setDisable(false);
		timeline.stop();

		for (FieldElement fieldElement : this.fieldElements) {
			fieldElement.stop();
		}
	}
}
