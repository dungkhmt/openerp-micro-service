package com.real_estate.post.services;

import com.real_estate.common.models.PostBuyEntity;
import com.real_estate.common.utils.PostStatus;
import com.real_estate.post.daos.interfaces.PostBuyDao;
import com.real_estate.post.dtos.request.CreatePostBuyRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PostBuyService {
	@Autowired
	@Qualifier("postBuyImpl")
	PostBuyDao postBuyDao;

	public void createPostBuy(CreatePostBuyRequestDto requestDto) {
		Long now = System.currentTimeMillis(); //
		PostBuyEntity entity = new PostBuyEntity();
		entity.setAuthorId(1L);
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

		postBuyDao.save(entity);
	}
}
