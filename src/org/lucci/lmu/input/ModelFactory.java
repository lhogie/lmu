package org.lucci.lmu.input;

import java.util.HashMap;
import java.util.Map;

import org.lucci.lmu.Model;

/*
 * Created on Oct 11, 2004
 */

/**
 * @author luc.hogie
 */
public abstract class ModelFactory
{

	static private Map<String, ModelFactory> factoryMap = new HashMap<String, ModelFactory>();

	static
	{
		factoryMap.put(null, LmuParser.getParser());
		factoryMap.put("lmu", LmuParser.getParser());
		factoryMap.put("jar", new JarFileAnalyser());
	}

	public static ModelFactory getModelFactory(String type)
	{
		return factoryMap.get(type);
	}

	public abstract Model createModel(byte[] data, AnalyzerListener al) throws ParseError, ModelException;
}
