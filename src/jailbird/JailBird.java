package jailbird;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.W32APIOptions;

public class JailBird {
	
	static JFrame frame = new JFrame("JailBird");
	static JTextArea Console;
	static JButton Start;
	static JButton Stop;
	static JLabel label;
	
	static boolean running = false;
	static boolean applicationFound = false;
	
	static int secDelay = 5;
	
	static String[] log = {"", "", "", "", "", "", ""};
	static int index = 0;

	public static void main(String[] args) throws InterruptedException {
		init();
		run();
	}
	
	public static void init() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(348, 198);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		
		Start = new JButton("Start");
		Start.setBackground(Color.GREEN);
		Start.setBounds(10, 11, 62, 55);
		frame.getContentPane().add(Start);
		
		Stop = new JButton("Stop");
		Stop.setBackground(Color.RED);
		Stop.setBounds(10, 77, 62, 55);
		frame.getContentPane().add(Stop);
		
		Start.addActionListener(new Start());
		Stop.addActionListener(new Stop());
		
		Console = new JTextArea();
		Console.setEditable(false);
		Console.setBounds(112, 11, 210, 121);
		frame.getContentPane().add(Console);
		
		
		label = new JLabel();
		label.setText("Standby");
		label.setBounds(112, 139, 55, 14);
		frame.getContentPane().add(label);
		
		frame.setVisible(true);
	}
	public static class Start implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			running = true;
		}
	}
	public static class Stop implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			running = false;
		}
	}
	
	public static void run() throws InterruptedException {
		long previousTime = System.currentTimeMillis();
		
		while (true) {
			//if running
			if (running) {
				if (applicationFound) {
					
					if (System.currentTimeMillis() >= previousTime + 4000) {
						System.out.println("here");
						label.setForeground(Color.RED);
						label.setText("Running");
						
						boolean target = target();
						if (target) {
							Random rand = new Random();
					    	int r = rand.nextInt(15)+1;
					    	
					    	switch (r) {
					    		//Left
					    		case 1:	
					    				logBuilder("Walking Left");
					    				robKeyHold('A');
					    				break;
			    				//Forwards
					    		case 2:	
				    					logBuilder("Walking Forward");
					    				robKeyHold('W');
					    				break;
			    				//Backwards
					    		case 3:	
				    					logBuilder("Walking Backward");
					    				robKeyHold('S');
					    				break;
			    				//Right
					    		case 4:	
				    					logBuilder("Walking Right");
					    				robKeyHold('D');
					    				break;
			    				//Pocket Watch
					    		case 5: 
				    					logBuilder("Pulled Out Pocket Watch");
					    				robKeyPress('0');
										break;
								//Primary Weapon
					    		case 7: 
				    					logBuilder("Pulled Out Primary Weapon");
					    				robKeyPress('1');
										break;
								//Secondary Weapon
					    		case 8: 
				    					logBuilder("Pulled Out Secondary Weapon");
					    				robKeyPress('2');
					    				break;
			    				//Shovel
					    		case 9:	
				    					logBuilder("Pulled Out Shovel");
					    				robKeyPress('6');	
					    				break;
			    				//Compass
					    		case 10:
				    					logBuilder("Pulled Out Compass");
					    				robKeyPress('7');
					    				break;
			    				//Spyglass
					    		case 11:
				    					logBuilder("Pulled Out Spyglass");
					    				robKeyPress('8');
										break;
								//Tankard
					    		case 12:
				    					logBuilder("Pulled Out Tankard");
					    				robKeyPress('9');
										break;
								//Lantern
					    		case 13:
				    					logBuilder("Pulled Out Lantern");
					    				robKeyPress('l');
										break;
								//Bucket
					    		case 14:
				    					logBuilder("Pulled Out Bucket");
					    				robKeyPress('b');
										break;
								//Instrument
					    		case 15:
				    					logBuilder("Pulled Out Instrument");
					    				robKeyPress('i');
										break;
					    	}
						}
				    	
						previousTime = System.currentTimeMillis();
					}
				}
				
				else if (!applicationFound) {
					logBuilder("Searching for Sea of Thieves");
					boolean target = target();
					if (target) {
						applicationFound = true;
						logBuilder("Connected to Sea of Thieves");
					}
					else {
						applicationFound = false;
						running = false;
						logBuilder("Can not find Sea of Thieves");
					}
				}
			}
			
			//if not running
			else if (!running) {
				label.setForeground(Color.BLACK);
				label.setText("Standby");
			}
		}
	}
	
	//User32 Utility
	public static boolean target() {
		User32 user32 = User32.instance;
		HWND window = user32.FindWindow("ApplicationFrameWindow", "Sea of Thieves"); //ClassName, WindowName
		if (window == null)
			return false;
		else {
            user32.ShowWindow(window, User32.SW_SHOW);  
            user32.SetForegroundWindow(window);
			return true;
		}
	}
	public interface User32 extends W32APIOptions {
        User32 instance = (User32) Native.loadLibrary("user32", User32.class, DEFAULT_OPTIONS);
        boolean ShowWindow(HWND hWnd, int nCmdShow);
        boolean SetForegroundWindow(HWND hWnd);
        HWND FindWindow(String winClass, String title);
        int SW_SHOW = 1;
    }
	
	//Console Utility
	public static void logBuilder(String st) {
		if (index < 7)
			log[index] = st;
		else {
			for (int i = 0; i < 6; i++) {
				log[i] = log[i+1];
			}
			log[6] = st;
		}
		
		Console.setText(log[0]+"\n"+log[1]+"\n"+log[2]+"\n"+log[3]+"\n"+log[4]+"\n"+log[5]+"\n"+log[6]);
		
		if (index < 8)
			index++;
	}
	
	//Robot Utility
	public static void robKeyPress(char c) throws InterruptedException {
		try {
			Robot robot = new Robot();
			robot.keyPress(c);
	    	robot.keyRelease(c);
		} 
		catch (AWTException e) {
			e.printStackTrace();
		}
    }
    
    public static void robKeyHold(char c) throws InterruptedException {
    	long previousTime = System.currentTimeMillis();
    	boolean loop = true;
    	while (loop) {
	    	try {
				Robot robot = new Robot();
				robot.keyPress(c);
				if (System.currentTimeMillis() >= previousTime + 500) {
					robot.keyRelease(c);
					loop = false;
				}
			} 
	    	catch (AWTException e) {
				e.printStackTrace();
			}
    	}
    }

}
