package com.example.computation.app;

import com.example.Main;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public abstract class Configuration {
    public static final String jdbcString = "jdbc:mysql://rc1d-x28y6rv3qt46j5ka.mdb.yandexcloud.net:3306/thesis?useSSL=true&rewriteBatchedStatements=true";
    public static final String outputFileName = "output.txt";
    public static final String errorFileName = "error_file.txt";
    public static final String databaseUser = "thesis";
    public static final String getDatabasePassword = "12345678";
    public static final int vertex = 11;
    public static  String resourcePath = "/com/example/web";
    public static final int webPort = 80;


}
