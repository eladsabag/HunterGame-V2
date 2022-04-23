package com.example.huntergame_v2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.huntergame_v2.R;
import com.example.huntergame_v2.activities.GameActivity;
import com.google.android.material.button.MaterialButton;

public class Fragment_Button extends Fragment {
    private MaterialButton scores_BTN_playagain;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_button, container, false);

        scores_BTN_playagain = view.findViewById(R.id.scores_BTN_playagain);

        scores_BTN_playagain.setOnClickListener(e -> {
            Intent intent = new Intent(getActivity(), GameActivity.class);
            // pass the correct arguements of the activity to game activity
            intent.putExtra("Sensor", getActivity().getIntent().getExtras().getBoolean("Sensor"));
            intent.putExtra("Sound", getActivity().getIntent().getExtras().getBoolean("Sound"));
            intent.putExtra("Vibration", getActivity().getIntent().getExtras().getBoolean("Vibration"));
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }


}
