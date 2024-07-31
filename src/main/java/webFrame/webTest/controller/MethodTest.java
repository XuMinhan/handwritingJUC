package webFrame.webTest.controller;


import webFrame.Scan.annotation.*;

@Controller
public class MethodTest {


    @GetMapping("/testGet")
    public Integer testGet(@RequestParam("serverId") String serverId, @RequestParam("address") String address, @RequestParam("port") int port) {
        return 1;
    }

    @PostMapping("/testLhk")
    public Integer testPost(@RequestParam("serverId") String serverId, @RequestParam("address") String address, @RequestParam("port") int port) {
        return 1;
    }

}
