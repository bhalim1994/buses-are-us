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
 * A parser for the data returned by Translink stops query
 */
public class StopParser {
    private String filename;

    public StopParser(String filename) {
        this.filename = filename;
    }

    /**
     * Parse stop data from the file and add all stops to stop manager.
     *
     */
    public void parse() throws IOException, StopDataMissingException, JSONException {
        DataProvider dataProvider = new FileDataProvider(filename);

        parseStops(dataProvider.dataSourceToString());
    }

    /**
     * Parse stop information from JSON response produced by Translink.
     * Stores all stops and routes found in the StopManager and RouteManager.
     *
     * @param  jsonResponse    string encoding JSON data to be parsed
     * @throws JSONException when:
     * <ul>
     *     <li>JSON data does not have expected format (JSON syntax problem)</li>
     *     <li>JSON data is not an array</li>
     * </ul>
     * @throws StopDataMissingException when
     * <ul>
     *     <li> JSON data is missing Name, StopNo, Routes or location (Latitude or Longitude) elements for any stop</li>
     * </ul>
     * If a StopDataMissingException is thrown, all stops for which all required data is available
     * are first added to the stop manager.
     */
    public void parseStops(String jsonResponse)
            throws JSONException, StopDataMissingException {
        JSONArray stops = new JSONArray(jsonResponse);
        for (int index = 0; index < stops.length(); index++) {
            JSONObject stop = stops.getJSONObject(index);
            parseStop(stop);
        }
    }

    private void parseStop(JSONObject stop) throws JSONException, StopDataMissingException {
        if (checkStopKeys(stop)) {
            String name = stop.getString("Name");
            Integer stopNo = stop.getInt("StopNo");
            Double latitude = stop.getDouble("Latitude");
            Double longitude = stop.getDouble("Longitude");
            String routes = stop.getString("Routes");
            String[] individualRoutes = routes.split(", ");
            LatLon latLon = new LatLon(latitude, longitude);
            Stop tempStop = StopManager.getInstance().getStopWithNumber(stopNo, name, latLon);
            addRouteToStop(individualRoutes, tempStop);
        } else {
            throw new StopDataMissingException();
        }
    }

    private void addRouteToStop(String[] individualRoutes, Stop tempStop) {
        for (int i = 0; i < individualRoutes.length; i++) {
            Route route = RouteManager.getInstance().getRouteWithNumber(individualRoutes[i]);
            tempStop.addRoute(route);
        }
    }

    private boolean checkStopKeys(JSONObject stop) {
        if (stop.has("Name")) {
            if (stop.has("StopNo")) {
                if (stop.has("Routes")) {
                    if (stop.has("Latitude")) {
                        return stop.has("Longitude");
                    }
                }
            }
        }
        return false;
    }
}
