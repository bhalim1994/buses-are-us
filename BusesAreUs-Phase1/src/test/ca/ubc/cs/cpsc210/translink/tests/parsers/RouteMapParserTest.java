package ca.ubc.cs.cpsc210.translink.tests.parsers;

import ca.ubc.cs.cpsc210.translink.model.*;
import ca.ubc.cs.cpsc210.translink.parsers.RouteMapParser;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test the parser for route pattern map information
 */
class RouteMapParserTest {
    @BeforeEach
    void setup() {
        RouteManager.getInstance().clearRoutes();
    }

    @Test
    void testRouteParserNormal() {
        RouteMapParser p = new RouteMapParser("allroutemaps.txt");
        p.parse();
        assertEquals(1232, countNumRoutePatterns());
    }

    /**
     * Helper to count number of route patterns
     * @return  total number of route patterns found in all routes in route manager
     */
    private int countNumRoutePatterns() {
        int count = 0;
        for (Route r : RouteManager.getInstance()) {
            for (RoutePattern rp : r.getPatterns()) {
                count ++;
            }
        }
        return count;
    }
}
