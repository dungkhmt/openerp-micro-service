package com.real_estate.post.services;

import com.real_estate.post.daos.interfaces.AccountDao;
import com.real_estate.post.daos.interfaces.PostBuyDao;
import com.real_estate.post.daos.interfaces.PostSellDao;
import com.real_estate.post.dtos.request.CreatePostBuyRequestDto;
import com.real_estate.post.dtos.request.UpdatePostBuyRequestDto;
import com.real_estate.post.dtos.response.PostBuyResponseDto;
import com.real_estate.post.dtos.response.PostSellResponseDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostBuyService {
	@Autowired
	@Qualifier("postBuyImpl")
	PostBuyDao postBuyDao;

	@Autowired
	@Qualifier("accountImpl")
	private AccountDao accountDao;

	@Autowired
	@Qualifier("postSellImpl")
	private PostSellDao postSellDao;

	@Transactional
	public void createPostBuy(CreatePostBuyRequestDto requestDto, Long accountId) {
		Long now = System.currentTimeMillis(); //
		PostBuyEntity entity = new PostBuyEntity();
		entity.setAuthorId(accountId);
		entity.setTitle(requestDto.getTitle());
		entity.setDescription(requestDto.getDescription());

		entity.setMinAcreage(requestDto.getMinAcreage());
		entity.setMaxAcreage(requestDto.getMaxAcreage());
		entity.setMinPrice(requestDto.getMinPrice());
		entity.setMaxPrice(requestDto.getMaxPrice());

		entity.setTypeProperty(requestDto.getTypeProperty().toString());
        entity.setMinBathroom(requestDto.getMinBathroom());
		entity.setMinBedroom(requestDto.getMinBedroom());
		entity.setMinParking(requestDto.getMinParking());
		entity.setMinFloor(requestDto.getMinFloor());
		entity.setLegalDocuments(
				requestDto.getLegalDocuments().stream().map(item -> {
					return item.toString();
				}).collect(Collectors.toList())
		);
		entity.setDirectionProperties(
				requestDto.getDirectionProperties().stream().map(item -> {
					return item.toString();
				}).collect(Collectors.toList())
		);

		entity.setMinHorizontal(requestDto.getMinHorizontal());
		entity.setMinVertical(requestDto.getMinVertical());

		entity.setNameProvince(requestDto.getNameProvince());
		entity.setProvinceId(requestDto.getProvinceId());
		entity.setDistrictIds(requestDto.getDistrictIds());
		entity.setNameDistricts(requestDto.getNameDistricts());

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

    public List<PostBuyResponseDto> getPostByAccountId(Long accountId) {
		return postBuyDao.findByAccountId(accountId);
    }

	public void updateStatus(Long postBuyId, Long accountId, PostStatus status) {
		int record = postBuyDao.updateStatusBy(postBuyId, accountId, status);
		if (record == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cập nhập không thành công");
		}
	}

	public void updatePostBuy(UpdatePostBuyRequestDto requestDto) {
		Long now = System.currentTimeMillis();
		PostBuyEntity entity = new PostBuyEntity().builder()
				.postBuyId(requestDto.getPostBuyId())
				.authorId(requestDto.getAuthorId())
				.title(requestDto.getTitle())
				.description(requestDto.getDescription())
				.typeProperty(requestDto.getTypeProperty().toString())
				.minAcreage(requestDto.getMinAcreage())
				.maxAcreage(requestDto.getMaxAcreage())
				.minPrice(requestDto.getMinPrice())
				.maxPrice(requestDto.getMaxPrice())
				.minBathroom(requestDto.getMinBathroom())
				.minBedroom(requestDto.getMinBedroom())
				.minFloor(requestDto.getMinFloor())
				.minParking(requestDto.getMinParking())

				.legalDocuments(requestDto.getLegalDocuments().stream().map(
						item -> item.toString()
				).toList())
				.directionProperties(requestDto.getDirectionProperties().stream().map(
						item -> item.toString()
				).toList())
				.minHorizontal(requestDto.getMinHorizontal())
				.minVertical(requestDto.getMinVertical())

				.provinceId(requestDto.getProvinceId())
				.nameProvince(requestDto.getNameProvince())
				.districtIds(requestDto.getDistrictIds())
				.nameDistricts(requestDto.getNameDistricts())

				.postStatus(requestDto.getPostStatus())
				.isAvailable(true)
				.createdAt(requestDto.getCreatedAt())
				.updatedAt(now)
				.build();

		postBuyDao.save(entity);
	}

	public List<PostSellResponseDto> findSellMatch(Long postBuyId, Long accountId) {
		PostBuyEntity buyEntity = postBuyDao.findById(postBuyId);
		if (buyEntity == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy bài viết phù hợp");
		}
		if (buyEntity.getAuthorId() != accountId) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chức năng này chỉ dành cho chủ bài viết");
		}
		List<PostSellResponseDto> result = postSellDao.findBy(buyEntity);
		return result;
	}
}
