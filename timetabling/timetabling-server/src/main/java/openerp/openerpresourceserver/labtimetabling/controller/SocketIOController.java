package openerp.openerpresourceserver.labtimetabling.controller;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.labtimetabling.entity.ScheduleConflict;
import openerp.openerpresourceserver.labtimetabling.entity.dto.ScheduleConflictResponse;
import openerp.openerpresourceserver.labtimetabling.entity.socket.ConflictCheckingRequest;
import openerp.openerpresourceserver.labtimetabling.service.AssignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class SocketIOController {

    @Autowired
    private SocketIOServer socketServer;

    @Autowired
    private AssignService assignService;

    SocketIOController(SocketIOServer socketServer){
        this.socketServer=socketServer;
        this.socketServer.addConnectListener(onUserConnectWithSocket);
        this.socketServer.addDisconnectListener(onUserDisconnectWithSocket);

        this.socketServer.addEventListener("conflict-checking", ConflictCheckingRequest.class, onSendCheckingRequest);
    }


    public ConnectListener onUserConnectWithSocket = client ->{
        System.out.println("connected: "+client.getSessionId());
    };


    public DisconnectListener onUserDisconnectWithSocket = new DisconnectListener() {
        @Override
        public void onDisconnect(SocketIOClient client) {
            client.disconnect();
            System.out.println("disconnected: "+client.getSessionId());
        }

    };

    public DataListener<ConflictCheckingRequest> onSendCheckingRequest = new DataListener<>() {
        @Override
        public void onData(SocketIOClient client, ConflictCheckingRequest conflictCheckingRequest, AckRequest ackRequest) throws Exception {
            client.joinRoom(conflictCheckingRequest.getSendId());
            System.out.println(conflictCheckingRequest.getSendId());
            List<ScheduleConflict> scheduleConflicts = assignService.findConflict();
            List<ScheduleConflictResponse> scheduleConflictResponseList = scheduleConflicts.stream().map(ScheduleConflictResponse::new).toList();
            socketServer.getRoomOperations(conflictCheckingRequest.getSendId()).sendEvent("response", scheduleConflictResponseList);
            ackRequest.sendAckData("Message send to target user successfully");
        }
    };
}
