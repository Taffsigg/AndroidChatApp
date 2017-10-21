package com.androidchatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class Chat extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    //Firebase reference1, reference2;
    DatabaseReference messageRef, chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) findViewById(R.id.layout2);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        messageRef = FirebaseDatabase.getInstance().getReference("/messages");
//        reference1 = new Firebase("https://rtchat-6d4d7.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
//        reference2 = new Firebase("https://rtchat-6d4d7.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);
        //Toast.makeText(Chat.this, "chat with : " + UserDetails.chatWith + "u are : " + UserDetails.username, Toast.LENGTH_LONG).show();

        final String type1, type2;
        type1 = UserDetails.username + "_" + UserDetails.chatWith;
        type2 = UserDetails.chatWith + "_" + UserDetails.username;
        //Toast.makeText(Chat.this, usertowith, Toast.LENGTH_LONG).show();
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(type1)) {
                    UserDetails.userType = "type1";
                } else if (dataSnapshot.hasChild(type2)) {
                    UserDetails.userType = "type2";
                } else {
                    UserDetails.userType = "type1";
                    dataSnapshot.child(type1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (UserDetails.userType.equals("type1")) {
            //type 1 child already exists :)
            chatRef = messageRef.child(type1);
        } else {
            //type 2 child already exixts :)
            chatRef = messageRef.child(type2);
        }


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.username);
                    chatRef.push().setValue(map);
//                    chatRef2.push().setValue(map);
//                    reference1.push().setValue(map);
//                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //  Object map = dataSnapshot.getValue();
//                Toast.makeText(Chat.this, "" + dataSnapshot.child("message").getValue(), Toast.LENGTH_LONG).show();
                String message = String.valueOf(dataSnapshot.child("message").getValue());
                String userName = String.valueOf(dataSnapshot.child("user").getValue());
//                String message = map.get("message").toString();
//                String userName = map.get("user").toString();

                if (userName.equals(UserDetails.username)) {
                    addMessageBox("You:-\n" + message, 1);
                } else {
                    addMessageBox(UserDetails.chatWith + ":-\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        } else {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}