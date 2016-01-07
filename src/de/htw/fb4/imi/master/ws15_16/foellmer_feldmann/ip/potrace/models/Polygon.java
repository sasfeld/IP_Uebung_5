/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models;

import java.util.ArrayList;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vector2D;

/**
 * Decorator class that extends the {@link ArrayList} functionality.
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 07.01.2016
 */
public class Polygon extends ArrayList<Vector2D> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7591470712506836435L;
	
	private boolean isOuter;
	
	public void setOuter(boolean isOuter) {
		this.isOuter = isOuter;
	}
	
	/**
	 * True if this polygon represents an outer outline. False if it represents an inner outline.
	 * @return
	 */
	public boolean isOuter() {
		return isOuter;
	}
}
