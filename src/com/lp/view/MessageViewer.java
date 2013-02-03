/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lp.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author marc
 */
public class MessageViewer extends JPanel {
   private JTextArea messageList;
   private JScrollPane scroller;

   public MessageViewer(String title){
      this.setBorder(BorderFactory.createTitledBorder(title));
      setLayout(new BorderLayout());

      messageList = new JTextArea();

      scroller = new JScrollPane(messageList);
      messageList.setEditable(false);
      scroller.setPreferredSize(new Dimension(350, 500));
      add(scroller, BorderLayout.CENTER);
   }

   public void addText(String text){
      messageList.append(text);
   }
}
