package org.lucci.lmu.output;

import java.util.HashMap;
import java.util.Map;

import org.lucci.lmu.Model;

/*
 * Created on Oct 3, 2004
 */

/**
 * @author luc.hogie
 */
public abstract class AbstractWriter
{
	public abstract byte[] writeModel(Model diagram)
		throws WriterException;
	



    
    
	static Map<String, AbstractWriter> factoryMap = new HashMap<String, AbstractWriter>();
	
	static
	{
        factoryMap.put(null, new LmuWriter());
		factoryMap.put("lmu", new LmuWriter());
		factoryMap.put("dot", new DotWriter());
		factoryMap.put("java", new JavaSourceWriter());
		factoryMap.put("ps", new GraphVizBasedViewFactory("ps"));
		factoryMap.put("png", new GraphVizBasedViewFactory("png"));
		factoryMap.put("fig", new GraphVizBasedViewFactory("fig"));
		factoryMap.put("svg", new GraphVizBasedViewFactory("svg"));
	}

	public static AbstractWriter getTextFactory(String type)
	{
	    AbstractWriter f = factoryMap.get(type);
	    
	    if (f == null)
	    {
            return new GraphVizBasedViewFactory(type);
	    }
	    else
	    {
	        return f;
	    }
	}
}
