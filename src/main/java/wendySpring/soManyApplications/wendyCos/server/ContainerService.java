package wendySpring.soManyApplications.wendyCos.server;

import wendySpring.soManyApplications.wendyCos.consist.HeartbeatContainer;
import wendySpring.springConsist.springBean.Component;
import wendySpring.springConsist.wendyNetty.AddressAndPort;

import java.time.Instant;
import java.util.*;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ContainerService {

    int CHECK_EVERY_SECOND = 60;  //检查时间

    int HEART_BEAT_TIMEOUT = 40;   //过期时间
    private final Duration heartbeatTimeout = Duration.ofSeconds(HEART_BEAT_TIMEOUT); // 示例：1分钟超时

    private final HashMap<String, List<HeartbeatContainer>> serviceContainer = new HashMap<>();
    private final Random random = new Random();

    private Boolean start = false;

    public int upload(String serverId, String address, int port) {

        if (!start) {
            startChecking();
            start = !start;
        }


        List<HeartbeatContainer> heartbeatContainers = serviceContainer.get(serverId);
        AddressAndPort addressAndPort = new AddressAndPort(address, port);
        HeartbeatContainer heartbeatContainer = new HeartbeatContainer(addressAndPort);

        if (heartbeatContainers == null) {
            ArrayList<HeartbeatContainer> newContainers = new ArrayList<>();
            newContainers.add(heartbeatContainer);
            serviceContainer.put(serverId, newContainers);
            System.out.println(serverId + "  " + address + "  " + port + "注册成功");
        } else {
            if (!heartbeatContainers.contains(heartbeatContainer)) {
                heartbeatContainers.add(heartbeatContainer);
                System.out.println(serverId + "  " + address + "  " + port + "注册成功");
            }
        }
        return 1;
    }

    public AddressAndPort getAddressAndPort(String serverId) {
        List<HeartbeatContainer> heartbeatContainers = serviceContainer.get(serverId);
        if (heartbeatContainers == null || heartbeatContainers.isEmpty()) {
            return null; // 或者抛出异常
        }
        int size = heartbeatContainers.size();
        HeartbeatContainer selectedContainer = heartbeatContainers.get(random.nextInt(size));
        return selectedContainer.getAddressAndPort();
    }

    public void updateHeartbeat(String serverId, String address, int port) {
        List<HeartbeatContainer> heartbeatContainers = serviceContainer.get(serverId);
        if (heartbeatContainers != null) {
            for (HeartbeatContainer container : heartbeatContainers) {
                AddressAndPort ap = container.getAddressAndPort();
                if (ap.getServerAddress().equals(address) && ap.getPort() == port) {
                    container.updateHeartbeat();
                    break;
                }
            }
        }
    }

    public void checkAndRemoveStaleInstances() {
        Instant now = Instant.now();
        for (Map.Entry<String, List<HeartbeatContainer>> entry : serviceContainer.entrySet()) {
            // 使用迭代器来遍历和删除满足条件的元素
            Iterator<HeartbeatContainer> iterator = entry.getValue().iterator();
            while (iterator.hasNext()) {
                HeartbeatContainer container = iterator.next();
                if (Duration.between(container.getLastHeartbeat(), now).compareTo(heartbeatTimeout) > 0) {

                    // 打印出被删除的服务信息
                    System.out.println("Removing service ID: " + entry.getKey() +
                            ", Address: " + container.getAddressAndPort().getServerAddress() +
                            ", Port: " + container.getAddressAndPort().getPort());
                    iterator.remove(); // 删除满足条件的容器
                }
            }
        }
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startChecking() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkAndRemoveStaleInstances();
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception
            }
        }, 0, CHECK_EVERY_SECOND, TimeUnit.SECONDS); // 例如，每分钟检查一次
    }

}
