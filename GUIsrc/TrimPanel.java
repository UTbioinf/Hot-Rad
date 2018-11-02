import javax.swing.JFileChooser;
//import javax.swing.ScrollPaneConstants.*;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.io.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.regex.*;

public class TrimPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JFrame frame;
	
	private JTextField barFileField;
	private JTextField cutSiteField;
	private JTextField minCutField;
	private JTextField sameLenField;
	private JTextField outFileField;
	private JTextField maxNField;
	private JTextField repLenField;
	
	private JTextArea seqFileArea;
	
	private JCheckBox sameLenCkBox;
	private JCheckBox indivFilesCkBox;
	
	private String scriptString;
	private String tempSameLen;
	private String tempOutFile;
	
	//private int[] actions;
	
	public TrimPanel(JFrame myframe, int[] actions_in) {
		setSize(445,445);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		// So we can do actions on the frame
		frame = myframe;
		setLayout(new GridLayout(0, 3, 0, 0));
		
		// In case we disable these text fields, keep old data
		tempSameLen = "100"; //Default
		tempOutFile = "";
		
		/*
		 * Label declarations
		 */
		//Stupid spacer labels
		JLabel spacerLabel = new JLabel("");
		JLabel spacerLabel2 = new JLabel("");
		//JLabel spacerLabel3 = new JLabel("");
		JLabel spacerLabel4 = new JLabel("");
		//JLabel spacerLabel5 = new JLabel("");
		JLabel spacerLabel6 = new JLabel("");
		//JLabel spacerLabel7 = new JLabel("");
		// JLabel spacerLabel8 = new JLabel("");
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
		//JLabel spacerLabel22 = new JLabel("");
		JLabel spacerLabel23 = new JLabel("");
		JLabel spacerLabel24 = new JLabel("");
		JLabel spacerLabel25 = new JLabel("");
		JLabel spacerLabel27 = new JLabel("");
		JLabel spacerLabel28 = new JLabel("");
		JLabel spacerLabel29 = new JLabel("");
		
		JLabel titleLabel = new JLabel("Trimming");
		titleLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel barFileLabel = new JLabel("Barcode File");
		barFileLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel seqFileLabel = new JLabel("Sequence File");
		seqFileLabel.setHorizontalAlignment(JTextField.CENTER);
		seqFileLabel.setToolTipText("Test");
		
		JLabel cutSiteLabel = new JLabel("Cut Site");
		cutSiteLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel minCutLabel = new JLabel("Min Cut Site Match");
		minCutLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel sameLenLabel = new JLabel("Length");
		sameLenLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel repLenLabel = new JLabel("N String Length");
		repLenLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel maxNLabel = new JLabel("Maximum N Count");
		maxNLabel.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel outFileLabel = new JLabel("Output File");
		outFileLabel.setHorizontalAlignment(JTextField.CENTER);
		
		/*
		 * Button declarations
		 */
		JButton barFileButton = new JButton("Choose File");
		barFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile(); 
		            barFileField.setText(file.getAbsolutePath());
		           
				}
			}
		});
		
		JButton seqFileButton = new JButton("Choose File");
		seqFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile(); 
		    		outFileField.setText(file.getAbsolutePath() + ".trimmed.fq");
		    		tempOutFile = file.getAbsolutePath() + ".trimmed.fq";
		            //seqFileField.setText(file.getAbsolutePath());
		    		seqFileArea.append(file.getAbsolutePath() + "\n");
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
		indivFilesCkBox = new JCheckBox("Individual Files");
		indivFilesCkBox.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  if (indivFilesCkBox.isSelected()) {
		    		  tempOutFile = outFileField.getText();
		    		  outFileField.setText("");
		    		  outFileField.setEditable(false); 
		    	  }
		    	  else {
		    		  outFileField.setEditable(true);
		    		  outFileField.setText(tempOutFile);
		    	  }
		      }
		});
		
		sameLenCkBox = new JCheckBox("Same Length");
		sameLenCkBox.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  if (sameLenCkBox.isSelected()) {
		    		  sameLenField.setEditable(true);
		    		  sameLenField.setText(tempSameLen);
		    	  }
		    	  else {
		    		  tempSameLen = sameLenField.getText();
		    		  sameLenField.setText("");
		    		  sameLenField.setEditable(false);
		    	  }
		      }
		});
		
		/*
		 * Text area declarations
		 */
		seqFileArea = new JTextArea(10, 10);
		//seqFileArea.setEditable(false);
		
		
		/*
		 * Text field declarations
		 */
		barFileField = new JTextField();
		barFileField.setEditable(false);
		barFileField.setColumns(10);
		
		cutSiteField = new JTextField();
		cutSiteField.setText("AATTC");
		// Modify min cut length automatically when cut site changed
		cutSiteField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent documentEvent) {
				if (cutSiteField.getText().length() == 0) {
					minCutField.setText("");
				}
				else {
					minCutField.setText(Integer.toString(cutSiteField.getText().length()-1));
				}
			}
			public void insertUpdate (DocumentEvent e) {
				if (cutSiteField.getText().length() == 0) {
					minCutField.setText("");
				}
				else {
					minCutField.setText(Integer.toString(cutSiteField.getText().length()-1));
				}
			}
			public void removeUpdate(DocumentEvent documentEvent) {
				if (cutSiteField.getText().length() == 0) {
					minCutField.setText("");
				}
				else {
					minCutField.setText(Integer.toString(cutSiteField.getText().length()-1));
				}
			}
		});
		cutSiteField.setHorizontalAlignment(JTextField.CENTER);
		cutSiteField.setColumns(10);
		
		minCutField = new JTextField();
		minCutField.setText("4");
		minCutField.setHorizontalAlignment(JTextField.CENTER);
		minCutField.setColumns(10);
		
		sameLenField = new JTextField();
		sameLenField.setHorizontalAlignment(JTextField.CENTER);
		sameLenField.setEditable(false);
		sameLenField.setColumns(10);
		
		maxNField = new JTextField();
		maxNField.setHorizontalAlignment(JTextField.CENTER);
		maxNField.setColumns(10);
		
		repLenField = new JTextField();
		repLenField.setText("20");
		repLenField.setHorizontalAlignment(JTextField.CENTER);
		repLenField.setColumns(10);
		
		outFileField = new JTextField();
		outFileField.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane(seqFileArea);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		/*
		 * Add elements to panel
		 */
		add(spacerLabel);
		add(titleLabel);
		add(spacerLabel2);
		
		add(barFileLabel);
		add(barFileField);
		//add(barFileArea);
		add(barFileButton);
		
		add(seqFileLabel);
		//add(seqFileArea);
		add(scrollPane);
		add(seqFileButton);
		
		//add(spacerLabel3);
		add(cutSiteLabel);
		add(spacerLabel4);
		add(minCutLabel);
		
		//add(spacerLabel5);
		add(cutSiteField);
		add(spacerLabel6);
		add(minCutField);
		
		//add(spacerLabel7);
		//add(minCutLabel);
		//add(spacerLabel8);

		//add(spacerLabel9);
		//add(minCutField);
		//add(spacerLabel10);

		add(spacerLabel11);
		add(indivFilesCkBox);
		add(spacerLabel12);

		add(spacerLabel13);
		add(sameLenCkBox);
		add(spacerLabel14);
		
		add(spacerLabel15);
		add(sameLenLabel);
		add(spacerLabel16);
		
		add(spacerLabel17);
		add(sameLenField);
		add(spacerLabel18);
		
		add(spacerLabel9);
		add(maxNLabel);		
		add(spacerLabel10);
		
		add(spacerLabel25);
		add(maxNField);
		add(spacerLabel28);
		
		add(spacerLabel27);
		add(repLenLabel);
		add(spacerLabel29);
		
		add(spacerLabel23);
		add(repLenField);
		add(spacerLabel24);
		
		add(spacerLabel19);
		add(spacerLabel21);
		add(spacerLabel20);
		
		add(outFileLabel);
		add(outFileField);
		add(outFileButton);
		

	}

