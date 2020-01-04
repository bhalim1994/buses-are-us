package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.RoutePattern;
import ca.ubc.cs.cpsc210.translink.parsers.exception.RouteDataMissingException;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Parse route information in JSON format.
 */
public class RouteParser {
    private String filename;

    public RouteParser(String filename) {
        this.filename = filename;
    }

    /**
     * Parse route data from the file and add all route to the route manager.
     *
     */
    public void parse() throws IOException, RouteDataMissingException, JSONException {
        DataProvider dataProvider = new FileDataProvider(filename);

        parseRoutes(dataProvider.dataSourceToString());
    }

    /**
     * Parse route information from JSON response produced by Translink.
     * Stores all routes and route patterns found in the RouteManager.  A pattern that
     * is missing any one of PatternNo, Destination or Direction is silently ignored
     * and not added to the route.
     *
     * @param  jsonResponse    string encoding JSON data to be parsed
     * @throws JSONException   when:
     * <ul>
     *     <li>JSON data does not have expected format (JSON syntax problem)
     *     <li>JSON data is not an array
     * </ul>
     * If a JSONException is thrown, no stops should be added to the stop manager
     *
     * @throws RouteDataMissingException when
     * <ul>
     *  <li>JSON data is missing RouteNo, Name, or Patterns element for any route</li>
     *  <li>The value of the Patterns element is not an array for any route</li>
     * </ul>
     *
     * If a RouteDataMissingException is thrown, all correct routes are first added to the route manager.
     */
    public void parseRoutes(String jsonResponse)
            throws JSONException, RouteDataMissingException {
        JSONArray routes = new JSONArray(jsonResponse);
        for (int index = 0; index < routes.length(); index++) {
            JSONObject route = routes.getJSONObject(index);
            parseRoute(route);
        }
    }

    private void parseRoute(JSONObject route) throws JSONException, RouteDataMissingException {
        if (checkRouteKeys(route)) {
            String name = route.getString("Name");
            String routeNo = route.getString("RouteNo");
            JSONArray patterns = route.getJSONArray("Patterns");
            Route tempRoute = RouteManager.getInstance().getRouteWithNumber(routeNo, name);
            parsePatterns(patterns, tempRoute);
        } else {
            throw new RouteDataMissingException();
        }
    }

    private boolean checkRouteKeys(JSONObject route) {
        if (route.has("RouteNo")) {
            if (route.has("Patterns")) {
                return (route.has("Name"));
            }
        }
        return false;
    }


    private void parsePatterns(JSONArray routePatterns, Route route) throws JSONException {
        for (int index = 0; index < routePatterns.length(); index++) {
            JSONObject pattern = routePatterns.getJSONObject(index);
            parsePattern(pattern, route);
        }
    }


    private void parsePattern(JSONObject pattern, Route route) throws JSONException {
        String destination = pattern.getString("Destination");
        String direction = pattern.getString("Direction");
        String patternNo = pattern.getString("PatternNo");
        RoutePattern tempRoutePattern = route.getPattern(patternNo, destination, direction);
        route.addPattern(tempRoutePattern);
    }
}
