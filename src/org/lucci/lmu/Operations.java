package org.lucci.lmu;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;


/*
 * Created on Oct 10, 2004
 */

/**
 * @author luc.hogie
 */
public class Operations
{
	public static Collection<ModelElement> findAllModelElementsInOperation(Operation operation)
	{
		Collection<ModelElement> res = new Vector<ModelElement>();
		res.add(operation.getType());
		res.add(operation.getVisibility());
		res.addAll(operation.getParameterList());
		return res;
	}

	
	public static Collection<Operation> findAllOperations(Collection<Entity> entities)
	{
		Collection<Operation> c = new Vector<Operation>();
		Iterator<Entity> entityIterator = entities.iterator();
		
		while (entityIterator.hasNext())
		{
			Entity e = entityIterator.next();
			c.addAll(e.getOperations());
		}
		
		return c;
	}

	
	public static void removeOperationParameters(Collection<Operation> operations)
	{
		for (Operation operation : operations)
		{
			operation.getParameterList().clear();
		}
	}


	public static Operation findOperationByName(Entity entity, String name)
	{
		for (Operation a : entity.getOperations())
		{
			if (a.getName().equals(name))
			{
				return a;
			}
		}

		return null;
	}


}
