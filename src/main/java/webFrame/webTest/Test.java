package webFrame.webTest;

import webFrame.WebServer;

public class Test {
    public static void main(String[] args) throws Exception {
        WebServer webServer = new WebServer(8080, Test.class);
        webServer.start();
    }
}
