package uni.fmi.masters.entities;

public class EntityPerson {
	private String nameFirst;
	private String nameSur;
	private String nameFamily;
	private String nameAlt;
	private Integer bornD;
	private Integer bornM;
	private Integer bornY;
	private Integer diedD;
	private Integer diedM;
	private Integer diedY;
	
	public EntityPerson(String nameFirst, String nameSur, String nameFamily, String nameAlt, Integer bornD,
			Integer bornM, Integer bornY, Integer diedD, Integer diedM, Integer diedY) {
		this.nameFirst = nameFirst;
		this.nameSur = nameSur;
		this.nameFamily = nameFamily;
		this.nameAlt = nameAlt;
		this.bornD = bornD;
		this.bornM = bornM;
		this.bornY = bornY;
		this.diedD = diedD;
		this.diedM = diedM;
		this.diedY = diedY;
	}

	public String getNameFirst() {
		return nameFirst;
	}

	public void setNameFirst(String nameFirst) {
		this.nameFirst = nameFirst;
	}

	public String getNameSur() {
		return nameSur;
	}

	public void setNameSur(String nameSur) {
		this.nameSur = nameSur;
	}

	public String getNameFamily() {
		return nameFamily;
	}

	public void setNameFamily(String nameFamily) {
		this.nameFamily = nameFamily;
	}
	
	public String getRealName() {
		StringBuilder ret = new StringBuilder();
		if (this.nameFirst != null) {
			ret.append(this.nameFirst);
		}
		else {
			ret.append("?");
		}
		if (this.nameSur != null) {
			ret.append(" " + this.nameSur);
		}
		if (this.nameFamily != null) {
			ret.append(" " + this.nameFamily);
		}
		return ret.toString();
	}

	public String getNameAlt() {
		return this.nameAlt;
	}

	public void setNameAlt(String nameAlt) {
		this.nameAlt = nameAlt;
	}

	public Integer getBornD() {
		return bornD;
	}

	public void setBornD(Integer bornD) {
		this.bornD = bornD;
	}

	public Integer getBornM() {
		return bornM;
	}

	public void setBornM(Integer bornM) {
		this.bornM = bornM;
	}

	public Integer getBornY() {
		return bornY;
	}

	public void setBornY(Integer bornY) {
		this.bornY = bornY;
	}
	
	public String getBornDate() {
		StringBuilder ret = new StringBuilder("");
		if (this.bornD != null) {
			ret.append(this.bornD);
		}
		else {
			ret.append("?");
		}
		ret.append(".");
		if (this.bornM != null) {
			ret.append(this.bornM);
		}
		else {
			ret.append("?");
		}
		ret.append(".");
		if (this.bornY != null) {
			ret.append(this.bornY);
		}
		else {
			ret.append("?");
		}
		return ret.toString();
	}

	public void setDiedD(Integer diedD) {
		this.diedD = diedD;
	}

	public Integer getDiedM() {
		return diedM;
	}

	public void setDiedM(Integer diedM) {
		this.diedM = diedM;
	}

	public Integer getDiedY() {
		return diedY;
	}

	public void setDiedY(Integer diedY) {
		this.diedY = diedY;
	}
	
	public String getDiedDate() {
		StringBuilder ret = new StringBuilder("");
		if (this.diedD != null) {
			ret.append(this.diedD);
		}
		else {
			ret.append("?");
		}
		ret.append(".");
		if (this.diedM != null) {
			ret.append(this.diedM);
		}
		else {
			ret.append("?");
		}
		ret.append(".");
		if (this.diedY != null) {
			ret.append(this.diedY);
		}
		else {
			ret.append("?");
		}
		return ret.toString();
	}
	
	public String toString() {
		StringBuilder ret = new StringBuilder("");
		if (this.nameFirst != null) {
			ret.append(this.nameFirst);
		}
		if (this.nameSur != null) {
			if (!ret.equals("")) {
				ret.append(" ");
			}
			ret.append(this.nameSur);
		}
		if (this.nameFamily != null) {
			if (!ret.equals("")) {
				ret.append(" ");
			}
			ret.append(this.nameFamily);
		}
		if (this.nameAlt != null) {
			if (ret.equals("")) {
				ret.append(this.nameAlt);
			}
			else {
				ret.append(" - " + this.nameAlt);
			}
		}
		if (this.bornY != null || this.diedY != null) {
			if (!ret.equals("")) {
				ret.append(" ");
			}
			else {
				ret.append("? ");
			}
			if (this.bornY != null && this.diedY != null) {
				ret.append("(" + this.bornY + "-" + this.diedY + ")");
			}
			if (this.bornY != null && this.diedY == null) {
				ret.append("(" + this.bornY + "-?)");
			}
			if (this.bornY == null && this.diedY != null) {
				ret.append("(?-" + this.diedY + ")");
			}
		}
		if (ret.equals("")) { // if unknown
			return "?";
		}
		
		return ret.toString();
	}
	
	
}
