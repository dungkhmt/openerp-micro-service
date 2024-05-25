package com.real_estate.post.services;

import com.real_estate.post.daos.interfaces.AccountDao;
import com.real_estate.post.daos.interfaces.PostBuyDao;
import com.real_estate.post.dtos.request.CreatePostBuyRequestDto;
import com.real_estate.post.dtos.response.PostBuyResponseDto;
import com.real_estate.post.models.PostBuyEntity;
import com.real_estate.post.utils.PostStatus;
import org.hibernate.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PostBuyService {
	@Autowired
	@Qualifier("postBuyImpl")
	PostBuyDao postBuyDao;

	@Autowired
	@Qualifier("accountImpl")
	private AccountDao accountDao;

	@Transactional
	public void createPostBuy(CreatePostBuyRequestDto requestDto, Long accountId) {
		Long now = System.currentTimeMillis(); //
		PostBuyEntity entity = new PostBuyEntity();
		entity.setAuthorId(accountId);
		entity.setTitle(requestDto.getTitle());
		entity.setDescription(requestDto.getDescription());

		entity.setMinAcreage(requestDto.getMinAcreage());
		entity.setFromPrice(requestDto.getFromPrice());
		entity.setToPrice(requestDto.getToPrice());
		entity.setFromPricePerM2(requestDto.getFromPricePerM2());
		entity.setToPricePerM2(requestDto.getToPricePerM2());

		entity.setTypeProperty(requestDto.getTypeProperty());
		entity.setMinBathroom(requestDto.getMinBathroom());
		entity.setMinBedroom(requestDto.getMinBedroom());
		entity.setMinParking(requestDto.getMinParking());
		entity.setMinFloor(requestDto.getMinFloor());
		entity.setLegalDocuments(requestDto.getLegalDocuments());
		entity.setDirectionsProperty(requestDto.getDirectionsProperty());

		entity.setMinHorizontal(requestDto.getMinHorizontal());
		entity.setMinVertical(requestDto.getMinVertical());
		entity.setProvince(requestDto.getProvince());
		entity.setDistrict(requestDto.getDistrict());
		entity.setPostStatus(PostStatus.OPENING.toString());
		entity.setIsAvailable(true);
		entity.setCreatedAt(now);
		entity.setUpdatedAt(now);

		try {
			postBuyDao.save(entity);
			accountDao.incOneTotalPostBuyBy(accountId);
		} catch (TransactionException transactionException) {
			String message = "Tạo bài viết không thành công";
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
		} catch (Exception exception) {
			String message = "Tạo bài viết không thành công";
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message);
		}
	}

	public Page<PostBuyResponseDto> getPageBuy(
			Integer page,
			Integer size,
			String province,
			String district
	) {
		Pageable pageable = PageRequest.of(page-1, size);
		long totalRecords = postBuyDao.countBy(province, district);
		if (totalRecords == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không có bài viết nào phù hợp");
		} else {
			List<PostBuyResponseDto> entities = postBuyDao.findPostBuyBy(
					pageable,
					province,
					district
			);
			return new PageImpl<>(entities, pageable, totalRecords);
		}
	}
}
