package de.tlongo.roscoe.routes;

import de.tlongo.roscoe.core.RoscoeRoute;
import de.tlongo.roscoe.core.ViewData;
import spark.Request;
import spark.Response;

/**
 * Created by tomas on 11.01.15.
 */
public class HelloWorldRoute extends RoscoeRoute {

    @Override
    public Object handle(Request request, Response response) {
        ViewData data = new ViewData().add("url", "localhost:4567").
                add("welcomemessage", "Roscoe has succesfully been set up");

        return viewHandler.loadView("helloworld.html.mustache", data);
    }
}
