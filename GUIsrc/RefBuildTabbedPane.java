

import javax.swing.JTabbedPane;

public class RefBuildTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	
	public void setInput(String input){
		((AlnPanel)(this.getComponentAt(0))).setInput(input);
		((FiltPanel)(this.getComponentAt(1))).setInput(((AlnPanel)(this.getComponentAt(0))).getOutput());
	}
	
	public void setSelectedIndex(int index) {
		super.setSelectedIndex(index);
		if (this.getTabCount() > 1) {
			String file = ((AlnPanel)(this.getComponentAt(0))).getOutput();
			// If the AlnPanel isn't disabled, then set our input to its output
			if (file != null) {
				((FiltPanel)(this.getComponentAt(1))).setInput(file);
				((FiltPanel)(this.getComponentAt(1))).hrAlign = ((AlnPanel)(this.getComponentAt(0))).alnEnabled();
			}
		}	
	}

}
