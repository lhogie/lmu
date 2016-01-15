package org.lucci.lmu;


/*
 * Created on Oct 2, 2004
 */

/**
 * @author luc.hogie
 */
public class AssociationRelation extends Relation
{
	public enum TYPE { ASSOCIATION, AGGREGATION, COMPOSITION, DIRECTION}
	
	private TYPE type = TYPE.ASSOCIATION;
	private String cardinality;
	private String label;

	public AssociationRelation(Entity tail, Entity head)
	{
		super(tail, head);
	}
    public String getLabel()
	{
		return label;
	}
	public void setLabel(String label)
	{
		this.label = label;
	}
	public TYPE getType()
	{
		return type;
	}
	public void setType(TYPE type)
	{
		if (type == null)
			throw new NullPointerException();
		
		this.type = type;
	}
	public String getCardinality()
	{
		return cardinality;
	}
	public void setCardinality(String cardinality)
	{
		this.cardinality = cardinality;
	}
	
	public Entity getContainerEntity()
	{
		return getHeadEntity();
	}
	
	public void setContainerEntity(Entity e)
	{
		setHeadEntity(e);
	}
	
	public Entity getContainedEntity()
	{
		return getTailEntity();
	}

	public void setContainedEntity(Entity e)
	{
		setTailEntity(e);
	}
}
