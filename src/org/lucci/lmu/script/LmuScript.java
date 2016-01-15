package org.lucci.lmu.script;

import java.util.Collection;
import java4unix.AbstractShellScript;
import java4unix.ArgumentSpecification;
import java4unix.License;
import java4unix.OptionSpecification;

/*
 * Created on Oct 2, 2004
 */

/**
 * @author luc.hogie
 */
public abstract class LmuScript extends AbstractShellScript
{


    @Override
    public String getAuthor()
    {
        return "Luc Hogie";
    }


    @Override
    public License getLicence()
    {
        return License.GPL;
    }

    @Override
    public String getShortDescription()
    {
        return "high-quality UML class diagram generator. Input files can be .lmu or .jar files. Output files can be .lmu, .pdf or any file type supported by graphviz";
    }

    @Override
    public String getVersion()
    {
        return "0.1";
    }

    @Override
    public String getYear()
    {
        return "2005-2010";
    }

	@Override
	public String getApplicationName()
	{
		return "lmu";
	}

	@Override
	protected void declareArguments(Collection<ArgumentSpecification> argumentSpecifications)
	{
		// TODO Auto-generated method stub
		
	}
	


    @Override
    public void declareOptions(Collection<OptionSpecification> specs)
    {
    }
}
