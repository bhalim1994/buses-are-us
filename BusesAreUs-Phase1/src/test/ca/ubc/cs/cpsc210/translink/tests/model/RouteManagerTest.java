package ca.ubc.cs.cpsc210.translink.tests.model;

import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test the RouteManager
 */
class RouteManagerTest {

    @BeforeEach
    void setup() {
        RouteManager.getInstance().clearRoutes();
    }

    @Test
    void testBasic() {
        Route r43 = new Route("43");
        Route r = RouteManager.getInstance().getRouteWithNumber("43");
        assertEquals(r43, r);
    }

    // TODO: design more tests
}
