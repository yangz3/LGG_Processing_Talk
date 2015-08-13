

public class MainServer implements SocketConnection, Runnable{
	
	TCPServer tcpServer = new TCPServer(10030);
	String stringForMainThreadToCall;
	boolean newDataAvalaible;

	MainServer(){
		stringForMainThreadToCall= "";
		newDataAvalaible = false;
	}
	
	public void run(){
		tcpServer.delegate = this;
		Thread t = new Thread() {
		   public void run() {
			   while(true) {
				   try {
					tcpServer.runServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			   }
		  };
		};
		
		t.start();	
	}

	public void clientDataReceived(String s)
	{
		
		stringForMainThreadToCall = s;
		newDataAvalaible = true;
	}
	
	public void sendData(String data){
		tcpServer.sendData(data);
	}

}
