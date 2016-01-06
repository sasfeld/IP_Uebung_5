/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus F�llmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm;

import java.util.ArrayList;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vector2D;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.BezierCurve;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Curve;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.util.VectorUtil;

/**
 * [SHORT_DESCRIPTION] 
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 06.01.2016
 */
public class BezierCurveFinder implements ICurveFinder {
	public static final Double DEFAULT_ALPHA = 1.333333;
	public static final Double DEFAUTL_MIN_ALPHA = 0.55;
	public static final Double DEFAULT_MAX_ALPHA = 1.0;
	
	protected double alpha = DEFAULT_ALPHA;
	protected double minAlpha = DEFAUTL_MIN_ALPHA;
	protected double maxAlpha = DEFAULT_MAX_ALPHA;
	private ArrayList<Object> curves;
	 
	/**
	 * Set the alpha
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	/**
	 * Set the minimum possible alpha.
	 * @param minAlpha
	 */
	public void setMinAlpha(double minAlpha) {
		this.minAlpha = minAlpha;
	}
	
	/**
	 * Set the maximum possible alpha.
	 * @param maxAlpha
	 */
	public void setMaxAlpha(double maxAlpha) {
		this.maxAlpha = maxAlpha;
	}

	/* (non-Javadoc)
	 * @see de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.ICurveFinder#calculateCurves(de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vector2D[])
	 */
	@Override
	public Curve[] calculateCurve(Vector2D[] polygon) {
		this.curves = new ArrayList<>();
		
		this.createCurves(polygon);		
		
		return curves.toArray(new Curve[curves.size()]);
	}

	private void createCurves(Vector2D[] polygon) {
		for (int i = 1; i < polygon.length; i++) {
			// Step 1: find Mittelpunkte (center)
			Vertex bIMinus1 = calcMiddle(polygon[i - 1].getA(), polygon[i - 1].getB());
			Vertex aI = polygon[i].getA();
			Vertex bI = calcMiddle(polygon[i].getA(), polygon[i].getB());
			
			// Step 2: calculate Alpha
			int d = calculateDistanceFromVertexToPath(aI, bIMinus1, bI);
			double a = this.alpha * ((d - 0.5) / d);
			
			// Step 3: add curve
			if (a < this.minAlpha) {
				a = this.minAlpha;
			}
			
			Curve c;			
			if (a > this.maxAlpha) {
				// gerade Verbindung b-1 -> ai -> bi
				c = new Curve();
				c.setFirst(bIMinus1);
				c.setSecond(aI);
				c.setLast(bI);
			} else {
				// Bezierkurve
				c = new BezierCurve();
				BezierCurve cBezier = (BezierCurve) c;
				c.setFirst(bIMinus1);
				Vertex z1 = new Vertex(
						(int) (bIMinus1.getX() + (a * (aI.getX() - bIMinus1.getX()))), 
						(int) (bIMinus1.getY() + (a * (aI.getY() - bIMinus1.getY()))));
				cBezier.setSecond(z1 );
				Vertex z2 = new Vertex(
						(int) (aI.getX() + (a * (bI.getX() - aI.getX()))), 
						(int) (aI.getY() + (a * (bI.getY() - aI.getY()))));
				cBezier.setThird(z2);
				c.setLast(bI);
			}
			
			this.curves.add(c);
		}		
	}

	private int calculateDistanceFromVertexToPath(Vertex v, Vertex a, Vertex b) {
		Vertex s = new Vertex(b.getY() - a.getY(), - (b.getX() - a.getX()));
		s = VectorUtil.normalize(s);		
		Vertex vMinusA = new Vertex(v.getX() - a.getX(), v.getY() - a.getY());
		
		int d = VectorUtil.calcScalarProduct(s, vMinusA);
		return d;
	}

	private Vertex calcMiddle(Vertex a, Vertex b) {
		return VectorUtil.calcMiddle(a, b);
	}

}
