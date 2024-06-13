package com.real_estate.post.services;

import com.real_estate.post.daos.interfaces.AccountDao;
import com.real_estate.post.daos.interfaces.PostSellDao;
import com.real_estate.post.dtos.request.CreatePostSellRequestDto;
import com.real_estate.post.dtos.request.UpdatePostSellRequestDto;
import com.real_estate.post.dtos.response.PostSellResponseDto;
import com.real_estate.post.models.DashboardPriceEntity;
import com.real_estate.post.models.PostSellEntity;
import com.real_estate.post.utils.PostStatus;
import org.hibernate.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PostSellService {
	@Autowired
	@Qualifier("postSellImpl")
	private PostSellDao postSellDao;

	@Autowired
	@Qualifier("accountImpl")
	private AccountDao accountDao;

	@Transactional
	public void createPostSell(CreatePostSellRequestDto requestDto, Long accountId) {
		Long now = System.currentTimeMillis();
		PostSellEntity post = new PostSellEntity();
		post.setAuthorId(accountId);

		post.setProvinceId(requestDto.getProvinceId());
		post.setNameProvince(requestDto.getNameProvince());
		post.setDistrictId(requestDto.getDistrictId());
		post.setNameDistrict(requestDto.getNameDistrict());
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
		post.setLegalDocument(requestDto.getLegalDocument());
		post.setDirectionProperty(requestDto.getDirectionProperty());
		post.setHorizontal(requestDto.getHorizontal());
		post.setVertical(requestDto.getVertical());
		post.setPostStatus(PostStatus.OPENING.toString());

		post.setIsAvailable(true);
		post.setCreatedAt(now);
		post.setUpdatedAt(now);

		try {
			postSellDao.save(post);
			accountDao.incOneTotalPostSellBy(accountId);
		} catch (TransactionException transactionException) {
			String message = "Tạo bài viết không thành công";
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
		} catch (Exception exception) {
			String message = "Tạo bài viết không thành công";
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message);
		}
	}

	public Page<PostSellResponseDto> getPageSell(
			Integer page,
			Integer size,
			String provinceId,
			String districtId,
			Long fromAcreage,
			Long toAcreage,
			Long fromPrice,
			Long toPrice,
			List<String> typeProperties,
			List<String> directions
	) {
		Pageable pageable = PageRequest.of(page-1, size);
		long totalRecords = postSellDao.countBy(provinceId,
												districtId,
												fromAcreage,
												toAcreage,
												fromPrice,
												toPrice,
												typeProperties,
												directions
		);
		if (totalRecords == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không có bài viết nào phù hợp");
		} else {
			List<PostSellResponseDto> entities = postSellDao.findPostSellBy(
					pageable,
					provinceId,
					districtId,
					fromAcreage,
					toAcreage,
					fromPrice,
					toPrice,
					typeProperties,
					directions
			);
			return new PageImpl<>(entities, pageable, totalRecords);
		}
	}

	public PostSellEntity getSellById(Long postSellId) {
		PostSellEntity entity = postSellDao.findById(postSellId);
		if (entity == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy bài viết");
		}
		return entity;
	}

	public void updatePostSell(UpdatePostSellRequestDto requestDto) {
		Long now = System.currentTimeMillis();
		PostSellEntity entity = new PostSellEntity().builder()
				.postSellId(requestDto.getPostSellId())
				.authorId(requestDto.getAuthorId())
				.title(requestDto.getTitle())
				.description(requestDto.getDescription())
				.typeProperty(requestDto.getTypeProperty().toString())
				.price(requestDto.getPrice())
				.pricePerM2(requestDto.getPricePerM2())
				.acreage(requestDto.getAcreage())
				.bathroom(requestDto.getBathroom())
				.parking(requestDto.getParking())
				.bedroom(requestDto.getBedroom())
				.floor(requestDto.getFloor())
				.legalDocument(requestDto.getLegalDocument().toString())
				.directionProperty(requestDto.getDirectionProperty().toString())
				.horizontal(requestDto.getHorizontal())
				.vertical(requestDto.getVertical())
				.position(requestDto.getPosition())

				.provinceId(requestDto.getProvinceId())
				.nameProvince(requestDto.getNameProvince())
				.districtId(requestDto.getDistrictId())
				.nameDistrict(requestDto.getNameDistrict())

				.address(requestDto.getAddress())
				.imageUrls(requestDto.getImageUrls())
				.postStatus(requestDto.getPostStatus())
				.isAvailable(true)
				.createdAt(requestDto.getCreatedAt())
				.updatedAt(now)
				.build();

		postSellDao.save(entity);
	}

	public List<PostSellResponseDto> getPostByAccountId(Long accountId) {
		return postSellDao.findByAccountId(accountId);
	}

	public List<DashboardPriceEntity> calculatePricePerM2(Long startTime, Long endTime) {
		return postSellDao.calculatePricePerM2(startTime, endTime);
	}

	public void updateStatus(Long postSellId, Long accountId, PostStatus status) {
		int record = postSellDao.updateStatusBy(postSellId, accountId, status);
		if (record == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cập nhập không thành công");
		}
	}
}
