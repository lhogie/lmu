package org.lucci.lmu;

/*
 * Created on Oct 10, 2004
 */

/**
 * @author luc.hogie
 */
public class InheritanceRelation extends Relation
{
	public InheritanceRelation(Entity tail, Entity head)
	{
		super(tail, head);
	}

	public Entity getSuperEntity()
	{
		return getHeadEntity();
	}
	
	public void setSuperEntity(Entity e)
	{
		setHeadEntity(e);
	}

	public Entity getSubEntity()
	{
		return getTailEntity();
	}

	public void setSubEntity(Entity e)
	{
		setTailEntity(e);
	}
}
