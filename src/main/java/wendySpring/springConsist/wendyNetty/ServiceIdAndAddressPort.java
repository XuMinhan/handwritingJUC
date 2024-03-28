package wendySpring.springConsist.wendyNetty;

public class ServiceIdAndAddressPort {
    private String serverId;
    private String serverAddress;

    @Override
    public String toString() {
        return "ServiceIdAndAddressPort{" +
                "serverId='" + serverId + '\'' +
                ", serverAddress='" + serverAddress + '\'' +
                ", port=" + port +
                '}';
    }

    public ServiceIdAndAddressPort(String serverId, String serverAddress, int port) {
        this.serverId = serverId;
        this.serverAddress = serverAddress;
        this.port = port;
    }

    public ServiceIdAndAddressPort() {
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private int port;

}
