package de.tlongo.roscoe.routes;

import de.tlongo.roscoe.core.RoscoeRoute;
import spark.Request;
import spark.Response;

/**
 * Created by tomas on 11.01.15.
 */
public class SamplePutRoute extends RoscoeRoute {

    @Override
    public Object handle(Request request, Response response) {
        return "This is a sample put route.";
    }
}
