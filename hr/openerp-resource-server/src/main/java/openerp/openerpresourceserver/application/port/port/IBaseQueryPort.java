package openerp.openerpresourceserver.application.port.port;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

//ILLEGAL OF HEXAGONAL ARCHITECTURE
//FOR CODE FASTER
//LEGAL: WRITE ALL METHOD
@NoRepositoryBean
public interface IBaseQueryPort<T, ID> extends JpaRepository<T, ID> {
}
