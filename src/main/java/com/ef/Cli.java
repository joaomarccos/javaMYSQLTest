package com.ef;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Cli {

    public static final String ACCESS_LOG = "--accesslog";
    public static final String START_DATE = "--startDate";
    public static final String DURATION = "--duration";
    public static final String THRESHOLD = "--threshold";

    private final ParserService service;
    private Map<String, String> params;

    @Autowired
    public Cli(ParserService service) {
        this.service = service;
    }

    public void init(String... args) {
        buildParams(args);

        if (isDefined(START_DATE, DURATION, THRESHOLD)) {
            try {

                if (params.containsKey(ACCESS_LOG)) {
                    service.store(Paths.get(params.get(ACCESS_LOG)));
                }

                System.out.print("Result: " + service.find(
                        params.get(START_DATE),
                        params.get(DURATION),
                        params.get(THRESHOLD)
                ).stream().collect(Collectors.joining(", ")));


            } catch (IllegalAccessException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("log file not found.");
            }
        } else {
            System.out.println("Required parameters not given");
        }

    }

    private boolean isDefined(String... args) {
        return Arrays.asList(args).stream().filter(param -> !params.containsKey(param)).count() == 0;
    }

    private void buildParams(String[] args) {
        this.params = Arrays.asList(args).stream().collect(Collectors.toMap((String e) -> {
            String[] split = e.split("=");
            return split.length > 0 ? split[0] : "";
        }, (String e) -> {
            String[] split = e.split("=");
            return split.length > 1 ? split[1] : "";
        }));
    }


}