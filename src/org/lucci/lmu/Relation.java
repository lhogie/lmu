package org.lucci.lmu;

/*
 * Created on Oct 10, 2004
 */

/**
 * @author luc.hogie
 */
public class Relation extends ModelElement
{
	private Entity tailEntity, headEntity;

	public Relation(Entity tail, Entity head)
	{
		setHeadEntity(head);
		setTailEntity(tail);
	}

	public Entity getHeadEntity()
	{
		return headEntity;
	}

	public void setHeadEntity(Entity headEntity)
	{
		if (headEntity == null) throw new NullPointerException();

		this.headEntity = headEntity;
	}

	public Entity getTailEntity()
	{
		return tailEntity;
	}

	public void setTailEntity(Entity tailEntity)
	{
		if (tailEntity == null) throw new NullPointerException();

		this.tailEntity = tailEntity;
	}

	public void reverse()
	{
		Entity tmp = headEntity;
		headEntity = tailEntity;
		tailEntity = tmp;
	}

	public boolean involve(Entity entity)
	{
		return getTailEntity() == entity || getHeadEntity() == entity;
	}

	public Entity getEntityRelatedTo(Entity entity)
	{
		if (entity == null) throw new NullPointerException();

		if (entity == this.tailEntity)
		{
			return this.headEntity;
		}
		else if (entity == this.headEntity)
		{
			return this.tailEntity;
		}
		else
		{
			throw new IllegalArgumentException("entity is not involved in relation");
		}
	}

	@Override
	public String toString()
	{
		return getClass().getName() + ": " + getTailEntity().getName() + " ---> " + getHeadEntity().getName();
	}
	

	@Override
	public boolean equals(Object obj)
	{
		return obj.getClass() == getClass() && ((Relation) obj).getTailEntity() == getTailEntity() && ((Relation) obj).getHeadEntity() == getHeadEntity();
	}

}
