package org.lucci.lmu.input;

import java.lang.reflect.Field;
import java.util.List;

import org.lucci.lmu.Entity;

public interface AnalyzerListener
{

	void analyzingAttributes(Class<?> clazz);

	void foundClasses(List<Class<?>> allClasses);

	void creatingEntity(Entity entity);

	void analyzingField(Field field);

	void NoClassDefFoundError(Entity entity, Class<?> clazz,  String className);

	void foundClass(Class<?> c);

}
