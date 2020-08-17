package com.nps.art;


import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nps.art.R;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditMessage extends Fragment {


    public EditMessage() {
        // Required empty public constructor
    }
     public static String messagees;
    TextView cancleUploadingMessage,uploadingMessage,headigMessage;
    TextInputEditText getMessages;
    FirebaseAuth mAuths;
    DatabaseReference authorityMessage,userRef;
    RelativeLayout cancle;
    String currentUsers;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_edit_message, container, false);
        getMessages=(TextInputEditText)view.findViewById(R.id.getMessage);
        cancleUploadingMessage=(TextView)view.findViewById(R.id.cancelUpdate);
        uploadingMessage=(TextView)view.findViewById(R.id.updatePost);
        headigMessage=(TextView)view.findViewById(R.id.updatinpostitle);
        mAuths= FirebaseAuth.getInstance();
        currentUsers=mAuths.getCurrentUser().getUid();
        cancle=(RelativeLayout)view.findViewById(R.id.editMessageParent);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        getMessages.setText(messagees);
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
                    HashMap putMess=new HashMap();
                    putMess.put("message",message);
                    putMess.put("userId",currentUsers);
                    authorityMessage.child(currentUsers).updateChildren(putMess);
                }

            }
        });


        return view;
    }

}
