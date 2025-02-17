package openerp.openerpresourceserver.generaltimetabling.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.generaltimetabling.exception.ClassroomNotFoundException;
import openerp.openerpresourceserver.generaltimetabling.exception.ClassroomUsedException;
import openerp.openerpresourceserver.generaltimetabling.exception.NotFoundException;
import openerp.openerpresourceserver.generaltimetabling.model.dto.GetClassRoomByBuildingsRequest;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.ClassroomDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
import openerp.openerpresourceserver.generaltimetabling.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/classroom")
public class ClassroomController {

    @Autowired
    private ClassroomService service;

    @PostMapping("/clear-all")
    public ResponseEntity<List<Classroom>> clearAllClassRoom() {
        service.clearAllClassRoom();
        List<Classroom> classroomList = service.getClassroom();
        if (!classroomList.isEmpty()) {
            return new ResponseEntity<>(classroomList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/clear-all-timetable")
    public ResponseEntity<List<Classroom>> clearAllClassRoomTimetable() {
        service.clearAllClassRoomTimetable();
        List<Classroom> classroomList = service.getClassroom();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Classroom>> getAllClassroom() {
        try {
            List<Classroom> classroomList = service.getClassroom();
            if (classroomList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(classroomList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all-building")
    public ResponseEntity<List<String>> getAllBuilding() {
        try {
            List<String> buildingList = service.getBuilding();
            if (buildingList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(buildingList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateClassroom(@Valid @RequestBody ClassroomDto requestDto) {
        try {
            service.updateClassroom(requestDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ClassroomNotFoundException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (ClassroomUsedException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createNewClassroom(@Valid @RequestBody ClassroomDto classroomDto) {
        try {
            service.create(classroomDto);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (ClassroomUsedException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteById(@RequestParam String id) {
        try {
            service.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ClassroomNotFoundException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (ClassroomUsedException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-ids")
    public ResponseEntity<Void> deleteByIds(@RequestParam List<String> ids) {
        try {
            service.deleteByIds(ids);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/")
    public ResponseEntity<List<Classroom>> requestGetClassroomByBuildings(@RequestBody(required = false) GetClassRoomByBuildingsRequest request) {
        if (request == null || (request.getGroupName() == null && request.getMaxAmount() == null)) {
            return getAllClassroom();
        }

        return ResponseEntity.ok(service.getMaxQuantityClassRoomByBuildings(request.getGroupName(), request.getMaxAmount()));
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> notFoundException(NotFoundException e) {
        return ResponseEntity.badRequest().body(e.getCustomMessage());
    }
}
