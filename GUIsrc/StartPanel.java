import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class StartPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel selectLabel;
	
	private JCheckBox trimCkBox;
	private JCheckBox refCkBox;
	//private JCheckBox alignCkBox;
	
	
	/**
	 * Create the panel.
	 */
	public StartPanel() {

		GridBagLayout gbl_startPane = new GridBagLayout();
		gbl_startPane.columnWidths = new int[]{141, 174, 0, 0};
		gbl_startPane.rowHeights = new int[]{57, 23, 23, 0, 0, 0, 63, 48, 0, 0, 0, 0, 0};
		gbl_startPane.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_startPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gbl_startPane);
		
		/*
		 * Label declarations
		 */
		selectLabel = new JLabel("Select Actions to Perform");
		GridBagConstraints gbc_selectLabel = new GridBagConstraints();
		gbc_selectLabel.insets = new Insets(0, 0, 5, 5);
		gbc_selectLabel.gridx = 1;
		gbc_selectLabel.gridy = 0;
		
		
		/*
		 * Checkbox declarations
		 */
		trimCkBox = new JCheckBox("Trim");
		GridBagConstraints gbc_trimCkBox = new GridBagConstraints();
		gbc_trimCkBox.anchor = GridBagConstraints.WEST;
		gbc_trimCkBox.insets = new Insets(0, 0, 5, 5);
		gbc_trimCkBox.gridx = 1;
		gbc_trimCkBox.gridy = 4;
		
		refCkBox = new JCheckBox("Build Reference");
		GridBagConstraints gbc_refCkBox = new GridBagConstraints();
		gbc_refCkBox.anchor = GridBagConstraints.WEST;
		gbc_refCkBox.insets = new Insets(0, 0, 5, 5);
		gbc_refCkBox.gridx = 1;
		gbc_refCkBox.gridy = 5;
		
		/*alignCkBox = new JCheckBox("Align");
		GridBagConstraints gbc_alignCkBox = new GridBagConstraints();
		gbc_alignCkBox.anchor = GridBagConstraints.WEST;
		gbc_alignCkBox.insets = new Insets(0, 0, 5, 5);
		gbc_alignCkBox.gridx = 1;
		gbc_alignCkBox.gridy = 5;*/
		
		
		add(selectLabel, gbc_selectLabel);
		add(trimCkBox, gbc_trimCkBox);
		add(refCkBox, gbc_refCkBox);
		//add(alignCkBox, gbc_alignCkBox);
		
		
	}

	// Check to see if there are any errors (and what the state of the checkboxes are)
	public int[] checker() {
		if (!(trimCkBox.isSelected()) && !(refCkBox.isSelected()) ) { //&& !(alignCkBox.isSelected())) {
			return null;
		}
		//System.out.println("CHECKER");
		int[] temp = {0, 0, 0};
		if (trimCkBox.isSelected()) {
			temp[0] = 1;
		}
		if (refCkBox.isSelected()) {
			temp[1] = 1;
		}
		/*if (alignCkBox.isSelected()) {
			if (temp[0] == 0 && temp[1] == 0){
				temp[2] = -1;
			}
			else {
				temp[2] = 1;
			}
		}*/
		
		return temp;
	}
	
}
