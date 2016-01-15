package org.lucci.lmu.gui;

import java.io.File;

/*
 * Created on Oct 4, 2004
 */

/**
 * @author luc.hogie
 */
public class FileChooser extends javax.swing.filechooser.FileFilter
{
	private String extension;
	private String description;
	
	public FileChooser(String e, String d)
	{
		setExtension(e);
		setDescription(d);
	}
	
	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File file)
	{
		if (file.isDirectory())
		{
			return true;
		}
		else
		{
			String ext = getFileExtension(file.getName());
			
			if (ext == null)
			{
				return false;
			}
			else
			{
				return ext.equalsIgnoreCase(getExtension());
			}
		}
	}

	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getExtension()
	{
		return extension;
	}
	public void setExtension(String extension)
	{
		this.extension = extension;
	}

	public static String getFileExtension(String s)
	{
		int pos = s.lastIndexOf(".");
		
		if (pos == -1)
		{
			return null;
		}
		else
		{
			return s.substring(pos + 1);
		}
	}

}
