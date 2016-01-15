package org.lucci.lmu;

import java.util.HashSet;
import java.util.Set;

import toools.collections.relation.HashRelation;


public class Group extends HashSet<Entity>
{
    private String colorName;
    private String label;
    private boolean isCluster = false; 

    
    
    public boolean isCluster()
    {
        return isCluster;
    }

    public void setCluster(boolean isCluster)
    {
        this.isCluster = isCluster;
    }

    public String getColorName()
    {
	return colorName;
    }

    public void setColorName(String colorName)
    {
	this.colorName = colorName;
    }

    public String getLabel()
    {
	return label;
    }

    public void setLabel(String label)
    {
	this.label = label;
    }

    public static Set<Group> groupByPackage(Set<Entity> entities)
    {
	toools.collections.relation.Relation<String, Entity> r = new HashRelation<String, Entity>();

	for (Entity e : entities)
	{
	    if (e.isVisible())
	    {
		r.add(e.getNamespace(), e);
	    }
	}

	Set<Group> groups = new HashSet<Group>();

	for (String namespace : r.getKeys())
	{
	    Group g = new Group();
	    g.setLabel(namespace);
	    g.addAll(r.getValues(namespace));
	    g.setColorName("grey");
	    groups.add(g);
	}

	return groups;
    }

    public static Set<Group> group(Set<Entity> entities, GroupRule rule)
    {
	Set<Group> groups = new HashSet<Group>();

	for (Entity a : entities)
	{
	    for (Entity b : entities)
	    {
		if (rule.group(a, b))
		{
		    for (Group g : groups)
		    {
			if (g.contains(a) && !g.contains(b))
			{
			    g.add(b);
			}
			else if (g.contains(b) && !g.contains(a))
			{
			    g.add(a);
			}
		    }

		    if (findGroupsContainingBoth(groups, a, b).isEmpty())
		    {
			Group g = new Group();
			g.add(a);
			g.add(b);
			g.setLabel(rule.getGroupName(a, b));
			groups.add(g);
		    }
		}
	    }
	}

	return groups;
    }

    public static Set<Group> findGroupsContaining(Set<Group> groups, Entity e)
    {
	Set<Group> res = new HashSet<Group>();

	for (Group g : groups)
	{
	    if (g.contains(e))
	    {
		res.add(g);
	    }
	}

	return res;
    }

    public static Set<Group> findGroupsContainingBoth(Set<Group> groups, Entity a, Entity b)
    {
	if (a == null)
	    throw new NullPointerException();

	if (b == null)
	    throw new NullPointerException();

	Set<Group> res = new HashSet<Group>();

	for (Group g : groups)
	{
	    if (g.contains(a) && g.contains(b))
	    {
		res.add(g);
	    }
	}

	return res;
    }

    public static interface GroupRule
    {
	boolean group(Entity a, Entity b);

	String getGroupName(Entity a, Entity b);
    }

    public static class PackageGroup implements GroupRule
    {

	@Override
	public boolean group(Entity a, Entity b)
	{
	    if (a == null)
		throw new NullPointerException();
	    if (b == null)
		throw new NullPointerException();
	    return a.getNamespace().equals(b.getNamespace());
	}

	@Override
	public String getGroupName(Entity a, Entity b)
	{
	    if (a == null)
		throw new NullPointerException();

	    if (b == null)
		throw new NullPointerException();

	    if (group(a, b))
	    {
		return a.getNamespace();
	    }
	    else
	    {
		throw new IllegalStateException();
	    }
	}

    }

    @Override
    public String toString()
    {

	return getLabel() + ": " + super.toString();
    }

    public static class RegexpGroup implements GroupRule
    {

	private String regexp;

	public RegexpGroup(String regpex)
	{
	    this.regexp = regexp;
	}

	@Override
	public boolean group(Entity a, Entity b)
	{
	    if (a == null)
		throw new NullPointerException();
	    if (b == null)
		throw new NullPointerException();
	    return a.getNamespace().equals(b.getNamespace());
	}

	@Override
	public String getGroupName(Entity a, Entity b)
	{
	    if (a == null)
		throw new NullPointerException();

	    if (b == null)
		throw new NullPointerException();

	    if (group(a, b))
	    {
		return a.getNamespace();
	    }
	    else
	    {
		throw new IllegalStateException();
	    }
	}

    }
}
