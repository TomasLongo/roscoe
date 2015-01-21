package de.tlongo.roscoe.core;

/**
 * Created by tomas on 02.01.15.
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

import static spark.Spark.*;
public class Roscoe {
    Logger logger = LoggerFactory.getLogger(Roscoe.class);

    public static void main(String[] args) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        new Roscoe().go();
    }

    /**
     * Initialises Roscoe and fires up the web server
     */
    private void go() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Path roscoeRoot = new File(".").toPath().toAbsolutePath();
        logger.debug("Roscoe Root at: {}", roscoeRoot.toString());
        System.setProperty("roscoe.root", roscoeRoot.toString());

        ConfigManager configManager = new ConfigManager();

        // NOTE: According to the Spark doc this method has to be called before any other
        //       Spark method
        externalStaticFileLocation(System.getProperty("roscoe.root"));

        ViewHandler viewHandler = instantiateViewHandler(configManager.getConfigItem("core", "viewhandler").asString());

        JsonArray routeArray = configManager.getConfigItem("routes", "routes").jsonElement().getAsJsonArray();
        routeArray.forEach(route -> {
            JsonObject jsonRoute = route.getAsJsonObject();

            String routeUrl = jsonRoute.get("route").getAsString();
            String routeImplementation = jsonRoute.get("implementation").getAsString();
            String routeMethod = jsonRoute.get("method").getAsString();

            try {
                Class routeClass = Class.forName(routeImplementation);

                RoscoeRoute roscoeRoute = (RoscoeRoute) routeClass.newInstance();
                roscoeRoute.setMethod(routeMethod);
                roscoeRoute.setRouteUrl(routeUrl);
                roscoeRoute.setViewHandler(viewHandler);

                addRouteToSpark(roscoeRoute);

                logger.debug("Loaded Route {}", roscoeRoute);
            } catch (ClassNotFoundException e) {
                handleClassLoadingError(routeUrl, routeImplementation);
            } catch (InstantiationException e) {
                handleClassLoadingError(routeUrl, routeImplementation);
            } catch (IllegalAccessException e) {
                handleClassLoadingError(routeUrl, routeImplementation);
            }

        });
    }

    /**
     * Adds a RoscoeRoute to Spark based on the request method
     */
    private void addRouteToSpark(RoscoeRoute route) {
        if (route.getMethod().equals("GET")) {
            get(route.getRouteUrl(), route);
        } else if (route.getMethod().equals("POST")) {
            post(route.getRouteUrl(), route);
        } else if (route.getMethod().equals("PUT")) {
            put(route.getRouteUrl(), route);
        } else if (route.getMethod().equals("DELETE")) {
            delete(route.getRouteUrl(), route);
        } else {
            logger.error("Could not create route for unknown request method '{}'", route.getMethod());
            throw new RuntimeException("Error creating route from config.");
        }
    }

    private void handleClassLoadingError(String routeName, String routeImplementation) {
        logger.error("Could not load implementation ({}) for Route {}", routeName, routeImplementation);
        throw new RuntimeException("Error creating route from config.");
    }

    /**
     * Instantiates a view handler denoted by a classname
     * @param className The qualified name of the class to instantiate
     */
    private ViewHandler instantiateViewHandler(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        logger.debug("loading viewhandler {}", className);
        Class viewHandlerClass = Class.forName(className);
        return (ViewHandler)viewHandlerClass.newInstance();
    }

}
