package ca.ubc.cs.cpsc210.translink.providers;

import ca.ubc.cs.cpsc210.translink.BusesAreUs;

/**
 * Data provider where data source is a file in Java (non-Android) environment
 */
public class FileDataProvider extends AndroidFileDataProvider {

    public FileDataProvider(String fileName) {
        super(BusesAreUs.getContext(), fileName);
    }
}
