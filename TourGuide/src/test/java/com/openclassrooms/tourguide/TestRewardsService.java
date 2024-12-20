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
import com.openclassrooms.tourguide.user.UserReward;

public class TestRewardsService {

	@Test
	public void userGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}

	@Test
	public void addReward() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		Attraction attraction = gpsUtil.getAttractions().get(0);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), attraction, new Date());

		UserReward userReward = new UserReward(visitedLocation, attraction, 50);
		user.addUserReward(userReward);

		tourGuideService.tracker.stopTracking();
		assertTrue(user.getUserRewards().get(0).getRewardPoints() == 50);

	}



	@Test
	public void addTwoRewardsForDifferentAttraction() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		Attraction attraction1 = gpsUtil.getAttractions().get(0);
		VisitedLocation visitedLocation1 = new VisitedLocation(user.getUserId(), attraction1, new Date());
		UserReward userReward1 = new UserReward(visitedLocation1, attraction1, 50);

		Attraction attraction2 = gpsUtil.getAttractions().get(1);
		VisitedLocation visitedLocation2 = new VisitedLocation(user.getUserId(), attraction2, new Date());
		UserReward userReward2 = new UserReward(visitedLocation2, attraction2, 10);

		user.addUserReward(userReward1);
		user.addUserReward(userReward2);

		tourGuideService.tracker.stopTracking();
		assertEquals(user.getUserRewards().size(), 2);

	}

	@Test
	public void addTwoRewardsForSameAttraction() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		Attraction attraction = gpsUtil.getAttractions().get(0);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), attraction, new Date());

		UserReward userReward = new UserReward(visitedLocation, attraction, 50);
		user.addUserReward(userReward);


		tourGuideService.tracker.stopTracking();
		assertEquals(user.getUserRewards().size(), 1);
	}

	@Test
	public void calculateReward() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), attraction, new Date());

		user.addToVisitedLocations(visitedLocation);

		int rewardSizeBefore = user.getUserRewards().size();
		rewardsService.calculateRewards(user);
		int rewardSizeAfter = user.getUserRewards().size();

		tourGuideService.tracker.stopTracking();

		assertTrue(rewardSizeBefore < rewardSizeAfter);
	}

	@Test
	public void calculateRewardAttractionNotNear() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		rewardsService.setProximityBuffer(Integer.MIN_VALUE);

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), attraction, new Date());

		user.addToVisitedLocations(visitedLocation);

		int rewardSizeBefore = user.getUserRewards().size();
		rewardsService.calculateRewards(user);
		int rewardSizeAfter = user.getUserRewards().size();

		tourGuideService.tracker.stopTracking();

		assertTrue(rewardSizeBefore == rewardSizeAfter);
	}

	@Test
	public void calculateRewardAllUser() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);


		List<User> allUser = new ArrayList<>();
		HashMap<UUID, Integer> userRewardSizeBefore = new HashMap<>();

		for ( int i =0; i<10; i++) {
			User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
			Attraction attraction = gpsUtil.getAttractions().get(0);
			VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), attraction, new Date());
			user.addToVisitedLocations(visitedLocation);
			allUser.add(user);
			userRewardSizeBefore.put(user.getUserId(), user.getUserRewards().size());
		}

		rewardsService.calculateRewardAllUser(allUser);

		List<User> allUserAfter = tourGuideService.getAllUsers();
		HashMap<UUID, Integer> userRewardSizeAfter = new HashMap<>();

		for (User user : allUserAfter) {
			userRewardSizeAfter.put(user.getUserId(), user.getUserRewards().size());
		}

		tourGuideService.tracker.stopTracking();

		for (User user : allUserAfter) {
			UUID userId = user.getUserId();
			Integer userRewardBefore = userRewardSizeBefore.get(userId);
			Integer userRewardAfter = userRewardSizeAfter.get(userId);
			assertTrue(userRewardBefore < userRewardAfter);
		}
	}

	@Test
	public void isWithinAttractionProximity() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}


	@Test
	public void nearAllAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = tourGuideService.getAllUsers().get(0);

		rewardsService.calculateRewards(user);


		List<UserReward> userRewards = tourGuideService.getUserRewards(user);
		tourGuideService.tracker.stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}

}
