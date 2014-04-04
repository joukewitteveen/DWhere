package nl.joukewitteveen.dwhere;

import javax.microedition.location.*;


public class GPS {
	private Log log;
	private LocationProvider lp;

	public GPS(Log _log) {
		log = _log;
		try {
			lp = LocationProvider.getInstance(null);
			// Enforce permission
			getPosition();
			log.log("Instantiated location provider");
		} catch (Exception e) {
			log.log("Error: No location provider");
			log.log("> " + e.getMessage());
		}
	}

	public TrackPoint getPosition() {
		try {
			return new TrackPoint(lp.getLocation(-1));
		} catch (Exception e) {
			log.log("Error: Could not get position");
			log.log("> " + e.getMessage());
			return null;
		}
	}
}


class TrackPoint extends Coordinates {
	private final long timestamp;

	public TrackPoint(Location location) {
		this(location.getQualifiedCoordinates(), location.getTimestamp());
	}

	public TrackPoint(Coordinates coordinates, long _timestamp) {
		super(coordinates.getLatitude(), coordinates.getLongitude(), coordinates.getAltitude());
		timestamp = _timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}
}


class Portage extends Coordinates {
	public final String name;

	public Portage(double latitude, double longitude, String _name) {
		super(latitude, longitude, Float.NaN);
		name = _name;
	}

	public float distanceKm(Coordinates to) {
		return ((int) (this.distance(to) / 10F + 0.5F)) / 100F;
	}

	public static int nextPortage(Coordinates pos) {
		double lat = pos.getLatitude(),
			   lon = pos.getLongitude();
		int i;
		for (i = 1; i < DW.length; i++) {
			if (DW[i].getLongitude() >= lon) break;
		}
		switch (i) {
		case 56:	// Blake's weir
			if (lat < DW[55].getLatitude()) i = 55;
			break;
		case 59:	// Shiplake
			if (lat > DW[60].getLatitude()) i = 61;
			else if (lat > DW[59].getLatitude()) i = 60;
			break;
		case 61:	// Hambledon
		case 62:	// Hurley
			if (lat < DW[60].getLatitude()) i = 60;
			break;
		case 65:	// Cookham
			if (lat < DW[66].getLatitude()) i = 67;
			else if (lat < DW[65].getLatitude()) i = 66;
			break;
		case 67:	// Bray
		case 68:	// Boveney
			if (lat > DW[66].getLatitude()) i = 66;
			break;
		case 70:	// Old Windsor
			if (lat < DW[70].getLatitude()) i = 71;
			break;
		case 73:	// Chertsey weir
			if (lat < DW[73].getLatitude()) i = 74;
			break;
		case 77:	// Teddington
		case 78:	// the finish
			if (lat > DW[77].getLatitude()) i = 78;
			else i = 77;
			break;
		}
		return i;
	}

