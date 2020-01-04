package ca.ubc.cs.cpsc210.translink.model;

import ca.ubc.cs.cpsc210.translink.model.exception.StopException;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import ca.ubc.cs.cpsc210.translink.util.SphericalGeometry;

import java.util.*;

// TODO: Task 2: Complete all the methods of this class

/**
 * Manages all bus stops.
 *
 * Singleton pattern applied to ensure only a single instance of this class that
 * is globally accessible throughout application.
 */
public class StopManager implements Iterable<Stop> {
    public static final int RADIUS = 10000;
    private static StopManager instance;
    // Use this field to hold all of the stops.
    // Do not change this field or its type, as the iterator method depends on it
    private Map<Integer, Stop> stopMap;
    private Stop selectedStop;

    /**
     * Constructs stop manager with empty collection of stops and null as the selected stop
     */
    private StopManager() {
        stopMap = new HashMap<>();
        selectedStop = null;
    }

    /**
     * Gets one and only instance of this class
     *
     * @return  instance of class
     */
    public static StopManager getInstance() {
        // Do not modify the implementation of this method!
        if (instance == null) {
            instance = new StopManager();
        }

        return instance;
    }

    public Stop getSelected() {
        return selectedStop;
    }

    /**
     * Get stop with given number, creating it and adding it to the collection of all stops if necessary.
     * If it is necessary to create a new stop, then provide it with an empty string as its name,
     * and a default location somewhere in the lower mainland as its location.
     *
     * In this case, the correct name and location of the stop will be provided later
     *
     * @param number  the number of this stop
     *
     * @return  stop with given number
     */
    //TODO: Check over code - make sure logic holds
    public Stop getStopWithNumber(int number) {
        Stop newStop = new Stop(number, "", new LatLon(49.2827, -123.1207));
        if (stopMap.containsKey(number)) {
            return stopMap.get(number);
        } else {
            stopMap.put(number, newStop);
            return newStop;
        }
    }

    /**
     * Get stop with given number, creating it and adding it to the collection of all stops if necessary,
     * using the given name and location
     *
     * @param number  the number of this stop
     * @param name   the name of this stop
     * @param locn   the location of this stop
     *
     * @return  stop with given number
     */
    //TODO: Check over code - make sure logic holds
    public Stop getStopWithNumber(int number, String name, LatLon locn) {
        Stop newStop = new Stop(number, name, locn);
        if (stopMap.containsKey(number)) {
            return stopMap.get(number);
        } else {
            stopMap.put(number, newStop);
            return newStop;
        }
    }

    /**
     * Set the stop selected by user
     *
     * @param selected   stop selected by user
     * @throws StopException when stop manager doesn't contain selected stop
     */
    public void setSelected(Stop selected) throws StopException {
        if (stopMap.containsValue(selected)) {
            selectedStop = selected;
        } else {
            throw new StopException("This stop doesn't exist!");
        }
    }

    /**
     * Clear selected stop (selected stop is null)
     */
    public void clearSelectedStop() {
        selectedStop = null;
    }

    /**
     * Get number of stops managed
     *
     * @return  number of stops added to manager
     */
    public int getNumStops() {
        return stopMap.size();
    }

    /**
     * Remove all stops from stop manager
     */
    public void clearStops() {
        stopMap.clear();
    }

    /**
     * Find nearest stop to given point.  Returns null if no stop is closer than RADIUS metres.
     *
     * @param pt  point to which nearest stop is sought
     * @return    stop closest to pt but less than RADIUS away; null if no stop is within RADIUS metres of pt
     */
    //TODO: Check over code - make sure logic holds
    public Stop findNearestTo(LatLon pt) {
        Stop nearestStop = null; //set new Stop
        //Iterate through the list to check, using the iterator (this corresponds to <Stop>)
        for (Stop nextStop : this) {
            //Need to check if distance is less than RADIUS
            if (RADIUS > SphericalGeometry.distanceBetween(pt, nextStop.getLocn())) {
                if (nearestStop == null) { //Safeguard if it's already set to a nextStop, if there is, go to line 146
                    nearestStop = nextStop; //Set null to nextStop
                } else if (SphericalGeometry.distanceBetween(pt, nearestStop.getLocn())
                        > SphericalGeometry.distanceBetween(pt, nextStop.getLocn())) {
                    nearestStop = nextStop;
                    //Set to the nextStop if the the distance of current is more than the distance of the next one
                }
            }
        }
        return nearestStop;
    }

    @Override
    public Iterator<Stop> iterator() {
        // Do not modify the implementation of this method!
        return stopMap.values().iterator();
    }
}