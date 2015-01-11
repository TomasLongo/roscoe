package de.tlongo.roscoe.core;

/**
 * Created by tomas on 02.01.15.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Route;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;
public class Roscoe {
    Logger logger = LoggerFactory.getLogger(Roscoe.class);

    public static void main(String[] args) {
        new Roscoe().go();

        /**
         * Start the web app by reading the config and creating the routes.
         */

        // 1. Determine the framework route and export to a property
        //   a. if none is specified in the config, determine it by yourself
        //   b. create and store paths to folders under the fw root
        //      - templates
        //      - views
        //      - assets
        //
        // 2. Create the routes
        //  a. For every route in the config, create a spark-route and register it.




    }

    private void go() {
        File file = new File(".");
        logger.debug("Roscoe Root at: ", file.toPath().toAbsolutePath().toString());
        System.setProperty("roscoe.root", file.toPath().toAbsolutePath().toString());

        Map<String, List<Route>> routes = new HashMap<>();
        ConfigManager configManager = new ConfigManager();

        configManager.getRoutes().forEach(route -> {
            if (route.getMethod().equals("GET")) {
                get(route.getRouteUrl(), route);
            }
        });
    }
}
