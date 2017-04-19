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
		Canvas canvas = new Canvas(200, 200);
		Image img = SwingFXUtils.toFXImage(bimg, null);
		canvas.getGraphicsContext2D().drawImage(img, 0, 0, 200, 200);
		getChildren().add(canvas);

		//TODO: Temp
		setStyle(String.format("-fx-border-color: rgb(%d, %d, %d);", (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));

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
