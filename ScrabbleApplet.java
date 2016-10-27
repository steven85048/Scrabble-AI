import javax.swing.JApplet;

public class ScrabbleApplet extends JApplet{
	public static int APPLET_WIDTH = 1000, APPLET_HEIGHT = 700;
	public static int BOARD_WIDTH = 15, BOARD_HEIGHT = 15;
	
	public void init(){
		setSize(APPLET_WIDTH, APPLET_HEIGHT);
		try {
			getContentPane().add(new ScrabblePanel());
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
