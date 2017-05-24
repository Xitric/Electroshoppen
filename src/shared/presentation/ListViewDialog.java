package shared.presentation;

import javafx.collections.FXCollections;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;

import java.util.Collection;

/**
 * A dialog pane for handling selection among the elements of a list.
 *
 * @param <T> the type of element to handle
 * @author Kasper
 */
public class ListViewDialog<T> extends Dialog<T> {

	/**
	 * Creates a new dialog for choosing elements in list.
	 *
	 * @param elements the elements to choose between
	 */
	public ListViewDialog(Collection<T> elements) {
		ListView<T> pageView = new ListView<>();
		pageView.setItems(FXCollections.observableArrayList(elements).sorted());
		getDialogPane().setContent(pageView);

		ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

		//Specify how a result is gathered from the dialog
		setResultConverter(button -> {
			if (button == confirmButtonType) {
				return pageView.getSelectionModel().getSelectedItem();
			}
			return null;
		});

		//Style the dialog
		getDialogPane().getStylesheets().add(getClass().getResource("electroshop.css").toExternalForm());
	}
}
