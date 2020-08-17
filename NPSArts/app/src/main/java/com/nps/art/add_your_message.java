package com.nps.art;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

public class add_your_message extends Fragment {

    TextView cancleUploadingMessage,uploadingMessage,headigMessage;
    TextInputEditText getMessages;
    FirebaseAuth mAuths;
    DatabaseReference authorityMessage,userRef;
    RelativeLayout cancle;
    String currentUsers;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_your_message, container, false);
        getMessages=(TextInputEditText)view.findViewById(R.id.getMessage);
        cancleUploadingMessage=(TextView)view.findViewById(R.id.cancelUpdate);
        uploadingMessage=(TextView)view.findViewById(R.id.updatePost);
        headigMessage=(TextView)view.findViewById(R.id.updatinpostitle);
        mAuths=FirebaseAuth.getInstance();
        currentUsers=mAuths.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(getActivity());
        cancle=(RelativeLayout)view.findViewById(R.id.cancleFrag);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        authorityMessage= FirebaseDatabase.getInstance().getReference().child("AuthorityMessage");
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        Typeface roboto=Typeface.createFromAsset(getActivity().getAssets(),"font/AvenyTMedium.otf");
        headigMessage.setTypeface(roboto);
        cancleUploadingMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        uploadingMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=String.valueOf(getMessages.getText());
                if (message.isEmpty()){
                    Toast.makeText(getActivity(), "Message can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    progressDialog.setMessage("Uploading, Please wait...");
                    progressDialog.setCancelable(true);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    HashMap putMess=new HashMap();
                    putMess.put("message",message);
                    putMess.put("userId",currentUsers);
                    authorityMessage.child(currentUsers).updateChildren(putMess).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                           if (task.isSuccessful()){
                               progressDialog.dismiss();
                               Toast.makeText(getActivity(), "Your Message Added Successfully", Toast.LENGTH_SHORT).show();
                               getActivity().onBackPressed();
                           }
                           else {
                               progressDialog.dismiss();
                               Toast.makeText(getActivity(), "Failed to add message", Toast.LENGTH_SHORT).show();
                           }
                        }
                    });
                }

            }
        });
        return view;
    }
}
