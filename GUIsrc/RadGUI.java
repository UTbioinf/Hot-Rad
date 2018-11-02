import javax.swing.*;

import java.awt.*;


public class RadGUI extends JFrame{

		// Added serialVersionUID to calm Eclipse down
		private static final long serialVersionUID = 1L;
		private static final int WIDTH = 400;
		private static final int HEIGHT = 400;
		
		public RadGUI()
		{
			setTitle("Hot RAD");
			Container pane = getContentPane();
			pane.setLayout(new GridLayout (4, 2));
			JLabel widthL = new JLabel("Enter the width: ", SwingConstants.CENTER);
			JLabel areaL = new JLabel("Area: ", SwingConstants.CENTER);
			pane.add(widthL);
			pane.add(areaL);
			setSize(WIDTH, HEIGHT);
			setResizable(false);
			setVisible(true);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
		

}
