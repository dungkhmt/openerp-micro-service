package com.real_estate.post.runner;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.real_estate.post.dtos.DataPage;
import com.real_estate.post.dtos.Location;
import com.real_estate.post.dtos.LocationData;
import com.real_estate.post.services.DistrictService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;


@Component
@Profile("start-crawl-district")
public class StartCrawlDistrict implements CommandLineRunner {
	private final Logger logger = LoggerFactory.getLogger(StartCrawlDistrict.class);

	ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	ModelMapper mapper;

	@Autowired
	DistrictService districtService;

	@Override
	public void run(String... args) throws Exception {
		logger.info("Starting crawl");
		LocationData locationData = objectMapper.readValue(new File("districts.json"), LocationData.class);
		System.out.println("exitcode: " + locationData.getMessage());
		DataPage dataPage = this.mapper.map(locationData.getDataPage(), DataPage.class);
		System.out.println(dataPage.getData());
		List<Location> location = Arrays.asList(dataPage.getData());
		districtService.createDistricts(location);
		context.close();
	}
}
