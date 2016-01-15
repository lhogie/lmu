package org.lucci.lmu;

import java.util.Collection;
import java.util.HashSet;

public class Package extends NamedModelElement
{
    Collection<Entity> entities = new HashSet<Entity>();
    
    public Collection<Entity> getEntities()
    {
        return this.entities;
    }
}
