package org.lucci.lmu;

import toools.util.assertion.Assertions;

/*
 * Created on Oct 2, 2004
 */

/**
 * @author luc.hogie
 */
public class TypedNamedModelElement extends NamedModelElement
{
	private Entity type;
	
	public Entity getType()
	{
		return type;
	}

	public void setType(Entity type)
	{      Assertions.ensureArgumentIsNotNull(type);

		this.type = type;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		TypedNamedModelElement clone = (TypedNamedModelElement) super.clone();
		clone.type = this.type;
		return clone;
	}
}
