package com.real_estate.post;

import com.real_estate.post.repositories.PostSellRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class PostApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(PostApplication.class, args);
//		PostSellRepository repo = context.getBean(PostSellRepository.class);
//		System.out.println(repo.findAll());
	}

}