	public static final Portage[] DW = {
		null,
		new Portage(51.36503, -1.71642, "Wootton Bottom lock"),
		new Portage(51.36721, -1.71171, "Heathey Close lock"),
		new Portage(51.36937, -1.70083, "Brimslade lock"),
		new Portage(51.36956, -1.69717, "Wootton Top lock"),
		new Portage(51.35910, -1.64438, "Crofton Top lock"),		// 5
		new Portage(51.35882, -1.64054, "Crofton lock"),
		new Portage(51.35840, -1.63598, "Crofton lock"),
		new Portage(51.35761, -1.63203, "Crofton lock"),
		new Portage(51.35748, -1.62830, "Crofton lock"),
		new Portage(51.35854, -1.62498, "Crofton lock"),			// 10
		new Portage(51.36052, -1.62229, "Crofton lock"),
		new Portage(51.36472, -1.61573, "Crofton lock"),
		new Portage(51.36725, -1.61150, "Crofton Bottom lock"),
		new Portage(51.37546, -1.60182, "Bedwyn Church lock"),
		new Portage(51.38226, -1.59446, "Burnt Mill lock"),			// 15
		new Portage(51.38750, -1.58688, "Potters lock"),
		new Portage(51.39189, -1.58379, "Little Bedwyn lock"),
		new Portage(51.40249, -1.57112, "Oakhill Down lock"),
		new Portage(51.40482, -1.56862, "Froxfield Middle lock"),
		new Portage(51.40736, -1.56564, "Froxfield Lower lock"),	// 20
		new Portage(51.41102, -1.54832, "Picketfield lock"),
		new Portage(51.41403, -1.53945, "Cobbler's lock"),
		new Portage(51.41484, -1.53252, "Hungerford Marsh lock"),
		new Portage(51.41686, -1.51815, "Hungerford lock"),
		new Portage(51.41171, -1.49622, "Dunmill lock"),			// 25
		new Portage(51.41046, -1.48014, "Wire lock"),
		new Portage(51.40598, -1.46771, "Brunsden lock"),
		new Portage(51.40210, -1.44708, "Kintbury lock"),
		new Portage(51.40285, -1.41175, "Dreweat's lock"),
		new Portage(51.40080, -1.40207, "Copse lock"),				// 30
		new Portage(51.40089, -1.39279, "Hamstead lock"),
		new Portage(51.39640, -1.37017, "Benham lock"),
		new Portage(51.39801, -1.35800, "Higg's lock"),
		new Portage(51.39938, -1.34764, "Guyer's lock"),
		new Portage(51.40146, -1.32554, "Newbury lock"),			// 35
		new Portage(51.40301, -1.31241, "Greenham lock"),
		new Portage(51.40224, -1.30132, "Ham lock"),
		new Portage(51.39720, -1.28529, "Bull's lock"),
		new Portage(51.39327, -1.27146, "Widmead lock"),
		new Portage(51.39257, -1.24825, "Monkey Marsh lock"),		// 40
		new Portage(51.39363, -1.22659, "Colthrop lock"),
		new Portage(51.39283, -1.20984, "Midgham lock"),
		new Portage(51.39329, -1.19404, "Old Heale's lock"),
		new Portage(51.39538, -1.18032, "Old Woolhampton lock"),
		new Portage(51.40014, -1.13782, "Aldermaston wharf lock"),	// 45
		new Portage(51.40067, -1.13058, "Padworth lock"),
		new Portage(51.40773, -1.12448, "Towney lock"),
		new Portage(51.41843, -1.10070, "Tyle Mill lock"),
		new Portage(51.42462, -1.08501, "Sulhamstead lock"),
		new Portage(51.43082, -1.06904, "Sheffield lock"),			// 50
		new Portage(51.43189, -1.05822, "Garston lock"),
		new Portage(51.43323, -1.03257, "Burghfield lock"),
		new Portage(51.43598, -1.00472, "Southcote lock"),
		new Portage(51.43447, -0.98671, "Fobney lock"),
		new Portage(51.45087, -0.97391, "County weir lock"),		// 55
		new Portage(51.45598, -0.95532, "Blake's weir lock"),
		new Portage(51.46000, -0.94299, "Dreadnought reach"),
		new Portage(51.47278, -0.91833, "Sonning lock"),
		new Portage(51.50158, -0.88335, "Shiplake lock"),
		new Portage(51.52891, -0.88551, "Marsh lock"),				// 60
		new Portage(51.56037, -0.87384, "Hambledon lock"),
		new Portage(51.55073, -0.81071, "Hurley lock"),
		new Portage(51.55201, -0.79447, "Temple lock"),
		new Portage(51.56748, -0.76918, "Marlow lock"),
		new Portage(51.56139, -0.69543, "Cookham lock"),			// 65
		new Portage(51.53571, -0.69872, "Boulters lock island"),
		new Portage(51.50985, -0.69047, "Bray lock"),
		new Portage(51.49089, -0.64158, "Boveney lock"),
		new Portage(51.49107, -0.60441, "Romney lock"),
		new Portage(51.46381, -0.56908, "Old Windsor lock"),		// 70
		new Portage(51.43861, -0.53874, "Bell weir lock"),
		new Portage(51.41502, -0.50105, "Penton Hook lock"),
		new Portage(51.39119, -0.48616, "Chertsey weir lock"),
		new Portage(51.38195, -0.45917, "Shepperton lock"),
		new Portage(51.40515, -0.40685, "Sunbury lock"),			// 75
		new Portage(51.40518, -0.34649, "Molesey lock"),
		new Portage(51.43094, -0.32299, "Teddington lock"),
		new Portage(51.50066, -0.12045, "the finish")
	};
}
