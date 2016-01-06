/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vector2D;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Curve;

/**
 * Interface for curve finding algorithms, e.g. bezier.
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 06.01.2016
 */
public interface ICurveFinder {

	/**
	 * Calculate curves in the given polygon array.
	 * 
	 * The polygon array is the Result of Potrace, step 2 and calculated by an {@link IPolygonFinder} algorithm.
	 * 
	 * This method returns an array of {@link Curve} which has the same order of vertices as in the given polygon array.
	 * 
	 * @param polygon
	 * @return
	 */
	Curve[] calculateCurve(Vector2D[] polygon);
}
