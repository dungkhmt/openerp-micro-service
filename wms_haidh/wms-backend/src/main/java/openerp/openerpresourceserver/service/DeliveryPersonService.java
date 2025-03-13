package openerp.openerpresourceserver.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.DeliveryPerson;
import openerp.openerpresourceserver.entity.User;
import openerp.openerpresourceserver.entity.projection.DeliveryPersonProjection;
import openerp.openerpresourceserver.model.request.DeliveryPersonRequest;
import openerp.openerpresourceserver.repository.DeliveryPersonRepository;
import openerp.openerpresourceserver.repository.UserRepository;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryPersonService {

	private UserRepository userRepository;
	private DeliveryPersonRepository deliveryPersonRepository;

	public List<DeliveryPersonProjection> getAllDeliveryPersons() {
		return deliveryPersonRepository.findAllDeliveryPersons();
	}

	public Page<DeliveryPerson> getAllDeliveryPersons(int page, int size, String search) {
		Pageable pageable = PageRequest.of(page, size);
		if (search != null && !search.trim().isEmpty()) {
			return deliveryPersonRepository.findByFullNameContainingIgnoreCase(search, pageable);
		}
		return deliveryPersonRepository.findAll(pageable);
	}
	
	public Optional<DeliveryPerson> getDeliveryPersonById(String userLoginId) {
        return deliveryPersonRepository.findById(userLoginId);
    }
	
	@Transactional
	public boolean createDeliveryPerson(DeliveryPersonRequest request) {
	    // Tìm user dựa trên email
	    Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

	    if (optionalUser.isEmpty()) {
	        return false; // Trả về false nếu không tìm thấy user
	    }

	    User user = optionalUser.get();

	    // Tạo đối tượng DeliveryPerson
	    DeliveryPerson deliveryPerson = DeliveryPerson.builder()
	            .userLoginId(user.getId())
	            .fullName(request.getFullName())
	            .phoneNumber(request.getPhoneNumber())
	            .build();

	    deliveryPersonRepository.save(deliveryPerson);
	    return true; // Trả về true nếu tạo thành công
	}

	public boolean updateDeliveryPerson(DeliveryPerson updatedPerson) {
		Optional<DeliveryPerson> optionalPerson = deliveryPersonRepository.findById(updatedPerson.getUserLoginId());
		if (optionalPerson.isPresent()) {
			DeliveryPerson existingPerson = optionalPerson.get();
			existingPerson.setFullName(updatedPerson.getFullName());
			existingPerson.setPhoneNumber(updatedPerson.getPhoneNumber());
			deliveryPersonRepository.save(existingPerson);
			return true;
		}
		return false;
	}

}
