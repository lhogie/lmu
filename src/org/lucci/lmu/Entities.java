package org.lucci.lmu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;





import org.lucci.lmu.input.ModelException;

import toools.collections.Collections;
import toools.collections.Filter;
import toools.collections.relation.HashRelation;

/*
 * Created on Oct 10, 2004
 */

/**
 * @author luc.hogie
 */
public class Entities
{

	public static Set<ModelElement> findAllModelElementsInEntity(Entity entity)
	{
		Set<ModelElement> res = new HashSet<ModelElement>();
		res.addAll(entity.getAttributes());
		res.addAll(entity.getOperations());

		{
			Iterator iterator = entity.getAttributes().iterator();

			while (iterator.hasNext())
			{
				Attribute attribute = (Attribute) iterator.next();
				res.addAll(Attributes.findAllModelElementsInAttribute(attribute));
			}
		}

		{
			Iterator iterator = entity.getOperations().iterator();

			while (iterator.hasNext())
			{
				Operation operation = (Operation) iterator.next();
				res.addAll(Operations.findAllModelElementsInOperation(operation));
			}
		}

		return res;
	}

	public static Entity findEntityByName(Model model, String name)
	{
		if (model == null) throw new NullPointerException();
		if (name == null) throw new NullPointerException();

		for (Entity entity : model.getEntities())
		{
			if (entity.getName().equals(name))
			{
				return entity;
			}
		}

		return null;
	}

	public static toools.collections.relation.Relation<String, Entity> findNameSpaces(Set<Entity> entities)
	{
		if (entities == null) throw new NullPointerException();
		toools.collections.relation.Relation<String, Entity> res = new HashRelation<String, Entity>();
		
		for (Entity entity : entities)
		{
			res.add(entity.getNamespace(), entity);
		}

		return res;
	}

	public static Set<Entity> findEntityByNameSpace(Set<Entity> entities, String name)
	{
		if (name == null) throw new NullPointerException();
		toools.collections.relation.Relation<String, Entity> r =  findNameSpaces(entities);
		Collection<Entity> entitiesInNamespace = r.getValues(name);
		return entitiesInNamespace == null ? new HashSet() : new HashSet(entitiesInNamespace);
	}

	
	public static Set<Set<Entity>> findConnectedComponents(Set<Entity> entities, final Model model)
	{
		entities = new HashSet<Entity>(entities);
		Set<Set<Entity>> connectedComponents = new HashSet<Set<Entity>>();

		while (!entities.isEmpty())
		{
			Set<Entity> someEntity = new HashSet(Collections.singleton(entities.iterator().next()));
			Set<Entity> connectedComponent = findEntitiesConnectedTo(someEntity, Integer.MAX_VALUE, model);
			connectedComponents.add(connectedComponent);
			entities.removeAll(connectedComponent);
		}

		return connectedComponents;
	}

	public static Set<Entity> findLargestConnectedComponent(Set<Entity> entities, final Model model)
	{
		return (Set<Entity>) Collections.getLargestCollections(findConnectedComponents(entities, model)).iterator().next();
	}

	public static Set<Entity> findIsolatedEntities(Set<Entity> entities, final Model model)
	{
		Set<Entity> isolatedEntities = new HashSet<Entity>();

		for (Entity entity : entities)
		{
			if (Relations.findRelationsInvolving(entity, model.getRelations()).isEmpty())
			{
				isolatedEntities.add(entity);
			}
		}

		return isolatedEntities;
	}

	public static Set<Entity> getNonPublicEntities(Set<Entity> entities)
	{
		return new HashSet<Entity>(Collections.filter(entities, new Filters.VisiblityFilter(Visibility.PUBLIC)));
	}

	public static Collection<org.lucci.lmu.Relation> removeEntities(Collection<Entity> entitiesToRemove, Model model)
	{
		Collection<org.lucci.lmu.Relation> removed = new HashSet<org.lucci.lmu.Relation>();

		for (Entity entity : entitiesToRemove)
		{
			removed.addAll(model.removeEntity(entity));
		}

		return removed;
	}

	public static Collection<Entity> getNeighborEntities(Entity entity, Model model)
	{
		Collection<Entity> neighbors = new HashSet<Entity>();
		Collection<org.lucci.lmu.Relation> relations = Relations.findRelationsInvolving(entity, model.getRelations());

		for (org.lucci.lmu.Relation relation : relations)
		{
			neighbors.add(relation.getTailEntity() == entity ? relation.getHeadEntity() : relation.getTailEntity());
		}

		return neighbors;
	}

