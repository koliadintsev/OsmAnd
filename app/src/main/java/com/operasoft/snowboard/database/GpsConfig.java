package com.operasoft.snowboard.database;

import com.operasoft.android.gps.ConfigurationProfile;

public class GpsConfig extends Dto {

	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	private String name;

	@Column(name = "heartbeat_delay_on")
	private int heartbeatDelayOn;

	@Column(name = "heartbeat_delay_off")
	private int heartbeatDelayOff;

	@Column(name = "heading_change")
	private int headingChange;

	@Column(name = "movement_start_delay")
	private int movementStartDelay;

	@Column(name = "movement_start_speed")
	private int movementStartSpeed;

	@Column(name = "movement_stop_delay")
	private int movementStopDelay;

	@Column(name = "movement_stop_speed")
	private int movementStopSpeed;

	@Column(name = "acceleration")
	private int acceleration;

	@Column(name = "braking")
	private int braking;

	@Column(name = "low_battery")
	private double lowBattery;

	public String getName() {
		return name;
	}

	public void fetch(ConfigurationProfile configProfile) {
		//TODO 00 Christian, confirm please
		configProfile.setHeartbeat(heartbeatDelayOn * 1000);
		configProfile.setMovementStart(movementStartDelay * 1000);
		configProfile.setMovementStop(movementStopDelay * 1000);
		configProfile.setMovementStartSpeed(movementStartSpeed);
		configProfile.setDirectionChange(headingChange);
		configProfile.setMovementStopSpeed(movementStopSpeed);
		configProfile.setAcceleration(acceleration);
		configProfile.setBraking(braking);
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHeartbeatDelayOn() {
		return heartbeatDelayOn;
	}

	public void setHeartbeatDelayOn(int heartbeatDelayOn) {
		this.heartbeatDelayOn = heartbeatDelayOn;
	}

	public int getHeartbeatDelayOff() {
		return heartbeatDelayOff;
	}

	public void setHeartbeatDelayOff(int heartbeatDelayOff) {
		this.heartbeatDelayOff = heartbeatDelayOff;
	}

	public int getHeadingChange() {
		return headingChange;
	}

	public void setHeadingChange(int headingChange) {
		this.headingChange = headingChange;
	}

	public int getMovementStartDelay() {
		return movementStartDelay;
	}

	public void setMovementStartDelay(int movementStartDelay) {
		this.movementStartDelay = movementStartDelay;
	}

	public int getMovementStartSpeed() {
		return movementStartSpeed;
	}

	public void setMovementStartSpeed(int movementStartSpeed) {
		this.movementStartSpeed = movementStartSpeed;
	}

	public int getMovementStopDelay() {
		return movementStopDelay;
	}

	public void setMovementStopDelay(int movementStopDelay) {
		this.movementStopDelay = movementStopDelay;
	}

	public int getMovementStopSpeed() {
		return movementStopSpeed;
	}

	public void setMovementStopSpeed(int movementStopSpeed) {
		this.movementStopSpeed = movementStopSpeed;
	}

	public int getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(int acceleration) {
		this.acceleration = acceleration;
	}

	public int getBraking() {
		return braking;
	}

	public void setBraking(int braking) {
		this.braking = braking;
	}

	public double getLowBattery() {
		return lowBattery;
	}

	public void setLowBattery(double lowBattery) {
		this.lowBattery = lowBattery;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}