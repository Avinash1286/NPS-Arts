package com.nps.art;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class About extends AppCompatActivity {
    RecyclerView showMessage;
    Toolbar aboutToolbar;
    DatabaseReference messageRef,userRefs;
    FirebaseAuth mAuuth;
    String currentId;
    TextView noMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        showMessage=(RecyclerView) findViewById(R.id.showMessage);
        showMessage.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        showMessage.setLayoutManager(layoutManager);
        mAuuth=FirebaseAuth.getInstance();
        currentId=mAuuth.getCurrentUser().getUid();
        noMessage=(TextView)findViewById(R.id.noMessage);
        noMessage.setVisibility(View.GONE);
        userRefs=FirebaseDatabase.getInstance().getReference().child("Users");
        aboutToolbar=(Toolbar)findViewById(R.id.aboutTool);
        setSupportActionBar(aboutToolbar);
        ActionBar actionBar=getSupportActionBar();
        messageRef= FirebaseDatabase.getInstance().getReference().child("AuthorityMessage");
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.about_tool,null);
        actionBar.setCustomView(view);
        ImageView back=(ImageView)findViewById(R.id.notificationImage);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final ImageView showOptions=(ImageView)findViewById(R.id.clearNotification);
        TextView heading=(TextView)findViewById(R.id.notificationHeading);
        heading.setTypeface(roboto);
        showMessage();
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    noMessage.setVisibility(View.GONE);
                     if (dataSnapshot.hasChild(currentId)){
                         showOptions.setVisibility(View.GONE);
                    }
                     else {
                         showOptions.setVisibility(View.VISIBLE);
                     }
                }
                else {
                        noMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userType=String.valueOf(dataSnapshot.child(currentId).child("userType").getValue());
                if (userType.equals("Principal") || userType.equals("Commandant") || userType.equals("Vice Principal") || userType.equals("Chair Person")){
                      showOptions.setVisibility(View.VISIBLE);
                }
                else {
                    showOptions.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        showOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(About.this,showOptions);
                popupMenu.getMenuInflater().inflate(R.menu.after_message,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.addMess:
                                add_your_message add_your_message=new add_your_message();
                                FragmentManager fragmentManager=getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                                fragmentTransaction.add(R.id.showAddmessage,add_your_message);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    }
    private void showMessage() {
        FirebaseRecyclerAdapter<MessageModel,MessageHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<MessageModel, MessageHolder>
                (MessageModel.class,R.layout.message_card,MessageHolder.class,messageRef) {
            @Override
            protected void populateViewHolder(final MessageHolder messageHolder, final MessageModel messageModel, int i) {
                final String userKey=getRef(i).getKey();
                messageHolder.setNameAndProfile(getApplicationContext(),messageModel.getUserId());
                messageHolder.setWholeMessage(getApplicationContext(),messageModel.getMessage());
                messageHolder.setOptionsVisibility(messageModel.getUserId());
                messageHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       Intent intent=new Intent(About.this,MessageDetails.class);
                       intent.putExtra("message",messageModel.getMessage());
                       intent.putExtra("id",messageModel.getUserId());
                       startActivity(intent);
                    }
                });
                messageHolder.showOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu=new PopupMenu(About.this,messageHolder.showOptions);
                        popupMenu.getMenuInflater().inflate(R.menu.before_message,popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.editMessage:
                                        EditMessage editMessage=new EditMessage();
                                        FragmentManager fragmentManager=getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction1=fragmentManager.beginTransaction();
                                        EditMessage.messagees=messageModel.getMessage();
                                        fragmentTransaction1.add(R.id.showAddmessage,editMessage);
                                        fragmentTransaction1.addToBackStack(null);
                                        fragmentTransaction1.commit();
                                        break;
                                    case R.id.deleteMessage:
                                        final AlertDialog.Builder builder=new AlertDialog.Builder(About.this);
                                        builder.setTitle("Deleting Post");
                                        builder.setMessage("Are you sure to delete this Message?");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                messageRef.child(userKey).removeValue();
                                            }
                                        });
                                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.create().show();

                                    break;
                                }



                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }
        };
        showMessage.setAdapter(firebaseRecyclerAdapter);
    }
    public static class MessageHolder extends RecyclerView.ViewHolder{
        View mView;
        FirebaseAuth mAuths;
        DatabaseReference userRefs;
        String currentID;
        TextView showName,actualMessage;
        ImageView showImage,showOptions;
        public MessageHolder(@NonNull View itemView) {
            super(itemView);
        mView=itemView;
        mAuths=FirebaseAuth.getInstance();
        currentID=mAuths.getCurrentUser().getUid();
        userRefs=FirebaseDatabase.getInstance().getReference().child("Users");
        showName=(TextView)mView.findViewById(R.id.holdNameInAbout);
        showImage=(ImageView)mView.findViewById(R.id.photoHolder);
        actualMessage=(TextView)mView.findViewById(R.id.holdDesInAbout);
        showOptions=(ImageView) mView.findViewById(R.id.showOptionsFromDot);
        }
        public void setOptionsVisibility(final String userKey){
          if (userKey.equals(currentID)){
              showOptions.setVisibility(View.VISIBLE);
          }
          else {
              showOptions.setVisibility(View.GONE);
          }
        }
        public void setNameAndProfile(final Context context, final String userKeys){
            userRefs.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              String name=String.valueOf(dataSnapshot.child(userKeys).child("fullname").getValue());
              String userType=String.valueOf(dataSnapshot.child(userKeys).child("userType").getValue());
              showName.setText(name+" ("+userType+")");
              String proLink=String.valueOf(dataSnapshot.child(userKeys).child("profileLink").getValue());
                 Picasso.with(context).load(proLink).placeholder(R.drawable.grayback).into(showImage);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
        }
        public void setWholeMessage(Context context,String message){
              actualMessage.setText(message);
        }

    }
}
