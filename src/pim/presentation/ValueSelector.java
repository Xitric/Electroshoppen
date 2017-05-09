package pim.presentation;

/**
 * Interface describing an element that can be used to select attribute values.
 *
 * @param <T> the type of the value
 * @author Kasper
 */
public interface ValueSelector<T> {

	/**
	 * Get the currently selected values. May be empty if no selection is made.
	 *
	 * @return the currently selected values
	 */
	T[] getValues();

	/**
	 * Get the primary selected value. The return value will be among those returned by {@link #getValues()}.
	 *
	 * @return the primary selected value
	 */
	T getValue();
}
