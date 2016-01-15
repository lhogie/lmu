package org.lucci.lmu;

import toools.collections.Filter;


/*
 * Created on Oct 10, 2004
 */

/**
 * @author luc.hogie
 */
public class Filters
{
	public static class VisiblityFilter<T extends ModelElement> implements Filter<T>
	{
		private Visibility visibility;
		
		public VisiblityFilter(Visibility v)
		{
			visibility = v;
		}
		
		public boolean accept(ModelElement o)
		{
			return o.getVisibility() == visibility;
		}
	}

	public static class NameFilter<T extends NamedModelElement> implements Filter<T>
	{
		private String pattern;
		
		public NameFilter(String pattern)
		{
			this.pattern = pattern;
		}
		
		public boolean accept(NamedModelElement o)
		{
			return o.getName().matches(pattern);
		}
	}
}
