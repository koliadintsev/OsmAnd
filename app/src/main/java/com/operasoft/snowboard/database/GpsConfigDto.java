package com.operasoft.snowboard.database;

public class GpsConfigDto extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double heading_threshold;
	private double heading_speed_delay;
	private double heading_speed_threshold;
	private long heartbeat_delay;

	public double getHeading_threshold() {
		return heading_threshold;
	}

	public void setHeading_threshold(double heading_threshold) {
		this.heading_threshold = heading_threshold;
	}

	public double getHeading_speed_delay() {
		return heading_speed_delay;
	}

	public void setHeading_speed_delay(double heading_speed_delay) {
		this.heading_speed_delay = heading_speed_delay;
	}

	public double getHeading_speed_threshold() {
		return heading_speed_threshold;
	}

	public void setHeading_speed_threshold(double heading_speed_threshold) {
		this.heading_speed_threshold = heading_speed_threshold;
	}

	public long getHeartbeat_delay() {
		return heartbeat_delay;
	}

	public void setHeartbeat_delay(long heartbeat_delay) {
		this.heartbeat_delay = heartbeat_delay;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}