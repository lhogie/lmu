package org.lucci.lmu;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/*
 * Created on Oct 2, 2004
 */

/**
 * @author luc.hogie
 */
public class Model extends ModelElement
{
	private Set<Entity> entities = new HashSet<Entity>();
	private Set<Relation> relations = new HashSet<Relation>();
	private Set<Group> groups = new HashSet<Group>();
	private Set<Collection<Entity>> alignments = new HashSet<Collection<Entity>>();

	public Collection<Collection<Entity>> getAlignments()
	{
		return alignments;
	}

	public Model()
	{
		addEntity(createPrimitiveEntity("int"));
		addEntity(createPrimitiveEntity("long"));
		addEntity(createPrimitiveEntity("char"));
		addEntity(createPrimitiveEntity("float"));
		addEntity(createPrimitiveEntity("double"));
		addEntity(createPrimitiveEntity("void"));
		addEntity(createPrimitiveEntity("string"));
		addEntity(createPrimitiveEntity("class"));
		addEntity(createPrimitiveEntity("boolean"));
		addEntity(createPrimitiveEntity("set"));
		addEntity(createPrimitiveEntity("sequence"));
		addEntity(createPrimitiveEntity("map"));
		addEntity(createPrimitiveEntity("date"));
		addEntity(createPrimitiveEntity("object"));
	}

	public Set<Entity> getEntities()
	{
		return Collections.unmodifiableSet(entities);
	}

	public Set<Relation> getRelations()
	{
		return Collections.unmodifiableSet(relations);
	}

	private Entity createPrimitiveEntity(String name)
	{
		if (name == null)
			throw new NullPointerException();

		Entity entity = new Entity();
		entity.setName(name);
		entity.setPrimitive(true);
		entity.setVisible(false);
		return entity;
	}

	public Collection<Group> getGroups()
	{
		return this.groups;
	}

	public void addEntity(Entity newEntity)
	{
		if (newEntity == null)
			throw new NullPointerException();

		Entity e = Entities.findEntityByName(this, newEntity.getName());

		// this entity does not yet exist
		if (e == null)
		{
			newEntity.setModel(this);
			this.entities.add(newEntity);
		}
	}

	public void addRelation(Relation newRelation)
	{
		if (newRelation == null)
			throw new NullPointerException();

		Relation r = Relations.findRelation(getRelations(), newRelation.getClass(),
				newRelation.getTailEntity(), newRelation.getHeadEntity());

		// this relation does not yet exist
		if (r == null)
		{
			this.relations.add(newRelation);
		}
	}

	public void merge(Model om)
	{
		for (Entity e : om.getEntities())
		{
			Entity correspondingLocalEntity = Entities
					.findEntityByName(this, e.getName());

			if (correspondingLocalEntity == null)
			{
				this.entities.add(e);
			}
			else
			{
				correspondingLocalEntity.merge(e);
			}
		}

		for (Relation r : om.getRelations())
		{
			Relation correspondingLocalRelation = Relations.findRelation(getRelations(),
					r.getClass(), r.getTailEntity(), r.getHeadEntity());

			if (correspondingLocalRelation == null)
			{
				this.relations.add(r);
			}
			else
			{
				throw new IllegalArgumentException("relation already exist");
			}
		}
	}

	public Set<Relation> removeEntity(Entity entity)
	{
		this.entities.remove(entity);
		return removeRelationsInvolving(entity);
	}

	/**
	 * Removes all the relations involving the given entity. Returns all the
	 * removed relations.
	 */
	public Set<Relation> removeRelationsInvolving(Entity entity)
	{
		Set<Relation> removed = new HashSet<Relation>();
		Iterator<Relation> relationIterator = this.relations.iterator();

		while (relationIterator.hasNext())
		{
			Relation rel = relationIterator.next();

			if (rel.involve(entity))
			{
				relationIterator.remove();
				removed.add(rel);
			}
		}

		return removed;
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		Model clone = new Model();

		for (Entity e : getEntities())
		{
			clone.addEntity((Entity) e.clone());
		}

		return clone;
	}



}
