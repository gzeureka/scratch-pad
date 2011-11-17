package util.condition;

import java.util.HashMap;
import java.util.Map;

public class FieldMap {
	private Map<String, String> map = new HashMap<String, String>();

	public FieldMap() {
	}

	public FieldMap(Map map) {
		for (Object key : map.keySet()) {
			if (key == null || map.get(key) == null)
				throw new IllegalArgumentException(
						"key and value cannot be null");
			this.map.put(key.toString(), map.get(key).toString());
		}
	}

	public FieldMap map(String key, String value) {
		return put(key, value);
	}

	public FieldMap put(String key, String value) {
		map.put(key, value);
		return this;
	}

	public String get(String key) {
		return map.get(key);
	}
}
