package uni.fmi.masters.entities;

public class EntityWork {
	private String name;
	private Integer createdY;
	
	public EntityWork(String name, Integer createdY) {
		super();
		this.name = name;
		this.createdY = createdY;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCreatedY() {
		return createdY;
	}

	public void setCreatedY(Integer createdY) {
		this.createdY = createdY;
	}

	public String toString() {
		StringBuilder ret = new StringBuilder("");
		if (this.name != null) {
			ret.append(this.name);
		}
		else {
			ret.append("?");
		}
		if (this.createdY != null) {
			ret.append(" (" + this.createdY + ")");
		}		
		return ret.toString();
	}
}

