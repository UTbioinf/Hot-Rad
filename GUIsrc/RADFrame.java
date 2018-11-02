import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;


public class RADFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel startPane;
	private JPanel startPane2;
	private TrimPanel testerPane;
	
	private JLabel selectLabel;
	
	private JCheckBox trimCkBox;
	private JCheckBox refCkBox;
	//private JCheckBox alignCkBox;
	
	private JButton continueButton;
	
//	private int[] actions;
	
	private CardLayoutPanel t;
	
	private JFrame me = this;
	
	/**
	 * Create the frame.
	 */
	public RADFrame() {
		
		setTitle("Hot RAD");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(450,550);
		setResizable(false);
		//setBounds(100, 100, 450, 450);
		
		t = new CardLayoutPanel(me, new int[] {0, 1, 0, 0, 0});
		StartPanel start = new StartPanel();
		
		startPane2 = new JPanel();
		startPane2.setBorder(new EmptyBorder(5, 5, 5, 5));
		startPane2.setLayout(null);
		testerPane = new TrimPanel(me, new int[] {0, 1, 0, 0, 0});
		
		//Set panel/card names
		start.setName("start");
		t.addCard(start, "Tester");
		
		JButton btnNewButton2 = new JButton("Whoops");
		btnNewButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("You hit the button 2!");
				me.remove(startPane2);
				me.setContentPane(testerPane);
				me.validate();
			}
		});
		btnNewButton2.setBounds(141, 224, 174, 48);
		startPane2.add(btnNewButton2);
		
		/*
		 * Pane set up
		 */
		startPane = new JPanel();
		startPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//setContentPane(startPane);
		setContentPane(t);
		GridBagLayout gbl_startPane = new GridBagLayout();
		gbl_startPane.columnWidths = new int[]{141, 174, 0, 0};
		gbl_startPane.rowHeights = new int[]{57, 23, 23, 0, 0, 0, 63, 48, 0, 0, 0, 0, 0};
		gbl_startPane.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_startPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		startPane.setLayout(gbl_startPane);
		
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
		gbc_trimCkBox.gridy = 3;
		
		refCkBox = new JCheckBox("Build Reference");
		GridBagConstraints gbc_refCkBox = new GridBagConstraints();
		gbc_refCkBox.anchor = GridBagConstraints.WEST;
		gbc_refCkBox.insets = new Insets(0, 0, 5, 5);
		gbc_refCkBox.gridx = 1;
		gbc_refCkBox.gridy = 4;
		
		/*alignCkBox = new JCheckBox("Align");
		GridBagConstraints gbc_alignCkBox = new GridBagConstraints();
		gbc_alignCkBox.anchor = GridBagConstraints.WEST;
		gbc_alignCkBox.insets = new Insets(0, 0, 5, 5);
		gbc_alignCkBox.gridx = 1;
		gbc_alignCkBox.gridy = 5;*/
		
		/*
		 * Button declarations
		 */
		
		
		startPane.add(selectLabel, gbc_selectLabel);
		startPane.add(trimCkBox, gbc_trimCkBox);
		startPane.add(refCkBox, gbc_refCkBox);
		//startPane.add(alignCkBox, gbc_alignCkBox);
		continueButton = new JButton("Continue...");
		continueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("You hit the button!");
				
				if (!(trimCkBox.isSelected()) && !(refCkBox.isSelected()) ) { //&& !(alignCkBox.isSelected())) {
					JOptionPane.showMessageDialog(me, "You have not selected any actions", "Error", JOptionPane.ERROR_MESSAGE);
				}
				else {
					
					// Construct list of actions to perform
					int[] tempList = new int[] {0, 0, 0};
					if (trimCkBox.isSelected()) {
						tempList[0] = 1;
						t.addCard(testerPane, "trim");
					}
					if (refCkBox.isSelected()) {
						tempList[1] = 1;
						t.addCard(startPane2, "ref");
					}
					/*if (alignCkBox.isSelected()) { 
						tempList[2] = 1;
						t.add(startPane2, "align");
					}*/
					
				  /*if (trimCkBox.isSelected()) {
						testerPane.setActions(tempList);
						me.setContentPane(testerPane);
					}
					if (refCkBox.isSelected()) {
						testerPane.setActions(tempList);
						me.setContentPane(startPane2);
					}
					if (alignCkBox.isSelected()) {
						testerPane.setActions(tempList);
						me.setContentPane(startPane2);
					}*/

					me.setContentPane(t);
					me.remove(startPane);
					me.validate();
				}
			}
		});
		GridBagConstraints gbc_continueButton = new GridBagConstraints();
		gbc_continueButton.insets = new Insets(0, 0, 5, 0);
		gbc_continueButton.gridwidth = 4;
		gbc_continueButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_continueButton.gridx = 0;
		gbc_continueButton.gridy = 14;
		gbc_continueButton.anchor = GridBagConstraints.PAGE_END;
		startPane.add(continueButton, gbc_continueButton);
		
		
	}
}
