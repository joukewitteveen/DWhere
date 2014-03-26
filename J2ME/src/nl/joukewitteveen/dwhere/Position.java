package nl.joukewitteveen.dwhere;

import java.util.*;


public class Position {
	private static int DefaultPeriod = 300;
	private Log log;
	private SMS sms;
	private GPS gps;
	private Enumeration locks;
	private int target;
	private int period;

	public Position(Log _log, SMS _sms, GPS _gps) {
		log = _log;
		sms = _sms;
		gps = _gps;
		target = 0;
	}

	public void start(Vector _locks) {
		TrackPoint position, lastPosition;
		locks = _locks.elements();
		if (!nextTarget()) {
			log.log("No target locks");
			return;
		}
		lastPosition = gps.getPosition();
		if (lastPosition == null) {
			lastPosition = gps.getPosition();
			if (lastPosition == null) return;
		}
		while (target > 0) {
			sleep();
			position = gps.getPosition();
			if (position != null) {
				run(lastPosition, position);
				lastPosition = position;
			}
		}
		log.log("Done");
	}

	private void run(TrackPoint lastPosition, TrackPoint position) {
		if (position.distance(lastPosition) == 0) return;
		int timespan = (int) ((position.getTimestamp() - lastPosition.getTimestamp()) / 1000);
		int estimate = (int) (position.distance(Portage.DW[target]) / position.distance(lastPosition) * timespan);
		int next = Portage.nextPortage(position);
		if (next == -1) next = Portage.DW.length;

		if (next > target) {
			sms.send(position.distanceKm(Portage.DW[target]) + " km after " + Portage.DW[target].name, position);
			while (nextTarget() && next > target);
		} else if (estimate - 2 <= timespan / 2 && period < DefaultPeriod) {
			sms.send(position.distanceKm(Portage.DW[target]) + " km before " + Portage.DW[target].name, position);
			nextTarget();
		} else if (estimate / 2 < period) {
			period = estimate / 2;
		}
	}

	private boolean nextTarget() {
		if (!locks.hasMoreElements()){
			target = -1;
			return false;
		}
		String lock = (String) locks.nextElement();
		int next;
		try {
			next = Integer.parseInt(lock);
			if (next <= target || next >= Portage.DW.length) throw new IndexOutOfBoundsException();
		} catch (Exception e) {
			log.log("Invalid target lock: " + lock);
			return nextTarget();
		}
		target = next;
		period = DefaultPeriod;
		return true;
	}

	private void sleep() {
		try {
			log.log("Sleeping for " + period + " seconds");
			Thread.sleep(period * 1000L);
		} catch (Exception e) {
			log.log("Error: Could not sleep");
			log.log("> " + e.getMessage());
		}
	}
}
