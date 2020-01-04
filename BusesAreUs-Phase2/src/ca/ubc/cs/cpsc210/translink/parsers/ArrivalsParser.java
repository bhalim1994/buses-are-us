package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Arrival;
import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.parsers.exception.ArrivalsDataMissingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A parser for the data returned by the Translink arrivals at a stop query.
 */
public class ArrivalsParser {

    /**
     * Parse arrivals from JSON response produced by TransLink query.  All parsed arrivals are
     * added to the given stop assuming that corresponding JSON object has a RouteNo and an
     * array of Schedules.  If RouteNo or array of Schedules is missing for a particular route,
     * then none of the arrivals for that route are added to the stop.
     * <p>
     * Each schedule must have an ExpectedCountdown, ScheduleStatus, and Destination.  If
     * any of the aforementioned elements is missing, the arrival is not added to the stop.
     *
     * @param stop         stop to which parsed arrivals are to be added
     * @param jsonResponse the JSON response produced by Translink
     * @throws JSONException                when:
     *                                      <ul>
     *                                      <li>JSON response does not have expected format (JSON syntax problem)</li>
     *                                      <li>JSON response is not an array</li>
     *                                      </ul>
     * @throws ArrivalsDataMissingException when no arrivals are added to the stop
     */
    public static void parseArrivals(Stop stop, String jsonResponse)
            throws JSONException, ArrivalsDataMissingException {
        int numberArrivalsAdded = 0;
        JSONArray routeArray = new JSONArray(jsonResponse);

        for (int i = 0; i < routeArray.length(); ++i) {
            JSONObject routeArrivalsObject = routeArray.getJSONObject(i);
            numberArrivalsAdded += parseRoute(stop, routeArrivalsObject);
        }

        if (numberArrivalsAdded == 0) {
            throw new ArrivalsDataMissingException("All arrivals are missing some information");
        }
    }

    /**
     * Parse a route object and add parsed routes to stop
     *
     * @param stop                stop to which parsed
     * @param routeArrivalsObject JSON object representing arrivals for route
     * @return number of arrivals added to stop
     */
    private static int parseRoute(Stop stop, JSONObject routeArrivalsObject) {
        int numberArrivalsAdded = 0;

        try {
            // This should be a route arrival, with RouteNo, Direction, RouteName, and Schedules fields
            String routeNo = routeArrivalsObject.getString("RouteNo");
            Route route = RouteManager.getInstance().getRouteWithNumber(routeNo);

            JSONArray arrivalsArray = routeArrivalsObject.getJSONArray("Schedules");
            numberArrivalsAdded += parseArrivalsForRoute(arrivalsArray, stop, route);
        } catch (JSONException e) {
            // Missing required data (RouteNo or Schedules) so ignore arrivals in this route object
        }

        return numberArrivalsAdded;
    }

    /**
     * Parse arrivals for a route and add them to stop
     *
     * @param arrivalsArray JSON array containing arrivals data for route
     * @param stop          stop to which arrivals are to be added
     * @param route         route on which arrivals are operating
     * @return number of arrivals added to stop
     */
    private static int parseArrivalsForRoute(JSONArray arrivalsArray, Stop stop, Route route) {
        int numberArrivalsAdded = 0;

        for (int i = 0; i < arrivalsArray.length(); i++) {
            try {
                JSONObject arrivalObject = arrivalsArray.getJSONObject(i);
                stop.addArrival(parseArrival(arrivalObject, route));
                numberArrivalsAdded++;
            } catch (JSONException e) {
                // don't add arrival to stop
            }
        }

        return numberArrivalsAdded;
    }

    /**
     * Parse an arrival from JSON object
     *
     * @param arrivalObject JSON object representing the arrival
     * @param route         the route on which the expected arrival is running
     * @return the parsed arrival
     * @throws JSONException if JSON object is missing any one of ExpectedCountdown, ScheduleStatus, and Destination
     */
    private static Arrival parseArrival(JSONObject arrivalObject, Route route) throws JSONException {
        int timeToStop = arrivalObject.getInt("ExpectedCountdown");
        String destination = arrivalObject.getString("Destination");
        String status = arrivalObject.getString("ScheduleStatus");
        Arrival arrival = new Arrival(timeToStop, destination, route);
        arrival.setStatus(status);
        return arrival;
    }
}
