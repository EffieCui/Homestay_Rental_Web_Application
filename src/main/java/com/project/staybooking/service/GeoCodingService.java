package com.project.staybooking.service;

import org.springframework.stereotype.Service;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.project.staybooking.exception.GeoCodingException;
import com.project.staybooking.exception.InvalidStayAddressException;
import com.project.staybooking.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.io.IOException;

@Service
public class GeoCodingService {
    private GeoApiContext context;

    @Autowired
    public GeoCodingService(GeoApiContext context) {
        this.context = context;
    }

    public Location getLatLng(Long id, String address) throws GeoCodingException {
        try {
            GeocodingResult result = GeocodingApi.geocode(context, address).await()[0]; //通过第0个元素去判断 （最好只有一个结果。如果有多个，且第0个都是partial match，后面肯定也是）

            if (result.partialMatch) { //精确匹配还是模糊匹配
                throw new InvalidStayAddressException("Failed to find stay address");
            }
            return new Location(id, new GeoPoint(result.geometry.location.lat, result.geometry.location.lng));
        } catch (IOException | ApiException | InterruptedException e) {
            e.printStackTrace();
            throw new GeoCodingException("Failed to return geo location stay address");
        }
    }
}
