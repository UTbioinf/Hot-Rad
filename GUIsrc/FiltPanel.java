import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


public class FiltPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	
	private String scriptString;
	private String tempPadding;
	public boolean hrAlign;
	
	private JTextField seqFileField;
	private JTextField minCoverField;
	private JTextField minLenField;
	private JTextField maxLenField;
	private JTextField paddingField;
	private JTextField outFileField;
	
	private JCheckBox oneContigCkBox;

	
	/**
	 * Create the panel.
	 */
	public FiltPanel(JFrame myframe, int[] actions_in) {
		setSize(445,445);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new GridLayout(0, 3, 0, 0));

		tempPadding = "30";
	
		
		/*
		 * Label declarations
		 */
		//Stupid spacer labels
		JLabel spacerLabel = new JLabel("");
		JLabel spacerLabel2 = new JLabel("");
		JLabel spacerLabel3 = new JLabel("");
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
		JLabel spacerLabel18 = new JLabel("");
		JLabel spacerLabel19 = new JLabel("");
		JLabel spacerLabel20 = new JLabel("");
		JLabel spacerLabel21 = new JLabel("");
		JLabel spacerLabel22 = new JLabel("");
		JLabel spacerLabel23 = new JLabel("");
		JLabel spacerLabel24 = new JLabel("");
		JLabel spacerLabel25 = new JLabel("");
		JLabel spacerLabel26 = new JLabel("");
	//	JLabel spacerLabel27 = new JLabel("");
		//JLabel spacerLabel28 = new JLabel("");
		//JLabel spacerLabel29 = new JLabel("");
		
		JLabel titleLabel = new JLabel("");
		titleLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel seqFileLabel = new JLabel("Contig File");
		seqFileLabel.setHorizontalAlignment(JTextField.CENTER);
		seqFileLabel.setToolTipText("Test");
		
		JLabel minCoverLabel = new JLabel("Minimum Coverage");
		minCoverLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel minLenLabel = new JLabel("Minimum Length");
		minLenLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel maxLenLabel = new JLabel("Maximum Length");
		maxLenLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel paddingLabel = new JLabel("Padding Ns");
		paddingLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel outputFileLabel = new JLabel("Output File");
		outputFileLabel.setHorizontalAlignment(JTextField.CENTER);
		
		/*
		 * Button declarations
		 */
		JButton seqFileButton = new JButton("Choose File");
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
		
		JButton outFileButton = new JButton("Choose File");
		outFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showDialog(frame, "Save");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile(); 
		            //System.out.println(file.getAbsolutePath());
		            outFileField.setText(file.getAbsolutePath());
				}
				
			}
		});
		
		
		/*
		 * Check box declarations
		 */
		oneContigCkBox = new JCheckBox("Single contig");
		oneContigCkBox.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  if (oneContigCkBox.isSelected()) {
		    		  paddingField.setEditable(true);
		    		  paddingField.setText(tempPadding);
		    	  }
		    	  else {
		    		  paddingField.setEditable(false);
		    		  tempPadding = paddingField.getText();
		    		  paddingField.setText("");
		    	  }
		      }
		});
		
		
		/*
		 * Text field declarations
		 */
		
		seqFileField = new JTextField();
		//seqFileField.setEditable(false);
		seqFileField.setColumns(10);
		
		minCoverField = new JTextField();
		minCoverField.setText("7");
		minCoverField.setHorizontalAlignment(JTextField.CENTER);
		minCoverField.setColumns(10);
		
		minLenField = new JTextField();
		minLenField.setText("105");
		minLenField.setHorizontalAlignment(JTextField.CENTER);
		minLenField.setColumns(10);
		
		maxLenField = new JTextField();
		maxLenField.setText("120");
		maxLenField.setHorizontalAlignment(JTextField.CENTER);
		maxLenField.setColumns(10);
		
		paddingField = new JTextField();
		paddingField.setText("");
		paddingField.setHorizontalAlignment(JTextField.CENTER);
		paddingField.setColumns(10);
		
		outFileField = new JTextField();
		outFileField.setColumns(10);
		
		/*
		 * Add elements to panel
		 */
		add(seqFileLabel);
		add(seqFileField);
		add(seqFileButton);
		
		add(spacerLabel);
		add(titleLabel);
		add(spacerLabel2);
	
		add(spacerLabel3);
		add(minCoverLabel);
		add(spacerLabel4);
		
		add(spacerLabel5);
		add(minCoverField);
		add(spacerLabel6);
		
		add(spacerLabel7);
		add(minLenLabel);
		add(spacerLabel8);

		add(spacerLabel9);
		add(minLenField);
		add(spacerLabel10);

		add(spacerLabel11);
		add(maxLenLabel);
		add(spacerLabel12);

		add(spacerLabel13);
		add(maxLenField);
		add(spacerLabel14);

		add(spacerLabel15);
		add(oneContigCkBox);
		add(spacerLabel16);
		
		add(spacerLabel17);
		add(paddingLabel);
		add(spacerLabel18);
		
		add(spacerLabel19);
		add(paddingField);
		add(spacerLabel20);
		
		add(spacerLabel21);
		add(spacerLabel22);
		add(spacerLabel23);
		
		add(spacerLabel24);
		add(spacerLabel26);
		add(spacerLabel25);
		
		add(outputFileLabel);
		add(outFileField);
		add(outFileButton);
		
		
		
		//System.out.println(inputFile);

	}

	// Set input file - for when we come from trim
	public void setInput(String input) {
		if (!input.equals("")){
			seqFileField.setText(input + "/final.txt");
		}
	}
	
	
	public JLabel[] checker() {
		JLabel[] labelsArray = new JLabel[7];
		int errors = 1;
		
		// Add min coverage
			if (!(Pattern.matches("[0-9]*", minCoverField.getText())) || minCoverField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Min cover invalid (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			
			}

		// Add min length 
		if (!(Pattern.matches("[0-9]*", minLenField.getText())) || minLenField.getText().equals("")) {
			labelsArray[errors] = new JLabel("+ Min length invalid (not a number)");
			labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
			errors += 1;		
		}
		
		//Add max len
		if (!(Pattern.matches("[0-9]*", maxLenField.getText())) || maxLenField.getText().equals("")) {
			labelsArray[errors] = new JLabel("+ Max length invalid (not a number)");
			labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
			errors += 1;
		}
		
		//One contig?
		if (oneContigCkBox.isSelected()) {
			// If our length field is valid we're good
			if (!(Pattern.matches("[0-9]*", paddingField.getText())) || paddingField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Invalid number of padding Ns (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
		}
		
		if (outFileField.getText().equals("")) {
			labelsArray[errors] = new JLabel("+ No filter output file provided");
			labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
			errors += 1;
		}
				
		labelsArray[0] = new JLabel("You have the following error(s):\n");
		
		return labelsArray;
	}
	
	public String getScriptString() {
		// Construct Python call for script
		// Checks are redundant
		
		scriptString = "python python/radtag1_makefakegenome.py ";		
		
		// Add min coverage
		if (Pattern.matches("[0-9]*", minCoverField.getText()) && !(minCoverField.getText().equals(""))) {
				scriptString += "-c " + minCoverField.getText() + " ";
			}

		
		// Add min length 
		if (Pattern.matches("[0-9]*", minLenField.getText()) && !(minLenField.getText().equals(""))) {
				scriptString += "-m " + minLenField.getText() + " ";
		}

		
		//Add max len
		if (Pattern.matches("[0-9]*", maxLenField.getText()) && !(maxLenField.getText().equals(""))) {
				scriptString += "-x " + maxLenField.getText() + " ";
		}
		
		//One contig?
		if (oneContigCkBox.isSelected()) {
			// If our length field is valid we're good
			if (Pattern.matches("[0-9]*", paddingField.getText()) && !(paddingField.getText().equals(""))) {
				scriptString += "-s -n " + paddingField.getText() + " ";
			}
		}
		//Output filename
		File outFile = new File(outFileField.getText());
		if (outFile.exists()){
			Object options[] = {"Yes, continue anyways", "Cancel"};
			int outResults = JOptionPane.showOptionDialog(frame, "Output file already exists, do you want to continue?", "Warning: Output Exists", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
			
			// If we hit cancel, don't continue
			if (outResults == 1){
				return null;
			}
			scriptString += "-o " + outFileField.getText() + " ";
		}
		
		if (hrAlign) {
			scriptString += "-r ";
		}
		
		scriptString += "-q " + seqFileField.getText() + "\n";
		
		return scriptString;
	}
	
}
