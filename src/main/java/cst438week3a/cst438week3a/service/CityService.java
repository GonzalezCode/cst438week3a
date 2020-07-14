package cst438week3a.cst438week3a.service;

import cst438week3a.cst438week3a.domain.*;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class CityService {
	
	@Autowired
	private CityRepository cityRepository;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private WeatherService weatherService;
	
	public CityInfo getCityInfo(String cityName) {
		List<City> cities = cityRepository.findByName(cityName);
		if (cities.size() == 0) {
			return null;
		}
		else {
			City city = cities.get(0);
			Country country = countryRepository.findByCode(city.getCountryCode());
			TempAndTime tempAndTime = weatherService.getTempAndTime(city.getName());
			double temp = (tempAndTime.temp - 273.15) * 9.0/5.0 + 32.0;

			// need to convert time to normal time
			// convert epoch time to regular time
			long timeOffset = tempAndTime.getTime() + tempAndTime.getTimezone();
			Instant unixTime = Instant.ofEpochSecond(timeOffset);
			int hour = unixTime.atZone(ZoneOffset.UTC).getHour();
			int minute = unixTime.atZone(ZoneOffset.UTC).getMinute();
			String time = (String.format("%d:%d", hour, minute));

			return new CityInfo(city, country.getName(), temp, time);
		}
	}

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private FanoutExchange fanout;

	public void requestReservation(
			String cityName,
			String level,
			String email) {
		String msg  = "{\"cityName\": \""+ cityName +
				"\" \"level\": \""+level+
				"\" \"email\": \""+email+"\"}" ;
		System.out.println("Sending message:"+msg);
		rabbitTemplate.convertSendAndReceive(
				fanout.getName(),
				"",   // routing key none.
				msg);
	}
	
}
