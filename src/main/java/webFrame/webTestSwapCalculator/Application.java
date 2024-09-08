package webFrame.webTestSwapCalculator;

import webFrame.WebServer;

public class Application {
    public static void main(String[] args) throws Exception {
        WebServer webServer = new WebServer(8080, Application.class);
        webServer.start();
    }
}
