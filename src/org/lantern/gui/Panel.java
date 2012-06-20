/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 * 
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.gui;

import java.util.List;
import org.lantern.gui.layout.HorisontalLayout;
import org.lantern.gui.layout.LanternLayout;
import org.lantern.gui.layout.SizePolicy;
import org.lantern.gui.layout.VerticalLayout;
import org.lantern.gui.theme.Theme.Category;
import org.lantern.terminal.TerminalPosition;
import org.lantern.terminal.TerminalSize;

/**
 *
 * @author mabe02
 */
public class Panel extends AbstractContainer
{
    private Border border;
    private LanternLayout layoutManager;
    private String title;

    public Panel()
    {
        this(Orientation.VERTICAL);
    }

    public Panel(String title)
    {
        this(title, Orientation.VERTICAL);
    }

    public Panel(Orientation panelOrientation)
    {
        this(new Border.Invisible(), panelOrientation);
    }

    public Panel(String title, Orientation panelOrientation)
    {
        this(title, new Border.Bevel(true), panelOrientation);
    }

    public Panel(Border border, Orientation panelOrientation)
    {
        this("", border, panelOrientation);
    }

    public Panel(String title, Border border, Orientation panelOrientation)
    {
        this.border = border;
        if(panelOrientation == Orientation.HORISONTAL)
            layoutManager = new HorisontalLayout();
        else
            layoutManager = new VerticalLayout();
        
        this.title = (title != null ? title : "");
    }

    public Border getBorder()
    {
        return border;
    }

    public void setBorder(Border border)
    {
        if(border != null)
            this.border = border;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = (title != null ? title : "");
    }

    public void setBetweenComponentsPadding(int paddingSize)
    {
        if(paddingSize < 0)
            paddingSize = 0;
        layoutManager.setPadding(paddingSize);
    }

    boolean maximisesVertically()
    {
        if(layoutManager instanceof VerticalLayout == false)
            return false;

        return ((VerticalLayout)layoutManager).isMaximising();
    }

    boolean maximisesHorisontally()
    {
        if(layoutManager instanceof HorisontalLayout == false)
            return false;

        return ((HorisontalLayout)layoutManager).isMaximising();
    }

    public void repaint(TextGraphics graphics)
    {
        border.drawBorder(graphics, new TerminalSize(graphics.getWidth(), graphics.getHeight()), title);
        TerminalPosition contentPaneTopLeft = border.getInnerAreaLocation(graphics.getWidth(), graphics.getHeight());
        TerminalSize contentPaneSize = border.getInnerAreaSize(graphics.getWidth(), graphics.getHeight());
        TextGraphics subGraphics = graphics.subAreaGraphics(contentPaneTopLeft, contentPaneSize);

        List<LanternLayout.LaidOutComponent> laidOutComponents = layoutManager.layout(contentPaneSize);
        for(LanternLayout.LaidOutComponent laidOutComponent: laidOutComponents) {
            TextGraphics subSubGraphics = subGraphics.subAreaGraphics(
                    laidOutComponent.getTopLeftPosition(), laidOutComponent.getSize());
            
            if(laidOutComponent.getComponent().isVisible())
                laidOutComponent.getComponent().repaint(subSubGraphics);
        }        
    }

    public TerminalSize getPreferredSize()
    {        
        return border.surroundAreaSize(layoutManager.getPreferredSize());
    }

    @Override
    public void addComponent(Component component)
    {
        addComponent(component, SizePolicy.CONSTANT);
    }
    
    public void addComponent(Component component, SizePolicy sizePolicy)
    {
        super.addComponent(component);
        layoutManager.addComponent(component, sizePolicy);
    }

    @Override
    public void removeComponent(Component component)
    {
        super.removeComponent(component);
        layoutManager.removeComponent(component);
    }
    
    public enum Orientation
    {
        HORISONTAL,
        VERTICAL
    }

    @Override
    public String toString()
    {
        return "Panel with " + getComponentCount() + " components";
    }
}