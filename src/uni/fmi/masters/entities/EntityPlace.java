package uni.fmi.masters.entities;

public class EntityPlace {
	private String name;
	private String nameAlt;
	private Double latitude;
	private Double lingitude;

	public EntityPlace(String name, String nameAlt, Double latitude, Double lingitude) {
		this.name = name;
		this.nameAlt = nameAlt;
		this.latitude = latitude;
		this.lingitude = lingitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameAlt() {
		return nameAlt;
	}

	public void setNameAlt(String nameAlt) {
		this.nameAlt = nameAlt;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLingitude() {
		return lingitude;
	}

	public void setLingitude(Double lingitude) {
		this.lingitude = lingitude;
	}

	public String toString() {
		if (this.name != null && this.nameAlt == null) {
			return this.name;
		}
		else if (this.name != null && this.nameAlt != null) {
			return this.name + " (" + this.nameAlt + ")";
		}
		
		return "?"; // unknown
	}
}

