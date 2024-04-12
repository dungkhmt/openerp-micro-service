package com.real_estate.post.services;

import com.real_estate.common.models.PostSellEntity;
import com.real_estate.common.utils.DirectionsStatus;
import com.real_estate.common.utils.LegalDocuments;
import com.real_estate.common.utils.PostStatus;
import com.real_estate.common.utils.TypeProperty;
import com.real_estate.post.daos.interfaces.PostSellDao;
import com.real_estate.post.dtos.request.CreatePostSellRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostSellService {
	@Autowired
	@Qualifier("postSellImpl")
	private PostSellDao postSellDao;

	public void createPostSell(CreatePostSellRequestDto requestDto) {
		Long now = System.currentTimeMillis();
		PostSellEntity post = new PostSellEntity();
		post.setAuthorId(1L);

		post.setProvince(requestDto.getProvince());
		post.setDistrict(requestDto.getDistrict());
		post.setAddress(requestDto.getAddress());
		post.setPosition(requestDto.getPosition());

		post.setImageUrls(requestDto.getImageUrls());

		post.setTitle(requestDto.getTitle());
		post.setDescription(requestDto.getDescription());
		post.setPrice(requestDto.getPrice());
		post.setPricePerM2(requestDto.getPricePerM2());
		post.setTypeProperty(requestDto.getTypeProperty());

		post.setFloor(requestDto.getFloor());
		post.setAcreage(requestDto.getAcreage());
		post.setBathroom(requestDto.getBathroom());
		post.setBedroom(requestDto.getBedroom());
		post.setParking(requestDto.getParking());
		post.setLegalDocuments(requestDto.getLegalDocuments());
		post.setDirectionsProperty(requestDto.getDirectionsProperty());
		post.setHorizontal(requestDto.getHorizontal());
		post.setVertical(requestDto.getVertical());
		post.setPostStatus(PostStatus.OPENING.toString());

		post.setIsAvailable(true);
		post.setCreatedAt(now);
		post.setUpdatedAt(now);

		postSellDao.save(post);
	}

	public Page<PostSellEntity> getPageSell(
			Integer page,
			Integer size,
			String province,
			String district,
			Long minAcreage,
			Long fromPrice,
			Long toPrice,
			String sortPrice,
			List<String> typeProperties,
			List<String> legalDocuments,
			List<String> directions,
			Long minFloor,
			Long minBathroom,
			Long minBedroom,
			Long minParking
	) {
		Pageable pageable = null;
		if (sortPrice == "ASC") {
			pageable = PageRequest.of(page - 1, size, Sort.by("price").ascending().and(Sort.by("updatedAt").descending()));
		} else if (sortPrice == "DESC") {
			pageable = PageRequest.of(page - 1, size, Sort.by("price").descending().and(Sort.by("updatedAt").descending()));
		} else {
			pageable = PageRequest.of(page - 1, size, Sort.by("updatedAt").descending());
		}

		long totalRecords = postSellDao.countBy(province,
												district,
												minAcreage,
												fromPrice,
												toPrice,
												typeProperties,
												legalDocuments,
												directions,
												minFloor,
												minBathroom,
												minBedroom,
												minParking
		);
		if (totalRecords == 0) {
			return new PageImpl<>(new ArrayList<>(), pageable, totalRecords);
		} else {
			List<PostSellEntity> entities = postSellDao.findPostSellBy(pageable,
																	   province,
																	   district,
																	   minAcreage,
																	   fromPrice,
																	   toPrice,
																	   typeProperties,
																	   legalDocuments,
																	   directions,
																	   minFloor,
																	   minBathroom,
																	   minBedroom,
																	   minParking
			);

			return new PageImpl<>(entities, pageable, totalRecords);
		}
	}
}
