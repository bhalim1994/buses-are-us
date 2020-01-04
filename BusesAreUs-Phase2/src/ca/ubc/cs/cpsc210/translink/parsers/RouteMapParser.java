package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.RoutePattern;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import ca.ubc.cs.cpsc210.translink.util.LatLon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for routes stored in a compact format in a txt file
 */
public class RouteMapParser {
    private String fileName;

    public RouteMapParser(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Parse the route map txt file
     */
    public void parse() {
        DataProvider dataProvider = new FileDataProvider(fileName);
        try {
            String dataAsString = dataProvider.dataSourceToString();
            if (!dataAsString.equals("")) {
                parseLines(dataAsString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse each line of data in source string
     *
     * @param dataAsString source string
     */
    private void parseLines(String dataAsString) {
        int posn = 0;
        while (posn < dataAsString.length()) {
            int endposn = dataAsString.indexOf('\n', posn);
            String line = dataAsString.substring(posn, endposn);
            parseOnePattern(line);
            posn = endposn + 1;
        }
    }

    /**
     * Parse one route pattern, adding it to the route that is named within it.
     *
     * @param str string representing one route pattern (single line of data from source file)
     *            <p>
     *            Each line begins with a capital N, which is not part of the route number, followed by the
     *            bus route number, a dash, the pattern name, a semicolon, and a series of 0 or more real
     *            numbers corresponding to the latitude and longitude (in that order) of a point in the pattern,
     *            separated by semicolons. The 'N' that marks the beginning of the line is not part of the bus
     *            route number.
     */
    private void parseOnePattern(String str) {
        String routeNumber;
        String patternName;
        int posn = 1;  // skip the leading 'N'
        int end;

        end = str.indexOf('-', posn);
        routeNumber = str.substring(posn, end);
        posn = end + 1;

        end = str.indexOf(';', posn);
        patternName = str.substring(posn, end);
        posn = end + 1;

        List<LatLon> elements = parseElements(str, posn);
        storeRouteMap(routeNumber, patternName, elements);
    }

    /**
     * Parse Lat/Lon pairs from string
     *
     * @param str  string representing one route pattern
     * @param posn posn in string at which to start parsing lat/lon pairs
     * @return list of LatLon objects parsed from string
     */
    private List<LatLon> parseElements(String str, int posn) {
        List<LatLon> elements = new ArrayList<>();
        int end;

        while (posn < str.length()) {
            end = str.indexOf(';', posn);
            String latStr = str.substring(posn, end);
            posn = end + 1;

            end = str.indexOf(';', posn);
            String lonStr = str.substring(posn, end);
            posn = end + 1;

            elements.add(parseLatLon(latStr, lonStr));
        }

        return elements;
    }

    /**
     * Parse LatLon object from latitude and longitude (as strings)
     *
     * @param latStr string representing latitude
     * @param lonStr string representing longitude
     * @return LatLon object
     */
    private LatLon parseLatLon(String latStr, String lonStr) {
        double lat = Double.parseDouble(latStr);
        double lon = Double.parseDouble(lonStr);
        return new LatLon(lat, lon);
    }

    /**
     * Store the parsed pattern into the named route
     * Your parser should call this method to insert each route pattern into the corresponding route object
     * There should be no need to change this method
     *
     * @param routeNumber the number of the route
     * @param patternName the name of the pattern
     * @param elements    the coordinate list of the pattern
     */
    private void storeRouteMap(String routeNumber, String patternName, List<LatLon> elements) {
        Route r = RouteManager.getInstance().getRouteWithNumber(routeNumber);
        RoutePattern rp = r.getPattern(patternName);
        if (rp == null) {
            // should never get here
            System.out.println("Can't store routeMap " + patternName + " in route " + r.getNumber());
        } else {
            rp.setPath(elements);
        }
    }
}
