package cst438week3a.cst438week3a.controllers;

import cst438week3a.cst438week3a.domain.*;
import cst438week3a.cst438week3a.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CityController {
	
	@Autowired
	private CityService cityService;
	
	@GetMapping("/cities/{city}")
	public String getWeather(@PathVariable("city") String cityName, Model model) {
		System.out.println("Get weather called");
		CityInfo searchedCity = cityService.getCityInfo(cityName);

		if (searchedCity == null) {
			return "city_not_found";
		}
		else {
			model.addAttribute("cityInfo", searchedCity);
			return "city_show";
		}
	}

	@PostMapping("/cities/reservation")
	public String createReservation(
			@RequestParam("city") String cityName,
			@RequestParam("email") String email,
			@RequestParam("level") String level,
			Model model) {
		System.out.println("create reservation called");
		model.addAttribute("city", cityName);
		model.addAttribute("level", level);
		model.addAttribute("email", email);
		cityService.requestReservation(cityName, level, email);
		return "request_reservation";
	}
	
}