package com.nps.art;


import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeMessage extends Fragment {

SharedPreferences sharedPreferences;
    public NoticeMessage() {
        // Required empty public constructor
    }
    TextView mustRead,terms;
    Button makeasRead;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view= inflater.inflate(R.layout.fragment_notice_message, container, false);
       makeasRead=(Button)view.findViewById(R.id.makeAsRead);
       mustRead=(TextView)view.findViewById(R.id.mustRead);
        sharedPreferences=getActivity().getSharedPreferences("setCon",MODE_PRIVATE);
       terms=(TextView)view.findViewById(R.id.termsandConditions);
        Typeface roboto=Typeface.createFromAsset(getActivity().getAssets(),"font/AvenyTMedium.otf");
        mustRead.setTypeface(roboto);
        terms.setTypeface(roboto);
        terms.setText(Html.fromHtml(getResources().getString(R.string.terms)));
        mustRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("getCondition","Yep");
                editor.apply();
                editor.commit();
            }
        });
        return view;
    }

}
