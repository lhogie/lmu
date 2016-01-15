package org.lucci.lmu.input;

import java.util.Arrays;
import java.util.Collection;

import org.lucci.lmu.NamedModelElement;

/*
 * Created on Oct 3, 2004
 */

/**
 * @author luc.hogie
 */
public class ModelException extends LmuException
{
    Collection<NamedModelElement> modelElementsInvolved;
    
    public ModelException(String s, String suggestion, NamedModelElement[] m)
    {
        super(-1, s, suggestion);
        this.modelElementsInvolved = Arrays.asList(m);
    }

    public Collection<NamedModelElement> getModelElementsInvolved()
    {
        return modelElementsInvolved;
    }

    @Override
    public String getMessage()
    {
        String s = super.getMessage();
        
        for (NamedModelElement me : this.modelElementsInvolved)
        {
            s += "\n- " + me.getName(); 
        }
        
        return s;
    }
}
