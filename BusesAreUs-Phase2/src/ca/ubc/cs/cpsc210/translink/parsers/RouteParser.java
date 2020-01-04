package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
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
     * Parse route data from the file and add all routes to the route manager.
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
     * @param jsonResponse string encoding JSON data to be parsed
     * @throws JSONException when:
     * <ul>
     *    <li>JSON data does not have expected format (JSON syntax problem)</li>
     *    <li>JSON data is not an array</li>
     * </ul>
     * If a JSONException is thrown, no stops should be added to the stop manager.
     * @throws RouteDataMissingException when
     * <ul>
     *    <li>JSON data is missing RouteNo, Name, or Patterns element for any route</li>
     *    <li>The value of the Patterns element is not an array for any route</li>
     * </ul>
     * If a RouteDataMissingException is thrown, all correct routes are first added to the route manager.
     */
    private void parseRoutes(String jsonResponse)
            throws JSONException, RouteDataMissingException {
        JSONArray routes = new JSONArray(jsonResponse);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < routes.length(); ++i) {
            JSONObject oneroute = routes.getJSONObject(i);
            parseRoute(sb, oneroute);
        }

        if (sb.length() > 0) {
            throw new RouteDataMissingException("Missing required data about routes: " + sb.toString());
        }
    }

    /**
     * Parse a route object and add to route manager.  Add route number of any route that is missing a route number
     * or name to stringBuilder.
     *
     * @param stringBuilder string builder to store route numbers of routes missing number or name
     * @param routeObject   JSON object representing route to be parsed
     */
    private void parseRoute(StringBuilder stringBuilder, JSONObject routeObject) {
        RouteManager routeManager = RouteManager.getInstance();
        String routeNumber = "";
        try {
            routeNumber = routeObject.getString("RouteNo");
            String routeName = routeObject.getString("Name");
            JSONArray routePatterns = routeObject.getJSONArray("Patterns");
            Route r = routeManager.getRouteWithNumber(routeNumber, routeName);
            parsePatternsFromRoute(routePatterns, r);
        } catch (JSONException e) {
            stringBuilder.append(routeNumber.length() > 0 ? routeNumber : "unnumbered route");
            stringBuilder.append(" ");
        }
    }

    /**
     * Parse patterns from route.
     *
     * @param routePatterns JSON array representing route patterns
     * @param route         route to which patterns will be added when parsed
     * @throws JSONException if a pattern is missing any one of PatternNo, Destination or Direction
     */
    private void parsePatternsFromRoute(JSONArray routePatterns, Route route) throws JSONException {
        for (int p = 0; p < routePatterns.length(); p++) {
            JSONObject onePattern = routePatterns.getJSONObject(p);
            parsePattern(route, onePattern);
        }
    }

    /**
     * Parse a pattern and add to route.  Pattern is silently ignored if it does
     * not contain PatternNo, Destination and Direction.
     *
     * @param route         route to which pattern is to be added
     * @param patternObject JSON object representing pattern
     */
    private void parsePattern(Route route, JSONObject patternObject) {
        try {
            String patternNumber = patternObject.getString("PatternNo");
            String patternDestination = patternObject.getString("Destination");
            String patternDirection = patternObject.getString("Direction");
            route.getPattern(patternNumber, patternDestination, patternDirection);
        } catch (JSONException e) {
            // silently ignore
        }
    }
}
