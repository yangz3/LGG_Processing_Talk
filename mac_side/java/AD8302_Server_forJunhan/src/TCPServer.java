//TCPServer.java

import java.io.*;
import java.net.*;

interface SocketConnection
{
	void clientDataReceived(String s);
}

public class TCPServer 
{

	public SocketConnection delegate;
	private ServerSocket server;
	private PrintWriter client;
	private BufferedReader inFromClient;
	private PrintWriter outToClient;
	private boolean isReady=false;
	
	public TCPServer(int sPort)
	{
		isReady=false;
		try {
			 server = new ServerSocket (sPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println ("TCPServer Waiting for client on port: "+sPort);
	}
		
	public void runServer() throws Exception
	{
		String fromclient;

        while(true) 
        {
           Socket connected = server.accept();
           System.out.println( " THE CLIENT"+" "+connected.getInetAddress() +":"+connected.getPort()+" IS CONNECTED ");
           isReady=true;
           inFromClient = new BufferedReader(new InputStreamReader(connected.getInputStream()));
           outToClient = new PrintWriter(connected.getOutputStream(),true);
           client = outToClient;
           System.out.print("bee");
           outToClient.write("Hello There!\n");
           outToClient.flush();
           System.out.print("eep!\n");

           while ( true )
           {           	
	           fromclient = inFromClient.readLine();	
              
            	  delegate.clientDataReceived(fromclient);
		    
               Thread.sleep(1);
			}  
         }
	}
	
	public void sendData(String data){
		if(isReady){
			outToClient.write(data);
	        outToClient.flush();
		}
	}
	
	public void disconnectAllClients()
	{
		
	}

}