/*	public void setActions(int[] actions) {
		this.actions = actions;
	}*/
	
	public String getOutput() {
		return outFileField.getText();
	}
	
	// Check to see if there are any errors - return error labels 
	public JLabel[] checker() {
		JLabel[] labelsArray = new JLabel[7];
		int errors = 1;
		File barFile = new File(barFileField.getText());
		//File barFile = new File(barFileArea.getText());
		//File seqFile = new File(seqFileField.getText());
		String[] seqFiles = seqFileArea.getText().split("\n");
		
		
		if (!(barFile.exists())) {

			labelsArray[errors] = new JLabel("+ Barcode file does not exist");
			labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
			errors += 1;
		}
		// Check all input files for existence
		String temperror = "";
		for (String filename:seqFiles)
		{
			File seqFile = new File(filename);
			if (!(seqFile.exists())) {

				temperror += "-- " + filename + "<br>";
			}
		}
		if (!temperror.equals(""))
		{
			labelsArray[errors] = new JLabel("<html>+ Following sequence file(s) do(es) not exist:<br>" + temperror + "</html>");
			labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
			errors += 1;
		}

		// Add cut site
		if (!(Pattern.matches("[atcgATCG]*", cutSiteField.getText()))) {
			labelsArray[errors] = new JLabel("+ Cut site invalid (characters are not A/T/C/G)");
			labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
			errors += 1;
		}
		// Add min cut site match 
		if (!(Pattern.matches("[0-9]*", minCutField.getText())) || minCutField.getText().equals("")) {
			labelsArray[errors] = new JLabel("+ Min cut site match invalid (not a number)");
			labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
			errors += 1;
		}

		//Same length?
		if (sameLenCkBox.isSelected()) {
			// If our length field is valid we're good
			if (!(Pattern.matches("[0-9]*", sameLenField.getText())) || sameLenField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ Length invalid (not a number)");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
		}
		
		
		// Check out file
		if (!indivFilesCkBox.isSelected()) {
			if (outFileField.getText().equals("")) {
				labelsArray[errors] = new JLabel("+ No output file provided");
				labelsArray[errors].setFont(new Font("Arial", Font.PLAIN, 10));
				errors += 1;
			}
		}
		
		
		labelsArray[0] = new JLabel("You have the following error(s):\n");
		return labelsArray;
	}

	// Generate the script string for the trimming process
	public String[] getScriptString() {
		scriptString = "python python/radtag0_trimmer.py ";
		
		// Add cut site
		if (cutSiteField.getText().equals("")){
				scriptString += "-c 0 ";
		}
		else {
			scriptString += "-c " + cutSiteField.getText() + " ";
		}

		// Add min cut site match 
		// If there is no cut site don't worry about it
		if (!(cutSiteField.getText().equals(""))) {
			scriptString += "-m " + minCutField.getText() + " ";
		}

		//Individual files?
		if (indivFilesCkBox.isSelected()) {
			int outResults = 0;
			
			scriptString += "-i ";
			Object options[] = {"Yes, continue", "Cancel"};
			outResults = JOptionPane.showOptionDialog(frame, "You cannot run reference construction with individual files. Continue?", "Warning: Individual Files", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
			if (outResults == 1){
				return null;
			}
		}
		
		if (!(repLenField.getText().equals("20"))) {
			scriptString += "-e " + repLenField.getText() + " ";
		}
		
		if (!(maxNField.getText().trim().equals(""))) {
			scriptString += "-n " + maxNField.getText() + " ";
		}
		
		//Same length?
		if (sameLenCkBox.isSelected()) {
			scriptString += "-s -l " + sameLenField.getText() + " ";
		}
		
		//Output filename
		if (!indivFilesCkBox.isSelected()) {
			int outResults = 0;
			
			File outFile = new File(outFileField.getText());
			if (outFile.exists()){
				Object options[] = {"Yes, continue anyways", "Cancel"};
				outResults = JOptionPane.showOptionDialog(frame, "Output file already exists, do you want to continue?", "Warning: Output Exists", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
				if (outResults == 1){
					return null;
				}
			}
			scriptString += "-o " + outFileField.getText() + " ";
		}
		
		String[] temparray = seqFileArea.getText().split("\n");
		
		String[] scripts = new String[temparray.length];
		int tempcounter = 0;
		
		for (String filename:temparray){
			scripts[tempcounter] = scriptString + "-q " + barFileField.getText() + " " + filename + "\n";
			tempcounter += 1;
		}
		
		return scripts;
	} 
	

}


