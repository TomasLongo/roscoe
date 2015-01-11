package de.tlongo.roscoe.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomas on 11.01.15.
 */
public class ConfigManager {
    Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    String roscoeRoot;
    File configDir;
    JsonObject routesConfig;
    JsonObject coreConfig;
    ViewHandler viewHandler;

    public ConfigManager() {
        roscoeRoot = System.getProperty("roscoe.root");
        configDir = new File(roscoeRoot + "/conf");
        try {
            routesConfig = new JsonParser().parse(new FileReader(configDir + "/routes.json")).getAsJsonObject();
            coreConfig = new JsonParser().parse(new FileReader(configDir + "/core.json")).getAsJsonObject();

            viewHandler = instantiateViewHandler(coreConfig.get("viewhandler").getAsString());
        } catch (FileNotFoundException e) {
            logger.error("Could not load routes config {}", configDir + ("/routes.json"));
            throw new RuntimeException("Could not load routes config.");
        } catch (ClassNotFoundException e) {
            logger.error("Could not find ViewHandler implemenation.", e);
            throw new RuntimeException("Could not load viewhandler");
        } catch (InstantiationException e) {
            logger.error("Could not instantiate ViewHandler implemenation.", e);
            throw new RuntimeException("Could not load viewhandler");
        } catch (IllegalAccessException e) {
            logger.error("Could not instantiate ViewHandler implemenation.", e);
            throw new RuntimeException("Could not load viewhandler");
        }
    }

    private ViewHandler instantiateViewHandler(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class viewHandlerClass = Class.forName(className);
        return (ViewHandler)viewHandlerClass.newInstance();
    }

    public List<RoscoeRoute> getRoutes() {
        final List<RoscoeRoute> routes = new ArrayList<>();
        JsonArray jsonRoutesArray = routesConfig.getAsJsonArray("routes");

        logger.debug("Loading Routes from config...");
        jsonRoutesArray.forEach(route -> {
            JsonObject jsonRoute = route.getAsJsonObject();

            String routeUrl = jsonRoute.get("route").getAsString();
            String routeImplementation = jsonRoute.get("implementation").getAsString();
            String routeMethod = jsonRoute.get("method").getAsString();

            try {
                Class routeClass = Class.forName(routeImplementation);

                RoscoeRoute roscoeRoute = (RoscoeRoute)routeClass.newInstance();
                roscoeRoute.setMethod(routeMethod);
                roscoeRoute.setRouteUrl(routeUrl);
                roscoeRoute.setViewHandler(viewHandler);

                logger.debug("Loaded Route {}", roscoeRoute);

                routes.add(roscoeRoute);
            } catch (ClassNotFoundException e) {
                handleClassLoadingError(routeUrl, routeImplementation);
            } catch (InstantiationException e) {
                handleClassLoadingError(routeUrl, routeImplementation);
            } catch (IllegalAccessException e) {
                handleClassLoadingError(routeUrl, routeImplementation);
            }

        });
        logger.debug("Found {} routes in config", routes.size());

        return routes;
    }

    private void handleClassLoadingError(String routeName, String routeImplementation) {
        logger.error("Could not load implementation ({}) for Route {}", routeName, routeImplementation);
        throw new RuntimeException("Error loading route from config.");
    }
}
