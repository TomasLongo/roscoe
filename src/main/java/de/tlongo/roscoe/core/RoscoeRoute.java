package de.tlongo.roscoe.core;

import spark.Route;

/**
 * Created by tomas on 11.01.15.
 */
public abstract class RoscoeRoute  implements Route {
    String routeUrl;
    String method;

    protected ViewHandler viewHandler;

    public void setRouteUrl(String routeUrl) {
        this.routeUrl = routeUrl;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRouteUrl() {
        return routeUrl;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "RoscoeRoute{" +
                "routeUrl='" + routeUrl + '\'' +
                ", method='" + method + '\'' +
                '}';
    }

    public void setViewHandler(ViewHandler viewHandler) {
        this.viewHandler = viewHandler;
    }
}
