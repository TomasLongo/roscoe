package de.tlongo.roscoe.routes;

import de.tlongo.roscoe.core.RoscoeRoute;
import spark.Request;
import spark.Response;

/**
 * Created by tomas on 11.01.15.
 */
public class HelloWorldRoute extends RoscoeRoute {

    @Override
    public Object handle(Request request, Response response) {
        return "Congratulations. You succesfully set up Roscoe";
    }
}
