package de.hs_mannheim.ffi.vs.syslog.model;

import java.time.Instant;

/**
 * Am Originator class to generate and set the data with the RFC 5424 standards
 * to be send from the client to the server.
 * 
 * 
 */
public class ContentOriginator {
	/**
	 * Message containing all information to send by the client to the server.
	 */
	private String MSG;
	/**
	 * STRUCTURED-DATA. here used - as it refers to null
	 */
	private static final String STRUCTUREDDATA = "-";
	/**
	 * the Version.
	 */
	private static final int VERSION = 1;
	/**
	 * PROCID describes the process name or ID. here used - as it refers to null.
	 */
	private static final String PROCID = "-";
	/**
	 * the App Name. here used "ClientApp" as it refers to client.
	 */
	private static String APPNAME = "ClientApp";
	/**
	 * Host name originated from host "mymachine.example.com". here used
	 * "Clienthost" as it refers to client.
	 */
	private static String HOSTNAME = "Clienthost";
	/**
	 * MSGID SHOULD identify the type of message.
	 */
	private static final String MSGID = "UDPIN";
	private String timeStamp;
	/**
	 * Facility a value to be used to calculate the priority and MUST be in the
	 * range of 0 to 23 inclusive.
	 */
	private int facility;
	/**
	 * Severity a value to be used to calculate the priority and MUST be in the
	 * range of 0 to 7 inclusive.
	 */
	private int severity;
	/**
	 * Priority known as the PRIVAL value it is used inside the <>
	 */
	private int priority;


	/**
	 * construct a ContentOriginator object.
	 * 
	 * @param args contains the facility at index 0, the severity at index 1 and the
	 *             message at index 2.
	 */
	public ContentOriginator(String[] args) {
		if (args.length != 3 || args == null) {
			throw new IllegalArgumentException("The number of Arguments is not correct");
		}
		generateTime();
		if (checkSeverityValidity(args[1]) && checkFacilityValidity(args[0])) {
			this.facility = convertStringToInt(args[0]);
			this.severity = convertStringToInt(args[1]);
		}
		calculatePriority();
		if (args[2].isEmpty() || args[2].trim().isEmpty()) {
			throw new IllegalArgumentException("The Arguments contains an Error. recieved an empty message");
		}

		this.MSG = args[2];
	}

	/**
	 * generate the current Time and set the timeStamp attribute to the current Time
	 * .
	 */
	private void generateTime() {
		this.timeStamp = Instant.now().toString();
	}

	/**
	 * The Priority value is calculated by multiplying the numerical value of
	 * Facility by 8 and then adding the numerical value of the Severity.
	 */
	private void calculatePriority() {
		this.priority = this.facility * 8 + severity;
	}

	/**
	 * checks if the Severity value is valid.
	 * 
	 * @param input string containing the Severity value
	 * @return true if valid . otherwise false
	 */
	private boolean checkSeverityValidity(String input) {
		if (onlyDigits(input) && (input.length() == 1) && (checkSeverityRange(convertStringToInt(input)))) {
			return true;
		}
		throw new IllegalArgumentException("The Arguments contains an Error");
	}

	/**
	 * checks the Severity value range,which must be in the range of 0 to 7
	 * inclusive.
	 * 
	 * @return true if it's in the range , otherwise false
	 */
	private Boolean checkSeverityRange(int input) {
		if (input <= 7 && input >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * checks if the Facility value is valid.
	 * 
	 * @param input string containing the Facility value
	 * @return true if valid . otherwise false
	 */
	private boolean checkFacilityValidity(String input) {
		if (onlyDigits(input) && (input.length() == 1 || input.length() == 2)) {
			if (checkFacilityRange(convertStringToInt(input))) {
				return true;
			}
		}
		throw new IllegalArgumentException("The Arguments contains an Error");
	}

	/**
	 * checks the Severity value range, which MUST be in the range of 0 to 23
	 * inclusive.
	 * 
	 * @return true if it's in the range , otherwise false
	 */
	private Boolean checkFacilityRange(int input) {
		if (input <= 23 && input >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * search the string passed in the arguments for any characters but numbers.
	 * 
	 * @return true if the string contains only Digits. otherwise throws an
	 *         IllegalArgumentException
	 */
	public static boolean onlyDigits(String str) {
		if (!(str.length() == 1 || str.length() == 2)) {
			throw new IllegalArgumentException("The Arguments contains an Error");
		}
		for (int i = 0; i < str.length(); i++) {
			if (!((str.charAt(i) >= '0') && (str.charAt(i) <= '9'))) {
				throw new IllegalArgumentException("The Arguments contains an Error");
			}
		}
		return true;
	}

	/**
	 * convert a String to int.
	 * 
	 * @param input number as java string
	 * @return the number passed on in the arguments as int
	 */
	private static int convertStringToInt(String input) {
		int number = 0;
		try {
			number = Integer.parseInt(input);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		return number;
	}

	/**
	 * returns the message as a String with the RFC 5424 standards.
	 */
	public String toString() {
		String msg = "<" + this.priority + ">" + VERSION + " " + this.timeStamp + " " + HOSTNAME + " " + APPNAME + " "
				+ PROCID + " " + MSGID + " " + STRUCTUREDDATA + " " + "BOM" + this.MSG;
		return msg;
	}

}