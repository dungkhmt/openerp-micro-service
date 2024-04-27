package openerp.openerpresourceserver.labtimetabling.controller;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.config.rabbitmq.RPCClient;
import openerp.openerpresourceserver.labtimetabling.entity.SchedulingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/lab-timetabling/submit")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SubmitController {
    private static final Logger logger = LoggerFactory.getLogger(SubmitController.class);
    private Gson gson = new Gson();
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody SchedulingRequest request) {
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try (RPCClient fibonacciRpc = new RPCClient()) {
                        System.out.println(" [x] Requesting fib(" + gson.toJson(request) + ")");
                        String response = fibonacciRpc.call(gson.toJson(request));
                        System.out.println(" [.] Got '" + gson.fromJson(response, SchedulingRequest.class));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
}
