/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lp.model;

import com.lp.io.DataInterpreter;
import com.lp.io.Message;
import com.lp.io.SocketConnector;
import com.lp.io.MessageConsumer;
import com.lp.io.SimpleLineInterpreter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
/**
 *
 * @author marc
 */
public class ClientConnection extends PropertyChangeEventProducer implements MessageConsumer {
   private static Logger log = Logger.getLogger(ClientConnection.class.getName());
  
   public static final String RECEIVED_MESSAGE = "receivedMessage";
   public static final String SENT_MESSAGE = "sentMessage";
   public static final String CONNECTION = "connection";

   private ArrayList<Message> receivedMessages = new  ArrayList<Message>();
   private ArrayList<Message> sentMessages = new  ArrayList<Message>();

   private DataInterpreter di;

   @Override
   public void onMessage(Message message) {
      receivedMessages.add(message);
      notifyListeners(RECEIVED_MESSAGE, null, message);
   }

   private SocketConnector connection;

   public SocketConnector getConnection() {
      return connection;
   }

   /**
    * Returns the average throughput for outgoing
    *  data connections.
    * @return
    */
   public double getOutGoingAverageThroughput() {
      long connectionTime = connection.getConnectionTime();
      if(connectionTime == 0){
         return 0;
      }
      return ((double)connection.getBytesSent() /(double)connectionTime)*1000;
   }

   /**
    * Returns the average thorughput for incoming
    *  data connections.
    * @return
    */
   public double getReceivedAverageThroughput() {
      long connectionTime = connection.getConnectionTime();
      if(connectionTime == 0){
         return 0;
      }
      return ((double)connection.getBytesReceived() /(double)connectionTime)*1000;
   }

   public ClientConnection(String host, int port) {
      di = new SimpleLineInterpreter();
      di.registerObserver(this);
      this.connection = new SocketConnector(host, port, di);
   }

   public void reconnect(){
      // Clean up the old di
      di.removeObserver(this);

      di = new SimpleLineInterpreter();
      di.registerObserver(this);
      SocketConnector oldSocket = connection;
      connection = new SocketConnector(connection.getHost(), connection.getPort(), di);
      notifyListeners(CONNECTION, oldSocket, connection);
   }

   public void send(String message) {
      try{
         Message msg = new Message(message);
         connection.send(msg.toBytes());
         sentMessages.add(msg);
         notifyListeners(SENT_MESSAGE, null, msg);
      }
      catch(IOException err) {
         log.warning(err.toString());
      }
   }
}
