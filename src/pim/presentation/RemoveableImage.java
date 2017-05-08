package pim.presentation;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * A control element consisting of an image with a close button in the top-right corner.
 *
 * @author Kasper
 */
class RemoveableImage extends StackPane {

	private final int WIDTH = 200;
	private final int HEIGHT = 200;
	private final Consumer<RemoveableImage> removeListener;

	/**
	 * Constructs a new control element consisting of an image with a close button in the top-right corner.
	 *
	 * @param bimg the buffered image to display
	 */
	RemoveableImage(BufferedImage bimg, Consumer<RemoveableImage> removeListener) {
		super();

		getStyleClass().add("RemoveableImage");
		getStylesheets().add("pim/presentation/removeableImage.css");

		//Set up canvas with image
		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		Image img = SwingFXUtils.toFXImage(bimg, null);

		//Keep the aspect ratio of the image and center on either horizontal or vertical axis
		double aspectRatio = img.getWidth() / img.getHeight();
		double w, h, xOffset, yOffset;

		if (img.getHeight() > img.getWidth()) { //Center on horizontal axis
			w = WIDTH * aspectRatio;
			h = HEIGHT;
			xOffset = (WIDTH - w) / 2.0;
			yOffset = 0;
		} else { //Center on vertical axis
			w = WIDTH;
			h = HEIGHT / aspectRatio;
			xOffset = 0;
			yOffset = (HEIGHT - h) / 2.0;
		}

		canvas.getGraphicsContext2D().drawImage(img, xOffset, yOffset, w, h);
		getChildren().add(canvas);

		//Set up close button
		BorderPane borderPane = new BorderPane();
		Button closeButton = new Button();
		BorderPane.setAlignment(closeButton, Pos.TOP_RIGHT);
		borderPane.setTop(closeButton);
		getChildren().add(borderPane);

		closeButton.setOnAction(this::closeButtonOnAction);
		this.removeListener = removeListener;
	}

	private void closeButtonOnAction(ActionEvent event) {
		removeListener.accept(this);
	}
}
