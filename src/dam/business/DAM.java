package dam.business;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import shared.Image;

import java.io.File;

/**
 * Dummy class representing a digital asset management system. This class is used merely for loading images to be used
 * with the other systems.
 *
 * @author Kasper
 */
public class DAM {

	private final Window gui;
	private File lastFile;

	//Not the best implementation, but this is just a dummy class
	public DAM(Window gui) {
		this.gui = gui;
	}

	/**
	 * Get an image from the DAM. This will open an image choosing dialog. The returned image will not have and id, as
	 * it is considered new to either the PIM or the CMS, depending on where it is received.
	 *
	 * @return the chosen image, or null if the user did not choose and image
	 */
	public Image getImage() {
		//Open a file chooser
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.bmp", "*.gif", "*.png", "*.jpeg", "*.wbmp"));

		//Open the last location, if it is specified
		if (lastFile != null) {
			if (lastFile.isDirectory()) {
				fileChooser.setInitialDirectory(lastFile);
			} else if (lastFile.getParentFile().isDirectory()) {
				fileChooser.setInitialDirectory(lastFile.getParentFile());
			}
		}

		File selectedFile = fileChooser.showOpenDialog(gui);

		if (selectedFile != null) {
			//Remember selection and return image
			lastFile = selectedFile;
			return new Image(selectedFile.getPath());
		}

		return null;
	}
}
