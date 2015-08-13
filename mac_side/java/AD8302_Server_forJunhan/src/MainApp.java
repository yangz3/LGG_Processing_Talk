import processing.core.PApplet;


public class MainApp extends PApplet{
	MainServer ms;
	Integer [] receivedAnalogRead;
	
	public void setup(){
		receivedAnalogRead = new Integer[8];
		ms = new MainServer();
		(new Thread(ms)).start();
		//ms.run();
		frameRate(10);
		size(127,127);
		System.out.println("done setting up!");
	}
	
	public void draw(){
		clear();
		background(0);
		System.out.printf("X:%d Y:%d\n", mouseX, mouseY);
		stroke(0,0,255);
		strokeWeight(20);
		point(mouseX,mouseY);
		byte[] message = new byte[2];
		message[0]=(byte) mouseX;
		message[1]=(byte) mouseY;
		String messageStr = new String(message);
		ms.sendData(messageStr);
		if(ms.newDataAvalaible){
			
			System.out.println("received hex string:");
			System.out.println(ms.stringForMainThreadToCall);
			System.out.println("translated to int:");
			receivedAnalogRead = translate(ms.stringForMainThreadToCall);
			for(int i = 0; i < 8; i++){
				System.out.print(receivedAnalogRead[i]);
				print(" ");
			}
			println("");
			
			ms.newDataAvalaible = false;
		}
	}
	
	private Integer[] translate(String s){
		Integer[] rst = new Integer[8];
		for(int i = 0; i < 8; i++){
			rst[i] = Integer.parseInt(s.substring(i*4, i*4+4), 16);
		}
		return rst;
	}

}
