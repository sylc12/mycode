import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;


public class MouseClick {

	/**
	 * @param args
	 * @throws AWTException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws AWTException, InterruptedException {
		Robot robot = new Robot();
		Point point;
		for (long i = 0; i < 900000L; i++) {
			point=MouseInfo.getPointerInfo().getLocation();
			/*robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);*/
			robot.mouseMove(point.x+1, point.y+1);
			robot.mouseMove(point.x, point.y);
			//System.out.println(i);
			//System.out.println(point.x+"  "+point.y);
		    Thread.currentThread().sleep(200000);
		   
		}

	}

}
 