package com.example.demo.service;

import java.util.List;
import com.example.demo.dto.response.CarTypeResponse;

public interface CarTypeService {

	List<CarTypeResponse> getCarTypesByHubId(Integer hubId);

}