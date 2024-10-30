package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com


import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer {
  private final String LOGIN_KEY = "loginID";

  private String getLoginKey() {
    return LOGIN_KEY;
  }


  //Constructors ****************************************************

  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) {
    super(port);
  }


  //Instance methods ************************************************

  /**
   * This method handles any messages received from the client.
   *
   * @param msg    The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
    String[] message = msg.toString().split(" ");
    if (message[0].equals("#login")) {
      System.out.println("Message received: " + msg + " from " + client.getInfo(getLoginKey()));
      client.setInfo(getLoginKey(), message[1]);
      System.out.println(client.getInfo(getLoginKey()) + " has logged on.");
    } else {
      String loginId = (String) client.getInfo(getLoginKey());
      String broadcastMessage = loginId + " > " + msg.toString();
      System.out.println("Message received: " + msg.toString() + " from " + loginId);
      this.sendToAllClients(broadcastMessage);
    }
  }


  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted() {
    System.out.println
            ("Server listening for connections on port " + getPort());
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped() {
    System.out.println("Server has stopped listening for connections.");
  }



  //Class methods ***************************************************

  /**
   * This method is responsible for the creation of
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555
   *          if no argument is entered.
   */

  /**
   * Implements the hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   *
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {
    System.out.println("A new client has connected to the server.");
  }

  /**
   * Implements the hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */

  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
    String loginId = (String) client.getInfo(getLoginKey());
    if (loginId != null) {
      System.out.println(loginId + " has disconnected.");
    } else {
      System.out.println("A client has disconnected.");
    }
  }

}
