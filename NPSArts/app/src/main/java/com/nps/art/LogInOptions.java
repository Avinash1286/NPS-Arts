package com.nps.art;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import dmax.dialog.SpotsDialog;
public class LogInOptions extends AppCompatActivity {
    Button signInWithPhone;
    TextView logInHeading;
    GoogleApiClient mGoogleSignInClient;
    DatabaseReference userRef;
    private FirebaseAuth mAuth;
    public static final int RC_SIGN_IN=1;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_options);
        Typeface avenyInstaRegular=Typeface.createFromAsset(getAssets(),"font/FacebookNarrow_A_Rg.ttf");
          logInHeading=(TextView)findViewById(R.id.welcomeHeading);
          logInHeading.setTypeface(avenyInstaRegular);
          mAuth=FirebaseAuth.getInstance();
          progressDialog=new ProgressDialog(this);
          signInWithPhone=(Button)findViewById(R.id.logInPhoneNumber);
          userRef= FirebaseDatabase.getInstance().getReference().child("Users");

    signInWithPhone.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(LogInOptions.this,PhoneNumberAuth.class));
             }
         });
     /*        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient=new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build(); */
    }
   /* private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                new SpotsDialog.Builder().setContext(LogInOptions.this).setMessage("Logging with you google account").build().show();
                GoogleSignInAccount account=result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LogInOptions.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                            String currentUser=mAuth.getCurrentUser().getUid();
                            String deviceToken= FirebaseInstanceId.getInstance().getToken();
                            userRef.child(currentUser).child("deviceToken").setValue(deviceToken);
                            startActivity(new Intent(LogInOptions.this,ShowPost.class));
                            finish();
                            progressDialog.dismiss();
                        } else {

                            String mess=task.getException().getMessage();
                            Toast.makeText(LogInOptions.this, "LogIn failed : "+mess, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
*/
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if (firebaseUser!=null){
            startActivity(new Intent(LogInOptions.this,ShowPost.class));
            finish();
        }

    }

}
