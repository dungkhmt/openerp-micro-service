package com.hust.baseweb.utils;

import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
@Component
public class GoogleMapUtils {

    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(
        GoogleMapUtils.class);

    @Value("${google.api_key}")
    public void setGoogleMapApiKey(String googleMapApiKey) {
        GEO_API_CONTEXT = new GeoApiContext.Builder().apiKey(googleMapApiKey).build();
    }

    private static GeoApiContext GEO_API_CONTEXT;

    public static GeocodingResult[] queryLatLng(String address) {
        try {
            GeocodingApiRequest geocodingApiRequest = new GeocodingApiRequest(GEO_API_CONTEXT);
            geocodingApiRequest.region("vn");
            geocodingApiRequest.address(address);
            geocodingApiRequest.language("vi");
            return geocodingApiRequest.await();
        } catch (Exception ex) {
            throw new RuntimeException("query error address: " + address + ", " + ex);
        }
    }

    public static DirectionsResult queryDirection(LatLng latLng1, LatLng latLng2) {
        DirectionsApiRequest directionsApiRequest = new DirectionsApiRequest(GEO_API_CONTEXT);
        directionsApiRequest.region("vn");
        directionsApiRequest.language("vi");
        directionsApiRequest.origin(latLng1);
        directionsApiRequest.destination(latLng2);
        DirectionsResult directionsResult = null;
        try {
            directionsResult = directionsApiRequest.await();
        } catch (ApiException | InterruptedException | IOException e) {
            logger.error(e);
        }
        return directionsResult;
    }

    @Scheduled(cron = "12,42 * * * * *", zone = "GMT+7")
    private void updateConfig() throws FileNotFoundException {

        String currentApiKey = Config.updateConfig();
        if (currentApiKey != null) {
            GEO_API_CONTEXT = new GeoApiContext.Builder().apiKey(currentApiKey).build();
        }
    }
}
