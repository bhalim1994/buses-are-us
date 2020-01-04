package ca.ubc.cs.cpsc210.translink.model;

import ca.ubc.cs.cpsc210.translink.util.LatLon;

// TODO: Task 2: Implement all the methods in this class

/**
 * Represents a bus having a destination, time and location that serves a particular route
 */
public class Bus {
    private Route route;
    private String dest;
    private String time;
    private LatLon location;
//    private double lat
//    private double lon

    /**
     * Constructor
     * @param route  the bus route
     * @param lat    latitude of bus
     * @param lon    longitude of bus
     * @param dest   destination
     * @param time   time at which location was recorded
     */

    //NEED TO MAKE LAT AND LON INTO LOCATION
    public Bus(Route route, double lat, double lon, String dest, String time) {
        this.route = route;
        this.dest = dest;
        this.time = time;
        this.location = new LatLon(lat, lon);
//        this.lat = lat;
//        this.lon = lon;
    }

    /**
     * Gets bus route
     * @return bus route
     */
    public Route getRoute() {
        return route;
    }

    /**
     * Gets bus location as LatLon object
     * @return bus location
     */
    public LatLon getLatLon() {
        return location;
    }

    /**
     * Gets destination
     * @return destination of this bus
     */
    public String getDestination() {
        return dest;
    }

    /**
     * Gets time bus location was recorded
     * @return  time location was recorded
     */
    public String getTime() {
        return time;
    }

}
