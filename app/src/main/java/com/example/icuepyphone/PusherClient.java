package com.example.icuepyphone;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;

import java.util.ArrayList;


public class PusherClient {
    private ArrayList<String> listData = new ArrayList<>();
    private Pusher pusher;
    private PusherOptions options;

    public PusherClient(ArrayList<String> listData){
        options = new PusherOptions().setCluster(listData.get(3));
        pusher = new Pusher(listData.get(1), options);
    }

    public void connectionListener(){
        // set up a ConnectionEventListener to listen for connection changes to Pusher
        ConnectionEventListener connectionEventListener = new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                System.out.println(String.format("Connection state changed from [%s] to [%s]", change.getPreviousState(), change.getCurrentState()));
            }

            @Override
            public void onError(String message, String code, Exception e) {
                System.out.println(String.format("An error was received with message [%s], code [%s], exception [%s]", message, code, e));
            }
        };
        // connect to Pusher
        pusher.connect(connectionEventListener);
        // set up a ChannelEventListener to listen for messages to the channel and event we are interested in
        ChannelEventListener channelEventListener = new ChannelEventListener() {
            @Override
            public void onSubscriptionSucceeded(String channelName) {
                System.out.println(String.format("Subscription to channel [%s] succeeded", channelName));
            }

            @Override
            public void onEvent(PusherEvent event) {
                System.out.println(String.format("Received event [%s]", event.toString()));
            }
        };
        // subscribe to the channel and with the event listener for the event name
        Channel channel = pusher.subscribe("api_Callback", channelEventListener, "api_event");
        // Keep main thread asleep while we watch for events or application will terminate
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
