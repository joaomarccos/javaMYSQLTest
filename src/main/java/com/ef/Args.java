package com.ef;


import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Args {

    public static final String ACCESS_LOG = "--accesslog";
    public static final String START_DATE = "--startDate";
    public static final String DURATION = "--duration";
    public static final String THRESHOLD = "--threshold";


    /**
     * Validate if the set of args is in the map
     *
     * @param params
     * @param args
     * @return
     */
    public static boolean isDefined(Map<String, String> params, String... args) {
        return Arrays.asList(args).stream().filter(param -> !params.containsKey(param)).count() == 0;
    }

    /**
     * Convert a string args in a map of params
     *
     * @param args
     * @return
     */
    public static Map<String, String> buildParams(String[] args) {
        Map<String, String> params;

        params = Arrays.asList(args).stream().collect(Collectors.toMap((String e) -> {
            String[] split = e.split("=");
            return split.length > 0 ? split[0] : "";
        }, (String e) -> {
            String[] split = e.split("=");
            return split.length > 1 ? split[1] : "";
        }));

        return params;
    }


}