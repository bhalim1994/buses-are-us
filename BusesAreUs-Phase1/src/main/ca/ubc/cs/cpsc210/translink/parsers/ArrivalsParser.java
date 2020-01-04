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
 * A parser for the data returned by the Translink arrivals at a stop query
 */
public class ArrivalsParser {

    /**
     * Parse arrivals from JSON response produced by TransLink query.  All parsed arrivals are
     * added to the given stop assuming that corresponding JSON object has a RouteNo and an
     * array of Schedules.  If RouteNo or array of Schedules is missing for a particular route,
     * then none of the arrivals for that route are added to the stop.
     *
     * Each schedule must have an ExpectedCountdown, ScheduleStatus, and Destination.  If
     * any of the aforementioned elements is missing, the arrival is not added to the stop.
     *
     * @param stop             stop to which parsed arrivals are to be added
     * @param jsonResponse    the JSON response produced by Translink
     * @throws JSONException  when:
     * <ul>
     *     <li>JSON response does not have expected format (JSON syntax problem)</li>
     *     <li>JSON response is not an array</li>
     * </ul>
     * @throws ArrivalsDataMissingException  when no arrivals are added to the stop
     */
    public static void parseArrivals(Stop stop, String jsonResponse)
            throws JSONException, ArrivalsDataMissingException {
        JSONArray arrivals = new JSONArray(jsonResponse);
        for (int index = 0; index < arrivals.length(); index++) {
            JSONObject arrival = arrivals.getJSONObject(index);
            if (checkArrivalKey(arrival)) {
                String routeNumber = arrival.getString("RouteNo");
                JSONArray schedules = arrival.getJSONArray("Schedules");
                Route route = RouteManager.getInstance().getRouteWithNumber(routeNumber);
                parseArrival(schedules, route, stop);
            } else {
                throw new ArrivalsDataMissingException();
            }
        }
    }

    private static boolean checkArrivalKey(JSONObject arrival) {
        if (arrival.has("RouteNo")) {
            return (arrival.has("Schedules"));
        }
        return false;
    }

    private static void parseArrival(JSONArray schedules, Route route, Stop stop) throws
            JSONException {
        for (int index = 0; index < schedules.length(); index++) {
            JSONObject schedule = schedules.getJSONObject(index);
            if (checkScheduleKeys(schedule)) {
                Integer expectedCountdown = schedule.getInt("ExpectedCountdown");
                String destination = schedule.getString("Destination");
                String scheduleStatus = schedule.getString("ScheduleStatus");
                Arrival tempArrivalSchedule = new Arrival(expectedCountdown, destination, route);
                tempArrivalSchedule.setStatus(scheduleStatus);
                stop.addArrival(tempArrivalSchedule);
            }
            //NOT SURE HOW TO THROW EXCEPTION ON WHEN YOU DON'T ADD ONE ARRIVAL? ASK TA HOW TO GO ABOUT DOING THAT
        }
    }

    private static boolean checkScheduleKeys(JSONObject schedule) {
        if (schedule.has("ExpectedCountdown")) {
            if (schedule.has("Destination")) {
                return (schedule.has("ScheduleStatus"));
            }
        }
        return false;
    }
}