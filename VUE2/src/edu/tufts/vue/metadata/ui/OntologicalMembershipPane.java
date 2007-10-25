
/*
 *
 * * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2003-2007
 * Tufts University. All rights reserved.</p>
 *
 *
 */

package edu.tufts.vue.metadata.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;

import tufts.vue.*;
import tufts.vue.gui.Widget;

import edu.tufts.vue.ontology.*;

/*
 * OntologicalMembershipPane.java
 *
 * Created on October 18, 2007, 8:45 PM
 *
 * @author dhelle01
 */
public class OntologicalMembershipPane extends javax.swing.JPanel implements ActiveListener {
    
    private static OntologicalMembershipPane global;
    
    private LWComponent current;
    private javax.swing.JList list;
    
    public static OntologicalMembershipPane getGlobal()
    {
        return global;
    }
    
    public OntologicalMembershipPane() 
    {
        
        setLayout(new BorderLayout());
        
        //todo: adjust font (html handles the text wrapping)
        //JLabel label = new JLabel("<html>Ontological Terms associated with this node:</html>");
        JLabel label = new JLabel("Ontological Terms associated with this node:",JLabel.LEFT);
        label.setFont(tufts.vue.gui.GUI.LabelFace);
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
        
        add(label,BorderLayout.NORTH);
        list = new javax.swing.JList(new OntologyTypeListModel());
        list.setCellRenderer(new OntologyTypeListRenderer());
        list.setOpaque(true);
        list.setBackground(getBackground());
        add(list);//,java.awt.BorderLayout.SOUTH);
        setPreferredSize(new Dimension(200,100));
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            
            int selected = 0;
            
            
            public void mousePressed(java.awt.event.MouseEvent e)
            {
                //System.out.println(e);
                int selected = list.locationToIndex(e.getPoint());
                
                if(list.getSelectedIndex() < 0 || !current.getMetadataList().hasOntologicalMetadata())
                {
                    return;
                }
                
                if(e.getPoint().x < OntologicalMembershipPane.this.getWidth() - 40)
                    return;
                
                int categoryIndex = ((edu.tufts.vue.metadata.MetadataList.CategoryFirstList)current.getMetadataList().getMetadata()).getCategoryEndIndex();
                
                //System.out.println("Mouse Pressed: getCategoryIndex() " + categoryIndex);
                
                current.getMetadataList().getMetadata().remove(categoryIndex + selected);
                
                OntologicalMembershipPane.this.validate();
            }
        });
        
        VUE.addActiveListener(LWComponent.class,this);
        
        if(global == null)
        {
            global = this;
        }
    }
    
    public LWComponent getCurrent()
    {
        return current;
    }
    
    public void activeChanged(ActiveEvent e)
    {
        if(e != null)
        {
            current = (LWComponent)e.active;
            
            if(current == null)
                return;
            
            ((OntologyTypeListModel)list.getModel()).refresh();
            
            adjustVisibility();

        }
    }
    
    public void refresh()
    {
        ((OntologyTypeListModel)list.getModel()).refresh();
            
        adjustVisibility();   
    }
    
    static public void adjustVisibility()
    {
            if(getGlobal().getCurrent().getMetadataList().hasOntologicalMetadata())
            {
              Widget.setHidden(getGlobal(),false);
            }
            else
            {
              Widget.setHidden(getGlobal(),true);
            }
    }
    
    class OntologyTypeListRenderer extends javax.swing.DefaultListCellRenderer
    {
        private javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout());
        private javax.swing.JLabel deleteButton = new JLabel(); //= new javax.swing.JButton("-");
        
        public OntologyTypeListRenderer()
        {
            setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
            javax.swing.border.Border lineBorder = javax.swing.BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY);
            javax.swing.border.Border emptyBorder = javax.swing.BorderFactory.createEmptyBorder(5,5,5,5);
            javax.swing.border.Border emptyBorder2 = javax.swing.BorderFactory.createEmptyBorder(5,5,5,5);
            javax.swing.border.Border innerCompoundBorder = javax.swing.BorderFactory.createCompoundBorder(emptyBorder,lineBorder);
            javax.swing.border.Border outerCompoundBorder = javax.swing.BorderFactory.createCompoundBorder(innerCompoundBorder,emptyBorder);
            panel.setBorder(outerCompoundBorder);
            
            deleteButton.setIcon(tufts.vue.VueResources.getImageIcon("ontologicalmembership.delete.up"));
        }
        
        public java.awt.Component getListCellRendererComponent(javax.swing.JList list,Object value,int index,boolean isSelected,boolean cellHasFocus)
        {
            String ontValue = ((edu.tufts.vue.metadata.VueMetadataElement)value).getValue();
            if(ontValue == null)
                return panel;
            String label = ontValue.substring(ontValue.indexOf("#")+1,ontValue.length());
            setText(label);
            panel.add(this);
            panel.add(deleteButton,java.awt.BorderLayout.EAST);
            return panel;
        }
    }
    
    class OntologyTypeListModel extends javax.swing.DefaultListModel
    {        
        public Object getElementAt(int i)
        {
           return (current.getMetadataList().getOntologyListElement(i)); 
        }
        
        public int getSize()
        {
            if(current !=null)
              return current.getMetadataList().getOntologyListSize();
            else
              return 0;
        }
        
        public void refresh()
        {
             fireContentsChanged(this,0,getSize());
        }
    }

}
