/*
 * JEditMode.java - jEdit editing mode
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

package org.gjt.sp.jedit;

import org.gjt.sp.util.Log;

/**
 * @author Matthieu Casanova
 * @version $Id$
 * @since jEdit 4.3pre10
 */
class JEditMode extends Mode
{
	//{{{ JEditMode constructor
	JEditMode(String name)
	{
		super(name);
	} //}}}

	//{{{ setProperty() method
	/**
	 * Sets a mode property.
	 * @param key The property name
	 * @param value The property value
	 */
	@Override
	public void setProperty(String key, Object value)
	{
		if (initialized)
		{
			jEdit.setProperty("mode." + name + '.' + key, value.toString());
		}
		props.put(key,value);
	} //}}}

	//{{{ unsetProperty() method
	/**
	 * Unsets a mode property.
	 * @param key The property name
	 */
	@Override
	public void unsetProperty(String key)
	{
		if (initialized)
		{
			jEdit.unsetProperty("mode." + name + '.' + key);
		}
		props.remove(key);
	} //}}}

	//{{{ getProperty() method
	/**
	 * Returns a mode property.
	 *
	 * @param key The property name
	 * @since jEdit 4.3pre10
	 */
	@Override
	public Object getProperty(String key)
	{
		String property = jEdit.getProperty("mode." + name + '.' + key);
		if(property != null)
		{
			Object value;
			try
			{
				value = Integer.valueOf(property);
			}
			catch(NumberFormatException nf)
			{
				value = property;
			}
			return value;
		}

		Object value = props.get(key);
		if(value != null)
			return value;

		String global = jEdit.getProperty("buffer." + key);
		if(global != null)
		{
			try
			{
				return Integer.valueOf(global);
			}
			catch(NumberFormatException nf)
			{
				return global;
			}
		}
		else
			return null;
	} //}}}

	//{{{ init() method
	/**
	 * Keeps track of mode initialization, to avoid overwriting
	 * custom mode properties in the user's settings.
	 */
	@Override
	public void init()
	{
		initialized = true;
		super.init();
	} //}}}

	//{{{ loadIfNecessary() method
	/**
	 * Loads the mode from disk if it hasn't been loaded already.
	 * @since jEdit 4.3pre10
	 */
	@Override
	public void loadIfNecessary()
	{
		if(marker == null)
		{
			jEdit.loadMode(this);
			if (marker == null)
				Log.log(Log.ERROR, this, "Mode not correctly loaded, token marker is still null");
		}
	} //}}}

	private boolean initialized;
}
