package org.lucci.lmu.input;

/*
 * Created on Oct 3, 2004
 */

/**
 * @author luc.hogie
 */
public class LmuException extends Exception
{
    private int line;
    private String suggestion;
    
    public LmuException(int line, String s)
    {
        super(s);
        this.line = line;
    }

    public LmuException(int line, String s, String suggestion)
    {
        super(s);
        this.line = line;
        this.suggestion = suggestion;
    }
    
    
    public String getMessage()
    {
        String s = "<i><b>Somewhere in line " + line + "</b></i>:<br>" + super.getMessage();
        
        if (suggestion != null)
        {
            s += "<br><div align=center>" + suggestion;
        }
        
        return s;
    }

    public int getLine()
    {
        return line;
    }

    public void setLine(int line)
    {
        this.line = line;
    }
}
