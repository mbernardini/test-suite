/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lp.view;

import com.lp.io.Message;
import com.lp.io.SocketConnector;
import com.lp.model.ClientConnection;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSplitPane;

/**
 *
 * @author marc
 */
public class ClientConnectionPanel extends JPanel implements PropertyChangeListener {

   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals(ClientConnection.RECEIVED_MESSAGE)) {
         incomingMessages.addText(((Message) evt.getNewValue()).getData());
      } else if (evt.getPropertyName().equals(ClientConnection.SENT_MESSAGE)) {
         outGoing.addText(((Message) evt.getNewValue()).getData());
      } else if (evt.getPropertyName().equals(ClientConnection.CONNECTION)) {
         remove(cip);
         add(cip = new ConnectionInformation(connection.getConnection()), BorderLayout.PAGE_START);
      }

   }

   private ClientConnection connection;
   private ConnectionInformation cip;
   private MessageViewer incomingMessages;
   private MessageViewer outGoing;
   private SendMessage sendMessage;
   private JSplitPane splitPane;
   private BackgroundUpdater bu;

   public ClientConnectionPanel(ClientConnection clientConnection) {
      this.setLayout(new BoxLayout(this,  BoxLayout.Y_AXIS));
      this.connection = clientConnection;
      this.connection.addChangeListener(this);

      add(cip = new ConnectionInformation(clientConnection.getConnection()));

      Dimension minSize = new Dimension(250, 300);
      incomingMessages = new MessageViewer("Incoming");
      incomingMessages.setMinimumSize(minSize);
      incomingMessages.setAlignmentX(Component.CENTER_ALIGNMENT);
      outGoing = new MessageViewer("Outgoing");
      outGoing.setMinimumSize(minSize);
      outGoing.setAlignmentX(Component.CENTER_ALIGNMENT);
      
      splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, incomingMessages, outGoing);
      splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
      add(splitPane);

      add(sendMessage = new SendMessage(connection));
      bu = new BackgroundUpdater(cip, connection.getConnection());
   }

   public class BackgroundUpdater extends Thread {

      private Logger log = Logger.getLogger(BackgroundUpdater.class.getName());

      private ConnectionInformation ci;
      private SocketConnector connection;

      public BackgroundUpdater(ConnectionInformation civ,SocketConnector con) {
         ci = civ;
         connection = con;
         start();
      }

      @Override
      public void run(){
         try{
            while(true){
              Thread.sleep(500);
              long ctime = connection.getConnectionTime();
              double oBps = 1000*(double)connection.getBytesSent() / (double)ctime;
              double iBps = 1000*(double)connection.getBytesReceived() / (double)ctime;
              ci.setBytes(connection.getBytesSent(), connection.getBytesReceived(), oBps,iBps);
            }
         }
         catch(Exception ex){
            log.warning(ex.toString());
         }
      }
      
   }

   public class SendMessage extends JPanel {

      private ClientConnection connection;
      private JButton send;
      private JTextField messageText;

      public SendMessage(ClientConnection cc) {
         this.connection = cc;
         send = new JButton("Send");
         send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
               String message = messageText.getText();
               connection.send(message + "\n");
               messageText.setText("");
            }
         });
         messageText = new JTextField();
         messageText.setColumns(50);
         add(messageText);
         add(send);
      }
   }

   public class ConnectionInformation extends JPanel implements PropertyChangeListener {

      private SocketConnector connection;
      private JTextField host,  port, outgoingBytes,oBps, incomingBytes,iBps;
      private ConnectionStateLabel connectionState;
      private JLabel hostLbl,  portLbl,  connectionStateLbl, outgoingBytesLbl, incomingBytesLbl;
      private JButton reconnect;

      public ConnectionInformation(SocketConnector connection) {
         //Host name/IP address
         hostLbl = new JLabel("Host");
         host = new JTextField(connection.getHost());
         host.setEditable(false);
         this.add(hostLbl);
         this.add(host);

         //Port number
         portLbl = new JLabel("Port");
         port = new JTextField(Integer.toString(connection.getPort()));
         port.setEditable(false);
         this.add(portLbl);
         this.add(port);

         //Connection State
         connectionStateLbl = new JLabel("State");
         connectionState = new ConnectionStateLabel(connection.getConnectionState());
         connectionState.setEditable(false);
         add(connectionStateLbl);
         add(connectionState);

         //Connection Statistics
         outgoingBytesLbl = new JLabel("Sent Bytes");
         outgoingBytes = new JTextField("0");
         outgoingBytes.setColumns(7);
         outgoingBytes.setEditable(false);
         oBps = new JTextField("0");
         oBps.setColumns(7);
         oBps.setEditable(false);
         add(outgoingBytesLbl);
         add(outgoingBytes);
         add(oBps);

         incomingBytesLbl = new JLabel("Received Bytes");
         incomingBytes = new JTextField("0");
         incomingBytes.setEnabled(false);
         incomingBytes.setColumns(7);
         iBps = new JTextField("0");
         iBps.setColumns(7);
         iBps.setEditable(false);
         add(incomingBytesLbl);
         add(incomingBytes);
         add(iBps);

         reconnect = new JButton("Reconnect");
         reconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
          //     String message = messageText.getText();
           //    connection.send(message + "\n");
          //     messageText.setText("");
            }
         });
         add(reconnect);

         // Set up clientConnection
         this.connection = connection;
         this.connection.addChangeListener(this);
      }

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
         if (evt.getPropertyName().equals("state")) {
            connectionState.setText((SocketConnector.State) evt.getNewValue());
         }
      }

      public void setBytes(int sent, int received, double sentBps, double rBps){
         outgoingBytes.setText(Integer.toString(sent));
         oBps.setText(String.format("%.2f bps", sentBps));
         incomingBytes.setText(Integer.toString(received));
         iBps.setText(String.format("%.2f bps", rBps));

      }
   }

   public class ConnectionStateLabel extends JTextField {

      public ConnectionStateLabel(SocketConnector.State state) {
         this.setText(connectionStateToString(state));
      }

      public void setText(SocketConnector.State state) {
         this.setText(connectionStateToString(state));
      }

      private String connectionStateToString(SocketConnector.State state) {
         switch (state) {
            case Connected:
               return "connected";
            case Connecting:
               return "connecting";
            case Failed:
               return "failed";
            case Closed:
               return "closed";
            default:
               return "unknown";
         }
      }
   }
}
