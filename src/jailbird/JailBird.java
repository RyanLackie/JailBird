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
	
	public static void run() throws InterruptedException {
		while (true) {
			//if running
			if (running) {
				if (!applicationFound)
					target();
				
				if (applicationFound) {
					label.setForeground(Color.RED);
					label.setText("Running");
					
					User32 user32 = User32.instance;  
		            HWND hWnd = user32.FindWindow("ApplicationFrameWindow", "Sea of Thieves"); //ClassName, WindowName
		            if (hWnd == null) {
		            	running = false;
		            	applicationFound = false;
		            }
		            user32.ShowWindow(hWnd, User32.SW_SHOW);  
		            user32.SetForegroundWindow(hWnd);
		            
			    	Random rand = new Random();
			    	int r = rand.nextInt(12)+1;
			    	
			    	switch (r) {
			    		case 1:	robKeyHold('A');
			    				break;
			    		case 2:	robKeyHold('W');
			    				break;
			    		case 3:	robKeyHold('S');
			    				break;
			    		case 4:	robKeyHold('D');
			    				break;
			    		case 5:	robKeyPress('6');
			    				break;
			    		case 6:	robKeyPress('7');
			    				break;
			    		case 7:robKeyPress('8');
								break;
			    		case 8:robKeyPress('9');
								break;
			    		case 9:robKeyPress('0');
								break;
			    		case 10:robKeyPress('l');
								break;
			    		case 11:robKeyPress('b');
								break;
			    		case 12:robKeyPress('i');
								break;
			    	}
			    	Thread.sleep(secDelay * 1000);
				}
			}
			//if not running
			if (!running) {
				label.setForeground(Color.BLACK);
				label.setText("Standby");
			}
		}
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
	
	public static void target() {
		logBuilder("Searching for Sea of Thieves");
		
		HWND find = User32.instance.FindWindow("ApplicationFrameWindow", "Sea of Thieves");
		if (find == null) {
			applicationFound = false;
			logBuilder("Can not find Sea of Thieves");
			running = false;
		}
		else {
			applicationFound = true;
			logBuilder("Connected to Sea of Thieves");
		}
	}
	public interface User32 extends W32APIOptions {
        User32 instance = (User32) Native.loadLibrary("user32", User32.class, DEFAULT_OPTIONS);
        boolean ShowWindow(HWND hWnd, int nCmdShow);
        boolean SetForegroundWindow(HWND hWnd);
        HWND FindWindow(String winClass, String title);
        int SW_SHOW = 1;
    }
	
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
	
	public static void robKeyPress(char c) throws InterruptedException {
		logBuilder(c + " is pressed");
		
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
    	logBuilder(c + " is being held for 1 second");
    	
    	long i = System.currentTimeMillis();
    	while (true) {
	    	try {
				Robot robot = new Robot();
				robot.keyPress(c);
				if (System.currentTimeMillis() >= i+1000) {
					robot.keyRelease(c);
					logBuilder(c + " is released");
					break;
				}
			} 
	    	catch (AWTException e) {
				e.printStackTrace();
			}
    	}
    }

}
