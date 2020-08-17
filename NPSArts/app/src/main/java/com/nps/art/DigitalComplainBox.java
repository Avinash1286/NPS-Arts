package com.nps.art;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.loadingview.LoadingView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
public class DigitalComplainBox extends AppCompatActivity {
    private RecyclerView showComment ;
    private ImageView sendComment;
    private EditText getCommentText;
    private String currentUser;
    private DatabaseReference commentRef,userRef,compHis;
    private FirebaseAuth commentAuth;
    private String userNameInComment,userProlinkInComment,getComment;
    private  long commentcounter;
    private Toolbar digitalTool;
    RelativeLayout noComplaints;
    LoadingView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_complain_box);
        Typeface aveny=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        showComment=(RecyclerView)findViewById(R.id.commentOnlineRecycle);
        showComment.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        Window window=getWindow();
        commentAuth=FirebaseAuth.getInstance();
        currentUser=commentAuth.getCurrentUser().getUid();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        showComment.setLayoutManager(layoutManager);
        digitalTool=(Toolbar)findViewById(R.id.digitalTool);
        setSupportActionBar(digitalTool);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        loadingView=(LoadingView)findViewById(R.id.loadingView);
        loadingView.start();
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.digital_complaintool_bar_layout,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.pendingpostAdminHeading);
        headingPendingPost.setTypeface(aveny);
        noComplaints=(RelativeLayout)findViewById(R.id.showNoComp);
        ImageView setBack=(ImageView)view.findViewById(R.id.backPendingPostAdmin);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        sendComment=(ImageView)findViewById(R.id.postCommentOnline);
        getCommentText=(EditText)findViewById(R.id.containOnlineCommen);
        commentRef= FirebaseDatabase.getInstance().getReference().child("DigitalComplains");
        compHis=FirebaseDatabase.getInstance().getReference().child("ComplainHis");
        commentRef.orderByChild("currentUser").startAt(currentUser).endAt(currentUser+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    noComplaints.setVisibility(View.GONE);
                    loadComplain();

                }
                else {
                    loadingView.stop();
                    loadingView.setVisibility(View.GONE);
                    noComplaints.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommentToDatabase();
                getCommentText.setText("");
            }
        });
        getUserInfo();

    }
    private void loadComplain() {
        Query onleyIdOwnerPost=commentRef.orderByChild("currentUser").startAt(currentUser).endAt(currentUser+"\uf8ff");
        FirebaseRecyclerAdapter<DigitalComplainModel,CommentHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<DigitalComplainModel, CommentHolder>
                (DigitalComplainModel.class,R.layout.digital_complain_box_layout,CommentHolder.class,onleyIdOwnerPost) {
            @Override
            protected void populateViewHolder(final CommentHolder commentHolder, DigitalComplainModel digitalComplainModel, int i) {
               final String key=getRef(i).getKey();
                commentHolder.setComplain(getApplicationContext(),digitalComplainModel.getComplain());
                commentHolder.showOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popupMenu=new PopupMenu(getApplicationContext(),commentHolder.showOptions);
                        popupMenu.getMenuInflater().inflate(R.menu.deleteoption,popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                commentRef.child(key).removeValue();
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }
        };
        loadingView.stop();
        loadingView.setVisibility(View.GONE);
       showComment.setAdapter(firebaseRecyclerAdapter);

    }

    private void getUserInfo() {
        userRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("fullname") && dataSnapshot.hasChild("profileLink")){
                    userNameInComment=dataSnapshot.child("fullname").getValue().toString();
                    userProlinkInComment=dataSnapshot.child("profileLink").getValue().toString();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendCommentToDatabase() {
        getComment=getCommentText.getText().toString();
        if (getComment.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please write something first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userNameInComment.isEmpty()){
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userProlinkInComment.isEmpty()){
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        else {

            commentRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){

                        if (dataSnapshot.exists()){
                            commentcounter=dataSnapshot.getChildrenCount();
                        }
                        else {
                            commentcounter=0;
                        }

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyyy");
            String date=simpleDateFormat.format(calendar.getTime());
            SimpleDateFormat simpleTimeFormate=new SimpleDateFormat("HH:mm:ss");
            String time=simpleTimeFormate.format(calendar.getTime());
            String random=time+date;
            HashMap putData=new HashMap();
            putData.put("Commentname",userNameInComment);
            putData.put("proLink",userProlinkInComment);
            putData.put("currentUser",currentUser);
            putData.put("complain",getComment);
            putData.put("CommentCounter",commentcounter);
            String randomValue=currentUser+date+time;
            commentRef.child(randomValue).updateChildren(putData).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (!task.isSuccessful()){
                        String mess=task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), "Error:"+mess, Toast.LENGTH_SHORT).show();
                    }

                }
            });
            compHis.child(random).updateChildren(putData).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
               if (!task.isSuccessful()){
                   Toast.makeText(DigitalComplainBox.this, "Something went wrong", Toast.LENGTH_SHORT).show();
               }
                }
            });


        }

    }

    public static class CommentHolder extends RecyclerView.ViewHolder{
        View mview;
        ImageView showOptions;
        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
            showOptions=(ImageView)mview.findViewById(R.id.showOption);
        }
        public void setComplain(Context context,String complain){
            TextView setComplain=(TextView)mview.findViewById(R.id.complainText);
            setComplain.setText(complain);
        }
    }

}
