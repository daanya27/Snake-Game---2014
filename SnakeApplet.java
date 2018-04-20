import java.applet.*;
import java.awt.*;

import javax.swing.UIManager;

public class SnakeApplet extends Applet{
	private snakeCanvas c;
	public void init() {
		
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			//empty
		}
		
		//add(this, CENTER);
		
		c = new snakeCanvas();
		c.setPreferredSize(new Dimension(1000, 500));
		c.setVisible(true);
		c.setFocusable(true);
		this.add(c);
		this.setVisible(true);
		this.setSize(new Dimension(1000, 500));
	}
	
	public void paint() {
		this.setSize(new Dimension(1000, 500));
	}	

}

// THINGS TO GET DONE: 
//	1. Look at double buffering
//	2. Customization?



