package nl.joukewitteveen.dwhere;

import java.util.*;
import javax.microedition.io.Connector;
import javax.wireless.messaging.*;


public class SMS {
	private static Calendar calendar = Calendar.getInstance();
	private Log log;
	private Vector recipients;
	private MessageConnection conn;
	private TextMessage text;

	public SMS(Log _log, Vector _recipients) {
		log = _log;
		recipients = _recipients;
		Enumeration to = recipients.elements();
		while(to.hasMoreElements()) {
			try {
		        conn = (MessageConnection) Connector.open("sms://" + to.nextElement());
		        text = (TextMessage) conn.newMessage(MessageConnection.TEXT_MESSAGE);
		        text.setPayloadText("DWhere subscription started");
		        conn.send(text);
		        conn.close();
		    } catch (Exception e) {
				log.log("Error: Could not send text");
				log.log("> " + e.getMessage());
			}
		}
	}

	public void send(String msg, TrackPoint pos) {
		Enumeration to = recipients.elements();
		calendar.setTime(new Date(pos.getTimestamp()));
		msg = calendar.get(Calendar.HOUR_OF_DAY) + ":" + twoDigits(calendar.get(Calendar.MINUTE)) + " - " + msg;
		while(to.hasMoreElements()) {
			try {
		        conn = (MessageConnection) Connector.open("sms://" + to.nextElement());
		        text = (TextMessage) conn.newMessage(MessageConnection.TEXT_MESSAGE);
		        text.setPayloadText(msg +
		        		" \nhttp://maps.google.com/?q=" + fiveDecimals(pos.getLatitude()) + "," + fiveDecimals(pos.getLongitude()));
		        conn.send(text);
		        conn.close();
		    } catch (Exception e) {
				log.log("Error: Could not send text");
				log.log("> " + e.getMessage());
			}
		}
		log.log(msg);
	}

	public static String twoDigits(int number) {
		if (number >= 10) {
			return Integer.toString(number);
		} else if (number > 0) {
			return "0" + number;
		}
		return "00";
	}

	public static String fiveDecimals(double number) {
		if (number < 0) return "-" + fiveDecimals(-number);
		int i = (int) (number * 1E5 + 0.5D);
		String d = "0000" + (i % 100000);
		return (i / 100000) + "." + d.substring(d.length() - 5);
	}
}
