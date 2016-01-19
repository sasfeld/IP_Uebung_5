/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm;

import java.util.ArrayList;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vector2D;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.BezierCurve;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Curve;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Polygon;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.util.VectorUtil;

/**
 * Algorithm to find bezier curves by a given {@link Vector2D} array.
 * 
 * If a special alpha is overhauled, a direct path will be added instead of a curve.
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
	public Curve[] calculateCurve(Polygon polygon) {
		this.curves = new ArrayList<>();
		
		this.createCurves(polygon);		
		
		return curves.toArray(new Curve[curves.size()]);
	}

	private void createCurves(Polygon polygon) {
		for (int i = 0; i < polygon.size(); i++) {
			Vertex bIMinus1 = null;
			
			// Step 1: find Mittelpunkte (center)
			Vertex aI = polygon.get(i).getA();
			Vertex bI = calcMiddle(polygon.get(i).getA(), polygon.get(i).getB());
			
			if (i == 0) {
				// last element is the last vertex within the polygon
				bIMinus1 = calcMiddle(polygon.get(polygon.size() - 1).getA(), polygon.get(polygon.size() - 1).getB());
			} else {
				bIMinus1 = calcMiddle(polygon.get(i - 1).getA(), polygon.get(i - 1).getB());
			}
			
			// Step 2: calculate Alpha
			int d = calculateDistanceFromVertexToPath(aI, bIMinus1, bI);
			double a = this.alpha * ((d - 0.5) / d);
			
			// Step 3: add curve
			if (a < this.minAlpha) {
				a = this.minAlpha;
			}
			
			System.out.println("a is: " + a);
			Curve c;			
			if (a > this.maxAlpha) {
				System.out.println("Füge gerade Verbindung hinzu");
				// gerade Verbindung b-1 -> ai -> bi
				c = new Curve();
				c.setFirst(bIMinus1);
				c.setSecond(aI);
				c.setLast(bI);
				c.setOuter(polygon.isOuter());
			} else {
				System.out.println("Füge Bezierkurve hinzu");
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
				c.setOuter(polygon.isOuter());
			}
			
			this.curves.add(c);
		}		
	}

	private int calculateDistanceFromVertexToPath(Vertex aI, Vertex bIMinus1, Vertex bI) {
		// aI, bIMinus1, bI
		Vertex s = new Vertex(bI.getY() - bIMinus1.getY(), - (bI.getX() - bIMinus1.getX()));
		s = VectorUtil.normalize(s);		
		Vertex vMinusA = new Vertex(aI.getX() - bIMinus1.getX(), aI.getY() - bIMinus1.getY());
		
		int d = VectorUtil.calcScalarProduct(s, vMinusA);
		return d;
	}

	private Vertex calcMiddle(Vertex a, Vertex b) {
		return VectorUtil.calcMiddle(a, b);
	}

}
