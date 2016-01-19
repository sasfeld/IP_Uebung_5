/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.util;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vector2D;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex;

/**
 * [SHORT_DESCRIPTION] 
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 30.11.2015
 */
public class VectorUtil {

	public static int calcCrossProduct(Vector2D a, Vector2D b) {
		return a.getX() * b.getY() - a.getY() * b.getX();	
	}

	public static Vertex calcMiddle(Vertex a, Vertex b) {
		int middleX = (a.getX() + b.getX()) / 2;
		int middleY = (a.getY() + b.getY()) / 2;
		
		return new Vertex(middleX, middleY);
	}

	public static Vertex normalize(Vertex s) {
		double length = Math.sqrt(Math.pow(s.getX(), 2) + Math.pow(s.getY(), 2));		
		
		return new Vertex((int) (s.getX() / length), (int) (s.getY() / length));
	}

	public static int calcScalarProduct(Vertex a, Vertex b) {
		return a.getX() * b.getX() + a.getY() * b.getY();
	}	
}
