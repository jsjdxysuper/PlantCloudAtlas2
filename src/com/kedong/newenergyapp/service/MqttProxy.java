package com.kedong.newenergyapp.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttProxy {
    private Context context;
    private MqttAndroidClient mqttAndroidClient;

    final String serverUri = "tcp://iot.eclipse.org:1883";
    String clientId = "ding";
    final String subscriptionTopic = "/com.kedong.newenergy/mobileapp";
    final String publishTopic = "exampleAndroidPublishTopic";
    final String publishMessage = "Hello World!";
    private Handler handler;

    public MqttProxy(Context context,Handler handler){
        this.context = context;
        this.handler = handler;
    }

    /**
     * 为Mqtt订阅要收到的消息的topic
     */
    public void setMqtt(){
        clientId = clientId + System.currentTimeMillis();

        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    //Toast.makeText(context,("MQTT Reconnected to : " + serverURI), Toast.LENGTH_LONG).show();
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic();
                } else {
                    //Toast.makeText(context,("MQTT Connected to: " + serverURI), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                //Toast.makeText(context,("The MQTT Connection was lost."), Toast.LENGTH_LONG).show();
                System.err.println("The Connection was lost.");
                cause.printStackTrace();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //Toast.makeText(context,("MQTT Incoming message: " + new String(message.getPayload())), Toast.LENGTH_LONG).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //Toast.makeText(context,("MQTT Failed to connect to: " + serverUri), Toast.LENGTH_LONG).show();
                    exception.printStackTrace();
                    Log.e("mqtt",exception.getMessage());
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
            Log.e("mqtt",ex.getMessage());
        }
    }


    public void subscribeToTopic(){
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                   // Toast.makeText(context,("MQTT Subscribed!"), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //Toast.makeText(context,("MQTT Failed to subscribe!"), Toast.LENGTH_LONG).show();

                }
            });

            // THIS DOES NOT WORK!
            mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String recLoadMsg = new String(message.getPayload(),"GBK");
                    // message Arrived!
                    System.err.println("MQTT Message: " + topic + " : " +recLoadMsg );
                    Message msg = new Message();
                    msg.what = 0x201;
                    msg.obj = recLoadMsg;
                    handler.sendMessage(msg);
                }
            });

        } catch (MqttException ex){
            System.err.println("MQTT Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
