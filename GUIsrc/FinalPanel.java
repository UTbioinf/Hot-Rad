import java.awt.GridLayout;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;


public class FinalPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JFrame frame;
	
	private JButton scriptButton;
	
	private ArrayList<String> commands;
	
	/**
	 * Create the panel.
	 */
	public FinalPanel(JFrame myframe, ArrayList<String> in_commands) {
		setSize(445,445);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new GridLayout(0, 1, 0, 0));
		
		frame = myframe;
		
		commands = in_commands;
		
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

		/*
		 * Button declarations
		 */
		
		JButton runButton = new JButton("Run");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (String command : commands){
					try {	
						Runtime.getRuntime().exec(command);
					}
					catch (Exception e){//Catch exception if any
						System.err.println("Error: " + e.getMessage());
					}
					//System.out.println("*" + command);
				}
			}
		});
		
		scriptButton = new JButton("Generate Shell Script");
		scriptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal =  fc.showDialog(frame, "Save");
				File directory = null;
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            directory = fc.getSelectedFile(); 
		            //System.out.println(directory.getAbsolutePath());
		            String filename = directory.getAbsolutePath();
		            scriptButton.setEnabled(false);
				
				try {
						// Create file 
						FileWriter fstream = new FileWriter(filename);
						BufferedWriter out = new BufferedWriter(fstream);
						for (String command : commands){
							out.write(command);
						}
						//Close the output stream
						out.close();
						Runtime.getRuntime().exec("chmod +x " + filename);
		            } 
					catch (Exception e){//Catch exception if any
						System.err.println("Error: " + e.getMessage());
					}
		            //System.out.println(filename);
				}
				
			}
		});
		
		/*
		 * Add everything
		 */		
		add(spacerLabel);
		add(spacerLabel2);
		add(spacerLabel3);
		
		add(runButton);
		add(spacerLabel4);
		add(scriptButton);
		
		add(spacerLabel5);
		add(spacerLabel6);
		add(spacerLabel7);
		
		
	}
	
	public void setCommands(ArrayList <String> in_commands) {
		commands = in_commands;
	}

}
