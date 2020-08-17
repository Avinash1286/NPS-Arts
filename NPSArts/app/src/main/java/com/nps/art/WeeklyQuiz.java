package com.nps.art;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.loadingview.LoadingView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import ticker.views.com.ticker.widgets.circular.timer.callbacks.CircularViewCallback;
import ticker.views.com.ticker.widgets.circular.timer.view.CircularView;
public class WeeklyQuiz extends AppCompatActivity {
    TextView questionHolder,showNoComptMessage;
    TextView but1,but2,but3,but4;
    DatabaseReference getQuizInfo,userRef;
    CardView noComptMessageHolder;
    ImageView noComptImage;
    CircularView setTimer;
    FirebaseAuth mAuth;
    String currentUser;
    LoadingView loadingView;
    Toolbar weeklyTool;
    String getQuestion,getOpt1,getOpt2,getOpt3,getOpt4,correct,date,timeWithSecond,time,userName,userProLink,userType;
    private long luckyWinnerCount;
    private Boolean checker=true;
    ImageView openHistory;
    RelativeLayout optionOne,optionTwo,optionThree,optionFour,showMain,noInt;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_quiz);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser().getUid();
        getQuizInfo= FirebaseDatabase.getInstance().getReference().child("WeeklyQuizRunning");
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog=new ProgressDialog(this);
        questionHolder=(TextView)findViewById(R.id.questionHolder);
        showNoComptMessage=(TextView)findViewById(R.id.showNoCompetitionmessage);
        noComptMessageHolder=(CardView)findViewById(R.id.noCompetitionMessaggeHolder);
        noComptImage=(ImageView)findViewById(R.id.noCompetitionImage);
        weeklyTool=(Toolbar)findViewById(R.id.quizTool);
        noInt=(RelativeLayout)findViewById(R.id.noInternetHolder);
        loadingView=(LoadingView)findViewById(R.id.loadingView);
        loadingView.start();
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        showMain=(RelativeLayout)findViewById(R.id.showMainBody);
        optionFour=(RelativeLayout)findViewById(R.id.optionFour);
        optionOne=(RelativeLayout)findViewById(R.id.optionOne);
        optionThree=(RelativeLayout)findViewById(R.id.optionsThree);
        optionTwo=(RelativeLayout)findViewById(R.id.optionTwo);
        setTimer=(CircularView)findViewById(R.id.timer);
        setSupportActionBar(weeklyTool);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.weekly_quix_tool_layout,null);
        actionBar.setCustomView(view);
        ImageView setBack=(ImageView)view.findViewById(R.id.backPendingPostAdmin);
        openHistory=(ImageView) view.findViewById(R.id.showHistoryButton);
        noComptMessageHolder.setVisibility(View.GONE);
        showMain.setVisibility(View.GONE);
        openHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WeeklyQuiz.this,QuizHistory.class));
            }
        });
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForConnctoin()){
                    userRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("verified").getValue().toString().equals("yes")){
                                getQuizInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            getQuizInfo.child("participants").child(currentUser).setValue("true");
                                            WeeklyQuiz.super.onBackPressed();
                                        }
                                        else {
                                            WeeklyQuiz.super.onBackPressed();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else {
                                WeeklyQuiz.super.onBackPressed();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    WeeklyQuiz.super.onBackPressed();
                }
            }
        });
        showUserProfile();
        but1=(TextView) findViewById(R.id.but1);
        but2=(TextView) findViewById(R.id.but2);
        but3=(TextView) findViewById(R.id.but3);
        but4=(TextView) findViewById(R.id.but4);
        if (checkForConnctoin()){
            noInt.setVisibility(View.GONE);
            getQuizInfo.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        showMain.setVisibility(View.VISIBLE);
                        noComptMessageHolder.setVisibility(View.GONE);
                        loadingView.stop();
                        loadingView.setVisibility(View.GONE);
                        getQuestion =String.valueOf(dataSnapshot.child("QuestionAndOptions").child("question").getValue());
                        getOpt1 = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("option1").getValue());
                        getOpt2 = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("option2").getValue());
                        getOpt3 = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("option3").getValue());
                        getOpt4 = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("option4").getValue());
                        correct = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("correct").getValue());
                        date = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("date").getValue());
                        timeWithSecond = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("timeWithSecond").getValue());
                        time = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("time").getValue());
                        but1.setText(getOpt1);
                        but2.setText(getOpt2);
                        but3.setText(getOpt3);
                        but4.setText(getOpt4);
                        questionHolder.setText(getQuestion);
                        userRef.child(currentUser).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                noComptMessageHolder.setVisibility(View.GONE);
                                if (String.valueOf(dataSnapshot.child("verified").getValue()).equals("yes")){
                                    getQuizInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("participants").hasChild(currentUser)){
                                                CircularView.OptionsBuilder builderWithoutText =
                                                        new CircularView.OptionsBuilder()
                                                                .setCounterInSeconds(CircularView.OptionsBuilder.INFINITE)
                                                                .setCustomText("Now you are participant");
                                                setTimer.setOptions(builderWithoutText);
                                            }
                                            else {
                                                openHistory.setEnabled(false);
                                                CircularView.OptionsBuilder builderWithTimer =
                                                        new CircularView.OptionsBuilder()
                                                                .shouldDisplayText(true)
                                                                .setCounterInSeconds(15)
                                                                .setCircularViewCallback(new CircularViewCallback() {
                                                                    @Override
                                                                    public void onTimerFinish() {
                                                                        openHistory.setEnabled(true);
                                                                        FragmentManager fragmentManager=getSupportFragmentManager();
                                                                        TimeUp timeUp=new TimeUp();
                                                                        FragmentTransaction fragmentTransaction1=fragmentManager.beginTransaction();
                                                                        fragmentTransaction1.add(R.id.showTimeUp,timeUp);
                                                                        fragmentTransaction1.addToBackStack(null);
                                                                        fragmentTransaction1.commit();
                                                                        Toast.makeText(WeeklyQuiz.this, "Timer Finished ", Toast.LENGTH_SHORT).show();
                                                                        userRef.child(currentUser).addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                if (dataSnapshot.child("verified").getValue().toString().equals("yes")){
                                                                                    getQuizInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                            if (dataSnapshot.exists()){
                                                                                                getQuizInfo.child("participants").child(currentUser).setValue("true");
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onTimerCancelled() {
                                                                    }
                                                                });
                                                setTimer.setOptions(builderWithTimer);
                                                setTimer.startTimer();
                                            }
                                            getQuizInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        if (dataSnapshot.hasChild("WriteAnsGiver")) {
                                                            luckyWinnerCount = dataSnapshot.child("WriteAnsGiver").getChildrenCount();
                                                            luckyWinnerCount = luckyWinnerCount + 1;
                                                        } else {
                                                            luckyWinnerCount = 1;
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                                else {
                                    CircularView.OptionsBuilder builderWithoutText =
                                            new CircularView.OptionsBuilder()
                                                    .setCounterInSeconds(CircularView.OptionsBuilder.INFINITE)
                                                    .setCustomText("Your Id is not verified");
                                    setTimer.setOptions(builderWithoutText);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                    else {
                        loadingView.stop();
                        loadingView.setVisibility(View.GONE);
                        showMain.setVisibility(View.GONE);
                        noComptMessageHolder.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        else {
            loadingView.setVisibility(View.GONE);
            loadingView.stop();
            noInt.setVisibility(View.VISIBLE);
        }

        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker=true;
                but1.setEnabled(false);
                but2.setEnabled(false);
                but3.setEnabled(false);
                but4.setEnabled(false);
                userRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (String.valueOf(dataSnapshot.child("verified").getValue()).equals("yes")){
                            getQuizInfo.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (checker.equals(true)){
                                        if (dataSnapshot.child("participants").hasChild(currentUser)){
                                            Toast.makeText(WeeklyQuiz.this, "You have already participated", Toast.LENGTH_SHORT).show();
                                            checker=false;
                                        }
                                        else {
                                            setTimer.stopTimer();
                                            getQuizInfo.child("participants").child(currentUser).setValue("true");
                                            if (getOpt1.equals(correct)) {
                                                optionOne.setBackgroundResource(R.color.right);
                                                String counter = String.valueOf(luckyWinnerCount);
                                                if (counter.equals("0") ||counter.isEmpty()){
                                                    Toast.makeText(WeeklyQuiz.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                               return;
                                                }
                                                else {

                                                    HashMap putDataInWeeklyQuiz = new HashMap();
                                                    putDataInWeeklyQuiz.put("currentUser", currentUser);
                                                    putDataInWeeklyQuiz.put("name", userName);
                                                    putDataInWeeklyQuiz.put("prolink", userProLink);
                                                    putDataInWeeklyQuiz.put("userType",userType);
                                                    getQuizInfo.child("WriteAnsGiver").child(counter).updateChildren(putDataInWeeklyQuiz).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if (!task.isSuccessful()) {
                                                                Toast.makeText(WeeklyQuiz.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                    but1.setEnabled(false);
                                                    but2.setEnabled(false);
                                                    but3.setEnabled(false);
                                                    but4.setEnabled(false);
                                                }
                                            } else {
                                                optionOne.setBackgroundResource(R.color.wrong);
                                                HashMap putDataInQuizCompt = new HashMap();
                                                putDataInQuizCompt.put("currentUser", currentUser);
                                                putDataInQuizCompt.put("name", userName);
                                                putDataInQuizCompt.put("proLink", userProLink);
                                                getQuizInfo.child("WrongAnsGiver").child(currentUser).updateChildren(putDataInQuizCompt).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        if (!task.isSuccessful()) {
                                                            Toast.makeText(WeeklyQuiz.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                but1.setEnabled(false);
                                                but2.setEnabled(false);
                                                but3.setEnabled(false);
                                                but4.setEnabled(false);

                                            }
                                            checker=false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }

                        else {
                            Toast.makeText(WeeklyQuiz.this, "Sorry your id is not verified yet", Toast.LENGTH_LONG).show();
                            checker=false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker=true;
                but1.setEnabled(false);
                but2.setEnabled(false);
                but3.setEnabled(false);
                but4.setEnabled(false);
                userRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (String.valueOf(dataSnapshot.child("verified").getValue()).equals("yes")){
                            getQuizInfo.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (checker.equals(true)){
                                        if (dataSnapshot.child("participants").hasChild(currentUser)){
                                            Toast.makeText(WeeklyQuiz.this, "You have already participated", Toast.LENGTH_SHORT).show();
                                            checker=false;
                                        }
                                        else {
                                            setTimer.stopTimer();
                                            getQuizInfo.child("participants").child(currentUser).setValue("true");
                                            if (getOpt2.equals(correct)) {
                                                optionTwo.setBackgroundResource(R.color.right);
                                                String counter = String.valueOf(luckyWinnerCount);
                                                if (counter.equals("0") ||counter.isEmpty()){
                                                    Toast.makeText(WeeklyQuiz.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                               return;
                                                }
                                                else {

                                                    HashMap putDataInWeeklyQuiz = new HashMap();
                                                    putDataInWeeklyQuiz.put("currentUser", currentUser);
                                                    putDataInWeeklyQuiz.put("name", userName);
                                                    putDataInWeeklyQuiz.put("prolink", userProLink);
                                                    putDataInWeeklyQuiz.put("userType",userType);
                                                    getQuizInfo.child("WriteAnsGiver").child(counter).updateChildren(putDataInWeeklyQuiz).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if (!task.isSuccessful()) {
                                                                Toast.makeText(WeeklyQuiz.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                    but1.setEnabled(false);
                                                    but2.setEnabled(false);
                                                    but3.setEnabled(false);
                                                    but4.setEnabled(false);
                                                }
                                            } else {
                                                optionTwo.setBackgroundResource(R.color.wrong);
                                                HashMap putDataInQuizCompt = new HashMap();
                                                putDataInQuizCompt.put("currentUser", currentUser);
                                                putDataInQuizCompt.put("name", userName);
                                                putDataInQuizCompt.put("proLink", userProLink);
                                                getQuizInfo.child("WrongAnsGiver").child(currentUser).updateChildren(putDataInQuizCompt).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        if (!task.isSuccessful()) {
                                                            Toast.makeText(WeeklyQuiz.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                but1.setEnabled(false);
                                                but2.setEnabled(false);
                                                but3.setEnabled(false);
                                                but4.setEnabled(false);

                                            }
                                            checker=false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                        else {
                            checker=false;
                            Toast.makeText(WeeklyQuiz.this, "Sorry your id is not verified yet", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }
        });

        but3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker=true;
                but1.setEnabled(false);
                but2.setEnabled(false);
                but3.setEnabled(false);
                but4.setEnabled(false);
                userRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (String.valueOf(dataSnapshot.child("verified").getValue()).equals("yes")){
                            getQuizInfo.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (checker.equals(true)){
                                        if (dataSnapshot.child("participants").hasChild(currentUser)){
                                            Toast.makeText(WeeklyQuiz.this, "You have already participated", Toast.LENGTH_SHORT).show();
                                            checker=false;
                                        }
                                        else {
                                            setTimer.stopTimer();
                                            getQuizInfo.child("participants").child(currentUser).setValue("true");
                                            if (getOpt3.equals(correct)) {
                                                optionThree.setBackgroundResource(R.color.right);
                                                String counter = String.valueOf(luckyWinnerCount);
                                                if (counter.equals("0") ||counter.isEmpty()){
                                                    Toast.makeText(WeeklyQuiz.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                               return;
                                                }
                                                else {

                                                    HashMap putDataInWeeklyQuiz = new HashMap();
                                                    putDataInWeeklyQuiz.put("currentUser", currentUser);
                                                    putDataInWeeklyQuiz.put("name", userName);
                                                    putDataInWeeklyQuiz.put("prolink", userProLink);
                                                    putDataInWeeklyQuiz.put("userType",userType);
                                                    getQuizInfo.child("WriteAnsGiver").child(counter).updateChildren(putDataInWeeklyQuiz).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if (!task.isSuccessful()) {
                                                                Toast.makeText(WeeklyQuiz.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                    but1.setEnabled(false);
                                                    but2.setEnabled(false);
                                                    but3.setEnabled(false);
                                                    but4.setEnabled(false);
                                                }
                                            } else {
                                                optionThree.setBackgroundResource(R.color.wrong);
                                                HashMap putDataInQuizCompt = new HashMap();
                                                putDataInQuizCompt.put("currentUser", currentUser);
                                                putDataInQuizCompt.put("name", userName);
                                                putDataInQuizCompt.put("proLink", userProLink);
                                                getQuizInfo.child("WrongAnsGiver").child(currentUser).updateChildren(putDataInQuizCompt).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        if (!task.isSuccessful()) {
                                                            Toast.makeText(WeeklyQuiz.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                but1.setEnabled(false);
                                                but2.setEnabled(false);
                                                but3.setEnabled(false);
                                                but4.setEnabled(false);

                                            }
                                            checker=false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });



                        }
                        else {
                            Toast.makeText(WeeklyQuiz.this, "Sorry your id is not verified yet", Toast.LENGTH_SHORT).show();
                            checker=false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        but4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker=true;
                but1.setEnabled(false);
                but2.setEnabled(false);
                but3.setEnabled(false);
                but4.setEnabled(false);
                userRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (String.valueOf(dataSnapshot.child("verified").getValue()).equals("yes")){
                            getQuizInfo.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (checker.equals(true)){
                                        if (dataSnapshot.child("participants").hasChild(currentUser)){
                                            Toast.makeText(WeeklyQuiz.this, "You have already participated", Toast.LENGTH_SHORT).show();
                                            checker=false;
                                        }
                                        else {
                                            setTimer.stopTimer();
                                            getQuizInfo.child("participants").child(currentUser).setValue("true");
                                            if (getOpt4.equals(correct)) {
                                                optionFour.setBackgroundResource(R.color.right);
                                                String counter = String.valueOf(luckyWinnerCount);
                                                if (counter.equals("0") ||counter.isEmpty()){
                                                    Toast.makeText(WeeklyQuiz.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                               return;
                                                }
                                                else {

                                                    HashMap putDataInWeeklyQuiz = new HashMap();
                                                    putDataInWeeklyQuiz.put("currentUser", currentUser);
                                                    putDataInWeeklyQuiz.put("name", userName);
                                                    putDataInWeeklyQuiz.put("prolink", userProLink);
                                                    putDataInWeeklyQuiz.put("userType",userType);
                                                    getQuizInfo.child("WriteAnsGiver").child(counter).updateChildren(putDataInWeeklyQuiz).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if (!task.isSuccessful()) {
                                                                Toast.makeText(WeeklyQuiz.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                    but1.setEnabled(false);
                                                    but2.setEnabled(false);
                                                    but3.setEnabled(false);
                                                    but4.setEnabled(false);
                                                }
                                            } else {
                                                optionFour.setBackgroundResource(R.color.wrong);
                                                HashMap putDataInQuizCompt = new HashMap();
                                                putDataInQuizCompt.put("currentUser", currentUser);
                                                putDataInQuizCompt.put("name", userName);
                                                putDataInQuizCompt.put("proLink", userProLink);
                                                getQuizInfo.child("WrongAnsGiver").child(currentUser).updateChildren(putDataInQuizCompt).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        if (!task.isSuccessful()) {
                                                            Toast.makeText(WeeklyQuiz.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                but1.setEnabled(false);
                                                but2.setEnabled(false);
                                                but3.setEnabled(false);
                                                but4.setEnabled(false);

                                            }
                                            checker=false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        else {
                            Toast.makeText(WeeklyQuiz.this, "Sorry your id is not verified yet", Toast.LENGTH_SHORT).show();
                            checker=false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


    }
    private void showUserProfile() {
        userRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    userName = String.valueOf(dataSnapshot.child("fullname").getValue());
                    userProLink = String.valueOf(dataSnapshot.child("profileLink").getValue());
                    userType=String.valueOf(dataSnapshot.child("userType").getValue());

                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(WeeklyQuiz.this,"Error "+e,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {


    }
    private boolean checkForConnctoin() {
        ConnectivityManager cm=(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected()){

            return true;

        }
        else {
            return false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (checkForConnctoin()){
            userRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("verified").getValue().toString().equals("yes")){
                        getQuizInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    getQuizInfo.child("participants").child(currentUser).setValue("true");
                                    WeeklyQuiz.super.onBackPressed();
                                }
                                else {
                                    WeeklyQuiz.super.onBackPressed();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else {
            WeeklyQuiz.super.onBackPressed();
        }
    }
}
