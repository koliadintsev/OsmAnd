package com.operasoft.snowboard.database;

/**
 * This DTO carries the information related to an Incident event created by the driver
 * that must be pushed towards the dispatcher user on Snowman.
 * @author Christian
 *
 */
public class Incident {
	private ServiceLocation serviceLocation = new ServiceLocation();
	private String userId;

}
