package com.hust.baseweb.repo;

import com.hust.baseweb.entity.Party;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PartyPagingRepo extends PagingAndSortingRepository<Party, String> {

}
