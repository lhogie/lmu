package org.lucci.lmu.test;

import java.io.File;
import java.io.IOException;

import org.lucci.lmu.Entities;
import org.lucci.lmu.Entity;
import org.lucci.lmu.Model;
import org.lucci.lmu.input.JarFileAnalyser;
import org.lucci.lmu.input.ParseError;
import org.lucci.lmu.input.StdOutAnalyserLog;

public class Test
{
	public static void main(String... args) throws ParseError, IOException
	{
		 Model model = new JarFileAnalyser().createModel(new File("/Users/lhogie/Downloads/fr.inria.aoste.timesquare.ccslkernel.solver_1.0.0.201403240946.jar"), new StdOutAnalyserLog());
		 Entity e = Entities.findEntityByName(model, "WaitExpression");
		 System.out.println(e.getName());
		 System.out.println(e.getAttributes());
	}
}
