package com.example.shared.db.repo;

import com.example.shared.db.dto.GetStudentRideDTO;
import com.example.shared.db.entities.Student;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("""
        select s from Student s
        where
        (:id is null or s.id = :id)
        and (:name is null or s.name is null or s.name ILIKE %:name%)
        and (:studentClass is null or s.studentClass is null or s.studentClass ILIKE %:studentClass%)
        and (:phoneNumber is null or s.phoneNumber like %:phoneNumber%)
        and (:parentId is null or s.parent.id = :parentId)
        """)
    Page<Student> searchPageStudent(
        @Param("id") Long id,
        @Param("name") String name,
        @Param("phoneNumber") String phoneNumber,
        @Param("parentId") Long parentId,
        @Param("studentClass") String studentClass,
        Pageable pageable
    );

    List<Student> findByParent_Id(Long id);

    //findStudentIdsByParentId
    @Query("select s.id from Student s where s.parent.id = :parentId")
    List<Long> findStudentIdsByParentId(@Param("parentId") Long parentId);

    // getStudentRides
//    @Query("""
//        select new com.example.shared.db.dto.GetStudentRideDTO(
//            s,
//            pp,
//            e
//        )
//        from Student s
//        join s.
//        join spp.pickupPoint pp
//        join pp.ridePickupPoints rpp
//        join rpp.ride r
//        join r.executions e
//        where s.id in :studentIds
//        order by e.ride.startTime desc
//        """)
//    List<GetStudentRideDTO> getStudentRides(Long studentIds);

    // findAllByPickupPointId
    @Query("""
        select s from Student s
        join StudentPickupPoint spp on s.id = spp.student.id
        join spp.pickupPoint pp
        where pp.id = :pickupPointId
    """)
    List<Student> findAllByPickupPointId(@Param("pickupPointId") Long pickupPointId);

    // findAllByParentId
    @Query("""
        select s from Student s
        where s.parent.id = :parentId
    """)
    List<Student> findAllByParentId(@Param("parentId") Long parentId);

    // findAllByParentIdAndStudentPhoneNumber
    @Query("""
        select s from Student s
        where s.parent.id = :parentId
        and (:studentPhoneNumber is null or s.phoneNumber like %:studentPhoneNumber%)
    """)
    List<Student> findAllByParentIdAndStudentPhoneNumber(
        @Param("parentId") Long parentId,
        @Param("studentPhoneNumber") String studentPhoneNumber
    );

    // countByIdIn
    Long countByIdIn(List<Long> ids);

}
