package org.lucci.lmu;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;


/*
 * Created on Oct 10, 2004
 */

/**
 * @author luc.hogie
 */
public class Models
{
    public static Collection<ModelElement> findAllModelElementsInModel(Model model)
    {
        Collection<ModelElement> res = new Vector<ModelElement>();
        res.addAll(model.getEntities());
        res.addAll(model.getRelations());

        for (Entity entity : model.getEntities())
        {
            res.addAll(Entities.findAllModelElementsInEntity(entity));
        }

        return res;
    }

    public static void removeModelElementsVisibility(Collection<ModelElement> modelElements)
    {
        Iterator<ModelElement> elementIterator = modelElements.iterator();

        while (elementIterator.hasNext())
        {
            ModelElement element = elementIterator.next();
            element.setVisibility(null);
        }
    }

    public static void removeTypedModelElementsTypes(Collection<TypedNamedModelElement> modelElements)
    {
        Iterator<TypedNamedModelElement> elementIterator = modelElements.iterator();

        while (elementIterator.hasNext())
        {
            TypedNamedModelElement element = elementIterator.next();
            element.setType(null);
        }
    }

    public static void hideNonPublicElements(Model model)
    {
        for (ModelElement me : Models.findAllModelElementsInModel(model))
        {
            if (me.getVisibility() != Visibility.PUBLIC)
            {
                me.setVisible(false);
            }
        }
    }

	public static void describeNamespace(Model model, String namespace)
	{
		Set<Entity> entities = Entities.findEntityWhoseNameSpaceMatches(model.getEntities(), namespace);

		if (entities.isEmpty())
			throw new IllegalArgumentException("no entity in namespace");
		
		Set<Entity> superEntities = new HashSet<Entity>();
		
		for (Entity e : entities)
		{
			superEntities.addAll(Entities.findSuperEntities(e, model));
		}
		
		entities.addAll(superEntities);
		Entities.retainEntities(entities, model);
	}

}
