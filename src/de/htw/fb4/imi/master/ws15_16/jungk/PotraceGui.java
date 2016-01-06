// Copyright (C) 2014 by Klaus Jung
// All rights reserved.
// Date: 2014-10-02
package de.htw.fb4.imi.master.ws15_16.jungk;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Factory;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vector2D;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.ff.AbstractFloodFilling.Mode;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.BezierCurveFinder;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.ICurveFinder;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.IOutlinePathFinder;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.IOutlinePathFinder.TurnPolicy;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.IPolygonFinder;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Curve;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Outline;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.svg.SvgBuilder;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.svg.SvgSaver;

public class PotraceGui extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int border = 10;
	private static final int maxWidth = 400;
	private static final int maxHeight = 400;
	private static final File openPath = new File(".");
	private static String title = "Potrace ";
	private static final String author = "Markus Föllmer & Sascha Feldmann";
	private static final String initalOpen = "head.png";
	private static final int TEXTAREA_COLS = 30;
	private static final int TEXTAREA_ROWS = 5;

	private static final int windowWidth = 800;
	private static final int windowHeight = 400;
	private static final int SIZE_SVG_INPUTFIELDS = 5;

	private JSlider zoomSlider;

	private static JFrame frame;

	private ImageView srcView; // source image view
	private ImageView dstView; // binarized image view

	private JComboBox<String> turnPoliciesList; // the selected binarization
												// method
	private JTextArea statusArea; // to print some status text

	private String message;
	protected Mode mode;
	private JCheckBox displayInnerCheckbox;
	private JPanel imagesPanel;
	private IOutlinePathFinder outlinePathFinderAlgorithm;
	private IPolygonFinder polygonFinderAlgorithm;
	private JCheckBox showOutlinesCheckbox;
	private JCheckBox showPolygonsCheckbox;
	private JCheckBox showImageCheckbox;
	private File input;
	private JCheckBox renderCurveFillings;
	private JTextField curveAlpha;
	private JTextField curveMinAlpha;
	private JTextField curveMaxAlpha;
	private ICurveFinder curveFinderAlgorithm;

	public PotraceGui() {
		super(new BorderLayout(border, border));

		// load the default image
		this.input = new File(initalOpen);

		if (!input.canRead())
			input = openFile(); // file not found, choose another image

		srcView = new ImageView(input);
		srcView.setMaxSize(new Dimension(maxWidth, maxHeight));

		// create an empty destination image
		dstView = new ImageView(maxWidth, maxHeight);
		dstView.setMaxSize(new Dimension(maxWidth, maxHeight));

		// load image button
		JButton load = new JButton("Bild öffnen");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input = openFile();
				if (input != null) {
					srcView.loadImage(input);
					runPotrace();
				}
			}
		});

		// selector for the binarization method
		JLabel turnPolicyText = new JLabel("Turn Policy:");

		turnPoliciesList = new JComboBox<String>(TurnPolicy.stringValues());
		turnPoliciesList.setSelectedIndex(0); // set initial method
		turnPoliciesList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runPotrace();
			}
		});

		// load raster checkbox
		JCheckBox rasterCheckBox = new JCheckBox("Raster");
		rasterCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					ImageView.boolRaster = true;
					runPotrace();
				} else {
					ImageView.boolRaster = false;
					runPotrace();
				}	
			}
		});

		// slider for zoom in picture
		JLabel zoomLabel = new JLabel("Zoom");
		this.zoomSlider = new JSlider((int) ImageView.MIN_ZOOM, 255, (int) ImageView.MIN_ZOOM);
		zoomSlider.setMinorTickSpacing(25);
		zoomSlider.setMajorTickSpacing(50);
		zoomSlider.setPaintLabels(true);
		zoomSlider.setPaintTicks(true);

		// Change-Listener for zoom slider
		zoomSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				dstView.setZoom(zoomSlider.getValue());
			}
		});

		this.showImageCheckbox = new JCheckBox("Image", ImageView.SHOW_IMAGE_DEFAULT);
		showImageCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				dstView.setShowImage(showImageCheckbox.isSelected());
				repaint();
			}
		});		

		this.showOutlinesCheckbox = new JCheckBox("Outlines", ImageView.SHOW_OUTLINES_DEFAULT);
		showOutlinesCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				dstView.setShowOutlines(showOutlinesCheckbox.isSelected());
				repaint();
			}
		});
		
		this.displayInnerCheckbox = new JCheckBox("Inner outlines", ImageView.SHOW_INNER_OUTLINES_DEFAULT);

		displayInnerCheckbox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				dstView.setDisplayInner(displayInnerCheckbox.isSelected());
				repaint();
			}
		});

		this.showPolygonsCheckbox = new JCheckBox("Polygons", ImageView.SHOW_OUTLINES_POLYGONS_DEFAULT);
		showPolygonsCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				dstView.setShowPolygons(showPolygonsCheckbox.isSelected());
				repaint();
			}
		});

		JPanel controls = prepareTopControls(load, turnPolicyText, rasterCheckBox, zoomLabel);		
		JPanel southControls = prepareBottomControls();

		this.imagesPanel = new JPanel(new FlowLayout());
		imagesPanel.setPreferredSize(new Dimension(windowWidth, windowHeight));
		imagesPanel.add(dstView);

		add(controls, BorderLayout.NORTH);
		add(imagesPanel, BorderLayout.CENTER);
		add(southControls, BorderLayout.SOUTH);

		setBorder(BorderFactory.createEmptyBorder(border, border, border, border));

		this.runPotrace();
	}

	protected JPanel prepareTopControls(JButton load, JLabel turnPolicyText, JCheckBox rasterCheckBox,
			JLabel zoomLabel) {
		// arrange all controls
		JPanel controls = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, border, 0, 0);
		controls.add(load, c);
		controls.add(turnPolicyText, c);
		controls.add(turnPoliciesList, c);
		controls.add(zoomLabel, c);
		controls.add(zoomSlider, c);
		controls.add(showImageCheckbox, c);
		controls.add(showOutlinesCheckbox, c);
		controls.add(displayInnerCheckbox, c);
		controls.add(showPolygonsCheckbox, c);
		controls.add(rasterCheckBox, c);
		return controls;
	}

	protected JPanel prepareBottomControls() {		
		// some status text
		statusArea = new JTextArea(TEXTAREA_ROWS, TEXTAREA_COLS);
		statusArea.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(statusArea);
		
		this.renderCurveFillings = new JCheckBox("Fill curves", ImageView.SHOW_OUTLINES_POLYGONS_DEFAULT);		
		JLabel curveAlphaLabel = new JLabel("Alpha:");
		this.curveAlpha = new JTextField(BezierCurveFinder.DEFAULT_ALPHA.toString(), SIZE_SVG_INPUTFIELDS);
		JLabel curveMinAlphaLabel = new JLabel("Min Alpha:");
		this.curveMinAlpha = new JTextField(BezierCurveFinder.DEFAUTL_MIN_ALPHA.toString(), SIZE_SVG_INPUTFIELDS);
		JLabel curveMaxAlphaLabel = new JLabel("Max Alpha:");
		this.curveMaxAlpha = new JTextField(BezierCurveFinder.DEFAULT_MAX_ALPHA.toString(), SIZE_SVG_INPUTFIELDS);
		
		// arrange curve / SVG controls
		JPanel southControls = new JPanel(new GridBagLayout());		
		GridBagConstraints cSouth = new GridBagConstraints();
		cSouth.insets = new Insets(0, border, 0, 0);		
		
		// save SVG button
		JButton saveSvg = new JButton("Create curves and save as SVG");
		saveSvg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Set<Curve[]> curves = createCurves();
				saveAsSVG(curves);
			}		
		});

		southControls.add(scrollPane, cSouth);		
		southControls.add(curveAlphaLabel, cSouth);		
		southControls.add(curveAlpha, cSouth);
		southControls.add(curveMinAlphaLabel, cSouth);	
		southControls.add(curveMinAlpha, cSouth);
		southControls.add(curveMaxAlphaLabel, cSouth);	
		southControls.add(curveMaxAlpha, cSouth);
		southControls.add(renderCurveFillings, cSouth);
		southControls.add(saveSvg, cSouth);
		
		return southControls;
	}

	private File openFile() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Images (*.jpg, *.png, *.gif)", "jpg", "png",
				"gif");
		chooser.setFileFilter(filter);
		chooser.setCurrentDirectory(openPath);
		int ret = chooser.showOpenDialog(this);
		if (ret == JFileChooser.APPROVE_OPTION) {
			frame.setTitle(title + chooser.getSelectedFile().getName());
			return chooser.getSelectedFile();
		}
		return null;
	}

	private static void createAndShowGUI() {
		// create and setup the window
		title = title + " - " + author + " - ";

		frame = new JFrame(title + initalOpen);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JComponent newContentPane = new PotraceGui();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// display the window.
		frame.pack();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	protected void runPotrace() {
		TurnPolicy policy = TurnPolicy.values()[turnPoliciesList.getSelectedIndex()];

		// image dimensions
		int width = srcView.getImgWidth();
		int height = srcView.getImgHeight();

		// get pixels arrays
		int srcPixels[] = srcView.getPixels();
		int dstPixels[] = java.util.Arrays.copyOf(srcPixels, srcPixels.length);

		// this.message = "Binarisieren mit \"" + methodName + "\"";
		this.message = "Konturenerkennung mit Potrace und Turn Policy \"" + policy;

		triggerOutlineFinding(policy, dstPixels);
		triggerPolygonFinding(dstPixels);

		statusArea.setText(message);

		dstView.setPixels(srcPixels, width, height);
		// dstView.setPixels(ImageUtil.get1DFrom2DArray(width, height,
		// ((Potrace) this.potraceAlgorithm).getProcessingPixels()), width,
		// height);
		frame.pack();

		dstView.saveImage("out.png");

	}
	

	private Set<Curve[]> createCurves() {
		Set<Curve[]> curves = new HashSet<>();
		this.curveFinderAlgorithm = Factory.newBezierCurveAlgorithm();
		
		if (this.hasValidInputs()) {
			Set<Vector2D[]> polygons = this.dstView.getPolygons();
			
			for (Vector2D[] polygon : polygons) {
				curves.add(this.curveFinderAlgorithm.calculateCurve(polygon));
			}			
		}		
		
		return curves;
	}		

	private boolean hasValidInputs() {
		try {
			double curveAlpha = Double.parseDouble(this.curveAlpha.getText());	
			((BezierCurveFinder) this.curveFinderAlgorithm).setAlpha(curveAlpha);
		} catch (Exception e) {
			this.showError("Invalid value for curve alpha. Must be a double!");
			return false;
		}
		
		try {
			double curveMinAlpha = Double.parseDouble(this.curveMinAlpha.getText());
			((BezierCurveFinder) this.curveFinderAlgorithm).setMinAlpha(curveMinAlpha);
		} catch (Exception e) {
			this.showError("Invalid value for curve min alpha. Must be a double!");
			return false;
		}
		
		try {
			double curveMaxAlpha = Double.parseDouble(this.curveMaxAlpha.getText());
			((BezierCurveFinder) this.curveFinderAlgorithm).setMaxAlpha(curveMaxAlpha);
		} catch (Exception e) {
			this.showError("Invalid value for curve max alpha. Must be a double!");			
			return false;
		}
		
		return true;
	}

	private void showError(String string) {
		JOptionPane.showMessageDialog(this,
				string,
				"Invalid value",
				JOptionPane.ERROR_MESSAGE);
	}

	private void saveAsSVG(Set<Curve[]> curves) {
		SvgBuilder svgBuilder = new SvgBuilder();
		svgBuilder.setRenderCurveFillings(this.renderCurveFillings.isSelected());
		svgBuilder.setTitle(this.input.getName() + " Curves");
		svgBuilder.setCurves(curves);
		
		String builtSvg = svgBuilder.build();
		
		SvgSaver svgSaver = new SvgSaver();
		svgSaver.setInputFileName(this.input.getName());
		svgSaver.save(builtSvg);		
		
		this.showSuccessMessage("The curves SVG file was saved as " + svgSaver.getSvgFileName());
	}
	
	private void showSuccessMessage(String string) {
		JOptionPane.showMessageDialog(this,
				string,
				"Success",
				JOptionPane.INFORMATION_MESSAGE);		
	}

	protected void triggerOutlineFinding(TurnPolicy policy, int[] dstPixels) {
		long time = 0;

		// trigger outline finding
		this.outlinePathFinderAlgorithm = Factory.newPotraceOutlineFinderAlgorithmWithContoursTxtFallback(input);
		this.outlinePathFinderAlgorithm.setTurnPolicy(policy);
		time = detectAndShowOutline(dstPixels);

		message += "\nRequired time for outline detection: " + time + " ms";
	}

	private long detectAndShowOutline(int[] dstPixels) {
		this.outlinePathFinderAlgorithm.setOriginalBinaryPixels(this.srcView.getImgWidth(), this.srcView.getImgHeight(),
				this.srcView.getPixels());

		// only measure labeling algorithm runtime, not the time to color pixels
		long startTime = System.currentTimeMillis();
		Set<Outline> foundOutlines = this.outlinePathFinderAlgorithm.find();
		long time = System.currentTimeMillis() - startTime;

		// mark outlines somehow
		// this.paintOutlines(foundOutlines, dstPixels);
		this.dstView.setOutlines(foundOutlines);

		return time;
	}

	private void triggerPolygonFinding(int[] dstPixels) {
		this.polygonFinderAlgorithm = Factory.newPotracePolyginFinderAlgorithm();

		long time = findAndShowPolygons(dstPixels);

		message += "\nRequired time for polygon finding: " + time + " ms";
	}

	private long findAndShowPolygons(int[] dstPixels) {
		Set<Outline> outlines = this.dstView.getOutlines();
		Set<Vector2D[]> outerPolygons = new HashSet<>();

		long startTime = System.currentTimeMillis();

		for (Outline outline : outlines) {
				int[] pivots = this.polygonFinderAlgorithm.findStraightPathes(outline);
				int[] possibleSegments = this.polygonFinderAlgorithm.findPossibleSegments(pivots);
				Vector2D[] polygon = this.polygonFinderAlgorithm.findOptimalPolygon(possibleSegments);
				outerPolygons.add(polygon);
		}

		long time = System.currentTimeMillis() - startTime;
		this.dstView.setOuterPolygons(outerPolygons);

		return time;
	}

	public static int calculateGrayValue(int pixelValue) {
		// greyValue = R + G + B / 3
		return ((pixelValue & 0xff) + ((pixelValue & 0xff00) >> 8) + ((pixelValue & 0xff0000) >> 16)) / 3;
	}
}
