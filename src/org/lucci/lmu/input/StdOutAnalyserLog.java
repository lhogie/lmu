package org.lucci.lmu.input;

import java.lang.reflect.Field;
import java.util.List;

import org.lucci.lmu.Entity;

public class StdOutAnalyserLog implements AnalyzerListener
{

	@Override
	public void analyzingAttributes(Class<?> clazz)
	{
		System.out.println("analyzing attributes of " + clazz);

	}

	@Override
	public void foundClasses(List<Class<?>> allClasses)
	{
		System.out.println("found " + allClasses.size() + " classes");

	}

	@Override
	public void creatingEntity(Entity entity)
	{
		System.out.println("creating entity " + entity.getName());
	}

	@Override
	public void analyzingField(Field field)
	{
		System.out.println("analyzing field " + field);
	}

	@Override
	public void NoClassDefFoundError(Entity entity, Class<?> clazz, String className)
	{
		System.err.println("NoClassDefFoundError " + className);
	}

	@Override
	public void foundClass(Class<?> c)
	{
		System.out.println("found class " + c);

	}

}
