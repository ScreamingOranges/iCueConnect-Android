package com.example.icuepyphone;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PusherClient {
    public Map<String,String> devices;
    private Pusher pusher;
    private PusherOptions options;
    private Context mainContext;

    public PusherClient(ArrayList<String> pusherCredentials, Context context){
        mainContext = context;
        //Make pusher receiving object
        options = new PusherOptions().setCluster(pusherCredentials.get(3));
        pusher = new Pusher(pusherCredentials.get(1), options);
    }

    public void eventHandler(PusherEvent event){
        switch (event.getEventName()){
            //For running test command from iCue connect API that shows pop up in app
            case "test_event":
                ContextCompat.getMainExecutor(mainContext).execute(()  -> {
                    Utility.showNotice(mainContext, "Notice!", event.getData());
                });
                break;
            //For receiving devices from iCue connect API
            case "api_event":
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
                    devices =  mapper.readValue(event.getData(), HashMap.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("UNRECOGNISED EVENT");
        }
    }

    public void connectionListener(){
        ConnectionEventListener connectionEventListener = new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                //System.out.println(String.format("Connection state changed from [%s] to [%s]", change.getPreviousState(), change.getCurrentState()));
            }

            @Override
            public void onError(String message, String code, Exception e) {
                //System.out.println(String.format("An error was received with message [%s], code [%s], exception [%s]", message, code, e));
            }
        };

        ChannelEventListener channelEventListener = new ChannelEventListener() {
            @Override
            public void onSubscriptionSucceeded(String channelName) {
                //System.out.println(String.format("Subscription to channel [%s] succeeded", channelName));
            }

            @Override
            public void onEvent(PusherEvent event) {
                //System.out.println(String.format("Received event [%s]", event.getEventName()));
                eventHandler(event);
            }
        };

        pusher.connect(connectionEventListener);
        Channel channel = pusher.subscribe("api_Callback", channelEventListener, "api_event", "test_event");


        while (true) {
            try {
                Thread.sleep(1000);
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
