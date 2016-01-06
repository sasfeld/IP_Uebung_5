/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.svg;

import java.util.Collection;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.BezierCurve;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Curve;

/**
 * Model to prepare SVG file generation.
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 06.01.2016
 */
public class SvgBuilder {
	private static String STYLE_HEADER = "<style type=\"text/css\">" + "<![CDATA[" + "text {font-size:24px; }" + "]]>"
			+ "</style>";
	private static final String HEADER = "<svg xmlns=\"http://www.w3.org/2000/svg\""
			+ " xmlns:xlink=\"http://www.w3.org/1999/xlink\""
			+ " xmlns:ev=\"http://www.w3.org/2001/xml-events\""
			+ " version=\"1.1\""
			+ " baseProfile=\"full\""
			+ " width=\"%d\""
			+ " height=\"%d\">";
	private static final String TITLE_HEADER = "<title>%s</title>";
	private static final String DIRECT_CURVE = "<path stroke=\"%s\" fill=\"%s\" stroke-width=\"%d\" d=\"%s\" />";
	private static final String BEZIER_CURVE = "<path stroke=\"%s\" fill=\"%s\" stroke-width=\"%d\" d=\"%s\" />";

	private static final String CLOSING_TAG = "</svg>";

	private int height = 300;
	private int width = 500;
	private int curveStrokeWidth = 1;
	private String curveStrokeColor = "black";
	private String curveFillColor = "green";

	private String title = "";

	private Collection<Curve[]> curves;

	private boolean renderCurveFillings;

	public void setRenderCurveFillings(boolean renderCurveFillings) {
		this.renderCurveFillings = renderCurveFillings;
	}

	public boolean isRenderCurveFillings() {
		return renderCurveFillings;
	}

	public void setCurves(Collection<Curve[]> curves) {
		this.curves = curves;
	}

	public Collection<Curve[]> getCurves() {
		return curves;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	/**
	 * Generate the SVG contents.
	 * 
	 * @return String the complete SVG contents.
	 */
	public String build() {
		StringBuilder strBuilder = new StringBuilder();

		appendHeader(strBuilder);
		appendTitle(strBuilder);
		appendStyle(strBuilder);

		appendCurves(strBuilder);

		appendClosingTag(strBuilder);

		return strBuilder.toString();
	}

	private void appendCurves(StringBuilder strBuilder) {
		for (Curve[] curve : curves) {
			this.appendConture(strBuilder, curve);
		}
	}

	private void appendConture(StringBuilder strBuilder, Curve[] curves) {
		for (Curve curve : curves) {
			if (curve instanceof BezierCurve) {
				this.appendBezier(strBuilder, (BezierCurve) curve);
			} else {
				this.appendDirectPath(strBuilder, curve);
			}
		}
	}

	private void appendDirectPath(StringBuilder strBuilder, Curve curve) {
		addLine(strBuilder, String.format(DIRECT_CURVE, this.curveStrokeColor, this.getFill(), this.curveStrokeWidth,
				this.buildDirectPath(curve)));
	}

	private String getFill() {
		return this.isRenderCurveFillings() ? this.curveFillColor : "none";
	}

	private String buildDirectPath(Curve curve) {
		return "M " + getCoordinateString(curve.getFirst()) + " L " + getCoordinateString(curve.getSecond()) + " L "
				+ getCoordinateString(curve.getLast());
	}

	private String getCoordinateString(Vertex vertex) {
		return "" + vertex.getX() + " " + vertex.getY();
	}

	private void appendBezier(StringBuilder strBuilder, BezierCurve curve) {
		addLine(strBuilder, String.format(BEZIER_CURVE, this.curveStrokeColor, this.getFill(), this.curveStrokeWidth,
				this.buildBezierPath(curve)));
	}

	private String buildBezierPath(BezierCurve curve) {
		return "M " + getCoordinateString(curve.getFirst()) + " C " + getCoordinateString(curve.getSecond()) + " "
				+ getCoordinateString(curve.getThird()) + " " + getCoordinateString(curve.getLast());
	}

	private void appendTitle(StringBuilder strBuilder) {
		addLine(strBuilder, String.format(TITLE_HEADER, this.title));
	}

	private void appendStyle(StringBuilder strBuilder) {
		addLine(strBuilder, STYLE_HEADER);
	}

	private void appendHeader(StringBuilder strBuilder) {
		addLine(strBuilder, String.format(HEADER, width, height));
	}

	private void addLine(StringBuilder strBuilder, String line) {
		strBuilder.append(line + "\n\n");
	}

	private void appendClosingTag(StringBuilder strBuilder) {
		addLine(strBuilder, CLOSING_TAG);
	}
}
