package pim.persistence;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 * Created by mstruntze on 10/05/17.
 */
public class BufferedImageWrapper extends BufferedImage {
	public BufferedImageWrapper() {
		super(10,10,1);
	}

	public BufferedImageWrapper(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied) {
		super(cm, raster, isRasterPremultiplied, null);
	}

}
