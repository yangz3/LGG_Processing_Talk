package processing.test.sketch_150726b;

import android.os.PowerManager;

import processing.core.PApplet;

public class sketch_150726b extends PApplet {
  private String displayText = "Hello Watch!\n";
  private float backR, backG, backB;
  private float cX,cY;
  private myBLEmanager myBLE;
  private boolean BLEon;
  private int connectNum=1;
  private PowerManager.WakeLock myScreenLock;
  public void setup() {
    //PowerManager myPowerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
    //myScreenLock = myPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP,"WakeLock");
    myBLE = new myBLEmanager(this);
    myBLE.setOutputData(Integer.toString(connectNum)+" times connected");
    BLEon=false;
    backR=0;
    backG=0;
    backB=0;
    cX=280/2;
    cY=280/2;
  }

  public void draw(){
    //myScreenLock.acquire();
    background(backR,backG,backB);
    fill(0,0,128);
    ellipse(cX, cY, 15, 15);
    while(myBLE.isNew_log()){
      displayText=myBLE.getLogMsg()+"\n"+displayText;
    }
    if(BLEon){
      backR=128;
    }else{
      backR=0;
    }
    if(myBLE.isConnected()){
      backG=128;
    }else{
      backG=0;
    }
    if(myBLE.isNew_read()){
      myBLE.setOutputData(Integer.toString(++connectNum)+" times connected");
      //if(backR>64)
      //  backR=0;
      //else
      //  backR=128;
    }
    if(myBLE.isNew_write()){
      //if(backG>64)
      //  backG=0;
      //else
      //  backG=128;
      String msg = myBLE.getInputData();
      cX=(float)msg.charAt(0);
      cX=cX*280/127;
      cY=(float)msg.charAt(1);
      cY=cY*280/127;
    }
    fill(255);
    text(displayText, 15, 25);
    //myScreenLock.release();
  }

  public void mousePressed(){
    displayText="clicked\n"+displayText;
    if(BLEon){
      myBLE.stopBLE();
      BLEon=false;
    }else{
      myBLE.startBLE();
      BLEon=true;
    }
  }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "sketch_150726b" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
