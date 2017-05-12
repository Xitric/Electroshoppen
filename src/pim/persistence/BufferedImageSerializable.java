package pim.persistence;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.Serializable;

/**
 * Created by mstruntze on 10/05/17.
 */
public class BufferedImageSerializable extends BufferedImageWrapper implements Serializable {

	public BufferedImageSerializable(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied) {
		super(cm, raster, isRasterPremultiplied);
	}

	public static BufferedImageSerializable bis(BufferedImage img) {
		ColorModel cm = img.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = img.copyData(null);
		return new BufferedImageSerializable(cm, raster, isAlphaPremultiplied);
	}
}
