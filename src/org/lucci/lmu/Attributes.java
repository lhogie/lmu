package org.lucci.lmu;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import toools.collections.Collections;
import toools.text.TextUtilities;


/*
 * Created on Oct 10, 2004
 */

/**
 * @author luc.hogie
 */
public class Attributes
{
	public static Collection<Entity> findAllModelElementsInAttribute(Attribute attribute)
	{
		Collection<Entity> res = new Vector<Entity>();
		res.add(attribute.getType());
		return res;
	}

	public static Collection<Attribute> findAllAttributes(Collection<Entity> entities)
	{
		Collection<Attribute> c = new Vector<Attribute>();

		for (Entity e : entities)
		{
			c.addAll(e.getAttributes());
		}

		return c;
	}

	public static void generateGettersForPrivateAttributes(Collection<Entity> entities)
	{
		for (Entity entity : entities)
		{
			Collection<Attribute> privateAttributes = Collections.filter(entity.getAttributes(), new Filters.VisiblityFilter(Visibility.PRIVATE));
			generateGetters(privateAttributes, entity);
		}
	}

	public static void generateGetters(Collection<Attribute> attributes, Entity entity)
	{
		for (Attribute attribute : attributes)
		{
			Operation getter = new Operation();
			getter.setName("get" + TextUtilities.capitalizeWord(attribute.getName()));
			getter.setType(attribute.getType());
			getter.setVisibility(Visibility.PUBLIC);
			entity.getOperations().add(getter);
		}
	}

	public static void generateSettersForPrivateAttributes(Collection<Entity> entities, Model model)
	{
		for (Entity entity : entities)
		{
			Collection privateAttributes = Collections.filter(entity.getAttributes(), new Filters.VisiblityFilter(Visibility.PRIVATE));
			generateSetters(privateAttributes, entity, model);
		}
	}

	public static void generateSetters(Collection<Attribute> attributes, Entity entity, Model model)
	{
		Iterator attributeIterator = attributes.iterator();

		while (attributeIterator.hasNext())
		{
			Attribute attribute = (Attribute) attributeIterator.next();

			Operation setter = new Operation();
			setter.setName("set" + TextUtilities.capitalizeWord(attribute.getName()));

			setter.getParameterList().add(attribute.getType());

			setter.setType(Entities.findEntityByName(model, "void"));

			setter.setVisibility(Visibility.PUBLIC);
			entity.getOperations().add(setter);
		}
	}

	public static Attribute findAttributeByName(Entity entity, String name)
	{
		for (Attribute a : entity.getAttributes())
		{
			if (a.getName().equals(name))
			{
				return a;
			}
		}

		return null;
	}

}
