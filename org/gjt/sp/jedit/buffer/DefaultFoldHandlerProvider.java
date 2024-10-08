/*
 * DummyFoldHandler.java - Fold handler used when folding is switched off
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001 Slava Pestov
 * Portions copyright (C) 2007 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.gjt.sp.jedit.buffer;

import org.gjt.sp.util.StandardUtilities;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Matthieu Casanova
 * @version $Id$
 */
public class DefaultFoldHandlerProvider implements FoldHandlerProvider
{
	private final Map<String, FoldHandler> folds = new HashMap<>();
	/**
	 * Returns the fold handler with the specified name, or null if
	 * there is no registered handler with that name.
	 *
	 * @param name The name of the desired fold handler
	 * @return the FoldHandler or null if it doesn't exist
	 * @since jEdit 4.3pre10
	 */
	@Override
	public FoldHandler getFoldHandler(String name)
	{
		return folds.get(name);
	}

	/**
	 * Returns an array containing the names of all registered fold
	 * handlers.
	 *
	 * @since jEdit 4.0pre6
	 */
	@Override
	public String[] getFoldModes()
	{
		return folds.keySet().toArray(StandardUtilities.EMPTY_STRING_ARRAY);
	}
	
	/**
	 * Add a new FoldHander.
	 *
	 * @param foldHandler the new foldHandler
	 * @since jEdit 4.3pre13
	 */
	public void addFoldHandler(FoldHandler foldHandler)
	{
		folds.put(foldHandler.getName(), foldHandler);
	}
}
