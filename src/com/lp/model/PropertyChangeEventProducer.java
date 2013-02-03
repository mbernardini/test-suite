/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lp.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 *
 * @author marc
 */
public class PropertyChangeEventProducer {
   private ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
   public void addChangeListener(PropertyChangeListener listener){
      listeners.add(listener);
   }
   public void removeChangeListener(PropertyChangeListener listener){
      listeners.remove(listener);
   }
   protected void notifyListeners(String property, Object oldValue, Object newValue){
      for(PropertyChangeListener listener: listeners){
         listener.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
      }
   }

}
