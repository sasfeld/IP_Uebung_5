/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus F�llmer, Sascha Feldmann
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
	private static final String STYLE_HEADER = "<style type=\"text/css\">" + "<![CDATA[" + "text {font-size:24px; }" + "]]>"
			+ "</style>";
	private static final String HEADER = ""
			+ "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n\n"
			+ "<svg xmlns=\"http://www.w3.org/2000/svg\""
			+ " xmlns:xlink=\"http://www.w3.org/1999/xlink\""
			+ " xmlns:ev=\"http://www.w3.org/2001/xml-events\""
			+ " version=\"1.1\""
			+ " baseProfile=\"full\""
			+ " width=\"%s\""
			+ " height=\"%s\" "
			+ " viewBox=\"0 0 %s %s\""
			+ ">";
	private static final String TITLE_HEADER = "<title>%s</title>";
	private static final String PATH = "<path stroke=\"%s\" fill=\"%s\" stroke-width=\"%s\" d=\"%s Z\" />";

	private static final String CLOSING_TAG = "</svg>";

	private String height = "100%";
	private String width = "100%";
	private String viewBoxHeight = "100%";
	private String viewBoxWidth = "100%";
	private String curveStrokeWidth = "0.1";
	private String curveStrokeColor = "black";
	private String outerCurveFillColor = "black";
	private String innerCurveFillColor = "white";

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

	public void setHeight(String height) {
		this.height = height;
	}

	public String getHeight() {
		return height;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getWidth() {
		return width;
	}
	
	public void setViewBoxHeight(String viewBoxHeight) {
		this.viewBoxHeight = viewBoxHeight;
	}
	
	public String getViewBoxHeight() {
		return viewBoxHeight;
	}
	
	public void setViewBoxWidth(String viewBoxWidth) {
		this.viewBoxWidth = viewBoxWidth;
	}
	
	public String getViewBoxWidth() {
		return viewBoxWidth;
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
			// paint outer contures first
			if (!isOuterConture(curve)) {
				continue;
			}
			this.appendConture(strBuilder, curve);
		}
		for (Curve[] curve : curves) {
			// paint inner contures last
			if (isOuterConture(curve)) {
				continue;
			}
			this.appendConture(strBuilder, curve);
		}
	}

	private boolean isOuterConture(Curve[] curve) {
		if (curve.length == 0) {
			return false;
		}
		
		if (curve[0].isOuter()) {
			return true;
		}
		
		return false;
	}

	private void appendConture(StringBuilder strBuilder, Curve[] curves) {
		StringBuilder pathBuilder = new StringBuilder();
		Curve lastCurve = null;
		
		for (Curve curve : curves) {
			if (null == lastCurve) {
				// append move to for first vertex on conture only
				this.appendPathBeginning(pathBuilder, curve);
			}
			lastCurve = curve;
	
			if (curve instanceof BezierCurve) {
				this.appendBezier(pathBuilder, (BezierCurve) curve);
			} else {
				this.appendDirectPath(pathBuilder, curve);
			}
		}
		
		if (null != lastCurve) {
			addLine(strBuilder, String.format(PATH, this.curveStrokeColor, this.getFill(lastCurve), this.curveStrokeWidth, pathBuilder.toString()));
		}
	}
	
	private void appendPathBeginning(StringBuilder pathBuilder, Curve curve) {
		pathBuilder.append("M ");
		pathBuilder.append(getCoordinateString(curve.getFirst()));
		pathBuilder.append("\n\t\t");
	}

	private void appendDirectPath(StringBuilder strBuilder, Curve curve) {
		strBuilder.append(this.buildDirectPath(curve));
		strBuilder.append("\n\t\t");
	}

	private String getFill(Curve c) {
		if (!this.isRenderCurveFillings()) {
			return "none";
		}
		
		if (c.isOuter()) {
			return this.outerCurveFillColor;
		} else {
			return this.innerCurveFillColor;
		}
	}

	private String buildDirectPath(Curve curve) {
		return " L " + getCoordinateString(curve.getSecond()) 
			 + " L "	+ getCoordinateString(curve.getLast());
	}

	private String getCoordinateString(Vertex vertex) {
		return "" + vertex.getX() + " " + vertex.getY();
	}

	private void appendBezier(StringBuilder strBuilder, BezierCurve curve) {
		strBuilder.append(this.buildBezierPath(curve));
		strBuilder.append("\n\t\t");
	}

	private String buildBezierPath(BezierCurve curve) {
		return " C " + getCoordinateString(curve.getSecond()) + " "
				+ getCoordinateString(curve.getThird()) + " " + getCoordinateString(curve.getLast());
	}

	private void appendTitle(StringBuilder strBuilder) {
		addLine(strBuilder, String.format(TITLE_HEADER, this.title));
	}

	private void appendStyle(StringBuilder strBuilder) {
		addLine(strBuilder, STYLE_HEADER);
	}

	private void appendHeader(StringBuilder strBuilder) {
		addLine(strBuilder, String.format(HEADER, width, height, viewBoxWidth, viewBoxHeight));
	}

	private void addLine(StringBuilder strBuilder, String line) {
		strBuilder.append(line.trim());
		strBuilder.append("\n\n");
	}

	private void appendClosingTag(StringBuilder strBuilder) {
		addLine(strBuilder, CLOSING_TAG);
	}
}
