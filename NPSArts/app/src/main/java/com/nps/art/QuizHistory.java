package com.nps.art;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
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
public class QuizHistory extends AppCompatActivity {
    DatabaseReference quizHistoryRef;
    Toolbar quizHistoryTool;
    RecyclerView recycleQuizHistory;
    TextView noHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_history);
        recycleQuizHistory=(RecyclerView) findViewById(R.id.recycleQuizHistory);
        recycleQuizHistory.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        quizHistoryRef= FirebaseDatabase.getInstance().getReference().child("QuizHistory");
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        quizHistoryTool=(Toolbar)findViewById(R.id.quizHistoryTool);
        noHistory=(TextView)findViewById(R.id.showNoHistory);
        setSupportActionBar(quizHistoryTool);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.pending_post_tool,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.pendingpostAdminHeading);
        headingPendingPost.setText("Quiz History");
        headingPendingPost.setTypeface(roboto);
        ImageView setBack=(ImageView)view.findViewById(R.id.backPendingPostAdmin);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        quizHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           if (dataSnapshot.exists()){
               noHistory.setVisibility(View.GONE);
               ShowQuizHistory();
           }
           else {
               noHistory.setVisibility(View.VISIBLE);
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void ShowQuizHistory() {
        FirebaseRecyclerAdapter<QuizHistoryModel,QuizHistoryViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<QuizHistoryModel, QuizHistoryViewHolder>
                (QuizHistoryModel.class,R.layout.quiz_history_layout,QuizHistoryViewHolder.class,quizHistoryRef) {
            @Override
            protected void populateViewHolder(QuizHistoryViewHolder quizHistoryViewHolder, QuizHistoryModel quizHistoryModel, int i) {
                quizHistoryViewHolder.setHistoryCorrect(getApplication(),quizHistoryModel.getHistoryCorrect());
                quizHistoryViewHolder.setHistoryQuestion(getApplicationContext(),quizHistoryModel.getHistoryQuestion());
                quizHistoryViewHolder.setHistoryDate(getApplicationContext(),quizHistoryModel.getHistoryDate());
                quizHistoryViewHolder.setHistoryTime(getApplicationContext(),quizHistoryModel.getHistoryTime());
                quizHistoryViewHolder.setHistoryWinnerName(getApplicationContext(),quizHistoryModel.getHistoryWinnerName());
                quizHistoryViewHolder.setHistoryWinnerProLink(getApplicationContext(),quizHistoryModel.getHistoryWinnerProLink());
                quizHistoryViewHolder.setHeadingFont(getApplicationContext());
            }
        };
        recycleQuizHistory.setAdapter(firebaseRecyclerAdapter);


    }
    public static class QuizHistoryViewHolder extends RecyclerView.ViewHolder{
        View view;
       Context context;
        TextView quizHistoryWinnerName,quizHistoryQuestion,quizWinnerHeading,quizHistoryCorrectAns,quizHistoryDate,quizHistoryTime;
        CircularImageView quizHistoryWinnerPrfile;


        public QuizHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
         view=itemView;
         quizHistoryWinnerPrfile=(CircularImageView)view.findViewById(R.id.showProInHistoryQuiz);
         quizHistoryWinnerName=(TextView)view.findViewById(R.id.winnerNameHolder);
         quizHistoryCorrectAns=(TextView)view.findViewById(R.id.showCorrectAns);
         quizHistoryQuestion=(TextView)view.findViewById(R.id.showQuestionHistoryQuiz);
         quizHistoryDate=(TextView)view.findViewById(R.id.showStartingTimeQuizHistory);
         quizHistoryTime=(TextView)view.findViewById(R.id.showEndingTimeQuizHistory);
         quizWinnerHeading=(TextView)view.findViewById(R.id.winnerHeading);


        }
        public void setHeadingFont(Context context){
            Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
            quizWinnerHeading.setTypeface(roboto);
        }
        public void setHistoryQuestion(Context context,String historyQuestion) {
            Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
            quizHistoryQuestion.setTypeface(roboto);
            quizHistoryQuestion.setText(historyQuestion);
        }
        public void setHistoryCorrect(Context context,String historyCorrect) {
            Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
            quizHistoryCorrectAns.setTypeface(roboto);
            quizHistoryCorrectAns.setText(historyCorrect);

        }
        public void setHistoryTime(Context context,String historyTime) {
          //  Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
           // quizHistoryTime.setTypeface(roboto);
            quizHistoryTime.setText("Time \n"+historyTime);

        }
        public void setHistoryDate(Context context,String historyDate) {
           // Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
           // quizHistoryDate.setTypeface(roboto);
            quizHistoryDate.setText("Date \n"+historyDate);

        }
        public void setHistoryWinnerName(Context context,String historyWinnerName) {
            Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
            quizHistoryWinnerName.setTypeface(roboto);
            quizHistoryWinnerName.setText(historyWinnerName);

        }
        public void setHistoryWinnerProLink(Context context,String historyWinnerProLink) {

            Picasso.with(context).load(historyWinnerProLink).placeholder(R.drawable.image_placeholder).into(quizHistoryWinnerPrfile);

        }

    }
}
