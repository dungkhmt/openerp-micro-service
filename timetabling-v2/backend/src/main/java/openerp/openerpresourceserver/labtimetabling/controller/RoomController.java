package openerp.openerpresourceserver.labtimetabling.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.entity.Room;
import openerp.openerpresourceserver.labtimetabling.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/lab-timetabling/room")
public class RoomController {
    private RoomService service;

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable Long id) {
        Room room = service.getRoomById(id);
        return ResponseEntity.ok().body(room);
    }
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Room room) {
        System.out.println(room);
        Room new_room = service.createRoom(room);
        if(new_room != null){
            return ResponseEntity.status(HttpStatus.OK).body(new_room);
        }else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchRoom(@PathVariable Long id, @RequestBody Room updatedRoom){
        Optional<Room> patchedRoom = service.patchRoom(id, updatedRoom);
        return patchedRoom.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id){
        if(service.deleteRoom(id)){
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllRooms() {
        List<Room> rooms = service.getAllRooms();
        return ResponseEntity.ok().body(rooms);
    }
    @GetMapping("/department/{id}")
    public ResponseEntity<?> getRoomsByDepartment(@PathVariable Long id) {
        List<Room> rooms = service.getRoomsByDepartmentId(id);
        return ResponseEntity.ok().body(rooms);
    }
}
