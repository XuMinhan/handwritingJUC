package wendySpring.springConsist.wendyNetty;

public class AddressAndPort {
    private String serverAddress;
    private int port;


    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public AddressAndPort() {
    }

    public AddressAndPort(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
