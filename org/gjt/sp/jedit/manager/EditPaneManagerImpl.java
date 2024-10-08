/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2020 jEdit contributors
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.gjt.sp.jedit.manager;

import org.gjt.sp.jedit.EditPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * The EditPane manager {@link org.gjt.sp.jedit.EditPane} provide
 * useful tools to list and iterate over EditPanes
 *
 * @author Matthieu Casanova
 * @since jEdit 5.6pre1
 * @version $Id$
 */
public class EditPaneManagerImpl implements EditPaneManager
{
	private final ViewManager viewManager;

	public EditPaneManagerImpl(ViewManager viewManager)
	{
		this.viewManager = viewManager;
	}

	@Override
	public List<EditPane> getEditPanes()
	{
		List<EditPane> editPanes = new ArrayList<>();
		viewManager.forEach(view -> Collections.addAll(editPanes, view.getEditPanes()));
		return editPanes;
	}

	@Override
	public void forEach(Consumer<? super EditPane> action)
	{
		viewManager.forEach(view -> view.forEachEditPane(action));
	}
}
