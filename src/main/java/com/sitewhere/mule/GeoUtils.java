package com.sitewhere.mule;

/**
 * Geographic utility methods.
 * 
 * @author Derek Adams
 */
public class GeoUtils {

	public static final double DEG_RAD = Math.PI / 180;

	public static final int MAX_LONGITUDE = 67108864;

	public static final int MAX_LATITUDE = 67108864;

	private static final double C_LONGITUDE = 360 / MAX_LONGITUDE;

	private static final double C_LATITUDE2 = MAX_LATITUDE / 2;

	/**
	 * Convert longitude to an x coordinate.
	 * 
	 * @param longitude
	 * @return
	 */
	public static int lon2x(double longitude) {
		return (int) ((longitude + 180) / C_LONGITUDE);
	}

	/**
	 * Convert latitude to a y coordinate.
	 * 
	 * @param latitude
	 * @return
	 */
	public static int lat2y(double latitude) {
		double latRad = latitude * DEG_RAD;
		return (int) ((1 - Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI) * C_LATITUDE2);
	}
}