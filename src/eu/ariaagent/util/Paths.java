package eu.ariaagent.util;

/**
 * Created by adg on 29/09/2015.
 */
public class Paths {

    private static String dataFolder = "Resources\\", resultsFolder = "Results\\";

    public static String getResourcesFolder(String path) {
        return dataFolder + path;
    }

    public static String getResultsFolder(String path) {
        return resultsFolder + path;
    }

    public static void setResourcesFolder(String dataFolder) {
        Paths.dataFolder = dataFolder.endsWith("\\") ? dataFolder : dataFolder + "\\";
    }

    public static void setResultsFolder(String resultsFolder) {
        Paths.resultsFolder = resultsFolder.endsWith("\\") ? resultsFolder : resultsFolder + "\\";
    }
}
