package com.mc.scl.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mc.scl.logger.AppLogger;
import com.mc.scl.logger.model.LogLayout;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class CommonUtility {

    private final AppLogger appLogger;

    public CommonUtility(AppLogger appLogger) {
        this.appLogger = appLogger;
    }

    /**
     * Reads git properties from the generated git.properties file
     *
     * @return Map containing git information (branch, build time, commit time)
     */
    @SuppressWarnings("unchecked")
    public Map<Object, Object> readGitProperties() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("git.properties");
        Map<Object, Object> map = new HashMap<>();

        try {
            if (inputStream != null) {
                map = new ObjectMapper().readValue(readFromInputStream(inputStream), Map.class);
            } else {
                map.put(VariablesConstant.RESPONSE, VariablesConstant.VERSION_INFO_ERROR);
            }
        } catch (IOException e) {
            map.put(VariablesConstant.RESPONSE, VariablesConstant.VERSION_INFO_ERROR);
            LogLayout logLayout = new LogLayout(
                    VariablesConstant.ERROR,
                    VariablesConstant.MODULE_COMMON,
                    VariablesConstant.COMMON_UTILITY,
                    this.getClass().getName(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    VariablesConstant.EMPTY_STRING,
                    VariablesConstant.EMPTY_STRING,
                    VariablesConstant.EMPTY_STRING,
                    "Failed to process git properties: " + e.getMessage()
            );
            appLogger.writeLog(logLayout, e, null);
        }

        return map;
    }

    /**
     * Reads content from InputStream and converts to String
     *
     * @param inputStream the InputStream to read from
     * @return String content of the InputStream
     * @throws IOException if reading fails
     */
    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line);
            }
        }
        return resultStringBuilder.toString();
    }
}