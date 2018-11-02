import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
//import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class AlnPanel extends JPanel {

	private static final long serialVersionUID = 1L;
		
	private JFrame frame;
		
	//private String inputFile;
	private String scriptString;
	
	private JTextField seqFileField;
	private JTextField segField;
	private JTextField minPercentField;
	private JTextField matchScoreField;
	private JTextField mismatchScoreField;
	private JTextField gapScoreField;
	private JTextField outFileField;
	private JTextField maxAlnField;
	private JTextField testAreaField;
	private JTextField maxDiffField;
	private JTextField overhangField;
	private JTextField numBasesField;
	private JTextField minInitField;
	private JTextField hrCoverField;
	private JTextField kBandField;
	
	private JRadioButton fastaButton;
	private JRadioButton fastqButton;
	
	private JButton seqFileButton;
	private JButton outFileButton;
	private JComboBox assembComboBox;

	private boolean disabled = false;
	private boolean trueDisabled = false;
	

	/**
	 * Create the panel.
	 */
	public AlnPanel(JFrame myframe, int[] actions_in) {
		setSize(445,445);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new GridLayout(0, 3, 0, 0));
		
		/*
		 * Label declarations
		 */
		//Stupid spacer labels
		JLabel spacerLabel = new JLabel("");
		JLabel spacerLabel2 = new JLabel("");
	/*	JLabel spacerLabel3 = new JLabel("");
		JLabel spacerLabel4 = new JLabel("");
		JLabel spacerLabel5 = new JLabel("");
		JLabel spacerLabel6 = new JLabel("");
		JLabel spacerLabel7 = new JLabel("");
		JLabel spacerLabel8 = new JLabel("");
		JLabel spacerLabel9 = new JLabel("");
		JLabel spacerLabel10 = new JLabel("");
		JLabel spacerLabel11 = new JLabel("");
		JLabel spacerLabel12 = new JLabel("");
		JLabel spacerLabel13 = new JLabel("");
		JLabel spacerLabel14 = new JLabel("");
		JLabel spacerLabel15 = new JLabel("");
		JLabel spacerLabel16 = new JLabel("");
		JLabel spacerLabel17 = new JLabel("");
		JLabel spacerLabel18 = new JLabel("");*/
		JLabel spacerLabel19 = new JLabel("");
		JLabel spacerLabel20 = new JLabel("");
		//JLabel spacerLabel21 = new JLabel("");
		JLabel spacerLabel22 = new JLabel("");
		//JLabel spacerLabel23 = new JLabel("");
		JLabel spacerLabel24 = new JLabel("");
		JLabel spacerLabel25 = new JLabel("");
		JLabel spacerLabel26 = new JLabel("");
		JLabel spacerLabel27 = new JLabel("");
	//	JLabel spacerLabel28 = new JLabel("");
		
		JLabel titleLabel = new JLabel("");
		titleLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel seqFileLabel = new JLabel("Sequence File");
		seqFileLabel.setHorizontalAlignment(JTextField.CENTER);
		seqFileLabel.setToolTipText("Test");
		
		JLabel assembLabel = new JLabel("Assembler");
		assembLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel segLabel = new JLabel("# Segments");
		segLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel minPercentLabel = new JLabel("Percent Identity");
		minPercentLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel matchScoreLabel = new JLabel("Match Score");
		matchScoreLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel mismatchScoreLabel = new JLabel("Mismatch Score");
		mismatchScoreLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel gapScoreLabel = new JLabel("Gap Score");
		gapScoreLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel outputFileLabel = new JLabel("Output Folder");
		outputFileLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel maxAlnLabel = new JLabel("Max Alignment Len");
		maxAlnLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel maxDiffLabel = new JLabel("Max Difference");
		maxDiffLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel overhangLabel = new JLabel("Overhang");
		overhangLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel numBasesLabel = new JLabel("# Bases to Compare");
		numBasesLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel minInitLabel = new JLabel("Min Initial Matching");
		minInitLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel testAreaLabel = new JLabel("Test Area");
		testAreaLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel hrCoverLabel = new JLabel("Minimum Group Size");
		hrCoverLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel kBandLabel = new JLabel("K Band");
		kBandLabel.setHorizontalAlignment(JTextField.CENTER);
		
		/*
		 * Button declarations
		 */
		seqFileButton = new JButton("Choose File");
		seqFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile(); 
		            seqFileField.setText(file.getAbsolutePath());
				}
			}
		});
		
		outFileButton = new JButton("Choose Folder");
		outFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showDialog(frame, "Select");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile(); 
		            //System.out.println(file.getAbsolutePath());
		            outFileField.setText(file.getAbsolutePath());
				}
				
			}
		});
		
		fastaButton = new JRadioButton("FASTA Input");
		fastaButton.setSelected(true);
		fastaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fastaButton.isSelected()) {
					fastqButton.setSelected(false);
				}
				// Don't deselect by clicking on selected
				if (!(fastaButton.isSelected())) {
					fastaButton.setSelected(true);
				}
			}
		});
		fastqButton = new JRadioButton("FASTQ Input");
		fastqButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fastqButton.isSelected()) {
					fastaButton.setSelected(false);
				}
				// Don't unselect by clicking on selected
				if (!(fastqButton.isSelected())) {
					fastqButton.setSelected(true);
				}
			}
		});
		
		/*
		 * Combo box declarations
		 */
		
		assembComboBox = new JComboBox(new String[] {"Makeflow", "Local", "Filter Only"});
		assembComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 JComboBox cb = (JComboBox)e.getSource();
			        String assembler = (String)cb.getSelectedItem();
			        if (assembler.equals("Local")) {
		        		segField.setEditable(false);
			        }
			        // If we only want to filter, disable everything
			        if (assembler.equals("Filter Only")) {
			        	disabled = true;
			        	segField.setEditable(false);
			        	minPercentField.setEditable(false);
			        	matchScoreField.setEditable(false);
			        	mismatchScoreField.setEditable(false);
			        	gapScoreField.setEditable(false);
			        	outFileField.setEditable(false);
			        	seqFileButton.setEnabled(false);
			        	outFileButton.setEnabled(false);
			        	//assembComboBox.setEnabled(false);
			        	//System.out.println("DISABLED");
			        }
			        // Re-enable (just in case disabled via Filter Only) 
			        // True disabled brought on by indiv files
			        else if (!trueDisabled) {
			        	disabled = false;
			        	if (assembler.equals("Makeflow")) {
			        		segField.setEditable(true);
			        	}
			        	minPercentField.setEditable(true);
			        	matchScoreField.setEditable(true);
			        	mismatchScoreField.setEditable(true);
			        	gapScoreField.setEditable(true);
			        	outFileField.setEditable(true);
			        	seqFileButton.setEnabled(true);
			        	outFileButton.setEnabled(true);
			        	//assembComboBox.setEnabled(true);
			        }
			        	
			}
		});
		
		
		/*
		 * Text field declarations
		 */
		
		seqFileField = new JTextField();
		seqFileField.setEditable(false);
		seqFileField.setColumns(10);
		
		segField = new JTextField();
		segField.setText("10");
		segField.setHorizontalAlignment(JTextField.CENTER);
		segField.setColumns(10);
		
		minPercentField = new JTextField();
		minPercentField.setText("90");
		minPercentField.setHorizontalAlignment(JTextField.CENTER);
		minPercentField.setColumns(10);
		
		maxAlnField = new JTextField();
		maxAlnField.setText("100");
		maxAlnField.setHorizontalAlignment(JTextField.CENTER);
		maxAlnField.setColumns(10);
		
		matchScoreField = new JTextField();
		matchScoreField.setText("1");
		matchScoreField.setHorizontalAlignment(JTextField.CENTER);
		matchScoreField.setColumns(10);
		
		hrCoverField = new JTextField();
		hrCoverField.setText("3");
		hrCoverField.setHorizontalAlignment(JTextField.CENTER);
		hrCoverField.setColumns(10);
		
		
		mismatchScoreField = new JTextField();
		mismatchScoreField.setText("0");
		mismatchScoreField.setHorizontalAlignment(JTextField.CENTER);
		mismatchScoreField.setColumns(10);
		
		gapScoreField = new JTextField();
		gapScoreField.setText("0");
		gapScoreField.setHorizontalAlignment(JTextField.CENTER);
		gapScoreField.setColumns(10);
		
		numBasesField = new JTextField();
		numBasesField.setText("50");
		numBasesField.setHorizontalAlignment(JTextField.CENTER);
		numBasesField.setColumns(10);
		
		maxDiffField = new JTextField();
		maxDiffField.setText("5");
		maxDiffField.setHorizontalAlignment(JTextField.CENTER);
		maxDiffField.setColumns(10);
		
		overhangField = new JTextField();
		overhangField.setText("3");
		overhangField.setHorizontalAlignment(JTextField.CENTER);
		overhangField.setColumns(10);
		
		testAreaField = new JTextField();
		testAreaField.setText("8");
		testAreaField.setHorizontalAlignment(JTextField.CENTER);
		testAreaField.setColumns(10);
		
		minInitField = new JTextField();
		minInitField.setText("5");
		minInitField.setHorizontalAlignment(JTextField.CENTER);
		minInitField.setColumns(10);
		
		kBandField = new JTextField();
		kBandField.setText("8");
		kBandField.setHorizontalAlignment(JTextField.CENTER);
		kBandField.setColumns(10);
		
		outFileField = new JTextField();
		outFileField.setColumns(10);
		
		/*
		 * Add elements to panel
		 */
		
