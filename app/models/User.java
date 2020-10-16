package models;

public class User {
	private String name;
	private userType type;
	private Location location;

	public enum userType {
		Parent,
		Child,
		Guest,
		Stranger
	}

	public User(String name, userType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public userType getType() {
		return type;
	}
	public void setType(userType type) {
		this.type = type;
	}

	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
}
