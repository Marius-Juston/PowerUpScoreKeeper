package sample;

import java.security.SecureRandom;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class FieldElement {

	private static final SecureRandom secureRandom = new SecureRandom();
	private static int score = 1;
	private static boolean stop;
	private final Button[] buttons;
	private final Color color;
	private final Timeline timeline = new Timeline();
	private final Timeline timeline2 = new Timeline();
	private SimpleIntegerProperty possessedSide = new SimpleIntegerProperty();
	private Button redSide;
	private Button blueSide;

	public FieldElement(Button rightSide, Button neutral, Button leftSide,
		Color color, SimpleIntegerProperty pointsProperty, SimpleIntegerProperty oppositeProperty) {
		this(rightSide, neutral, leftSide, color, pointsProperty);

		timeline2.getKeyFrames().add(new KeyFrame(
			Duration.seconds(1),
			event -> update(oppositeProperty, score)));
		timeline2.setCycleCount(Animation.INDEFINITE);
	}

	public FieldElement(Button rightSide, Button neutral, Button leftSide,
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

	public static int getScore() {
		return score;
	}

	public static void setScore(int score) {
		FieldElement.score = score;
	}

	public void update(SimpleIntegerProperty simpleIntegerProperty, int score) {
		if (!stop) {
			simpleIntegerProperty.set(simpleIntegerProperty.get() + score);
		}
	}

	private void changeObserved(int oldSelected, int newSelected) {
		String setSelected = ";-fx-border-width: 2;-fx-border-color: violet";

		Button oldButton = buttons[oldSelected];
		oldButton.setStyle(oldButton.getStyle().replace(setSelected, ""));

		Button newButton = buttons[newSelected];
		newButton.setStyle(newButton.getStyle() + setSelected);

		if (!stop) {
			if (buttons[newSelected] == (color == Color.RED ? getRedSide() : getBlueSide())) {
				timeline.play();
				timeline2.stop();
			} else {
				timeline.stop();
				timeline2.play();
			}
		}
	}

	public void randomizeSides() {
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

	public Button getBlueSide() {
		return blueSide;
	}

	public Button getNeutral() {
		return buttons[1];
	}

	public void stop() {
		stop = true;

		timeline2.stop();
		timeline.stop();
	}

	private Button getRedSide() {
		return redSide;
	}

	public void play() {
		stop = false;

	}

	public void reset() {
		possessedSide.set(1);
		stop();
	}
}
