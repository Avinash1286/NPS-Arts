package com.nps.art;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
public class GiggleItVerification extends AppCompatActivity {
    SwipeRefreshLayout refreshManageUser;
    RecyclerView recycleAllUsers;
    DatabaseReference userRef;
    Boolean verifiedChecker=false;
    Toolbar manageUserTool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giggle_it_verification);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        refreshManageUser=(SwipeRefreshLayout)findViewById(R.id.refreshManageUser);
        manageUserTool=(Toolbar)findViewById(R.id.giggleItTool);
        setSupportActionBar(manageUserTool);
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.user_verification_too,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.manageUserHeading);
        headingPendingPost.setTypeface(roboto);
        ImageView setBack=(ImageView)view.findViewById(R.id.manageUserBack);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recycleAllUsers=(RecyclerView)findViewById(R.id.recycleAllUsers);
        recycleAllUsers.setHasFixedSize(true);
        recycleAllUsers.setLayoutManager(new LinearLayoutManager(this));
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseRecyclerAdapter<ManageUserModel,ManageUserViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<ManageUserModel, ManageUserViewHolder>
                (ManageUserModel.class,R.layout.manage_user_layout,ManageUserViewHolder.class,userRef) {
            @Override
            protected void populateViewHolder(final ManageUserViewHolder manageUserViewHolder, final ManageUserModel manageUserModel, int i) {
                final String userKey=getRef(i).getKey();
                manageUserViewHolder.setProfileLink(getApplicationContext(),manageUserModel.getProfileLink());
                manageUserViewHolder.setFullname(getApplicationContext(),manageUserModel.getFullname());
                manageUserViewHolder.verifiedStatus(userKey);
                manageUserViewHolder.setNumberOfPost(getApplicationContext(),userKey);
                manageUserViewHolder.permissionChecker(userKey);
                manageUserViewHolder.permission.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userRef.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String permissionValue=dataSnapshot.child("permission").getValue().toString();
                                if (permissionValue.equals("yes")){
                                   manageUserViewHolder.permission.setChecked(true);
                                    userRef.child(userKey).child("permission").setValue("no");
                                }
                                else {
                                    manageUserViewHolder.permission.setChecked(false);
                                    ShowWarningBottomSheet showWarningBottomSheet=new ShowWarningBottomSheet();
                                    ShowWarningBottomSheet.userKey=userKey;
                                    showWarningBottomSheet.show(getSupportFragmentManager(),"ShowWarning");
                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                });
                manageUserViewHolder.verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder=new AlertDialog.Builder(GiggleItVerification.this);
                        builder.setTitle("Modifying User");
                        builder.setMessage("Are you sure you want to do this?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                verifiedChecker=true;
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (verifiedChecker){
                                            String getVerifiedValue=dataSnapshot.child(userKey).child("verified").getValue().toString();
                                            if (getVerifiedValue.equals("no")){
                                                userRef.child(userKey).child("verified").setValue("yes");
                                                verifiedChecker=false;
                                            }
                                            else {
                                                userRef.child(userKey).child("verified").setValue("no");
                                                verifiedChecker=false;
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();

                    }
                });
            }
        };
        recycleAllUsers.setAdapter(firebaseRecyclerAdapter);
    }
    public static class ManageUserViewHolder extends RecyclerView.ViewHolder{
        View mViews;
        TextView setNoOfPost,vefifyHeading,permessionHeading;
        CheckBox verify,permission;
         DatabaseReference userReff,postRef;
        public ManageUserViewHolder(@NonNull View itemView) {
            super(itemView);
            mViews=itemView;
            setNoOfPost=(TextView)mViews.findViewById(R.id.holdsNoofPost);
            verify=(CheckBox) mViews.findViewById(R.id.verificationButton);
            permission=(CheckBox) mViews.findViewById(R.id.generalPermissionButton);
            vefifyHeading=(TextView)mViews.findViewById(R.id.verifyHeading);
            permessionHeading=(TextView)mViews.findViewById(R.id.permissionHeading);
            userReff=FirebaseDatabase.getInstance().getReference().child("Users");
            postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        }
        public void setProfileLink(Context context, String profileLink) {
            CircularImageView setPro=(CircularImageView)mViews.findViewById(R.id.holdeProfileInMagageAccount);
            Picasso.with(context).load(profileLink).placeholder(R.drawable.image_placeholder).into(setPro);

        }
        public void setFullname(Context context,String fullname) {
            Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
            TextView setName=(TextView)mViews.findViewById(R.id.setNameInManageAccount);
            setName.setTypeface(roboto);
            setName.setText(fullname);
        }

        public void verifiedStatus(String userKey){
            userReff.child(userKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 String verifiedValue=dataSnapshot.child("verified").getValue().toString();
                 if (verifiedValue.equals("yes")){
                     vefifyHeading.setText("Verified");
                     verify.setChecked(true);
                 }
                 else {
                     vefifyHeading.setText("Verify");
                     verify.setChecked(false);
                 }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setNumberOfPost(final Context context, String userKey){
            Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
            setNoOfPost.setTypeface(roboto);
            postRef.orderByChild("uid").startAt(userKey).endAt(userKey+"\uf8ff").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()){
                     int noOfPosts=(int)dataSnapshot.getChildrenCount();
                     setNoOfPost.setText(noOfPosts+" Posts");
                 }
                 else {
                     setNoOfPost.setText("0 Post");
                 }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        public void permissionChecker(String userKey){
            userReff.child(userKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String permissionValue=dataSnapshot.child("permission").getValue().toString();
                    if (permissionValue.equals("yes")){
                        permessionHeading.setText("Permission Grant");
                        permission.setChecked(true);
                    }
                    else {
                        permessionHeading.setText("No Permission");
                        permission.setChecked(false);
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
