package ca.ubc.cs.cpsc210.translink.tests.parsers;

import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.parsers.BusParser;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Unit tests for BusParser
class BusParserTest {
    @Test
    void testBusLocationsParserNormal() throws JSONException {
        Stop s = StopManager.getInstance().getStopWithNumber(51479);
        s.clearBuses();

        // Add routes 004 and 014 to stop, otherwise buses running on these
        // routes won't be added to stop
        Route n04 = RouteManager.getInstance().getRouteWithNumber("004");
        Route n14 = RouteManager.getInstance().getRouteWithNumber("014");
        s.addRoute(n04);
        s.addRoute(n14);

        String data = "";

        try {
            data = new FileDataProvider("buslocations.json").dataSourceToString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can't read the bus locations data");
        }

        BusParser.parseBuses(s, data);

        assertEquals(4, s.getBuses().size());
    }

    // TODO: design more tests
}
