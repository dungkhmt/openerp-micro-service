package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.DistributingChannel;

public interface DistributingChannelRepo  extends JpaRepository<DistributingChannel, Long> {
    @Query(value = "select * from scm_distributing_channel where is_deleted = 0", nativeQuery = true)
    Page<DistributingChannel> search(Pageable pageable);

    DistributingChannel getDistributingChannelById(long id);
    DistributingChannel getDistributingChannelByCode(String code);
}