//		add(spacerLabel19);
		add(assembComboBox);
		add(fastaButton);
		add(fastqButton);
//		add(spacerLabel20);
		
		add(spacerLabel19);
		add(spacerLabel25);
		add(spacerLabel20);
		
		add(seqFileLabel);
		add(seqFileField);
		add(seqFileButton);
		
	
		
		add(spacerLabel24);
		add(segLabel);
		//add(spacerLabel25);
		add(spacerLabel27);
		
		
		add(spacerLabel26);
		add(segField);
		add(spacerLabel22);
		
	//	add(spacerLabel26);
		
		//add(spacerLabel3);
		add(minPercentLabel);
		add(kBandLabel);
		add(maxAlnLabel);
		
		//add(spacerLabel5);
		add(minPercentField);
		add(kBandField);
		add(maxAlnField);
		
		//add(spacerLabel7);
		add(matchScoreLabel);
		add(mismatchScoreLabel);
		add(gapScoreLabel);
		
		//add(spacerLabel8);

		//add(spacerLabel9);
		add(matchScoreField);
		add(mismatchScoreField);
		add(gapScoreField);
		//add(spacerLabel10);

		//add(spacerLabel11);
		
		//add(spacerLabel12);

		//add(spacerLabel13);
		
		//add(spacerLabel14);

		//add(spacerLabel15);
		
		//add(spacerLabel16);
		
		//add(spacerLabel17);
		
		//add(spacerLabel18);
		
		add(numBasesLabel);
		//add(spacerLabel16);
		add(maxDiffLabel);
		add(hrCoverLabel);
		
		add(numBasesField);
		//add(spacerLabel17);
		add(maxDiffField);
		add(hrCoverField);
		
		add(testAreaLabel);
		add(overhangLabel);
		add(minInitLabel);
		
		add(testAreaField);
		add(overhangField);
		add(minInitField);
		
		add(spacerLabel);
		add(titleLabel);
		add(spacerLabel2);
		
		add(outputFileLabel);
		add(outFileField);
		add(outFileButton);
		
		//System.out.println(inputFile);

	}

	// Set input file - for when we come from trim
	public void setInput(String input) {
		if (input.equals("")) {
			disabled = true;
			trueDisabled = true;
			segField.setEditable(false);
        	minPercentField.setEditable(false);
        	matchScoreField.setEditable(false);
        	mismatchScoreField.setEditable(false);
        	gapScoreField.setEditable(false);
        	outFileField.setEditable(false);
        	seqFileButton.setEnabled(false);
        	outFileButton.setEnabled(false);
        	assembComboBox.setEnabled(false);
        	//System.out.println("DISABLED");
		}
		else {
			// In case we've disabled before
			disabled = false;
			trueDisabled = false;
			segField.setEditable(true);
        	minPercentField.setEditable(true);
        	matchScoreField.setEditable(true);
        	mismatchScoreField.setEditable(true);
        	gapScoreField.setEditable(true);
        	outFileField.setEditable(true);
        	seqFileButton.setEnabled(true);
        	outFileButton.setEnabled(true);
        	assembComboBox.setEnabled(true);
        	
			seqFileField.setText(input);
			//outFileField.setText(input + ".contigs");
		}
	}
	
	
	// Check to see if there are any errors - return error labels 
		public JLabel[] checker() {
			
			JLabel[] labelsArray = new JLabel[7];
			int errors = 1;
			
			// If we're not assembling, give it the go ahead regardless
			if (disabled) {
				labelsArray[0] = new JLabel("You have the following error(s):\n");	
				return labelsArray;
			}
			
			// Check segment count
			if (!(Pattern.matches("[0-9]*", segField.getText())) || segField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Num segments invalid (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}

			// Check min percent
			if (!(Pattern.matches("[0-9]*", minPercentField.getText())) || minPercentField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Min percent invalid (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
			// Check for valid percent
			else if (Integer.parseInt(minPercentField.getText().trim()) > 100 || Integer.parseInt(minPercentField.getText().trim()) < 0) {
				labelsArray[errors] = new JLabel("+ Min percent invalid (not between 0 and 100)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
			
			
			// Check match score
			if (!(Pattern.matches("[0-9]*", matchScoreField.getText())) || matchScoreField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Match score invalid (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
			
			//Check mismatch score
			if (!(Pattern.matches("[0-9]*", mismatchScoreField.getText())) || mismatchScoreField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Mismatch score invalid (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
			
			//Check count length
			if (!(Pattern.matches("[0-9]*", numBasesField.getText())) || numBasesField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Num bases invalid (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
			
			//Check max align
			if (!(Pattern.matches("[0-9]*", maxAlnField.getText())) || maxAlnField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Max align invalid (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
			
			//Check test area
			if (!(Pattern.matches("[0-9]*", testAreaField.getText())) || testAreaField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Test area invalid (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
			
			//Check maximum diff
			if (!(Pattern.matches("[0-9]*", maxDiffField.getText())) || maxDiffField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Max difference invalid (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
			
			//Check overhang
			if (!(Pattern.matches("[0-9]*", overhangField.getText())) || overhangField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Overhang invalid (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
			
			//Check min init
			if (!(Pattern.matches("[0-9]*", minInitField.getText())) || minInitField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Min init invalid (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
			
			//Check group size
			if (!(Pattern.matches("[0-9]*", hrCoverField.getText())) || hrCoverField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Group size invalid (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
			
			// Check gap score
			if (!(Pattern.matches("[0-9]*", gapScoreField.getText())) || gapScoreField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Gap score invalid (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
			
			// Check out file
			if (outFileField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ No output folder provided");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
			
			labelsArray[0] = new JLabel("You have the following error(s):\n");	
			return labelsArray;
		}
	
	
	public String getScriptString() {
		// Construct Python call for script
		// Checks are redundant
		
		String configName = seqFileField.getText() + ".config";
		
		// If we're not assembling, send a blank string
		if (disabled) {
			return "";
		}
		//If Makeflow, add the actual running of things
		String assembler = (String)assembComboBox.getSelectedItem();
		if (assembler.equals("Makeflow")) {
			scriptString = "construct.sh ";
		}
		if (assembler.equals("Local")) {
			scriptString = "assemble.sh assembDist.jar ";
		}
		
		scriptString += seqFileField.getText() + " ";
		
		// Number of segments
		if (!assembler.equals("Local")) {
			scriptString += segField.getText() + " ";
		}
		// Config file
		scriptString += configName + " ";
		
		// File format
		if (fastaButton.isSelected()) {
			if (!assembler.equals("Local")) {
				scriptString += "-a ";
			}
			else
				scriptString += "a ";
		}
		else {
			if (!assembler.equals("Local")) {
				scriptString += "-q ";
			}
			else
				scriptString += "q ";
		}
		
		// Destination
		scriptString += outFileField.getText();
		if (assembler.equals("Local")) {
			scriptString += "final.txt\n";
		}
		else
			scriptString += "\n";
		
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(configName));
			// Add min percentage
			if (Pattern.matches("[0-9]*", minPercentField.getText())&& !(minPercentField.getText().equals(""))) {
				bw.write("percentIdent=" + minPercentField.getText() + "\n");
			}
			
			// Add match score
			if (Pattern.matches("[0-9]*", matchScoreField.getText()) && !(matchScoreField.getText().equals(""))) {
				bw.write("matchScore=" + matchScoreField.getText() + "\n");
			}
		
			//Add mismatch score
			if (Pattern.matches("[0-9]*", mismatchScoreField.getText()) && !(mismatchScoreField.getText().equals(""))) {
				bw.write("mismatchScore=" + mismatchScoreField.getText() + "\n");
			}
			
			// Add gap score
			if (Pattern.matches("[0-9]*", gapScoreField.getText()) && !(gapScoreField.getText().equals(""))) {
				bw.write("gapScore=" + gapScoreField.getText() + "\n");		
			}
			
			bw.write("countLen=" + numBasesField.getText() + "\n");
			bw.write("filtMaxDiff=" + maxDiffField.getText() + "\n");
			bw.write("minGroupSizeD=" + hrCoverField.getText() + "\n");
			bw.write("maxAlign=" + maxAlnField.getText() + "\n");
			bw.write("testArea=" + testAreaField.getText() + "\n");
			bw.write("minInitial=" + minInitField.getText() + "\n");
			bw.write("overhang=" + overhangField.getText() + "\n");
			bw.write("kband=" + kBandField.getText() + "\n");
			
			bw.close();
			
		}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		
		//If Makeflow, add the actual running of things
	    if (assembler.equals("Makeflow")) {
        	scriptString += "cd " + outFileField.getText() + "\n makeflow \n";
        }
		
		return scriptString;
	}
	
	public String getOutput() {
		if (!disabled) {
			return outFileField.getText();
		}
		
		return null;
	}
	
	public boolean alnEnabled() {
		return !disabled;
	}
}