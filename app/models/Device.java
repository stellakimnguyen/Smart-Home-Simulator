package models;

public abstract class Device {
	private String name;
	private String status;
	private Location location;

	// Common strings
	public static final String actionOn = "turn on";
	public static final String actionOff = "turn off";
	public static final String statusOn = "on";
	public static final String statusOff = "off";
	public static final String actionOpen = "open";
	public static final String actionClose = "close";
	public static final String statusOpen = "open";
	public static final String statusClosed = "closed";

	public Device(String name, Location location) {
		this.name = name;
		this.location = location;
		this.status = "";
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * Default action system.
	 * @param action String with the action code to be performed
	 * @return true if the action was performed, false otherwise.
	 */
	public boolean doAction(String action){
		return false; // By default, no action is performed
	}
}
