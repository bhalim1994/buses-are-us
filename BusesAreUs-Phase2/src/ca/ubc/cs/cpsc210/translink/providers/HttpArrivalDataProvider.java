package ca.ubc.cs.cpsc210.translink.providers;

import ca.ubc.cs.cpsc210.translink.model.Stop;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static ca.ubc.cs.cpsc210.translink.auth.TranslinkToken.TRANSLINK_API_KEY;

/**
 * Wrapper for Translink Arrival Data Provider
 */
public class HttpArrivalDataProvider extends AbstractHttpDataProvider {
    private Stop stop;

    public HttpArrivalDataProvider(Stop stop) {
        super();
        this.stop = stop;
    }

    @Override
    /**
     * Produces URL used to query Translink web service for expected arrivals at
     * the stop specified in call to constructor.
     *
     * @returns URL to query Translink web service for arrival data
     */
    protected URL getUrl() throws MalformedURLException {
        if (stop != null) {
            String url = "http://api.translink.ca/rttiapi/v1/stops/" + stop.getNumber() + "/estimates?apikey="
                    + TRANSLINK_API_KEY;
            return new URL(url);
        } else {
            throw new MalformedURLException();
        }
    }

    @Override
    public byte[] dataSourceToBytes() throws IOException {
        return new byte[0];
    }
}
