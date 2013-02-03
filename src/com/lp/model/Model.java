/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lp.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * Model class for our MVC.
 * @author marc
 */
public enum Model  {
   INSTANCE;

   private static Logger log = Logger.getLogger(Model.class.getName());

   private ArrayList<ClientConnection> clientConnections = new ArrayList<ClientConnection>();

   public void addClientConnection(ClientConnection client) {
      clientConnections.add(client);
   }
   
   private String hostAddress = getLocalIpAddress();

   /**
    * Returns the current host IP address
    * @return A string representation of the local IP address
    */
   public String getHostAddress() {
      return hostAddress;
   }
   

   /**
    * Returns the local host network address. Currently this
    * is not the hosts network address, its a looback address
    * to get back to the current localhost.
    * @return
    */
   private String getLocalIpAddress(){
      try{
         InetAddress localhost = InetAddress.getLocalHost();
         return localhost.getHostAddress();
      }
      catch(Exception ex){
         log.warning(ex.toString());
      }
      return "127.0.0.1";
   }

}
