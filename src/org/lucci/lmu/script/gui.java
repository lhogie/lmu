package org.lucci.lmu.script;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java4unix.CommandLine;


import org.lucci.lmu.gui.LMUGui;

import toools.extern.ExternalProgram;
import toools.io.FileUtilities;
import toools.thread.Threads;
import toools.util.assertion.Assertions;

/*
 * Created on Oct 2, 2004
 */

/**
 * @author luc.hogie
 */
public class gui extends LmuScript
{


    @Override
    public int runScript(CommandLine cmdLine) throws IOException
    {
    	Assertions.ensure(ExternalProgram.commandIsAvailable("dot"), "Graphiz is not installed on this computer (the 'dot' command was not found)! Please check http://www.graphviz.org/");
    	List<String> args = cmdLine.findArguments();

    	if (!args.isEmpty())
    	{
    		File f = new File(args.get(0));
    		
    		if (f.getName().endsWith(".lmu"))
    		{
    	    	new LMUGui(new String(FileUtilities.getFileContent(f)));
    		}
    		else if (f.getName().endsWith(".jar"))
    		{
    	    	new LMUGui("load " + f.getAbsolutePath());
    		}
    		else
    		{
    			throw new IllegalArgumentException("don't know what to do with this file");
    		}
    	}
    	else
    	{
    		new LMUGui(null);
    	}
    	
    	Threads.sleepForever();
        return 0;
    }

    public static void main(String[] args) throws Throwable
	{
		new gui().run("/Users/lhogie/dev/builds/lmu/lmu-2011.01.29.16.26.42.jar");
	}

}
