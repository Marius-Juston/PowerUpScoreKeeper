package org.waltonrobotics;

import java.security.SecureRandom;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.WritableIntegerValue;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.util.Duration;

class FieldElement {

	private static final SecureRandom secureRandom = new SecureRandom();
	public static SimpleIntegerProperty blueScore, redScore;
	private static boolean stop;
	private final Button[] buttons;
	private final Color color;
	private final Timeline timeline = new Timeline();
	private final Timeline timeline2 = new Timeline();
	private final SimpleIntegerProperty possessedSide = new SimpleIntegerProperty();
	private int score = 1;
	private Button redSide;
	private Button blueSide;

	FieldElement(Button rightSide, Button neutral, Button leftSide,
		Color color, SimpleIntegerProperty pointsProperty, SimpleIntegerProperty oppositeProperty) {
		this(rightSide, neutral, leftSide, color, pointsProperty);

		timeline2.getKeyFrames().add(new KeyFrame(
			Duration.seconds(1),
			event -> update(oppositeProperty, score)));
		timeline2.setCycleCount(Animation.INDEFINITE);
	}


	FieldElement(Button rightSide, Button neutral, Button leftSide,
		Color color, SimpleIntegerProperty pointsProperty) {
		this.color = color;
		buttons = new Button[]{rightSide, neutral, leftSide};

		possessedSide.addListener(
			(observable, oldValue, newValue) -> changeObserved(oldValue.intValue(),
				newValue.intValue()));
		possessedSide.set(1);

		for (int i = 0; i < buttons.length; i++) {
			int finalI = i;
			buttons[i].setOnAction(event -> {
					if (!stop) {
						possessedSide.set(finalI);
					}
				}
			);
		}

		timeline.getKeyFrames().add(new KeyFrame(
			Duration.seconds(1),
			event -> update(pointsProperty, score)));
		timeline.setCycleCount(Animation.INDEFINITE);
	}

	public static void setBlueScore(SimpleIntegerProperty blueScore) {
		FieldElement.blueScore = blueScore;
	}

	public static void setRedScore(SimpleIntegerProperty redScore) {
		FieldElement.redScore = redScore;
	}

	private static void update(WritableIntegerValue simpleIntegerProperty, int score) {
		if (!stop) {
			simpleIntegerProperty.set(simpleIntegerProperty.get() + score);
		}
	}

	public static final void play() {
		stop = false;

	}

	public final Button[] getButtons() {
		return buttons;
	}

	public final int getScore() {
		return score;
	}

	public final void setScore(int score) {
		this.score = score;
	}

	private void changeObserved(int oldSelected, int newSelected) {
		String setSelected = ";-fx-border-width: 2;-fx-border-color: violet";

		Button oldButton = buttons[oldSelected];
		oldButton.setStyle(oldButton.getStyle().replace(setSelected, ""));

		Button newButton = buttons[newSelected];
		newButton.setStyle(newButton.getStyle() + setSelected);

		if (!stop) {
			if (newSelected == 1) {
				timeline.stop();
				timeline2.stop();
			} else if (newButton == (color == Color.RED ? redSide : blueSide)) {
				System.out.println("MY COLOR");
				if (color == Color.BLUE) {
					blueScore.set(blueScore.get() + score);
				} else {
					redScore.set(redScore.get() + score);
				}

				timeline.play();
				timeline2.stop();
			} else {
				System.out.println("OPPONENT COLOR");
				if (color == Color.BLUE) {
					redScore.set(redScore.get() + score);
				} else {
					blueScore.set(blueScore.get() + score);
				}

				timeline.stop();
				timeline2.play();
			}
		}
	}

	public final void randomizeSides() {
		boolean rightIsBlue = secureRandom.nextBoolean();

		if (rightIsBlue) {
			blueSide = buttons[0];
			blueSide.setStyle("-fx-background-color: blue");

			redSide = buttons[2];
			redSide.setStyle("-fx-background-color: red");

		} else {

			blueSide = buttons[2];
			blueSide.setStyle("-fx-background-color: blue");

			redSide = buttons[0];
			redSide.setStyle("-fx-background-color: red");
		}
	}

	public final Button getBlueSide() {
		return blueSide;
	}

	public final Button getNeutral() {
		return buttons[1];
	}

	public final void stop() {
		stop = true;

		timeline2.stop();
		timeline.stop();
	}

	public final Button getRedSide() {
		return redSide;
	}

	public final int getPossessedSide() {
		return possessedSide.get();
	}


	public final void setPossessedSide(int possessedSide) {
		this.possessedSide.set(possessedSide);
	}


	public final void reset() {
		possessedSide.set(1);
		stop();
	}
}
