package com.nps.art;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/**
 * A simple {@link Fragment} subclass.
 */
public class ShowNotVerifiedMessage extends Fragment {
    public ShowNotVerifiedMessage() {
    }
    TextView okButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_show_not_verified_message, container, false);
        okButton=(TextView)view.findViewById(R.id.okbutton);
        Typeface avenyInstaRegular=Typeface.createFromAsset(getActivity().getAssets(),"font/FacebookNarrow_A_Rg.ttf");
       okButton.setTypeface(avenyInstaRegular);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return view;
    }
}
