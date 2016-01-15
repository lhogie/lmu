package org.lucci.lmu.script;

import java.io.IOException;
import java.util.List;
import java4unix.CommandLine;

import org.lucci.lmu.Entities;
import org.lucci.lmu.Model;
import org.lucci.lmu.input.ModelException;
import org.lucci.lmu.input.ModelFactory;
import org.lucci.lmu.input.ParseError;
import org.lucci.lmu.input.StdOutAnalyserLog;
import org.lucci.lmu.output.AbstractWriter;
import org.lucci.lmu.output.WriterException;

import toools.io.FileUtilities;
import toools.io.file.RegularFile;

/*
 * Created on Oct 2, 2004
 */

/**
 * @author luc.hogie
 */
public class cmd extends LmuScript
{
	public static void main(String[] args) throws Throwable
	{
		new cmd().run("/Users/lhogie/latex/fff/uml/basic.lmu");
	}

	@Override
	public int runScript(CommandLine cmdLine)
	{
		// Assertions.ensure(Posix.commandIsAvailable("dot"),
		// "Graphiz is not installed on this computer (the 'dot' command was not found)! Please check http://www.graphviz.org/");
		List<String> args = cmdLine.findArguments();
		RegularFile inputFile = new RegularFile(args.get(0));
		String inputType = FileUtilities.getFileNameExtension(inputFile.getName());

		try
		{
			ModelFactory modelFactory = ModelFactory.getModelFactory(inputType);

			if (modelFactory == null)
			{
				printNonFatalError("No parser defined for input type '" + inputType + "\n");
			}
			else
			{
				RegularFile outputFile = new RegularFile(args.size() == 1 ? FileUtilities.replaceExtensionBy(inputFile.getName(), "pdf") : args.get(1));
				String outputType = FileUtilities.getFileNameExtension(outputFile.getName());
				AbstractWriter factory = AbstractWriter.getTextFactory(outputType);

				if (factory == null)
				{
					printFatalError("Do not know how to generate '" + outputType + "' code\n");
				}
				else
				{
					byte[] inputData = inputFile.getContent();
					Model model = modelFactory.createModel(inputData, new StdOutAnalyserLog());

					printMessage(model.getEntities().size() + " entities and " + model.getRelations().size() + " relations\n");

					System.out.println(Entities.getTopSubClasses(model.getEntities(), model.getRelations()));
					
					try
					{
						printMessage("Writing file " + outputFile.getPath() + "\n");
						byte[] outputBytes = factory.writeModel(model);
						outputFile.setContent(outputBytes);
					}
					catch (WriterException ex)
					{
						System.err.println(ex.getMessage() + "'\n");
					}
					catch (IOException ex)
					{
						System.err.println("I/O error while writing file " + outputFile.getPath() + "\n");
					}
				}
			}
		}
		catch (ParseError ex)
		{
			System.err.println("Parse error: " + ex.getMessage() + "\n");
		}
		catch (ModelException ex)
		{
			System.err.println("Model error: " + ex.getMessage() + "\n");
		}
		catch (IOException ex)
		{
			System.err.println("I/O error: " + ex.getMessage() + "\n");
		}

		return 0;
	}

}
