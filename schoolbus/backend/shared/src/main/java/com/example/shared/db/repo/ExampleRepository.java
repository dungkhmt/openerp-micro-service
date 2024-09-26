package com.example.shared.db.repo;

import com.example.shared.db.entities.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface ExampleRepository extends JpaRepository<Example, Long> {
    @Query(value = """
                select e
                from Example e
                where e.description like :description
            """,
            countQuery = """
                select count(*)
                from Example e
                where e.description like :description
            """ )
    Page<Example> getExampleByDescription(
            @Param("description") String description,
            Pageable pageable
    );
}
