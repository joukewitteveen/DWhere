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
	}

	public void start(Vector _locks) {
		TrackPoint position, lastPosition;
		locks = _locks.elements();
		lastPosition = gps.getPosition();
		if (lastPosition == null) {
			lastPosition = gps.getPosition();
			if (lastPosition == null) return;
		}
		for (nextTarget(Portage.nextPortage(lastPosition)); target > 0; lastPosition = position) {
			do {
				sleep();
				position = gps.getPosition();
			} while (position == null);
			run(lastPosition, position);
		}
		log.log("Done");
	}

	private void run(TrackPoint lastPosition, TrackPoint position) {
		if (position.distance(lastPosition) == 0) return;
		int timespan = (int) ((position.getTimestamp() - lastPosition.getTimestamp()) / 1000);
		int estimate = (int) (position.distance(Portage.DW[target]) / position.distance(lastPosition) * timespan);
		int next = Portage.nextPortage(position);

		if (next > target) {
			sms.send(Portage.DW[target].distanceKm(position) + " km after " + Portage.DW[target].name, position);
			nextTarget(next);
		} else if (estimate - 2 <= timespan / 2 && period < DefaultPeriod) {
			sms.send(Portage.DW[target].distanceKm(position) + " km before " + Portage.DW[target].name, position);
			nextTarget(next + 1);
		} else if (estimate / 2 < period) {
			period = estimate / 2;
		}
	}

	private void nextTarget(int minimum) {
		while (locks.hasMoreElements()) {
			String lock = (String) locks.nextElement();
			try {
				target = Integer.parseInt(lock);
				if (target < minimum || target >= Portage.DW.length) throw new IndexOutOfBoundsException();
				period = DefaultPeriod;
				return;
			} catch (Exception e) {
				log.log("Invalid target lock: " + lock);
			}
		}
		target = -1;
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
