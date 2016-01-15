package org.lucci.lmu.gui;

import javax.swing.JLabel;

/*
 * Created on Oct 10, 2004
 */

/**
 * @author luc.hogie
 */
public class ErrorMessageLabel extends JLabel
{
	@Override
	public String getText()
	{
		return "<html><br>" + super.getText() + "<br>";
	}}
