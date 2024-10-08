/*
 * FoldHandlerProvider.java - Fold handler provider interface
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007 Matthieu Casanova
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

/**
 * @author Matthieu Casanova
 * @version $Id$
 * @since jEdit 4.3pre10
 */
public interface FoldHandlerProvider
{
	/**
	 * Returns the fold handler with the specified name, or null if
	 * there is no registered handler with that name.
	 * @param name The name of the desired fold handler
	 * @return the FoldHandler or null if it doesn't exist
	 * @since jEdit 4.3pre10
	 */
	FoldHandler getFoldHandler(String name);

	/**
	 * Returns an array containing the names of all registered fold
	 * handlers.
	 *
	 * @since jEdit 4.0pre6
	 */
	String[] getFoldModes();
}
