package Tools.Core.Files.Data;

public abstract class DataSubtype extends DataClass {
	private String type;
	
	public String getDataType() {
		return type;
	}
	public void setDataType(String type) {
		this.type = type;
	}
	
}
