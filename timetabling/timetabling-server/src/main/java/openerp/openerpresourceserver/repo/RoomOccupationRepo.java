package openerp.openerpresourceserver.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.model.entity.occupation.RoomOccupation;

@Repository
public interface RoomOccupationRepo extends JpaRepository<RoomOccupation, String> {
    
    public List<RoomOccupation> findAllBySemester(String semester);

    void deleteBySemester(String semester);
} 
