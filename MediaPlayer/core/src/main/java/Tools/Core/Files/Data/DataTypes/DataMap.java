package Tools.Core.Files.Data.DataTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import Tools.Core.Files.Util;
import Tools.Core.Files.Data.DataType;
import Tools.Core.Files.Data.Return2;
import Tools.Core.Files.Data.Exceptions.DataTypeException;
import Tools.Core.Files.Data.Exceptions.InvalidFormatException;

public class DataMap<K extends DataType, V extends DataType> extends DataType {
	private Map<K, V> data;
	private Supplier<K> keySupplier;
	private Supplier<V> valueSupplier;
	public DataMap(Supplier<K> keySupplier, Supplier<V> valueSupplier) {
		this.data = new HashMap<>();
		this.keySupplier = keySupplier;
		this.valueSupplier = valueSupplier;
	}
	public DataMap(Supplier<K> keySupplier, Supplier<V> valueSupplier, Map<K, V> data) {
		this(keySupplier, valueSupplier);
		this.data = data;
		this.created = true;
	}
	public Map<K, V> get() {
		this.created = true;
		return this.data;
	}
	public V put(K k, V v) {
		return data.put(k, v);
	}
	
	@Override
	public void setData(String s) throws DataTypeException {
		created = true;
		data.clear();
		if(s.length() < 3) return;
		while(!s.isEmpty()) {
			if(s.charAt(0) != '[') throw new InvalidFormatException("Cant read Map");
			s = s.substring(1);
			Return2<List<String>, String> ret2 = Util.getStringParts(s, ',', 2);
			s = ret2.two;
			String keyString = ret2.one.get(0);
			String valueString = ret2.one.get(1);
			K key = keySupplier.get();
			V value = valueSupplier.get();
			key.setData(keyString);
			value.setData(valueString);
			data.put(key, value);
			if(s.length() > 1) s = s.substring(2);
			else if(!s.isEmpty()) s = s.substring(1);
		}
	}
	@Override
	public String getData() {
		if(!created) return "";
		List<String> list = new ArrayList<>();
		data.forEach((m, d) -> {
			list.add("[" + Util.getSendable(m.getData()) + "," + Util.getSendable(d.getData()) + "]");
		});
		String ret = "";
		for(int i = 0; i < list.size(); i++) {
			if(ret.length() > 0) ret += ",";
			ret += list.get(i);
		}
		return ret;
	}
}
