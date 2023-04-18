package localsearch.domainspecific.vehiclerouting.apps.sharedaride;

import java.util.ArrayList;
import java.util.HashMap;

import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.RelatedPointBuckets;

public class SearchInput {
	public ArrayList<Point> pickupPoints;
	public ArrayList<Point> deliveryPoints;
	public ArrayList<Point> rejectPoints;
	public ArrayList<Point> rejectPickupGoods;
	public ArrayList<Point> rejectPickupPeoples;
	public HashMap<Point, Integer> earliestAllowedArrivalTime;
	public HashMap<Point, Integer> serviceDuration;
	public HashMap<Point, Integer> lastestAllowedArrivalTime;
	public HashMap<Point,Point> pickup2DeliveryOfGood;
	public HashMap<Point,Point> pickup2DeliveryOfPeople;
	public HashMap<Point, Point> pickup2Delivery;
	public HashMap<Point,Point> delivery2Pickup;
	
	public SearchInput(ArrayList<Point> pickupPoints,
			ArrayList<Point> deliveryPoints, ArrayList<Point> rejectPoints,
			ArrayList<Point> rejectPickupGoods,
			ArrayList<Point> rejectPickupPeoples,
			HashMap<Point, Integer> earliestAllowedArrivalTime,
			HashMap<Point, Integer> serviceDuration,
			HashMap<Point, Integer> lastestAllowedArrivalTime,
			HashMap<Point, Point> pickup2DeliveryOfGood,
			HashMap<Point, Point> pickup2DeliveryOfPeople,
			HashMap<Point, Point> pickup2Delivery,
			HashMap<Point, Point> delivery2Pickup) {
		
		super();
		this.pickupPoints = pickupPoints;
		this.deliveryPoints = deliveryPoints;
		this.rejectPoints = rejectPoints;
		this.rejectPickupGoods = rejectPickupGoods;
		this.rejectPickupPeoples = rejectPickupPeoples;
		this.earliestAllowedArrivalTime = earliestAllowedArrivalTime;
		this.serviceDuration = serviceDuration;
		this.lastestAllowedArrivalTime = lastestAllowedArrivalTime;
		this.pickup2DeliveryOfGood = pickup2DeliveryOfGood;
		this.pickup2DeliveryOfPeople = pickup2DeliveryOfPeople;
		this.pickup2Delivery = pickup2Delivery;
		this.delivery2Pickup = delivery2Pickup;
	}

	public ArrayList<Point> getPickupPoints() {
		return pickupPoints;
	}

	public void setPickupPoints(ArrayList<Point> pickupPoints) {
		this.pickupPoints = pickupPoints;
	}

	public ArrayList<Point> getDeliveryPoints() {
		return deliveryPoints;
	}

	public void setDeliveryPoints(ArrayList<Point> deliveryPoints) {
		this.deliveryPoints = deliveryPoints;
	}

	public ArrayList<Point> getRejectPoints() {
		return rejectPoints;
	}

	public void setRejectPoints(ArrayList<Point> rejectPoints) {
		this.rejectPoints = rejectPoints;
	}

	public ArrayList<Point> getRejectPickupGoods() {
		return rejectPickupGoods;
	}

	public void setRejectPickupGoods(ArrayList<Point> rejectPickupGoods) {
		this.rejectPickupGoods = rejectPickupGoods;
	}

	public ArrayList<Point> getRejectPickupPeoples() {
		return rejectPickupPeoples;
	}

	public void setRejectPickupPeoples(ArrayList<Point> rejectPickupPeoples) {
		this.rejectPickupPeoples = rejectPickupPeoples;
	}

	public HashMap<Point, Integer> getEarliestAllowedArrivalTime() {
		return earliestAllowedArrivalTime;
	}

	public void setEarliestAllowedArrivalTime(
			HashMap<Point, Integer> earliestAllowedArrivalTime) {
		this.earliestAllowedArrivalTime = earliestAllowedArrivalTime;
	}

	public HashMap<Point, Integer> getServiceDuration() {
		return serviceDuration;
	}

	public void setServiceDuration(HashMap<Point, Integer> serviceDuration) {
		this.serviceDuration = serviceDuration;
	}

	public HashMap<Point, Integer> getLastestAllowedArrivalTime() {
		return lastestAllowedArrivalTime;
	}

	public void setLastestAllowedArrivalTime(
			HashMap<Point, Integer> lastestAllowedArrivalTime) {
		this.lastestAllowedArrivalTime = lastestAllowedArrivalTime;
	}

	public HashMap<Point, Point> getPickup2DeliveryOfGood() {
		return pickup2DeliveryOfGood;
	}

	public void setPickup2DeliveryOfGood(HashMap<Point, Point> pickup2DeliveryOfGood) {
		this.pickup2DeliveryOfGood = pickup2DeliveryOfGood;
	}

	public HashMap<Point, Point> getPickup2DeliveryOfPeople() {
		return pickup2DeliveryOfPeople;
	}

	public void setPickup2DeliveryOfPeople(
			HashMap<Point, Point> pickup2DeliveryOfPeople) {
		this.pickup2DeliveryOfPeople = pickup2DeliveryOfPeople;
	}

	public HashMap<Point, Point> getPickup2Delivery() {
		return pickup2Delivery;
	}

	public void setPickup2Delivery(HashMap<Point, Point> pickup2Delivery) {
		this.pickup2Delivery = pickup2Delivery;
	}

	public HashMap<Point, Point> getDelivery2Pickup() {
		return delivery2Pickup;
	}

	public void setDelivery2Pickup(HashMap<Point, Point> delivery2Pickup) {
		this.delivery2Pickup = delivery2Pickup;
	}
}
