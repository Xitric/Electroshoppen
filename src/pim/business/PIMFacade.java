package pim.business;

/**
 * Facade class for the business layer of the PIM.
 *
 * @author Kasper
 */
public class PIMFacade {

	private static PIM pim;

	/**
	 * Get the singleton instance of the PIM.
	 *
	 * @return the singleton instance of the PIM
	 */
	public static PIM getPIM() {
		if (pim == null) {
			//PIMImpl is currently the only implementation of the PIM
			pim = new PIMImpl();
		}

		return pim;
	}
}
