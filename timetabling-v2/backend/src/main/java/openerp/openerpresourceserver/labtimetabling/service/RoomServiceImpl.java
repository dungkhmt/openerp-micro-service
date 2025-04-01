package openerp.openerpresourceserver.labtimetabling.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.labtimetabling.entity.Room;
import openerp.openerpresourceserver.labtimetabling.repo.RoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class RoomServiceImpl implements RoomService{

    private RoomRepo repo;

    @Override
    public List<Room> getAllRooms() {
        return repo.findAll();
    }

    @Override
    public Room getRoomById(Long id) {
        Optional<Room> room = repo.findById(id);
        if (room.isEmpty()) {
            throw new NoSuchElementException("Not exist room with id " + id);
        }
        return room.get();
    }

    @Override
    public Room createRoom(Room room) {
        Date currentDate = new Date();
//        room.setCreatedDate(currentDate);
//        room.setLastModifiedDate(currentDate);
        if (!repo.existsByName(room.getName())){
            return repo.save(room);
        }
        return null;
    }

    @Override
    public Optional<Room> patchRoom(Long id, Room updatedRoom) {
        Optional<Room> existingRoom = repo.findById(id);
        Date currentDate = new Date();
        existingRoom.ifPresent(room -> {
            if (!updatedRoom.getName().isEmpty()) {
                room.setName(updatedRoom.getName());
            }
            if (updatedRoom.getCapacity() != 0) {
                room.setCapacity(updatedRoom.getCapacity());
            }
            room.setDepartment_id(updatedRoom.getDepartment_id());
//            room.setLastModifiedDate(currentDate);
            repo.save(room);
        });
        return existingRoom;
    }

    @Override
    public boolean deleteRoom(Long id) {
        Optional<Room> existingRoom = repo.findById(id);
        if(existingRoom.isPresent()){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Room> getRoomsByDepartmentId(Long id) {
        return repo.findAllByDepartment_id(id);
    }
}
