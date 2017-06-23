/* AddonExampleNetThread.java is a class used by AddonExample.java
 * See AddonExample.java for more details.
 */

import java.io.*;
import java.net.*;

class AddonExampleNetThread extends Thread {

	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter ou = null;
	private boolean remoteExit;
	private String hostName = "localhost";
	private int portNumber = 10747;

	AddonExampleNetThread(String hn, int pn) {
		hostName = hn;
		portNumber = pn;
	}

	public void run() {

		try {
			socket = new Socket(hostName, portNumber);
			ou = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.out.println(e);
			return;
		} catch (IOException e) {
			System.out.println(e);
			return;
		}

		// *********************************************************************
		// Reader:

		try {

			String received;
			char qCategory;
			int qIndex;
			int val;
			int parseMark;

			while (true) {

				if ((received = in.readLine()) != null) {
					try {
						if (received.charAt(0) == 'Q') {
							parseMark = received.indexOf('=');
							try {
								qIndex = Integer.parseInt(received.substring(2, parseMark));
								qCategory = received.charAt(1);
								parseMark++;

								if (qCategory == 's') {

									if (qIndex == AddonExample.qsFreqsAntenna) {
										AddonExample.setLabel_Freqs("FreqsAntenna: "
												+ received.substring(parseMark).trim());
									}
									if (qIndex == AddonExample.qsLtVoltages) {
										AddonExample.setHardwareFlood(received.substring(parseMark)
												.trim());
									}
									if (qIndex == AddonExample.qsCdusActSubsys) {
										AddonExample.manageNoodlePage(received.substring(parseMark,
												parseMark + 2).equals("13"));
									}

								} else if (qCategory == 'i') {

									if (qIndex == AddonExample.qiMcpWdoSpd) {
										val = Integer
												.parseInt(received.substring(parseMark).trim());
										AddonExample.setLabel_McpSpd(val);
									}

								} else if (qCategory == 'h') {

									val = Integer.parseInt(received.substring(parseMark).trim());
									if (qIndex == AddonExample.qhKeybCduL) {
										AddonExample.setLabel_CduLKey("KeybCduL: " + val);
									} else if (qIndex == AddonExample.mastWarnCaptain.qIndex) {
										AddonExample.mastWarnCaptain.transfer(
												AddonExample.FROM_PSX, val);
									}

								}

							} catch (NumberFormatException nfe) {
								nfe.printStackTrace();
							}

						} else if (received.charAt(0) == 'L') { // Lexicon (at net connect)

							// System.out.println(received);
							parseMark = received.indexOf('(');
							try {
								qCategory = received.charAt(1);
								qIndex = Integer.parseInt(received.substring(2, parseMark));
								parseMark = received.indexOf('=') + 1;

								if (qCategory == 's') {

									if (received.substring(parseMark).equals("LcduTitle")) {
										AddonExample.strLcduTitle = "Qs" + qIndex + '=';
									}
									if (received.substring(parseMark).equals("LcduLine1s")) {
										AddonExample.strLcduLine1s = "Qs" + qIndex + '=';
									}
									if (received.substring(parseMark).equals("LcduLine1b")) {
										AddonExample.strLcduLine1b = "Qs" + qIndex + '=';
									}
									if (received.substring(parseMark).equals("LcduLine2s")) {
										AddonExample.strLcduLine2s = "Qs" + qIndex + '=';
									}
									if (received.substring(parseMark).equals("LcduLine2b")) {
										AddonExample.strLcduLine2b = "Qs" + qIndex + '=';
									}
									if (received.substring(parseMark).equals("LcduLine3s")) {
										AddonExample.strLcduLine3s = "Qs" + qIndex + '=';
									}
									if (received.substring(parseMark).equals("LcduLine3b")) {
										AddonExample.strLcduLine3b = "Qs" + qIndex + '=';
									}
									if (received.substring(parseMark).equals("LcduLine4s")) {
										AddonExample.strLcduLine4s = "Qs" + qIndex + '=';
									}
									if (received.substring(parseMark).equals("LcduLine4b")) {
										AddonExample.strLcduLine4b = "Qs" + qIndex + '=';
									}
									if (received.substring(parseMark).equals("LcduLine5s")) {
										AddonExample.strLcduLine5s = "Qs" + qIndex + '=';
									}
									if (received.substring(parseMark).equals("LcduLine5b")) {
										AddonExample.strLcduLine5b = "Qs" + qIndex + '=';
									}
									if (received.substring(parseMark).equals("LcduLine6s")) {
										AddonExample.strLcduLine6s = "Qs" + qIndex + '=';
									}
									if (received.substring(parseMark).equals("LcduLine6b")) {
										AddonExample.strLcduLine6b = "Qs" + qIndex + '=';
									}
									if (received.substring(parseMark).equals("LcduScrPad")) {
										AddonExample.strLcduScrPad = "Qs" + qIndex + '=';
									}
									if (received.substring(parseMark).equals("CdusActSubsys")) {
										AddonExample.qsCdusActSubsys = qIndex;
									}
									if (received.substring(parseMark).equals("FreqsAntenna")) {
										AddonExample.qsFreqsAntenna = qIndex;
									}
									if (received.substring(parseMark).equals("LtVoltages")) {
										AddonExample.qsLtVoltages = qIndex;
									}

								} else if (qCategory == 'i') {

									if (received.substring(parseMark).equals("McpWdoSpd")) {
										AddonExample.qiMcpWdoSpd = qIndex;
									}
									if (received.substring(parseMark).equals("BlankTimeCduL")) {
										AddonExample.strCduBlanking = "Qi" + qIndex + "=500";
									}

								} else if (qCategory == 'h') {

									if (received.substring(parseMark).equals("LcpFloodCp")) {
										AddonExample.initFlood(qIndex);
									}
									if (received.substring(parseMark).equals("MastWarnCp")) {
										AddonExample.mastWarnCaptain.init(qIndex);
									}
									if (received.substring(parseMark).equals("HdgTru")) {
										AddonExample.strHdg_tru = "Qh" + qIndex + "=1";
										AddonExample.strHdg_norm = "Qh" + qIndex + "=0";
									}
									if (received.substring(parseMark).equals("LtIndTest")) {
										AddonExample.strIndLts_test = "Qh" + qIndex + "=1";
										AddonExample.strIndLts_bright = "Qh" + qIndex + "=0";
										AddonExample.strIndLts_dim = "Qh" + qIndex + "=-1";
									}
									if (received.substring(parseMark).equals("KeybCduL")) {
										AddonExample.qhKeybCduL = qIndex;
									}

								}

							} catch (NumberFormatException nfe) {
								nfe.printStackTrace();
							}

						} else if (received.substring(0, 3).equals("id=")) {

							try {
								System.out.println("Connection OK. Our client id: " + received);
							} catch (NumberFormatException nfe) {
								nfe.printStackTrace();
							}

						} else if (received.length() > 8
								&& received.substring(0, 8).equals("version=")) {
							// Check version agreement if required
						} else if (received.equals("load1")) {
							// Situation loading phase 1 (paused and reading variables)
						} else if (received.equals("load2")) {
							// Situation loading phase 2 (reading model options)
						} else if (received.equals("load3")) {
							// Situation loading phase 3 (unpaused)
						} else if (received.equals("exit")) {
							remoteExit = true;
							break;
						} else if (received.startsWith("metar=")) {
							// METAR feeder status message
						}
					} catch (StringIndexOutOfBoundsException sioobe) {
						sioobe.printStackTrace();
					}
				}
			}

		} catch (IOException e) {
		}
		finalJobs();
	}

	void finalJobs() {
		try {
			if (!remoteExit && ou != null) {
				ou.println("exit");
				try {
					sleep(20);
				} catch (InterruptedException e) {
				}
				ou.close();
			}
			if (in != null)
				in.close();
			if (socket != null)
				socket.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	void send(String s) {
		if (ou != null) {
			ou.println(s);
			if (ou.checkError())
				finalJobs();
		}
	}

}