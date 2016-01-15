package org.lucci.lmu.output;

import java.util.Arrays;
import java.util.Collection;

import org.lucci.lmu.Model;

import toools.extern.ExternalProgram;
import toools.extern.Proces;

/*
 * Created on Oct 3, 2004
 */

/**
 * @author luc.hogie
 */
public class GraphVizBasedViewFactory extends AbstractWriter
{
	private final String outputType;

    public final static Collection<String> supportedOutputTypes = Arrays
            .asList(new String[] { "canon", "dot", "xdot", "cmap", "dia",
                    "fig", "gd", "gd2", "gif", "hpgl", "imap", "cmapx",
                    "ismap", "jpg", "jpeg", "mif", "mp", "pcl", "pic", "plain",
                    "plain-ext", "png", "ps", "ps2", "svg", "svgz", "vrml",
                    "vtx", "wbmp" });
    

	public GraphVizBasedViewFactory(String type)
	{
		this.outputType = type;
	}

	@Override
	public byte[] writeModel(Model model)
		throws WriterException
	{
		DotWriter dotTextFactory = new DotWriter();
		byte[] dotText = dotTextFactory.writeModel(model);
		return Proces.exec("dot", dotText, "-T" + getOutputType());
	}

	public String getOutputType()
	{
		return outputType;
	}
}
