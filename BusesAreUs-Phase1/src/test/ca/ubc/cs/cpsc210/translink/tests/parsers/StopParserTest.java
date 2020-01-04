package ca.ubc.cs.cpsc210.translink.tests.parsers;

import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.parsers.StopParser;
import ca.ubc.cs.cpsc210.translink.parsers.exception.StopDataMissingException;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Tests for the StopParser
 */
class StopParserTest {
    @BeforeEach
    void setup() {
        StopManager.getInstance().clearStops();
    }

    @Test
    void testStopParserNormal() throws StopDataMissingException, JSONException, IOException {
        StopParser p = new StopParser("stops.json");
        p.parse();
        assertEquals(8524, StopManager.getInstance().getNumStops());
    }

    // TODO: design more tests
}
