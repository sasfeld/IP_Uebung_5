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
public class Curve {
	protected Vertex first;
	protected Vertex last;	
	
	protected Vertex second;
	
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
	 * @return the first
	 */
	public Vertex getFirst() {
		return first;
	}
	
	/**
	 * @param first the first to set
	 */
	public void setFirst(Vertex first) {
		this.first = first;
	}
	
	/**
	 * @return the last
	 */
	public Vertex getLast() {
		return last;
	}
	
	/**
	 * @param last the last to set
	 */
	public void setLast(Vertex last) {
		this.last = last;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((last == null) ? 0 : last.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Curve other = (Curve) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (last == null) {
			if (other.last != null)
				return false;
		} else if (!last.equals(other.last))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Curve [getSecond()=");
		builder.append(getSecond());
		builder.append(", getFirst()=");
		builder.append(getFirst());
		builder.append(", getLast()=");
		builder.append(getLast());
		builder.append("]");
		return builder.toString();
	}	
}
