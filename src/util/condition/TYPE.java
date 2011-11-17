package util.condition;

import java.util.HashMap;
import java.util.Map;

public enum TYPE {
	STRING, NUMBER, DATE, DATE_TIME;

	private static Map<String, TYPE> map = new HashMap<String, TYPE>();

	static {
		for (TYPE value : TYPE.values())
			map.put(value.name(), value);
	}

	public static TYPE fromStr(String name) {
		TYPE ret = map.get(name);
		if (ret != null)
			return ret;
		else
			throw new IllegalArgumentException("No enum const " + TYPE.class
					+ "." + name);
	}

}
