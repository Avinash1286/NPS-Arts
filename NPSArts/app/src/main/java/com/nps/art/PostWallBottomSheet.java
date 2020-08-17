package com.nps.art;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PostWallBottomSheet extends BottomSheetDialogFragment {
    TextView createPostHeading,artsPOstHeading,creativePostHeading;
    ImageView postCreativeHands,postArticles;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.post_wall_arts_bottomsheet,container,false);
        Typeface robot=Typeface.createFromAsset(getActivity().getAssets(),"font/AvenyTMedium.otf");
        createPostHeading=(TextView)view.findViewById(R.id.createPostTitle);
        artsPOstHeading=(TextView)view.findViewById(R.id.artsPostHeading);
        creativePostHeading=(TextView)view.findViewById(R.id.imagepostHeading);
        postArticles=(ImageView)view.findViewById(R.id.artspostImage);
        postCreativeHands=(ImageView)view.findViewById(R.id.imagePostImage);
        createPostHeading.setTypeface(robot);
        artsPOstHeading.setTypeface(robot);
        creativePostHeading.setTypeface(robot);
        postCreativeHands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),PostingCreativeHands.class));
            }
        });
        postArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),PostingWallMagazine.class));
            }
        });
        return view;
    }
}
