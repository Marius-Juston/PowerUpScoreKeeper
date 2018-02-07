package org.waltonrobotics;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.WritableIntegerValue;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.util.Duration;

class FieldElement {


	private static SimpleIntegerProperty blueScore;
	private static SimpleIntegerProperty redScore;
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
		Color color, SimpleIntegerProperty pointsProperty, WritableIntegerValue oppositeProperty) {
		this(rightSide, neutral, leftSide, color, pointsProperty);

		this.timeline2.getKeyFrames().add(new KeyFrame(
			Duration.seconds(1),
			event -> FieldElement.update(oppositeProperty, this.score)));
		this.timeline2.setCycleCount(Animation.INDEFINITE);
	}


	FieldElement(Button rightSide, Button neutral, Button leftSide,
		Color color, WritableIntegerValue pointsProperty) {
		this.color = color;
		this.buttons = new Button[]{rightSide, neutral, leftSide};

		this.possessedSide.addListener(
			(observable, oldValue, newValue) -> this.changeObserved(oldValue.intValue(),
				newValue.intValue()));
		this.possessedSide.set(1);

		for (int i = 0; i < this.buttons.length; i++) {
			int finalI = i;
			this.buttons[i].setOnAction(event -> {
					if (!FieldElement.stop) {
						this.possessedSide.set(finalI);
					}
				}
			);
		}

		this.timeline.getKeyFrames().add(new KeyFrame(
			Duration.seconds(1),
			event -> FieldElement.update(pointsProperty, this.score)));
		this.timeline.setCycleCount(Animation.INDEFINITE);
	}

	public static void setBlueScore(SimpleIntegerProperty blueScore) {
		FieldElement.blueScore = blueScore;
	}

	public static void setRedScore(SimpleIntegerProperty redScore) {
		FieldElement.redScore = redScore;
	}

	private static void update(WritableIntegerValue simpleIntegerProperty, int score) {
		if (!FieldElement.stop) {
			simpleIntegerProperty.set(simpleIntegerProperty.get() + score);
		}
	}

	public static final void play() {
		FieldElement.stop = false;

	}

	public final Button[] getButtons() {
		return this.buttons;
	}

	public final void setScore(int score) {
		this.score = score;
	}

	private void changeObserved(int oldSelected, int newSelected) {
		String setSelected = ";-fx-border-width: 2;-fx-border-color: violet";

		Button oldButton = this.buttons[oldSelected];
		oldButton.setStyle(oldButton.getStyle().replace(setSelected, ""));

		Button newButton = this.buttons[newSelected];
		newButton.setStyle(newButton.getStyle() + setSelected);

		if (!FieldElement.stop) {
			if (newSelected == 1) {
				this.timeline.stop();
				this.timeline2.stop();
			} else if (newButton == ((this.color == Color.RED) ? this.redSide : this.blueSide)) {
//				System.out.println("MY COLOR");
				if (this.color == Color.BLUE) {
					FieldElement.blueScore.set(FieldElement.blueScore.get() + this.score);
				} else {
					FieldElement.redScore.set(FieldElement.redScore.get() + this.score);
				}

				this.timeline.play();
				this.timeline2.stop();
			} else {
//				System.out.println("OPPONENT COLOR");
				if (this.color == Color.BLUE) {
					FieldElement.redScore.set(FieldElement.redScore.get() + this.score);
				} else {
					FieldElement.blueScore.set(FieldElement.blueScore.get() + this.score);
				}

				this.timeline.stop();
				this.timeline2.play();
			}
		}
	}

	public final void randomizeSides(boolean rightIsBlue) {

		if (rightIsBlue) {
			this.blueSide = this.buttons[0];
			this.blueSide.setStyle("-fx-background-color: blue");

			this.redSide = this.buttons[2];
			this.redSide.setStyle("-fx-background-color: red");

		} else {

			this.blueSide = this.buttons[2];
			this.blueSide.setStyle("-fx-background-color: blue");

			this.redSide = this.buttons[0];
			this.redSide.setStyle("-fx-background-color: red");
		}
	}

	public final Button getBlueSide() {
		return this.blueSide;
	}

	public final void stop() {
		FieldElement.stop = true;

		this.timeline2.stop();
		this.timeline.stop();
	}

	public final Button getRedSide() {
		return this.redSide;
	}

	public final int getPossessedSide() {
		return this.possessedSide.get();
	}


	public final void setPossessedSide(int possessedSide) {
		this.possessedSide.set(possessedSide);
	}


	public final void reset() {
		this.possessedSide.set(1);
		this.stop();
	}
}
