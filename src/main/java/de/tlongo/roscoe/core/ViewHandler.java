package de.tlongo.roscoe.core;

/**
 * Created by tomas on 30.12.14.
 */
public interface ViewHandler {
    String loadView(String viewName, ViewData data);

    String loadView(String viewName);
}
