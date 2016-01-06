/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex;

/**
 * [SHORT_DESCRIPTION] 
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 06.01.2016
 */
public class BezierCurve extends Curve {
	protected Vertex second;
	protected Vertex third;
	
	/**
	 * @return the second
	 */
	public Vertex getSecond() {
		return second;
	}
	
	/**
	 * @param second the second to set
	 */
	public void setSecond(Vertex second) {
		this.second = second;
	}
	
	/**
	 * @return the third
	 */
	public Vertex getThird() {
		return third;
	}
	
	/**
	 * @param third the third to set
	 */
	public void setThird(Vertex third) {
		this.third = third;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		result = prime * result + ((third == null) ? 0 : third.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BezierCurve other = (BezierCurve) obj;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		if (third == null) {
			if (other.third != null)
				return false;
		} else if (!third.equals(other.third))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BezierCurve [getSecond()=");
		builder.append(getSecond());
		builder.append(", getThird()=");
		builder.append(getThird());
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}	
}
