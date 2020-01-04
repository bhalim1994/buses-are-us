package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.parsers.exception.StopDataMissingException;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import ca.ubc.cs.cpsc210.translink.util.LatLon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * A parser for the data returned by Translink stops query.
 */
public class StopParser {

    private String filename;

    public StopParser(String filename) {
        this.filename = filename;
    }

    /**
     * Parse stop data from the file and add all stops to stop manager.
     */
    public void parse() throws IOException, StopDataMissingException, JSONException {
        DataProvider dataProvider = new FileDataProvider(filename);

        parseStops(dataProvider.dataSourceToString());
    }

    /**
     * Parse stop information from JSON response produced by Translink.
     * Stores all stops and routes found in the StopManager and RouteManager.
     *
     * @param jsonResponse string encoding JSON data to be parsed
     * @throws JSONException when:
     * <ul>
     *    <li>JSON data does not have expected format (JSON syntax problem)</li>
     *    <li>JSON data is not an array</li>
     * </ul>
     * @throws StopDataMissingException when
     * <ul>
     *    <li> JSON data is missing Name, StopNo, Routes or location (Latitude or Longitude)
     *                                  elements for any stop.</li>
     * </ul>
     * If a StopDataMissingException is thrown, all stops for which all required data is available
     *                                  are first added to the stop manager.
     */
    private void parseStops(String jsonResponse)
            throws JSONException, StopDataMissingException {
        JSONArray stops = new JSONArray(jsonResponse);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < stops.length(); ++i) {
            JSONObject onestop = stops.getJSONObject(i);
            parseStop(sb, onestop);
        }

        if (sb.length() > 0) {
            throw new StopDataMissingException("Missing required data about stops: " + sb.toString());
        }
    }

    /**
     * Parse a stop and add routes to stop.
     *
     * @param stringBuilder string builder to store numbers of stops not parsed
     * @param stopObject    JSON object representing stop to be parsed
     */
    private void parseStop(StringBuilder stringBuilder, JSONObject stopObject) {
        int stopNo = 0;
        try {
            stopNo = stopObject.getInt("StopNo");
            String stopName = stopObject.getString("Name");
            double stopLat = stopObject.getDouble("Latitude");
            double stopLon = stopObject.getDouble("Longitude");
            String routes = stopObject.getString("Routes");
            addRoutesToStop(stopNo, stopName, stopLat, stopLon, routes);
        } catch (JSONException e) {
            stringBuilder.append(stopNo != 0 ? stopNo : "unnumbered route");
            stringBuilder.append(" ");
        }
    }

    /**
     * Add routes to stop.
     *
     * @param stopNo   the stop number
     * @param stopName the stop name
     * @param stopLat  latitude of stop
     * @param stopLon  longitude of stop
     * @param routes   routes to be added to stop (as string)
     */
    private void addRoutesToStop(int stopNo, String stopName, double stopLat, double stopLon, String routes) {
        String[] routearray = routes.split(", *");
        Stop s = StopManager.getInstance().getStopWithNumber(stopNo, stopName, new LatLon(stopLat, stopLon));
        for (String route : routearray) {
            Route r = RouteManager.getInstance().getRouteWithNumber(route);
            s.addRoute(r);
        }
    }
}
