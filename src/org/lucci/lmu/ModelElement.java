package org.lucci.lmu;

import java.util.ArrayList;
import java.util.Collection;

import toools.Clazz;
import toools.util.assertion.Assertions;



/*
 * Created on Oct 2, 2004
 */

/**
 * @author luc.hogie
 */
public class ModelElement implements Cloneable
{
    private Visibility visibility = Visibility.PUBLIC;

    private String comment;

    private boolean visible = true;

    public void initVisible()
    {
        this.visible = true;
    }

    public Visibility getVisibility()
    {
        return visibility;
    }

    public void setVisibility(Visibility visibility)
    {
        Assertions.ensureArgumentIsNotNull(visibility);

        this.visibility = visibility;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public static <T extends ModelElement> Collection<T> findVisibleModelElements(Collection<T> modelElements)
    {
        Collection<T> visiblemodelElements = new ArrayList<T>();

        for (T modelElement : modelElements)
        {
            if (modelElement.isVisible())
            {
                visiblemodelElements.add(modelElement);
            }
        }

        return visiblemodelElements;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

	@Override
	public  Object clone() throws CloneNotSupportedException
	{
		ModelElement clone = Clazz.makeInstance(getClass());
		clone.comment = this.comment;
		clone.visibility = this.visibility;
		clone.visible = this.visible;
		return clone;

	}


}
