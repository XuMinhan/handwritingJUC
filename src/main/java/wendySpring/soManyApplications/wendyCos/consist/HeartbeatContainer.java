package wendySpring.soManyApplications.wendyCos.consist;

import wendySpring.springConsist.wendyNetty.AddressAndPort;

import java.time.Instant;

public class HeartbeatContainer {
    private AddressAndPort addressAndPort;
    private Instant lastHeartbeat;

    public HeartbeatContainer(AddressAndPort addressAndPort) {
        this.addressAndPort = addressAndPort;
        this.lastHeartbeat = Instant.now(); // 初始化时记录当前时间
    }

    public AddressAndPort getAddressAndPort() {
        return addressAndPort;
    }

    public Instant getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void updateHeartbeat() {
        this.lastHeartbeat = Instant.now(); // 更新心跳时间
        System.out.println(Instant.now());
    }
}
