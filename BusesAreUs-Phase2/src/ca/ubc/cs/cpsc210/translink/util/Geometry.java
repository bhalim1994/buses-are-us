package ca.ubc.cs.cpsc210.translink.util;

import org.osmdroid.util.GeoPoint;

/**
 * Compute relationships between points, lines, and rectangles represented by LatLon objects
 */
public class Geometry {
    /**
     * Return true if the point is inside of, or on the boundary of, the rectangle formed by northWest and southeast
     * @param northWest         the coordinate of the north west corner of the rectangle
     * @param southEast         the coordinate of the south east corner of the rectangle
     * @param point             the point in question
     * @return                  true if the point is on the boundary or inside the rectangle
     */
    public static boolean rectangleContainsPoint(LatLon northWest, LatLon southEast, LatLon point) {
        double nwlat;
        double nwlon;
        double selat;
        double selon;
        double lat;
        double lon;

        nwlat = northWest.getLatitude();
        nwlon = northWest.getLongitude();
        selat = southEast.getLatitude();
        selon = southEast.getLongitude();
        lat = point.getLatitude();
        lon = point.getLongitude();
        return between(selat, nwlat, lat) && between(nwlon, selon, lon);
    }

    /**
     * Return true if the rectangle intersects the line
     * @param northWest         the coordinate of the north west corner of the rectangle
     * @param southEast         the coordinate of the south east corner of the rectangle
     * @param src               one end of the line in question
     * @param dst               the other end of the line in question
     * @return                  true if any point on the line is on the boundary or inside the rectangle
     */
    public static boolean rectangleIntersectsLine(LatLon northWest, LatLon southEast, LatLon src, LatLon dst) {
        double nwlat = northWest.getLatitude();
        double nwlon = northWest.getLongitude();
        double selat = southEast.getLatitude();
        double selon = southEast.getLongitude();

        double nwlat2 = (src.getLatitude() < dst.getLatitude() ? dst.getLatitude() : src.getLatitude());
        double selat2 = (src.getLatitude() < dst.getLatitude() ? src.getLatitude() : dst.getLatitude());
        double nwlon2 = (src.getLongitude() > dst.getLongitude() ? dst.getLongitude() : src.getLongitude());
        double selon2 = (src.getLongitude() > dst.getLongitude() ? src.getLongitude() : dst.getLongitude());

        return !(selon < nwlon2 || selon2 < nwlon || nwlat < selat2 || nwlat2 < selat);
    }

    /**
     * A utility method that you might find helpful in implementing the two previous methods
     * Return true if x is >= lwb and <= upb
     * @param lwb      the lower boundary
     * @param upb      the upper boundary
     * @param x         the value in question
     * @return          true if x is >= lwb and <= upb
     */
    private static boolean between(double lwb, double upb, double x) {
        return lwb <= x && x <= upb;
    }

    /**
     * Convert LatLon to GeoPoint
     *
     * @param ll  the LatLon
     * @return  GeoPoint at same location as the LatLon
     */
    public static GeoPoint gpFromLatLon(LatLon ll) {
        return new GeoPoint(ll.getLatitude(), ll.getLongitude());
    }
}
