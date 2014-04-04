package nl.joukewitteveen.dwhere;

import java.util.*;


public class Interval extends TimerTask {
	private Log log;
	private SMS sms;
	private GPS gps;

	public Interval(Log _log, SMS _sms, GPS _gps) {
		log = _log;
		sms = _sms;
		gps = _gps;
	}

	public void start(long delay, long interval) {
		new Timer().scheduleAtFixedRate(this, delay, interval);
	}

	public void run() {
		TrackPoint pos = gps.getPosition();
		if (pos == null) {
			pos = gps.getPosition();
			if (pos == null) return;
		}
		int next = Portage.nextPortage(pos);
		if (next == Portage.DW.length) {
			log.log("Error: Position beyond the finish");
			return;
		}
		sms.send(Portage.DW[next].distanceKm(pos) + " km before " + Portage.DW[next].name, pos);
	}
}
