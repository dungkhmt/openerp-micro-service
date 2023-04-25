package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.ContractType;

public interface ContractTypeRepo extends JpaRepository<ContractType, Long> {
    @Query(value = "select * from contract_type", nativeQuery = true)
    Page<ContractType> search(Pageable pageable);

    ContractType getContractTypeById(long id);
    ContractType getContractTypeByCode(String code);
}