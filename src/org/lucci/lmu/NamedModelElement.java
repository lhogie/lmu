package org.lucci.lmu;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import toools.math.MathsUtilities;



/*
 * Created on Oct 9, 2004
 */

/**
 * @author luc.hogie
 */
public class NamedModelElement extends ModelElement
{
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        if (name == null)
            throw new NullPointerException();

        name = name.trim();

        if (name.length() == 0)
            throw new IllegalArgumentException("name is empty");

        if (MathsUtilities.isNumber(name))
            throw new IllegalArgumentException("name cannot be set to a number");
        
        
        this.name = name;
    }

    public static void sortByName(List<NamedModelElement> parms)
    {
        Collections.sort(parms, new Comparator<NamedModelElement>()
        {
            public int compare(NamedModelElement e1, NamedModelElement e2)
            {
                return e1.getName().compareToIgnoreCase(e2.getName());
            }
        });
    }
    
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		NamedModelElement clone = (NamedModelElement) super.clone();
		clone.name = this.name;
		return clone;
	}
}
