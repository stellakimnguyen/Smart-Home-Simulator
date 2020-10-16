package models;

public class Location {
	private String name;
	private int temperature;
	private LocationType locationType;

	public enum LocationType {
		Outdoor, // A room that is still part of the house, but is not enclosed.
		Indoor, // A room that is part of the house
		Outside // Not in the house
	}

	public Location(String name, int temperature, LocationType locationType) {
		this.name = name;
		this.temperature = temperature;
		this.locationType = locationType;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getTemperature() {
		return temperature;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public LocationType getLocationType() {
		return locationType;
	}
	public void setLocationType(LocationType locationType) {
		if (locationType == LocationType.Outside) {
			this.locationType = LocationType.Outdoor;
		} else {
			this.locationType = locationType;
		}
	}
}
