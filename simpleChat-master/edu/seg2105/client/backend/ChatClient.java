// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;
import java.util.Arrays;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;

  /**
   * Stores the users login ID
   */
  private String loginId = "";


    //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   * @param loginId The users login ID
   */
  
  public ChatClient(String loginId, String host, int port, ChatIF clientUI)
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginId = loginId;
    openConnection();
  }

  
  //Instance methods ************************************************

    /**
     * Returns the users login ID
     * @return loginId
     */
    public String getLoginId() {
        return loginId;
    }

    /**
     * Sets the users login ID
     * @param loginId
     */
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message) {
    try
    {
      if(message.startsWith("#")) handleUserCommand(message);
      else sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server. Terminating client.");
      quit();
    }
  }

  private void handleUserCommand(String message) throws IOException {
    String[] command = message.split(" ");
        switch (command[0]) {
            case "#quit" -> quit();
            case "#logoff" -> closeConnection();
            case "#sethost" -> {
                if (!isConnected()) {
                    String newHost = command[1];
                    setHost(newHost);
                    System.out.println("Host has been successfully set.");
                } else {
                    System.out.println("Cannot set new host while logged on.");
                }
            }
            case "#setport" -> {
                if (!isConnected()) {
                    int newPort = Integer.parseInt(command[0]);
                    setPort(newPort);
                    System.out.println("Port has been successfully set.");
                } else {
                    System.out.println("Cannot set new port while logged on.");
                }
            }
            case "#login" -> {
                if (!isConnected()) {
                    openConnection();
                    System.out.println("Successfully logged in.");
                } else {
                    System.out.println("Currently logged in.");
                }
            }
            case "#gethost" -> System.out.println("Current host: " + getHost());
            case "#getport" -> System.out.println("Current port: " + getPort());
            default -> System.out.println("Please enter a valid command.");
        }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  /**
   * Implements the hook method called after the connection has been closed. The default
   * implementation does nothing. The method may be overriden by subclasses to
   * perform special processing such as cleaning up and terminating, or
   * attempting to reconnect.
   */
  @Override
  protected void connectionClosed() {
    clientUI.display("Connection closed.");
  }

    /**
     * Implements the hook method called after a connection has been established. The default
     * implementation does nothing. It may be overridden by subclasses to do
     * anything they wish.
     */
    @Override
    protected void connectionEstablished() {
        try {
            sendToServer("#login " + getLoginId());
        } catch (IOException e) {
            System.out.println("ERROR - Could not send login ID to server");
            System.exit(1);
        }
    }

  /**
   * Implements the hook method called each time an exception is thrown by the client's
   * thread that is waiting for messages from the server. The method may be
   * overridden by subclasses.
   *
   * @param exception
   *            the exception raised.
   */
  @Override
  protected void connectionException(Exception exception) {
    clientUI.display("The server has shut down.");
    quit();
  }
}

//End of ChatClient class
