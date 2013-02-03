/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apache.ws.commons.tcpmon;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author marc
 */
public class ViewUpdater extends Thread {
    /**
     * Field inputText
     */
    JTextArea inputText = null;

    /**
     * Field inputScroll
     */
    JScrollPane inputScroll = null;

    /**
     * Field outputText
     */
    JTextArea outputText = null;

    /**
     * Field outputScroll
     */
    JScrollPane outputScroll = null;

}
