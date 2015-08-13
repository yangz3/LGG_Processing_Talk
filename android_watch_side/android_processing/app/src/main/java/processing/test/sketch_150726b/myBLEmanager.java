package processing.test.sketch_150726b;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by hanny on 2015/7/26.
 */
public class myBLEmanager {

    private static final String TAG = "MyActivity";
    private static final String SERVICE_UUID_1 = "552a95e0-6a69-4772-958f-e53fbc37bb72";
    private static final String Char_UUID_1 = "552a95e2-6a69-4772-958f-e53fbc37bb72";
    private static final String SERVICE_DEVICE_INFORMATION = "0000180a-0000-1000-8000-00805f9b34fb";

    private String serviceOneCharUuid;
    private BluetoothAdapter mBT;
    private BluetoothLeAdvertiser mBLE;
    private ArrayList<BluetoothGattService> adService;
    private List<ParcelUuid> serviceUuids;
    private boolean advertising = false;
    private boolean isConnected = false;
    private BluetoothGattServer gattServer;
    private int connectNum=1;
    //private AdvertiseCallback advertiseCallback; //Must implement and set
    //private BluetoothGattServerCallback gattServerCallback; //Must implement and set

    private Activity myActivity;
    private String inputData;
    private String outputData;
    private ArrayList<String> logMsg;
    private boolean new_read;
    private boolean new_write;

    private AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings advertiseSettings) {
            String successMsg = "Advertisement command attempt successful";
            Log.d(TAG, successMsg);
            show_message(successMsg);
        }

        @Override
        public void onStartFailure(int i) {
            String failMsg = "Advertisement command attempt failed: " + i;
            Log.e(TAG, failMsg);
            show_message(failMsg);
        }
    };

    private BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.d(TAG, "onConnectionStateChange status=" + status + "->" + newState);
            show_message("Connected state changed!");
            switch (newState){
                case BluetoothProfile.STATE_CONNECTED:
                    show_message("State Connected!");
                    isConnected = true;
                    break;
                case BluetoothProfile.STATE_CONNECTING:
                    show_message("State Connecting!");
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    show_message("State Disconnected!");
                    isConnected = false;
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    show_message("State Disconnecting!");
                    break;
            }
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
            show_message("Service added!");
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.d(TAG, "onCharacteristicReadRequest requestId=" + requestId + " offset=" + offset);
            show_message("Got a read request!");
            if (characteristic.getUuid().equals(UUID.fromString(serviceOneCharUuid))) {
                Log.d(TAG, "SERVICE_UUID_1");
                //characteristic.setValue("Text:This is a test characteristic");
                //characteristic.setValue(Integer.toString(connectNum)+" times connected");
                //connectNum++;
                characteristic.setValue(outputData);
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                        characteristic.getValue());
                new_read=true;
            }

        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            if(responseNeeded) {
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
            }
            Log.d(TAG, "onCharacteristicWriteRequest requestId=" + requestId + " preparedWrite="
                    + Boolean.toString(preparedWrite) + " responseNeeded="
                    + Boolean.toString(responseNeeded) + " offset=" + offset);
            show_message("Got a write request with values:");
            show_message(new String(value));
            inputData= new String(value);
            new_write=true;
        }
    };

    public myBLEmanager(Activity a){
        myActivity= a;
        inputData="no input";
        outputData="no output";
        logMsg = new ArrayList<>();
        new_read=false;
        new_write=false;
        BLEsetup();
    }

    private void BLEsetup(){
        if (myActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            show_message("Bluetooth setting up");
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) myActivity.getSystemService(Context.BLUETOOTH_SERVICE);
            mBT = bluetoothManager.getAdapter();
            mBT.setName("Mi's LG Watch");
            mBLE=mBT.getBluetoothLeAdvertiser();
            adService = new ArrayList<BluetoothGattService>();
            serviceUuids = new ArrayList<ParcelUuid>();
            gattServer = bluetoothManager.openGattServer(myActivity, gattServerCallback);
        }
    }

    private void startAdvertise() {
        startGattServer();

        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();


        dataBuilder.setIncludeTxPowerLevel(false); //necessity to fit in 31 byte advertisement

        //dataBuilder.setServiceUuids(serviceUuids);

        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        settingsBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        //settingsBuilder.setType(AdvertiseSettings.ADVERTISE_TYPE_CONNECTABLE);

        mBLE.startAdvertising(settingsBuilder.build(), dataBuilder.build(), advertiseCallback);
        advertising = true;
    }

    private void stopAdvertise() {
        mBLE.stopAdvertising(advertiseCallback);
        gattServer.clearServices();
        gattServer.close();
        adService.clear();
        advertising = false;
    }

    private void startGattServer() {

        for(int i = 0; i < adService.size(); i++) {
            gattServer.addService(adService.get(i));
        }
    }

    private void addService(BluetoothGattService service) {
        adService.add(service);
        serviceUuids.add(new ParcelUuid(service.getUuid()));
    }

    private void addServiceToGattServer() {
        //serviceOneCharUuid = UUID.randomUUID().toString();
        serviceOneCharUuid=Char_UUID_1;

        BluetoothGattService firstService = new BluetoothGattService(
                UUID.fromString(SERVICE_UUID_1),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        // alert level char.
        BluetoothGattCharacteristic firstServiceChar = new BluetoothGattCharacteristic(
                UUID.fromString(serviceOneCharUuid),
                BluetoothGattCharacteristic.PROPERTY_READ |
                        BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ |
                        BluetoothGattCharacteristic.PERMISSION_WRITE);
        firstService.addCharacteristic(firstServiceChar);
        addService(firstService);
    }

    private void show_message(final String message){
        logMsg.add(message);
    }

    public void startBLE(){
        if(!advertising){
            addServiceToGattServer();
            startAdvertise();
        }else{
            show_message("BLE already started!");
        }
    }

    public void stopBLE(){
        if(advertising){
            stopAdvertise();
        }else{
            show_message("BLE already stopped!");
        }
    }

    public void setOutputData(String data){
        outputData=data;
    }

    public String getInputData(){
        return inputData;
    }

    public boolean isConnected(){
        return isConnected;
    }

    public boolean isNew_read(){
        boolean val = new_read;
        new_read=false;
        return val;
    }

    public boolean isNew_write(){
        boolean val = new_write;
        new_write=false;
        return val;
    }

    public boolean isNew_log(){
        return !logMsg.isEmpty();
    }

    public String getLogMsg(){
        if(logMsg.isEmpty())
            return null;
        String msg = logMsg.get(0);
        logMsg.remove(0);
        return msg;
    }
    /*
    @Override
    public void onClick(View v) {
        if(!advertising){
            addServiceToGattServer();
            startAdvertise();
        }else{
            stopAdvertise();
        }
    }
    */
}
