#import "LXCBAppDelegate.h"
#import "LXCBCentralClient.h"
#import "Communicator.h"
#import "NSData+Conversion.h"

@interface LXCBAppDelegate () <LXCBCentralClientDelegate>

@property (nonatomic, strong) LXCBCentralClient *central;

// for TCP socket
@property (nonatomic, strong) Communicator *c;


@end

@implementation LXCBAppDelegate

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification {
  // Set up Bluetooth Central implementation
    
  self.central = [[LXCBCentralClient alloc] initWithDelegate:self];
    
  self.central.serviceName = @"Test";
    
    //self.central.deviceName =@"Mi's LG Watch";

  self.central.serviceUUIDs = @[
      //[CBUUID UUIDWithString:@"6E400001-B5A3-F393-E0A9-E50E24DCCA9E"]
      [CBUUID UUIDWithString:@"552a95e0-6a69-4772-958f-e53fbc37bb72"]
                                ]; // service uuid
    
  self.central.characteristicUUIDs = @[
      //[CBUUID UUIDWithString:@"6E400003-B5A3-F393-E0A9-E50E24DCCA9E"]
      [CBUUID UUIDWithString:@"552a95e2-6a69-4772-958f-e53fbc37bb72"]
      ]; // characteristic uuid
    
    /**/
  // Set up TCP socket
  self.c = [[Communicator alloc] init];
    self.c.delegate = self;
    
  self.c->host = @"http://127.0.0.1";
  self.c->port = 10030;
  //  self.c->host= @"http://128.237.209.129";
  //  self.c->port=27015;
    
  [self.c setup];
  [self.c open];
    

  // Set up some basic hooks in the interface.
  self.textView.font = [NSFont fontWithName:@"Monaco" size:12];
}

- (void)applicationWillTerminate:(NSNotification *)notification {
    
    
    
    
    
    

}

- (void)appendLogMessage:(NSString *)message {
  self.textView.string = [self.textView.string stringByAppendingFormat:@"%@\n", message];
  [self.textView performSelector:@selector(scrollPageDown:) withObject:nil afterDelay:0];
}

- (IBAction)buttonDidPress:(id)sender {
  [self.central connect];
}
- (IBAction)button2DidPress:(id)sender {
    // close BLE connection
    [self.central disconnect];
    
    // close TCP socket
    [self.c close];
    
    NSLog(@"TCP and BLE disconnected");
    
}
- (IBAction)button3Press:(id)sender {
    NSString* str = @"Hi there!";
    NSData* data = [str dataUsingEncoding:NSUTF8StringEncoding];
    /*      _______________
            |||||||||||||||
            |||||||||||||||
            |||||||||||||||
            |||||||||||||||
            |||||||||||||||
            |||||||||||||||
            |||||||||||||||    Call this to write data to the BLE peripheral
            |||||||||||||||    Takes in NSData type
            |||||||||||||||
            |||||||||||||||
            |||||||||||||||    Can't do much with the socket
            |||||||||||||||    It looks like the input/output stream are all
            |||||||||||||||    setup but the inputstream is only ready if
            |||||||||||||||    the processing program closes for some reason.
            |||||||||||||||
            |||||||||||||||
    ________|||||||||||||||_________
    \\\\\\\\|||||||||||||||/////////
     \\\\\\\\|||||||||||||/////////
      \\\\\\\|||||||||||||////////
       \\\\\\\|||||||||||////////
        \\\\\\|||||||||||///////
         \\\\\\|||||||||///////
          \\\\\|||||||||//////
           \\\\\|||||||//////
            \\\\|||||||/////
             \\\\|||||/////
              \\\\||||////
               \\\|||////
                \\\||///
                 \\||//
                  \||/
                   \/
     */
    [self.c writeOut:@"Hello There!\n"];
    //[self.central write:data];
}

#pragma mark - LXCBCentralClientDelegate

- (void)centralClientDidConnect:(LXCBCentralClient *)central {
  [self appendLogMessage:@"Connnected to Peripheral"];
    [self.central subscribe];
}

- (void)centralClientDidDisconnect:(LXCBCentralClient *)central {
  [self appendLogMessage:@"Disconnected to Peripheral"];

}

- (void)centralClientDidSubscribe:(LXCBCentralClient *)central {
  [self appendLogMessage:@"Subscribed to Characteristic"];
   
}

- (void)centralClientDidUnsubscribe:(LXCBCentralClient *)central {
  [self appendLogMessage:@"Unsubscribed to Characteristic"];

}


- (void)centralClient:(LXCBCentralClient *)central
       characteristic:(CBCharacteristic *)characteristic
       didUpdateValue:(NSData *)value {
    
    
    NSString *someDataHexadecimalString = [value hexadecimalString];
    
   // NSString *printable = [[NSString alloc] initWithData:value encoding:NSUTF8StringEncoding];
   //NSLog(@"didUpdateValue: %@", printable); // print received data to console (from BLE)

    
  [self.c writeOut:someDataHexadecimalString];
    
    // send data to socket (to server) IMP bytes should have '\n' at the end for socket to know
    //it is the end of this transmission'\n'
    
   [self.c writeOut:@"\n"];

}

- (void)centralClient:(LXCBCentralClient *)central connectDidFail:(NSError *)error {
  NSLog(@"Error: %@", error);
  [self appendLogMessage:[error description]];
}

- (void)centralClient:(LXCBCentralClient *)central
requestForCharacteristic:(CBCharacteristic *)characteristic
              didFail:(NSError *)error {
  NSLog(@"Error: %@", error);
  [self appendLogMessage:[error description]];
}

- (void)Communicator:(Communicator*)Comm
     receivedMessage:(NSString*)message;{
    [self appendLogMessage:@"Received message"];
    [self appendLogMessage:message];
    NSData* data = [message dataUsingEncoding:NSUTF8StringEncoding];
    [self.central write:data];
}

- (BOOL)applicationShouldTerminateAfterLastWindowClosed:(NSApplication *)theApplication {
    return YES;
}

@end
