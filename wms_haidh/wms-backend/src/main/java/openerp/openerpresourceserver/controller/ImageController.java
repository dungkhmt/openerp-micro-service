package openerp.openerpresourceserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import openerp.openerpresourceserver.service.mongodb.ImageService;

@RestController
@RequestMapping("/images")
public class ImageController {

	@Autowired
	private ImageService imageService; 

	@GetMapping("/{imageId}")
	public ResponseEntity<byte[]> getImage(@PathVariable String imageId) {
		
		byte[] imageData = imageService.getImage(imageId);

		if (imageData == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok()
				.contentType(MediaType.IMAGE_JPEG).body(imageData);
	}
}
