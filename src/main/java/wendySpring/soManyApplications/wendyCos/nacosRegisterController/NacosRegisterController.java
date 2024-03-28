package wendySpring.soManyApplications.wendyCos.nacosRegisterController;

import wendySpring.soManyApplications.wendyCos.server.ContainerService;
import wendySpring.springConsist.springBean.Resource;
import wendySpring.springConsist.springBean.Result;
import wendySpring.springConsist.wendyNetty.AddressAndPort;
import wendySpring.springConsist.wendyNetty.ServiceIdAndAddressPort;
import wendySpring.springConsist.wendyNetty.processors.httpProcessor.*;


@Controller
public class NacosRegisterController {
    @Resource
    ContainerService containerService;

    @GetMapping("/getUpload")
    public Result getUpload(@RequestParam("serverId") String serverId, @RequestParam("address") String address, @RequestParam("port") int port) {
        return new Result(containerService.upload(serverId, address, port));

    }


    @PostMapping("/postUpload")
    public Result postUpload(@RequestBody ServiceIdAndAddressPort serviceIdAndAddressPort) {
        int port = serviceIdAndAddressPort.getPort();
        String address = serviceIdAndAddressPort.getServerAddress();
        String serverId = serviceIdAndAddressPort.getServerId();
        return new Result(containerService.upload(serverId, address, port));
    }

    @GetMapping("/getRandomAddressAndPort")
    public AddressAndPort getAddressAndPort(@RequestParam("serverId")String serverId){
        return containerService.getAddressAndPort(serverId);
    }

    @PostMapping("/heartBeat")
    public void heartBeat(@RequestBody ServiceIdAndAddressPort serviceIdAndAddressPort){
        containerService.updateHeartbeat(serviceIdAndAddressPort.getServerId(), serviceIdAndAddressPort.getServerAddress(), serviceIdAndAddressPort.getPort());
    }

}
