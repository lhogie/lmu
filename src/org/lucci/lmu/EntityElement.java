package org.lucci.lmu;

/*
 * Created on Oct 28, 2004
 */

/**
 * @author luc.hogie
 */
public class EntityElement extends TypedNamedModelElement
{
	private boolean classStaticElement;

	public boolean isClassStatic()
	{
		return classStaticElement;
	}
	public void setClassStatic(boolean classElement)
	{
		this.classStaticElement = classElement;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		EntityElement clone = (EntityElement) super.clone();
		clone.classStaticElement = this.classStaticElement;
		return clone;
	}
}
