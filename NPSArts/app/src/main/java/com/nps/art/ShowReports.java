package com.nps.art;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
public class ShowReports extends AppCompatActivity {
    SwipeRefreshLayout refreshShowPosts;
    RecyclerView recycleShowPosts;
    DatabaseReference reportRef,postRef;
    Toolbar reportsTools;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reports);
        reportsTools=(Toolbar)findViewById(R.id.showReportsTool);
        reportsTools=(Toolbar)findViewById(R.id.showReportsTool);
        setSupportActionBar(reportsTools);
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        ActionBar actionBar=getSupportActionBar();
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionBar.setDisplayShowCustomEnabled(true);
        View view=layoutInflater.inflate(R.layout.reviewing_article_tool,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.reviewHeading);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        headingPendingPost.setTypeface(roboto);
        ImageView setBack=(ImageView)view.findViewById(R.id.reviewBack);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        refreshShowPosts=(SwipeRefreshLayout)findViewById(R.id.refreshshowpost);
        recycleShowPosts=(RecyclerView)findViewById(R.id.recycleShowReports);
        recycleShowPosts.setHasFixedSize(true);
        recycleShowPosts.setLayoutManager(new LinearLayoutManager(this));
        reportRef= FirebaseDatabase.getInstance().getReference().child("Reports");
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        recycleShowPosts.setHasFixedSize(true);
        recycleShowPosts.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerAdapter<ShowReportsModel,ReportsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<ShowReportsModel, ReportsViewHolder>
                (ShowReportsModel.class,R.layout.report_layout,ReportsViewHolder.class,reportRef) {
            @Override
            protected void populateViewHolder(ReportsViewHolder reportsViewHolder, final ShowReportsModel showReportsModel, int i) {
                final String reportKey=getRef(i).getKey();
                reportsViewHolder.setMessage(getApplicationContext(),showReportsModel.getReporterName(),showReportsModel.getPostTitle(),showReportsModel.getPostUserName(),showReportsModel.getReason());
                reportsViewHolder.setReporterProLink(getApplicationContext(),showReportsModel.getReporterProLink());
                reportsViewHolder.delectepost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder=new AlertDialog.Builder(ShowReports.this);
                        builder.setTitle("Deleting Post");
                        builder.setMessage("Are you sure to delete this post?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postRef.child(showReportsModel.getPostKeyValue()).removeValue();
                                reportRef.child(reportKey).removeValue();
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
                reportsViewHolder.reviewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ShowReports.this,PostDetails.class);
                        intent.putExtra("postKey",showReportsModel.getPostKeyValue());
                        intent.putExtra("uidKey",showReportsModel.getUidKey());
                        intent.putExtra("heading",showReportsModel.getPostTitle());
                        startActivity(intent);
                    }
                });
                reportsViewHolder.declinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder=new AlertDialog.Builder(ShowReports.this);
                        builder.setTitle("Decline Report");
                        builder.setMessage("Are you sure to decline this report?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reportRef.child(reportKey).removeValue();
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
        recycleShowPosts.setAdapter(firebaseRecyclerAdapter);

    }
    public static  class ReportsViewHolder extends RecyclerView.ViewHolder{
        View mViewReports;
        ImageView declinButton,reviewButton,delectepost;
        public ReportsViewHolder(@NonNull View itemView) {
            super(itemView);
            mViewReports=itemView;
            declinButton=(ImageView) mViewReports.findViewById(R.id.delcine);
            reviewButton=(ImageView) mViewReports.findViewById(R.id.review);
            delectepost=(ImageView) mViewReports.findViewById(R.id.delectePost);
        }
        public void setMessage(Context context, String reporterName,  String postTitle, String postUserName, String reason) {
            Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
            TextView showMessage=(TextView)mViewReports.findViewById(R.id.showReportMessage);
            showMessage.setTypeface(roboto);
            String messageFormate="<b>"+reporterName+"</b>"+" has reported "+"<b>"+"\""+postTitle+"\""+"</b>"+" article posted by "+"<b>"+postUserName+"</b>"+" because of this given reason "+"<b>"+"\""+reason+"\""+"</b>";
             showMessage.setText(Html.fromHtml(messageFormate));
        }
        public void setReporterProLink(Context context,String reporterProLink) {
            CircularImageView showProImage=(CircularImageView)mViewReports.findViewById(R.id.showProfileInReports);
            Picasso.with(context).load(reporterProLink).placeholder(R.drawable.image_placeholder).into(showProImage);
        }
    }
}
