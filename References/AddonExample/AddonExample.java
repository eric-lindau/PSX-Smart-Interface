/* AddonExample.java and AddonExampleNetThread.java 
 * are free software for PSX Alpha testers. You may use the code
 * to start writing an addon yourself, but please keep it private in the Alpha 
 * team until PSX is publicly released. Thereafter you may distribute your addon 
 * as freeware, shareware, commercial ware, whatever you like.
 * 
 * I will extend AddonExample from time to time.
 * 
 * Author: Hardy Heinlin                                Last change: 26 NOV 2012
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.*;

public class AddonExample {

	static String version = "4";
	static AddonExampleNetThread netThread;
	static JButton buttMastWarn;
	static String strIndLts_test;
	static String strIndLts_bright;
	static String strIndLts_dim;
	static String strHdg_tru;
	static String strHdg_norm;
	static String strCduBlanking;
	static JLabel lab_FloodPwr = new JLabel("---");
	static JLabel lab_CduLKey = new JLabel("---");
	static JLabel lab_Freqs = new JLabel("---");
	static JLabel lab_McpSpd = new JLabel("---");
	static final boolean TO_PSX = true, FROM_PSX = !TO_PSX;
	static final int PUSHED = 1, NOT_PUSHED = Integer.MAX_VALUE - PUSHED;
	static final int TEST_ON = 8192;
	static final int LIGHT_ON1 = TEST_ON + 128;
	static final int LIGHT_ON2 = TEST_ON + 256;
	static boolean noodleOn, noodleEnabled;
	static int qhKeybCduL;
	static int qsCdusActSubsys;
	static String strLcduTitle;
	static String strLcduLine1s;
	static String strLcduLine1b;
	static String strLcduLine2s;
	static String strLcduLine2b;
	static String strLcduLine3s;
	static String strLcduLine3b;
	static String strLcduLine4s;
	static String strLcduLine4b;
	static String strLcduLine5s;
	static String strLcduLine5b;
	static String strLcduLine6s;
	static String strLcduLine6b;
	static String strLcduScrPad;
	static int qiMcpWdoSpd, qsFreqsAntenna, qsLtVoltages;

	static class Event {
		/**
		 * @param i - status bitmask 
		 */
		void process(int i) {
		}
	}

	static Event evtMastWarn = new Event() {
		private StringBuilder sbEvt = new StringBuilder("Master Warning Captain");

		void process(int i) {
			sbEvt.delete(22, sbEvt.length());
			if ((i & LIGHT_ON1) != 0)
				sbEvt.append(" W");
			else
				sbEvt.append(" O");
			if ((i & LIGHT_ON2) != 0)
				sbEvt.append(" C");
			else
				sbEvt.append(" O");
			buttMastWarn.setText(sbEvt.toString());
		}
	};

	static class MomentaryActionSwitch {
		// 1 = pushed
		// 2 = bulb A fail
		// 4 = bulb B fail
		// 8 = bulb C fail
		// 16 = bulb D fail
		// 32 = covered
		// 64 = wired
		// 128 = light contact 1 closed
		// 256 = light contact 2 closed
		// 8192 = test light contact closed
		private int valStart, bits = -1;
		private Event event;
		int qIndex = -1;
		// Make string operations with StringBuilder to relieve Java's garbage
		// collector. Avoid operations like "ABC" + "XYZ". Use append() instead.
		private StringBuilder sb;

		MomentaryActionSwitch(Event e) {
			event = e;
		}

		void init(int qi) {
			sb = new StringBuilder("Qh").append(qi).append('=');
			valStart = sb.length();
			qIndex = qi;
		}

		synchronized void transfer(boolean toPsx, int i) {
			// synchronized method to keep the latest bitmask status intact
			// (Not really required in this simple example yet, but it'll be necessary
			// when more complex bit read/write actions are performed in this method).
			if (toPsx) {
				if (qIndex > -1) // assure init() has been done
					sendToPsx(sb.replace(valStart, sb.length(),
							Integer.toString(bits | PUSHED)).toString());
				// PSX deletes any buttonpush bit itself. For the momentary action
				// it is not necessary to send an unpush command after a push command.
				// So it's not required to cache the push status either: It's always
				// unpushed anyway - except in the short moment when this method is
				// invoked.
				// Note: argument int i is not used within these parantheses
			} else if (bits != i) {
				bits = i;
				// if the bits have changed and an event is interfaced, do something:
				if (event != null)
					event.process(bits);
				/*
				 * The following sendToPsx() assures network synchronity: When the addon
				 * was sending a bitmask to PSX while PSX was sending a bitmask to the
				 * addon, the addon's sent bitmask may not contain the latest settings
				 * from PSX, thus may undo the latest events in PSX. So, if the addon's
				 * current bitmask disagrees with the latest receipt, the latest receipt
				 * will be resent to PSX to force synchronity. Example: The addon sends
				 * a button push command with the light bits off, at the same time PSX
				 * sends a bitmask with the light bits on. Result: PSX has illuminated
				 * its lights, and a fraction of a second later the addon extinguishes
				 * the lights (and pushes the button). This would be a synchronity
				 * error. To avoid this, the addon's sent bitmask will be followed by
				 * another, corrected bitmask. PSX is always the winner. (The addon may
				 * control the lights, but PSX may reset it during one of the next
				 * events in its electrical simulation. The purpose of this example is
				 * to show how to push a button and how to read the lights. I.e. only
				 * the button push bit should have read and write permission, all other
				 * bits should only have read permission.) Note: PSX deletes any
				 * buttonpush bit itself. The latest status, therefore, is always
				 * unpushed. Avoid double-pushes. Push only when the addon user actually
				 * triggers a push.
				 */
				sendToPsx(sb.replace(valStart, sb.length(), Integer.toString(bits))
						.toString());
			}
		}

	}

	static MomentaryActionSwitch mastWarnCaptain = new MomentaryActionSwitch(
			evtMastWarn);

	public static void main(String[] args) {

		// Use setLookAndFeel() if you want the standard Java look on the Mac:
		// try {
		// UIManager
		// .setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// begin network connection checkbox **************************************
		ItemListener ilNetConn = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					netThread = new AddonExampleNetThread("localhost", 10747);
					netThread.start();
				} else {
					if (netThread != null)
						netThread.finalJobs();
				}
			}
		};
		JCheckBox cbNetConn = new JCheckBox("Connected to PSX server");
		cbNetConn.addItemListener(ilNetConn);
		// end network connection checkbox ****************************************

		// begin IND LTS switch ***************************************************
		Action acIndLts_test = new AbstractAction("IND LTS - TEST") {
			public void actionPerformed(ActionEvent e) {
				sendToPsx(strIndLts_test);
			}
		};
		Action acIndLts_bright = new AbstractAction("IND LTS - BRIGHT") {
			public void actionPerformed(ActionEvent e) {
				sendToPsx(strIndLts_bright);
			}
		};
		Action acIndLts_dim = new AbstractAction("IND LTS - DIM") {
			public void actionPerformed(ActionEvent e) {
				sendToPsx(strIndLts_dim);
			}
		};
		JRadioButton rbIndLts_test = new JRadioButton(acIndLts_test);
		JRadioButton rbIndLts_bright = new JRadioButton(acIndLts_bright);
		JRadioButton rbIndLts_dim = new JRadioButton(acIndLts_dim);
		ButtonGroup bgIndLts = new ButtonGroup();
		bgIndLts.add(rbIndLts_test);
		bgIndLts.add(rbIndLts_bright);
		bgIndLts.add(rbIndLts_dim);
		// end IND LTS switch *****************************************************

		// begin FLOOD POT ********************************************************
		JSlider slFlood = new JSlider(0, 270, 0);
		slFlood.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sendFlood(((JSlider) e.getSource()).getValue());
			}
		});
		slFlood.setPaintTicks(true);
		slFlood.setMinorTickSpacing(10);
		slFlood.setMajorTickSpacing(30);
		slFlood.setValueIsAdjusting(false);
		slFlood.setPaintLabels(true);
		Hashtable<Integer, JLabel> hdgTable = new Hashtable<Integer, JLabel>();
		hdgTable.put(new Integer(0), new JLabel("000"));
		hdgTable.put(new Integer(90), new JLabel("090"));
		hdgTable.put(new Integer(180), new JLabel("180"));
		hdgTable.put(new Integer(270), new JLabel("270"));
		slFlood.setLabelTable(hdgTable);
		// end FLOOD POT **********************************************************

		// begin HDG TRU/NORM switch **********************************************
		ItemListener ilHdg = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					sendToPsx(strHdg_tru);
				} else {
					sendToPsx(strHdg_norm);
				}
			}
		};
		JCheckBox cbHdg_tru = new JCheckBox("HDG - TRU/NORM");
		cbHdg_tru.addItemListener(ilHdg);
		// end HDG TRU/NORM switch ************************************************

		// begin MASTER WARNING SW ************************************************
		Action actionMastWarn = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				mastWarnCaptain.transfer(TO_PSX, 0); // 0 is meaningless if TO_PSX
			}
		};
		buttMastWarn = new JButton();
		buttMastWarn.setAction(actionMastWarn);
		buttMastWarn.setText("---");
		// end MASTER WARNING SW **************************************************

		// begin CDU BLANKING *****************************************************
		Action acCduBlanking = new AbstractAction("Blank left CDU for 500 ms") {
			public void actionPerformed(ActionEvent e) {
				sendToPsx(strCduBlanking);
			}
		};
		JButton buttCduBlanking = new JButton(acCduBlanking);
		// end CDU BLANKING *******************************************************

		// begin CDU SUB SYSTEM INSTALLATION **************************************
		ItemListener ilNoodle = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					sendToPsx("cduL=13<NOODLE");
					noodleEnabled = true;
				} else {
					sendToPsx("cduL=13");
					noodleEnabled = false;
				}
			}
		};
		JCheckBox cbNoodle = new JCheckBox(
				"NOODLE installed on left CDU in MENU line 3L");
		cbNoodle.addItemListener(ilNoodle);
		// end CDU SUB SYSTEM INSTALLATION ****************************************

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		panel.setLayout(new GridLayout(0, 1));
		panel.add(cbNetConn);
		panel.add(new JPanel());
		panel.add(rbIndLts_test);
		panel.add(rbIndLts_bright);
		panel.add(rbIndLts_dim);
		panel.add(new JLabel(
				"Captain's flood light potentiometer (hardware to PSX):"));
		panel.add(slFlood);
		panel.add(lab_FloodPwr);
		panel.add(cbHdg_tru);
		panel.add(buttMastWarn);
		panel.add(buttCduBlanking);
		panel.add(cbNoodle);
		panel.add(lab_CduLKey);
		panel.add(lab_Freqs);
		panel.add(lab_McpSpd);

		JFrame frame = new JFrame("Sample addon version " + version + " for PSX");
		frame.setPreferredSize(new Dimension(500, 750));
		frame.setLocation(600, 100);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (netThread != null)
					netThread.finalJobs();
			}
		});

	}

	static void setLabel_CduLKey(String s) {
		lab_CduLKey.setText(s);
	}

	static StringBuilder sbMcpSpd = new StringBuilder("McpWdoSpd: ");

	static void setLabel_McpSpd(int i) {
		if (i > 999)
			lab_McpSpd.setText("McpWdoSpd: BLANK");
		else if (i > 399)
			lab_McpSpd.setText(sbMcpSpd.delete(11, sbMcpSpd.length())
					.append("MACH .").append(i).toString());
		else
			lab_McpSpd.setText(sbMcpSpd.delete(11, sbMcpSpd.length()).append("IAS ")
					.append(i).toString());
	}

	static void setLabel_Freqs(String s) {
		lab_Freqs.setText(s);
	}

	static int startFloodVal = 0;
	static StringBuilder sbFloodPot = new StringBuilder();

	static void initFlood(int i) {
		sbFloodPot.delete(0, sbFloodPot.length()).append("Qh").append(i)
				.append('=');
		startFloodVal = sbFloodPot.length();
	}

	static void sendFlood(int i) {
		if (startFloodVal > 3) { // assure initFlood() has been done
			sbFloodPot.delete(startFloodVal, sbFloodPot.length()).append(
					i * 4713 / 270);
			sendToPsx(sbFloodPot.toString());
		}
	}

	static StringBuilder sbFloodPower = new StringBuilder(
			"Captain's flood light power (PSX to hardware): ");

	static void setHardwareFlood(String s) {
		int i = s.indexOf(';');
		i++;
		i = s.indexOf(';', i);
		i++;
		i = s.indexOf(';', i);
		i++;
		i = s.indexOf(';', i);
		i++;
		int e = s.indexOf(';', i);
		sbFloodPower.delete(47, sbFloodPower.length()).append(s.substring(i, e));
		lab_FloodPwr.setText(sbFloodPower.toString());
	}

	static void manageNoodlePage(boolean on) {
		if (noodleOn != on) { // do something only if there's an on/off event
			noodleOn = on;
			if (noodleOn && noodleEnabled) {
				Thread t = new Thread(new ThreadDrawPage());
				t.setPriority(Thread.MIN_PRIORITY);
				t.start();
			}
		}
	}

	static class ThreadDrawPage extends Thread {
		public void run() {
			StringBuilder sbNoo = new StringBuilder();
			sbNoo.append(strLcduTitle).append("_________NOODLE_________");
			sendToPsx(sbNoo.toString());
			sbNoo.delete(0, sbNoo.length()).append(strLcduLine1s).append(
					"WINE________________MILK");
			sendToPsx(sbNoo.toString());
			sbNoo.delete(0, sbNoo.length()).append(strLcduLine1b).append(
					"<SEL________________SEL>");
			sendToPsx(sbNoo.toString());
			sbNoo.delete(0, sbNoo.length()).append(strLcduLine2s).append(
					"________________________");
			sendToPsx(sbNoo.toString());
			sbNoo.delete(0, sbNoo.length()).append(strLcduLine2b).append(
					"MAKE_THIS_LINE_SMALL____-");
			sendToPsx(sbNoo.toString());
			sbNoo.delete(0, sbNoo.length()).append(strLcduLine3s).append(
					"________________________");
			sendToPsx(sbNoo.toString());
			sbNoo.delete(0, sbNoo.length()).append(strLcduLine3b).append(
					"MAKE_THIS_WORD_SMALL____++++++++++----+");
			sendToPsx(sbNoo.toString());
			sbNoo.delete(0, sbNoo.length()).append(strLcduLine4s).append(
					"________________________");
			sendToPsx(sbNoo.toString());
			sbNoo.delete(0, sbNoo.length()).append(strLcduLine4b).append(
					"________________________");
			sendToPsx(sbNoo.toString());
			sbNoo.delete(0, sbNoo.length()).append(strLcduLine5s).append(
					"________________________");
			sendToPsx(sbNoo.toString());
			sbNoo.delete(0, sbNoo.length()).append(strLcduLine5b).append(
					"________________________");
			sendToPsx(sbNoo.toString());
			sbNoo.delete(0, sbNoo.length()).append(strLcduLine6s).append(
					"------------------------");
			sendToPsx(sbNoo.toString());
			sbNoo.delete(0, sbNoo.length()).append(strLcduLine6b).append(
					"<DING______________DONG>");
			sendToPsx(sbNoo.toString());
			sbNoo.delete(0, sbNoo.length()).append(strLcduScrPad)
					.append("SCRATCHPAD");
			sendToPsx(sbNoo.toString());
		}
	}

	synchronized static void sendToPsx(String s) {
		if (netThread != null && s != null)
			netThread.send(s);
	}

}