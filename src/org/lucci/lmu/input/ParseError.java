package org.lucci.lmu.input;

/*
 * Created on Oct 3, 2004
 */

/**
 * @author luc.hogie
 */
public class ParseError extends LmuException
{
    public ParseError(int line, String s)
    {
	super(line, s);
    }

    public ParseError(int line, String s, String suggestion)
    {
	super(line, s, suggestion);
    }
}
