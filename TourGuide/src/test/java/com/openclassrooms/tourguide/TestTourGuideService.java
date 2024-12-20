package com.openclassrooms.tourguide;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import tripPricer.Provider;

public class TestTourGuideService {

	@Test
	public void getUserLocation() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	@Test
	public void addUser() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		User retrivedUser = tourGuideService.getUser(user.getUserName());
		User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

		tourGuideService.tracker.stopTracking();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}

	@Test
	public void getAllUsers() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void trackUser() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void trackAllUsersLocation() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(100);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		List<User> allUser = tourGuideService.getAllUsers();
		HashMap<UUID,Integer> userIdUserLocationMapBeforeTracking = new HashMap<>();
		for (User user : allUser) {
			userIdUserLocationMapBeforeTracking.put(user.getUserId(), user.getVisitedLocations().size());
		}

		tourGuideService.trackAllUserLocation(allUser);

		List<User> allUserAfter = tourGuideService.getAllUsers();
		HashMap<UUID,Integer> userIdUserLocationMapAfterTracking = new HashMap<>();
		for (User user : allUserAfter) {
			userIdUserLocationMapAfterTracking.put(user.getUserId(), user.getVisitedLocations().size());
		}

		for (User user : allUser) {
			UUID userId = user.getUserId();
			Integer locationSizeBefore = userIdUserLocationMapBeforeTracking.get(userId);
			Integer locationSizeAfter = userIdUserLocationMapAfterTracking.get(userId);
			assertTrue(locationSizeBefore < locationSizeAfter);
		}





	}


	@Test
	public void get5NearbyAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		LinkedHashMap<Attraction,Double> attractions = tourGuideService.getNearByAttractions(visitedLocation);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, attractions.size());
	}


	@Test
	public void getNearbyAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		tourGuideService.setNearByAttractionNumber(gpsUtil.getAttractions().size());


		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		LinkedHashMap<Attraction,Double> allAttractionDistance = tourGuideService.getNearByAttractions(visitedLocation);

		tourGuideService.setNearByAttractionNumberDefault();
		LinkedHashMap<Attraction,Double> defaultAttractionDistance = tourGuideService.getNearByAttractions(visitedLocation);

		tourGuideService.tracker.stopTracking();

		Iterator<Attraction> keyIteratorDefaultAttractionDistance = defaultAttractionDistance.keySet().iterator();
		Iterator<Attraction> keyIteratorAllAttractionDistance = allAttractionDistance.keySet().iterator();
		while (keyIteratorDefaultAttractionDistance.hasNext()) {
			Attraction attractionDefaultKey = keyIteratorDefaultAttractionDistance.next();
			Attraction attractionAllKey = keyIteratorAllAttractionDistance.next();
            assertEquals(attractionDefaultKey.attractionName, attractionAllKey.attractionName);
		}
	}

	@Test
	public void getNearbyAttractionsOrdered() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		tourGuideService.setNearByAttractionNumber(gpsUtil.getAttractions().size());

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		LinkedHashMap<Attraction,Double> attractionDistance = tourGuideService.getNearByAttractions(visitedLocation);

		tourGuideService.tracker.stopTracking();

		Iterator<Attraction> keyIteratorAttractionDistance = attractionDistance.keySet().iterator();
		while (keyIteratorAttractionDistance.hasNext()) {
			Attraction attractionKey = keyIteratorAttractionDistance.next();
			Double distance1 = attractionDistance.get(attractionKey);
			Attraction attractionKey2  = keyIteratorAttractionDistance.next();
			double distance2 = attractionDistance.get(attractionKey2);
			assertTrue(distance1 > distance2);
		}
	}

	public void getTripDeals() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(10, providers.size());
	}

}
