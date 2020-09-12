package uni.fmi.masters.entities;

public class EntityActivity {
	private String name;
	private Integer startedD;
	private Integer startedM;
	private Integer startedY;
	private Integer finishedD;
	private Integer finishedM;
	private Integer finishedY;
	
	public EntityActivity(String name, Integer startedD, Integer startedM, Integer startedY, Integer finishedD, Integer finishedM, Integer finishedY){
		this.name = name;
		this.startedD = startedD;
		this.startedM = startedM;
		this.startedY = startedY;
		this.finishedD = finishedD;
		this.finishedM = finishedM;
		this.finishedY = finishedY;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStartedD() {
		return startedD;
	}
	public void setStartedD(Integer startedD) {
		this.startedD = startedD;
	}
	public Integer getStartedM() {
		return startedM;
	}
	public void setStartedM(Integer startedM) {
		this.startedM = startedM;
	}
	public Integer getStartedY() {
		return startedY;
	}
	public void setStartedY(Integer startedY) {
		this.startedY = startedY;
	}
	public Integer getFinishedD() {
		return finishedD;
	}
	public void setFinishedD(Integer finishedD) {
		this.finishedD = finishedD;
	}
	public Integer getFinishedM() {
		return finishedM;
	}
	public void setFinishedM(Integer finishedM) {
		this.finishedM = finishedM;
	}
	public Integer getFinishedY() {
		return finishedY;
	}
	public void setFinishedY(Integer finishedY) {
		this.finishedY = finishedY;
	}
	
	public String toString() {
		StringBuilder ret = new StringBuilder("");
		if (this.name != null) {
			ret.append(this.name);
		}
		if (this.startedY != null || this.finishedY != null) {
			if (!ret.equals("")) {
				ret.append(" ");
			}
			else {
				ret.append("? ");
			}
			if (this.startedY != null && this.finishedY != null) {
				ret.append("(" + this.startedY + "-" + this.finishedY + ")");
			}
			else if (this.startedY != null && this.finishedY == null) {
				ret.append("(" + this.startedY + ")");
			}
			else { // this.startedY == null && this.finishedY != null
				ret.append("(?-" + this.finishedY);
			}
		}
		if (ret.equals("")) { // if unknown
			return "?"; 
		}
		return ret.toString();
		
	}

}