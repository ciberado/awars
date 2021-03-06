package com.javiermoreno.awsdemos.attacker;

import java.util.Date;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class App {

    private static final String APP_ID = "client";
    
    private final String server;
    private final int serverPort;
    private final String warId;
    private String defenderWarId;
    private final int threads;

    public App(String server, int serverPort, String warId, int threads) {
        this.server = server;
        this.serverPort = serverPort;
        this.warId = warId;
        this.threads = threads;
    }
    
    
    public void execute() throws InterruptedException {
            cls();
            println("Starting the fight.");
            println("War ID: %s.", warId);
            println("Attacked server: %s.", server);
            println("Number of concurrent threads: %d.", threads);
            WarService warService = new WarServiceImpl(warId, server, serverPort);
            this.defenderWarId = warService.getDefenderWarId();
            println("Opponent War ID: %s.", this.defenderWarId);
            warService.addListener(new WarService.WarServiceListener() {
                public void requestCompleted(WarService.RequestCompletedEvent evt) {
                    showRequestCompletedMessage(evt.getTimestamp(), evt.getStatus(), evt.getResponseTimeMs());
                }
            });
            warService.attack();
    }
    

    private void showRequestCompletedMessage(Date timestamp, int status, long responseTimeMs) {
        gotoxy(1,8);
        println("%tT: Request completed in %4d ms with a response status of %d.", timestamp, responseTimeMs, status);
    }
    
    private void gotoxy(int x, int y) {
        if (x == 0 || y == 0) throw new IllegalArgumentException();
        System.out.format("%c[%d;%df", 0x1B, y, x);
        System.out.flush();
    }

    private void cls() {
        System.out.format("%c[2J", 0x1B);
        gotoxy(1,1);
        System.out.flush();
    }
    
    private void println(String msg, Object...args) {
        System.out.format(msg + "%n", args);
        System.out.flush();
    }
    
    public static void main(String[] args) {    
        try {
            if (args.length == 0) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "java -jar attacker", createCliOptions());            
            } else {
                CommandLineParser parser = new GnuParser();
                CommandLine line = parser.parse(createCliOptions(), args);
                String server = line.getOptionValue("server");
                int serverPort = Integer.parseInt(line.getOptionValue("port", "80"));
                String warId = line.getOptionValue("war-id");
                int threads = Integer.parseInt(line.getOptionValue("threads"));
                App app = new App(server, serverPort, warId, threads);
                app.execute();
            }
        } catch (ParseException ex) {
            System.err.println("LOG: Error parsing command line: " + ex.getMessage() + ".");
            ex.printStackTrace(System.err);
        } catch (InterruptedException ex) {
            System.err.println("LOG: concurrency problem: " + ex.getMessage() + ".");
            ex.printStackTrace(System.err);
        }
    }
    
    
    
    private static Options createCliOptions() {
        Options options = new Options();
        
        options.addOption("s", "server", true, "Attacked server address");
        options.addOption("p", "port", true, "Attacked server port");
        options.addOption("i", "war-id", true, "Name of the attacker");
        options.addOption("t", "threads", true, "Number of threads.");
        
        return options;
    }
    
}
