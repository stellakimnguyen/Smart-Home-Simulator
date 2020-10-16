package models;

public class Connection extends Device {
	private Location secondLocation;

	public Connection(String name, Location location, Location secondLocation) {
		super(name, location);
		super.setStatus(Device.statusOpen);
		this.secondLocation = secondLocation;
	}

	public Location getSecondLocation() {
		return secondLocation;
	}
	public void setSecondLocation(Location secondLocation) {
		this.secondLocation = secondLocation;
	}

	@Override
	public void setStatus(String status) {} // do nothing
}