	public static Set<Entity> getNeighborEntities(Collection<Entity> entities, Model model)
	{
		Set<Entity> neighbors = new HashSet<Entity>();

		for (Entity e : entities)
		{
			neighbors.addAll(getNeighborEntities(e, model));
		}

		return neighbors;
	}

	public static Set<Entity> findEntitiesWhoseNameMatch(Collection<Entity> entities, String regexp)
	{
		Set<Entity> matchingEntities = new HashSet<Entity>();

		for (Entity entity : entities)
		{
			if (entity.getName().matches(regexp))
			{
				matchingEntities.add(entity);
			}
		}

		return matchingEntities;
	}

	public static Set<Entity> findEntitiesConnectedTo(Set<Entity> entities, int distance, Model model)
	{
		if (distance < 0) throw new IllegalArgumentException();
		Set<Entity> res = new HashSet<Entity>(entities);

		int previousSize = -1;
		int size = res.size();

		while (distance-- > 0 && size != previousSize)
		{
			res.addAll(getNeighborEntities(res, model));
			previousSize = size;
			size = res.size();
		}

		return res;
	}

	public static void removeJavaPackageNames(Set<Entity> entities, Model model) throws ModelException
	{
		for (Entity entity : entities)
		{
			int pos = entity.getName().lastIndexOf('.');

			if (pos > 0)
			{
				String newName = entity.getName().substring(pos + 1);
				Entity clashingEntity = findEntityByName(model, newName);

				if (clashingEntity != null)
				{
					throw new ModelException("Cannot do that because then two classes would confict!", null, new NamedModelElement[]
					{ entity, clashingEntity });
				}

				entity.setName(newName);
			}
		}
	}

	public static boolean isValidEntityName(String s)
	{
		if (s.length() == 0)
		{
			return false;
		}
		else
		{
			if (!Character.isLetter(s.charAt(0)) && s.charAt(0) != '_')
			{
				return false;
			}
			else
			{
				if (s.indexOf("..") >= 0 || s.startsWith(".") || s.endsWith("."))
				{
					return false;
				}
				else
				{
					for (int i = 1; i < s.length(); ++i)
					{
						char c = s.charAt(i);

						if (!Character.isLetterOrDigit(c) && c != '_' && c != '.')
						{
							return false;
						}
					}

					return true;
				}
			}
		}
	}

	public static void retainEntities(Set<Entity> s, Model model)
	{
		removeEntities((Set<Entity>) Collections.difference(model.getEntities(), s), model);
	}

	public static Set<Entity> findNonPrimitiveEntities(Model model)
	{
		Set<Entity> matchingEntities = new HashSet<Entity>();

		for (Entity entity : model.getEntities())
		{
			if (!entity.isPrimitive())
			{
				matchingEntities.add(entity);
			}
		}

		return matchingEntities;
	}

	public static Set<Entity> findEntityWhoseNameSpaceMatches(Set<Entity> model, String regexp)
	{
		toools.collections.relation.Relation<String, Entity> namespaces = findNameSpaces(model);
		Set<Entity> res = new HashSet<Entity>();
		
		for (String namespace : namespaces.getKeys())
		{
			if (namespace.matches(regexp))
			{
				res.addAll(namespaces.getValues(namespace));
			}
		}
		
		return res;
	}

	public static Collection<? extends Entity> findSuperEntities(Entity subEntity, Model model)
	{
		Set<Entity> res = new HashSet<Entity>();
		
		for (Entity e : model.getEntities())
		{
			if (Entities.isSuperEntity(e, subEntity, model.getRelations()))
			{
				res.add(e);
			}
		}
		
		return res;
	}

	public static boolean isSuperEntity(Entity mother, Entity child, Set<Relation> among)
	{
		for (Relation r : Relations.findRelationsInvolving(among, mother, child))
		{
			if (r instanceof InheritanceRelation)
			{
				InheritanceRelation ir = (InheritanceRelation) r;
				
				if (ir.getSubEntity() == child && ir.getSuperEntity() == mother)
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static Set<Entity> getTopSubClasses(Set<Entity> entities, Set<Relation> relations)
	{
		Set<Entity> r = new HashSet<>();

		for (Entity e : entities)
		{
			if (Relations.findSubClasses(e, relations).isEmpty())
			{
				r.add(e);
			}
		}

		return r;
	}
}
