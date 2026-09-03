package Tools.Core.Files.Data.DataTypes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Tools.Core.Files.Data.*;

public class DataDate extends DataType {
	private LocalDateTime data;
	public DataDate() {
		super();
		this.data = LocalDateTime.now();
	}
	public DataDate(LocalDateTime data) {
		this.set(data);
	}
	public DataDate(boolean created) {
		this.data = LocalDateTime.now();
		this.created = created;
	}
	public void set(LocalDateTime data) {
		this.data = data;
		this.created = true;
	}
	public LocalDateTime get() {
		return data;
	}
	public DataDate copy() {
		return new DataDate(data);
	}
	@Override
	public void setData(String s) {
		if(!s.equals("")) 
			this.data = LocalDateTime.parse(s);
		this.created = true;
	}

	@Override
	public String getData() {
		if(!created) return "";
		return data.format(DateTimeFormatter.ISO_DATE_TIME);
	}
	
	public static DataDate now() {
		return new DataDate(LocalDateTime.now());
	}

}
