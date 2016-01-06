/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.svg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * [SHORT_DESCRIPTION] 
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 06.01.2016
 */
public class SvgSaver {
	protected String inputFileName;
	
	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}
	
	public String getInputFileName() {
		return inputFileName;
	}
	
	public void save(String svgContents) {
		FileWriter fw = null;
		BufferedWriter bf = null;
		try {			
			fw = new FileWriter(new File(getSvgFileName()));
			bf = new BufferedWriter(fw);
			
			bf.write(svgContents);
			bf.flush();			
		} catch (IOException e) {
		} finally {
			if (null != bf) { try { bf.close(); } catch ( Exception e2) {} }
			if (null != fw) { try { fw.close(); } catch ( Exception e2) {} }
		}
	}

	public String getSvgFileName() {
		return inputFileName + "_curves.svg";
	}

}
