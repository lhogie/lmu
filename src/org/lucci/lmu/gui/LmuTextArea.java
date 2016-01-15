package org.lucci.lmu.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;


/*
 * Created on Oct 4, 2004
 */

/**
 * @author luc.hogie
 */
public class LmuTextArea extends JTextArea
{

	@Override
    public void setText(String arg0)
    {
        super.setText(arg0);
        setCaretPosition(0);
    }

    public void insertText(String s)
    {
        try
        {
            getDocument().insertString(getCaretPosition(), s, null);
        }
        catch (BadLocationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    
    
    LmuTextArea()
	{
		setBackground(new Color(0xE0, 0xE0, 0xE0));
		setFont(new Font("Courier", Font.PLAIN, 12));

		final UndoManager undo = new UndoManager();
		Document doc = getDocument();

		// Listen for undo and redo events
		doc.addUndoableEditListener(new UndoableEditListener()
		{
			public void undoableEditHappened(UndoableEditEvent evt)
			{
				undo.addEdit(evt.getEdit());
			}
		});

		// Create an undo action and add it to the text component
		getActionMap().put("Undo", new AbstractAction("Undo")
		{
			public void actionPerformed(ActionEvent evt)
			{
				try
				{
					if (undo.canUndo())
					{
						undo.undo();
					}
				} catch (CannotUndoException e)
				{
				}
			}
		});

		// Bind the undo action to ctl-Z
		getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

		// Create a redo action and add it to the text component
		getActionMap().put("Redo", new AbstractAction("Redo")
		{
			public void actionPerformed(ActionEvent evt)
			{
				try
				{
					if (undo.canRedo())
					{
						undo.redo();
					}
				} catch (CannotRedoException e)
				{
				}
			}
		});

		// Bind the redo action to ctl-Y
		getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
        setText("LMU language version 1\n\n");
	}
}