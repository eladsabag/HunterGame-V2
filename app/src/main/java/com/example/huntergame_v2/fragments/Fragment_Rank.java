package com.example.huntergame_v2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.huntergame_v2.CallBack_ScoreClicked;
import com.example.huntergame_v2.R;
import com.google.android.material.button.MaterialButton;

import java.util.Set;
import java.util.TreeSet;

public class Fragment_Rank extends Fragment {
    private MaterialButton[] ranks;
    private CallBack_ScoreClicked callBack_ScoreClicked;
    private TreeSet<String> scores = null;

    public void setCallBack_ScoreClicked(CallBack_ScoreClicked callBack_ScoreClicked) {
        this.callBack_ScoreClicked = callBack_ScoreClicked;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank, container, false);

        findViews(view);

        ranks[0].setOnClickListener(e -> {
            if(callBack_ScoreClicked != null)
                callBack_ScoreClicked.scoreClicked();
        });

        if(scores != null)
            setRankScores();

        return view;
    }

    private void findViews(View view) {
        ranks = new MaterialButton[] {
          view.findViewById(R.id.rank1),view.findViewById(R.id.rank2),view.findViewById(R.id.rank3),view.findViewById(R.id.rank4),
                view.findViewById(R.id.rank5),view.findViewById(R.id.rank6),view.findViewById(R.id.rank7),view.findViewById(R.id.rank8)
                ,view.findViewById(R.id.rank9),view.findViewById(R.id.rank10)
        };
    }

    public void setRankScores() {
        int i = 0;
        for(String s : scores) {
            ranks[i].setText(s);
            i++;
        }
    }

    public void setScores(TreeSet<String> scores) {
        this.scores=scores;
    }
}