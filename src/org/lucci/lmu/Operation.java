package org.lucci.lmu;

import java.util.ArrayList;
import java.util.List;


/*
 * Created on Oct 2, 2004
 */

/**
 * @author luc.hogie
 */
public class Operation extends EntityElement
{
	public List<Entity> parameterList = new ArrayList<Entity>();

	
	public List<Entity> getParameterList()
	{
		return parameterList;
	}
	
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		Operation clone = (Operation) super.clone();
		clone.parameterList = new ArrayList<Entity>(this.parameterList);
		return clone;
	}
}
