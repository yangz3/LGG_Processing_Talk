
#include <SPI.h>
#include "Adafruit_BLE_UART.h"

// Connect CLK/MISO/MOSI to hardware SPI
// e.g. On UNO & compatible: CLK = 13, MISO = 12, MOSI = 11
#define ADAFRUITBLE_REQ 10
#define ADAFRUITBLE_RDY 2     // This should be an interrupt pin, on Uno thats #2 or #3
#define ADAFRUITBLE_RST 9

Adafruit_BLE_UART BTLEserial = Adafruit_BLE_UART(ADAFRUITBLE_REQ, ADAFRUITBLE_RDY, ADAFRUITBLE_RST);
/**************************************************************************/
/*!
    Configure the Arduino and start advertising with the radio
*/
/**************************************************************************/
long count = 0;

void setup(void)
{ 
  Serial.begin(9600);
  while(!Serial); // Leonardo/Micro should wait for serial init
  Serial.println(F("Adafruit Bluefruit Low Energy nRF8001 Print echo demo"));

  BTLEserial.setDeviceName("AD8302"); /* 7 characters max! */

  BTLEserial.begin();
}

/**************************************************************************/
/*!
    Constantly checks for new events on the nRF8001
*/
/**************************************************************************/
aci_evt_opcode_t laststatus = ACI_EVT_DISCONNECTED;

void loop()
{
  // Tell the nRF8001 to do whatever it should be working on.
  BTLEserial.pollACI();

  // Ask what is our current status
  aci_evt_opcode_t status = BTLEserial.getState();
  // If the status changed....
  if (status != laststatus) {
    // print it out!
    if (status == ACI_EVT_DEVICE_STARTED) {
        Serial.println(F("* Advertising started"));
    }
    if (status == ACI_EVT_CONNECTED) {
        Serial.println(F("* Connected!"));
    }
    if (status == ACI_EVT_DISCONNECTED) {
        Serial.println(F("* Disconnected or advertising timed out"));
    }
    // OK set the last status change to this one
    laststatus = status;
  }


  // for mac to send commands to BLE
  // disabled for now
  /*
  if (status == ACI_EVT_CONNECTED) {
    // Lets see if there's any data for us!
    if (BTLEserial.available()) {
      Serial.print("* "); Serial.print(BTLEserial.available()); Serial.println(F(" bytes available from BTLE"));
    }
    // OK while we still have something to read, get a character and print it out
    while (BTLEserial.available()) {
      char c = BTLEserial.read();
      Serial.print(c);
    }
   */
    
    

    // see if we have any data to get from the Serial console
    if (status == ACI_EVT_CONNECTED) {
      
      //count++;

      // We need to convert the reading to bytes, no more than 20 at this time
      uint8_t sendbuffer[20];

      delay(2);
      int a0Read = analogRead(A0);
      //a0Read = count;
      sendbuffer[0] = (uint8_t)((a0Read >> 8) & 0xff);
      sendbuffer[1] = (uint8_t)(a0Read & 0xff);
      
      delay(2);
      int a1Read = analogRead(A1);
      //a1Read = count;
      sendbuffer[2] = (uint8_t)((a1Read >> 8) & 0xff);
      sendbuffer[3] = (uint8_t)(a1Read & 0xff);
      
      delay(2);
      int a2Read = analogRead(A2);
      //a2Read = count;
      sendbuffer[4] = (uint8_t)((a2Read >> 8) & 0xff);
      sendbuffer[5] = (uint8_t)(a2Read & 0xff);
      
      delay(2);
      int a3Read = analogRead(A3);
      //a3Read = count;
      sendbuffer[6] = (uint8_t)((a3Read >> 8) & 0xff);
      sendbuffer[7] = (uint8_t)(a3Read & 0xff);
      
      delay(2);
      int a4Read = analogRead(A4);
      //a4Read = count;
      sendbuffer[8] = (uint8_t)((a4Read >> 8) & 0xff);
      sendbuffer[9] = (uint8_t)(a4Read & 0xff);
      
      delay(2);
      int a5Read = analogRead(A5);
      //a5Read = count;
      sendbuffer[10] = (uint8_t)((a5Read >> 8) & 0xff);
      sendbuffer[11] = (uint8_t)(a5Read & 0xff);
      
      delay(2);
      int a6Read = analogRead(A6);
      //a6Read = count;
      sendbuffer[12] = (uint8_t)((a6Read >> 8) & 0xff);
      sendbuffer[13] = (uint8_t)(a6Read & 0xff);
      
      delay(2);
      int a7Read = analogRead(A7);
      //a7Read = count;
      sendbuffer[14] = (uint8_t)((a7Read >> 8) & 0xff);
      sendbuffer[15] = (uint8_t)(a7Read & 0xff);
      sendbuffer[16] = (uint8_t)(0x0A);

      char sendbuffersize = 17;

      //Serial.print(F("\n* Sending -> \"")); Serial.print((char *)sendbuffer); Serial.println("\"");

       //delay(10); // required between BTLEserial.write cannot be less!
       // has been distributed between analogRead!
       
      // write the data
      BTLEserial.write(sendbuffer, sendbuffersize);
    

  }
}
