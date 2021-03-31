package io.cubyz.gui;

import java.lang.reflect.InvocationTargetException;

import com.google.gson.JsonObject;

import io.cubyz.gui.element.Picture;
import io.cubyz.utils.datastructures.Registry;
import io.cubyz.utils.log.Log;

public class ComponentRegistry {
	//List of all Components
	public static final Registry ComponentList = new Registry(new Picture());	
	
	public static Component createByJson(JsonObject jsonObject) {
		Component component = (Component)ComponentList.getById(jsonObject.getAsJsonPrimitive("type").getAsString());
		try {
			Component c = component.getClass().getConstructor().newInstance();
			c.create(jsonObject);
			return c;
		} catch (Exception e) {
			Log.severe(e);
		}
		return null;
	}
}