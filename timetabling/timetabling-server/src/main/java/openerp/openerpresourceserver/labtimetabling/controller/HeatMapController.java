package openerp.openerpresourceserver.labtimetabling.controller;


import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.entity.Assign;
import openerp.openerpresourceserver.labtimetabling.entity.Class;
import openerp.openerpresourceserver.labtimetabling.entity.Room;
import openerp.openerpresourceserver.labtimetabling.entity.dto.HeatMap;
import openerp.openerpresourceserver.labtimetabling.entity.dto.HeatMapResponse;
import openerp.openerpresourceserver.labtimetabling.entity.dto.HeatValue;
import openerp.openerpresourceserver.labtimetabling.service.AssignService;
import openerp.openerpresourceserver.labtimetabling.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/lab-timetabling/heatmap")
public class HeatMapController {
    private RoomService roomService;
    private AssignService assignService;
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam Long semesterId, @RequestParam Long week){
        List<Assign> assignList = assignService.getAssignsBySemester(semesterId);
        List<Assign> assignFiltered = assignList.stream().filter(assign -> Objects.equals(assign.getWeek(), week)).toList();

        List<Class> classList = new java.util.ArrayList<>(assignFiltered.stream().map(Assign::getLesson).toList());
        List<Room> roomList = roomService.getAllRooms();
        classList.sort(Comparator.comparing(Class::getNote));
        roomList.sort(Comparator.comparing(Room::getName));

        HeatMap heatMap = new HeatMap();
        assignFiltered.forEach(assign -> {
            for(int i=0;i<assign.getLesson().getPeriod();i++){
                HeatValue heatValue = new HeatValue();
                heatValue.setRoom_index((long) roomList.indexOf(assign.getRoom()));
                heatValue.setTime_slot((assign.getDay_of_week()-2)*12 + (assign.getPeriod()-1)*6 + assign.getStart_slot()+i);
                heatValue.setValue(Long.valueOf(assign.getLesson().getQuantity()));
                heatValue.setClass_index((long) classList.indexOf(assign.getLesson()));
                heatMap.getHeatValues().add(heatValue);
            }
        });

        HeatMapResponse response = new HeatMapResponse();
        response.setHeatMap(heatMap);
        response.setClassList(classList);
        response.setRoomList(roomList);
        response.getClassList().stream().forEach(c-> System.out.println(c.getNote()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
