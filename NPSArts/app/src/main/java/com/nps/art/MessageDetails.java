package com.nps.art;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessageDetails extends AppCompatActivity {
    Toolbar readMoreTool;
    TextView showEssay;
    String getMessage,getId;
    DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        getMessage=getIntent().getExtras().get("message").toString();
        getId=getIntent().getExtras().get("id").toString();
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        readMoreTool=(Toolbar)findViewById(R.id.readmoreTool);
        showEssay=(TextView)findViewById(R.id.readMoreEssay);
        showEssay.setTypeface(roboto);
        showEssay.setText(getMessage);
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        setSupportActionBar(readMoreTool);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.voting_tools,null);
        actionBar.setCustomView(view);
        final TextView headingPendingPost=(TextView)view.findViewById(R.id.readMoreHeading);
        headingPendingPost.setTypeface(roboto);
        ImageView setBack=(ImageView)view.findViewById(R.id.readMoreBack);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        userRef.child(getId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=String.valueOf(dataSnapshot.child("fullname").getValue());
                String userType=String.valueOf(dataSnapshot.child("userType").getValue());
                headingPendingPost.setText(name+" ("+userType+")");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
