package com.real_estate.post.services;

import com.real_estate.post.daos.interfaces.AccountDao;
import com.real_estate.post.daos.interfaces.PostBuyDao;
import com.real_estate.post.daos.interfaces.PostSellDao;
import com.real_estate.post.daos.interfaces.LikeDao;
import com.real_estate.post.dtos.request.CreatePostBuyRequestDto;
import com.real_estate.post.dtos.request.UpdatePostBuyRequestDto;
import com.real_estate.post.dtos.response.CountPostByProvinceResponseDto;
import com.real_estate.post.dtos.response.PostBuyResponseDto;
import com.real_estate.post.dtos.response.PostSellResponseDto;
import com.real_estate.post.models.PostBuyEntity;
import com.real_estate.post.utils.PostStatus;
import com.real_estate.post.utils.TypePost;
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

	@Autowired
	@Qualifier("likeImpl")
	private LikeDao likeDao;

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

		entity.setTypeProperty(requestDto.getTypeProperty());
        entity.setMinBathroom(requestDto.getMinBathroom());
		entity.setMinBedroom(requestDto.getMinBedroom());
		entity.setMinParking(requestDto.getMinParking());
		entity.setMinFloor(requestDto.getMinFloor());
		entity.setLegalDocuments(requestDto.getLegalDocuments());
		entity.setDirectionProperties(requestDto.getDirectionProperties());

		entity.setMinHorizontal(requestDto.getMinHorizontal());
		entity.setMinVertical(requestDto.getMinVertical());

		entity.setNameProvince(requestDto.getNameProvince());
		entity.setProvinceId(requestDto.getProvinceId());
		entity.setDistrictIds(requestDto.getDistrictIds());
		entity.setNameDistricts(requestDto.getNameDistricts());

		entity.setPostStatus(PostStatus.OPENING);
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
			String provinceId,
			Long finderId
	) {
		Pageable pageable = PageRequest.of(page-1, size);
		long totalRecords = postBuyDao.countBy(provinceId);
		if (totalRecords == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không có bài viết nào phù hợp");
		} else {
			List<PostBuyResponseDto> entities = postBuyDao.findPostBuyBy(
					pageable,
					provinceId
			);

			if (finderId != null && finderId > 0) {
				entities = entities.stream().map(entity -> {
					Long likeId = likeDao.getId(entity.getPostBuyId(), finderId, TypePost.BUY);
					entity.setLikeId(likeId);
					return entity;
				}).toList();
			}
			return new PageImpl<>(entities, pageable, totalRecords);
		}
	}

    public List<PostBuyResponseDto> getPostByAccountId(Long accountId, Long finderId) {
		List<PostBuyResponseDto> dtos = postBuyDao.findByAccountId(accountId);

		if (finderId != null && finderId != accountId && finderId > 0) {
			dtos = dtos.stream().map(dto -> {
				Long likeId = likeDao.getId(dto.getPostBuyId(), finderId, TypePost.BUY);
				dto.setLikeId(likeId);
				return dto;
			}).toList();
		}
		return dtos;
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
				.typeProperty(requestDto.getTypeProperty())
				.minAcreage(requestDto.getMinAcreage())
				.maxAcreage(requestDto.getMaxAcreage())
				.minPrice(requestDto.getMinPrice())
				.maxPrice(requestDto.getMaxPrice())
				.minBathroom(requestDto.getMinBathroom())
				.minBedroom(requestDto.getMinBedroom())
				.minFloor(requestDto.getMinFloor())
				.minParking(requestDto.getMinParking())

				.legalDocuments(requestDto.getLegalDocuments())
				.directionProperties(requestDto.getDirectionProperties())
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
		result = result.stream().map(dto -> {
			Long likeId = likeDao.getId(dto.getPostSellId(), accountId, TypePost.SELL);
			dto.setLikeId(likeId);
			return dto;
		}).toList();
		return result;
	}

	public List<CountPostByProvinceResponseDto> getTotalPost() {
		return postBuyDao.countPost();
	}
}
