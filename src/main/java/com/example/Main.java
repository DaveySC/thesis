package com.example;

import com.example.computation.app.Application;
import com.example.computation.app.Configuration;
import com.example.computation.app.compute.ComputeWithSql;
import com.example.computation.app.compute.ComputeWithoutSql;
import com.example.web.MainServlet;
import org.apache.commons.cli.*;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        Option mode = new Option("m", "mode", true, "application mode working\n" +
                "- web        (For launching web server)\n" +
                "- compute    (For launch computing without sql connection) \n" +
                "- computeSql (For launching with sql connection)");

        mode.setRequired(true);
        options.addOption(mode);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            String modeValue = cmd.getOptionValue("mode");
            int statusCode = (switch(modeValue){
                case "web" -> new MainServlet().start();
                case "compute" -> new Application(new ComputeWithoutSql()).start();
                case "computeSql" -> new Application(new ComputeWithSql()).start();
                default -> throw new ParseException("Wrong parameter = " + modeValue);
            });
            if (statusCode != 10) {
                System.exit(statusCode);
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }
    }




}
