import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/*
 * IMPORTANT: For this to function, all new panels MUST call .setName("")
 * This name is what is used to do handling on the components when continue/back button is hit
 * 
 * Lauren Assour
 * 5 October 2012
 * 
 */
public class CardLayoutPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private AlnPanel aln;
	private FiltPanel filt;
	private JPanel temp;
	private TrimPanel testerPane;
	private FinalPanel fin;
	
	private RefBuildTabbedPane tabPane;
	private JFrame frame;
	
	private JButton continueButton;
	
	private ArrayList<String> scripts;
	
	/**
	 * Create the panel.
	 */
	public CardLayoutPanel(JFrame myframe, int[] actions_in) {
		super(new BorderLayout());
		
		frame = myframe;
		
		testerPane = new TrimPanel(frame, new int[] {0, 1, 0, 0, 0});
		aln = new AlnPanel(myframe, actions_in);
		filt = new FiltPanel(myframe, actions_in);
		temp = new JPanel(new CardLayout());
		fin = new FinalPanel(myframe, scripts);
		tabPane = new RefBuildTabbedPane();
		
		scripts = new ArrayList<String>();
		
		
		
		JPanel grid = new JPanel(new GridLayout(1,2));
		
		// BACK BUTTON
		JButton backButton = new JButton("<< Back");
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout)(temp.getLayout());
				int ncomponents = temp.getComponentCount();
				//System.out.println(Integer.toString(ncomponents));
				String visibleCard = "";
				
				int i;
				
				for (i = 0 ; i < ncomponents ; i++) {
				    Component comp = temp.getComponent(i);
				    if (comp.isVisible()) {
				    
				        visibleCard = comp.getName();
				        //System.out.println(visibleCard);
				        break;
				    }
				}
				
				//Re-enable continue button
				if (i == ncomponents-1) {
					continueButton.setEnabled(true);
					continueButton.setVisible(true);
				}
				
				if (visibleCard.equals("start")) {
					//System.out.println("LOL NO");
				}
				else {
						cl.previous(temp);
				}
			}
		});
		
		
		// CONTINUE BUTTON
		continueButton = new JButton("Continue >>");
		continueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout)(temp.getLayout());
				int ncomponents = temp.getComponentCount();
				//System.out.println(Integer.toString(ncomponents));
				String visibleCard = "";
				Component visibleComp = null;
				boolean errors = false;
				
				int i;
				
				for (i = 0 ; i < ncomponents ; i++) {
				    Component comp = temp.getComponent(i);
				    if (comp.isVisible()) {
				    
				        visibleCard = comp.getName();
				        //System.out.println(visibleCard);
				        visibleComp = comp;
				        break;
				    }
				}
				
				//Begin handling of each view
				if (visibleCard.equals("start")) {
					errors = startHandler(cl, visibleCard, visibleComp);
					//System.out.println( "ERRORS? " + errors);
				}
				else if (visibleCard.equals("trim")) {
					errors = trimHandler(cl, visibleCard, visibleComp);
					// Set input for alignment/filtering
					if (temp.getComponent(i+1).getName().equals("ref")) {
						((RefBuildTabbedPane)temp.getComponent(i+1)).setInput(((TrimPanel)visibleComp).getOutput());
						//((AlnPanel)(((JTabbedPane)temp.getComponent(i+1)).getComponentAt(0))).setInput(((TrimPanel)visibleComp).getOutput());
						//((FiltPanel)(((JTabbedPane)temp.getComponent(i+1)).getComponentAt(1))).setInput("g");
					}
				}
				else if (visibleCard.equals("ref")) {
					errors = refHandler(cl, visibleCard, visibleComp);
				}
				
				//No errors so we should keep going
				if (!errors) {
					//Disable continue button for final screen and give scripts
					if (i+1 == ncomponents-1) {
						fin.setCommands(scripts);
						continueButton.setEnabled(false);
						continueButton.setVisible(false);
					}
				}
		
			}
		});
		
		
		//Add all the stuffs
		grid.add(backButton);
		grid.add(continueButton);
		add(temp, BorderLayout.CENTER);
		
		tabPane.add("Alignment", aln);
		tabPane.add("Filtering", filt);
		add(grid, BorderLayout.PAGE_END);

	}
	
	public void addCard(JPanel pane, String name) {
		temp.add(pane, name);
	}

	public void callError(JFrame frame, Object errors, String title){
		JOptionPane.showMessageDialog(frame, errors, title, JOptionPane.ERROR_MESSAGE);
	}
	
	// Handle start frame continue
	public boolean startHandler(CardLayout cl, String visibleCard, Component visibleComp) {
		int[] results = ((StartPanel)visibleComp).checker();
		//Clear list just to make sure we don't get weird things
		// if people have gone back and changed options
		temp.removeAll();
		// Clear script list too just in case
		scripts = new ArrayList<String>();
		//Re-add the start panel
		temp.add(visibleComp, "start");
		if (results != null){
			if (results[0] == 1) {
				testerPane.setName("trim");
				temp.add(testerPane, "trim");
			}
			if (results[1] == 1) {
				tabPane.setName("ref");
				temp.add(tabPane, "ref");
			}
			if (results[2] == 1) {

			}
			fin.setName("final");
			temp.add(fin, "final");
			
		}
		if (results != null && results[2] == -1)
		{
			callError(frame, "You must select another option in addition to align", "Error");
			return true;
		}
		else if (results == null) {
			callError(frame, "You have not selected any actions", "Error");
			return true;
		}
		cl.next(temp);
		return false;
	}
	
	// Handle trim continue
	public boolean trimHandler(CardLayout cl, String visibleCard, Component visibleComp) {
		JLabel[] results = ((TrimPanel)visibleComp).checker();
		if (results[1] == null){
			String[] scriptStrings = ((TrimPanel)visibleComp).getScriptString();
			if (scriptStrings != null) {
				for (String script:scriptStrings)
				{
					scripts.add(script);
				}
				//System.out.println(scriptString);
				cl.next(temp);
			}
			else {
				return true;
			}
		}
		else {
			callError(frame, results, "Error: Incorrect or Missing Parameters");
			return true;
		}
		return false;
	}
	
	// Handle reference construction continue
	public boolean refHandler(CardLayout cl, String visibleCard, Component visibleComp) {
		boolean moveOn  = true;
		String scriptStringAln = "";
		String scriptStringFilt = "";
		
		AlnPanel alnPanel = (AlnPanel) ((JTabbedPane)visibleComp).getComponentAt(0);
		FiltPanel filtPanel = (FiltPanel) ((JTabbedPane)visibleComp).getComponentAt(1);
		
		//Check alignment tab
		JLabel[] resultsAln = alnPanel.checker();
		if (resultsAln[1] == null){
			scriptStringAln = alnPanel.getScriptString();
			if (scriptStringAln == null) {
				moveOn = false;
			}
		}
		else {
			moveOn = false;
			callError(frame, resultsAln, "Align Error: Incorrect or Missing Parameters");
		}
		
		// Check filter tab
		JLabel[] resultsFilt = filtPanel.checker();
		if (resultsFilt[1] == null){
			scriptStringFilt = filtPanel.getScriptString();
			if (scriptStringFilt == null) {
				moveOn = false;
			}
		}
		else {
			moveOn = false;
			callError(frame, resultsFilt, "Filter Error: Incorrect or Missing Parameters");
		} 
		
		if (moveOn) {
			scripts.add(scriptStringAln);
			scripts.add(scriptStringFilt);
			//System.out.println(scriptStringAln);
			//System.out.println(scriptStringFilt);
			cl.next(temp);
			return false;
		}
		return true;
		
	}

}
