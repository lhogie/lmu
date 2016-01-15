package org.lucci.lmu;


/*
 * Created on Oct 9, 2004
 */

/**
 * @author luc.hogie
 */
public class Visibility extends ModelElement
{
   
	public static final Visibility PUBLIC = new Visibility();
	public static final Visibility PROTECTED = new Visibility();
	public static final Visibility PRIVATE = new Visibility();
    public static final Visibility DEFAULT = new Visibility();

    
    private Visibility()
    {
    	
    }

}
