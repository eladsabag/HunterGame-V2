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
import java.util.TreeMap;
import java.util.TreeSet;

public class Fragment_Rank extends Fragment {
    private MaterialButton[] ranks;
    private CallBack_ScoreClicked callBack_ScoreClicked;
    private Set<Integer> scores = null;
    TreeMap<Integer, Double> scoresAndLatitude = null;
    TreeMap<Integer, Double> scoresAndLongitude = null;

    public void setCallBack_ScoreClicked(CallBack_ScoreClicked callBack_ScoreClicked) {
        this.callBack_ScoreClicked = callBack_ScoreClicked;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank, container, false);

        findViews(view);

        initViews();

        if(scoresAndLatitude != null && scoresAndLongitude != null)
            setRankScores();

        return view;
    }

    private void initViews() {
        for (int i = 0; i < ranks.length; i++) {
            int finalI = i;
            ranks[i].setOnClickListener(e -> {
                if(callBack_ScoreClicked != null)
                    callBack_ScoreClicked.scoreClicked(finalI);
            });
        }
    }

    private void findViews(View view) {
        ranks = new MaterialButton[] {
          view.findViewById(R.id.rank1),view.findViewById(R.id.rank2),view.findViewById(R.id.rank3),
          view.findViewById(R.id.rank4), view.findViewById(R.id.rank5),view.findViewById(R.id.rank6),
          view.findViewById(R.id.rank7),view.findViewById(R.id.rank8),view.findViewById(R.id.rank9),
          view.findViewById(R.id.rank10)
        };
    }

    /**
     * This function sets the rank scores in the Fragment Rank in reverse order.
     */
    public void setRankScores() {
        int i = 0;
        for(Integer s : scores) {
            ranks[i].setText(s+"");
            i++;
        }
    }

    public void setMaps(TreeMap<Integer, Double> scoresAndLatitude, TreeMap<Integer, Double> scoresAndLongitude) {
        this.scoresAndLatitude = scoresAndLatitude;
        this.scoresAndLongitude = scoresAndLongitude;
    }

    public void setAllScores(Set<Integer> keySet) {
        this.scores = keySet;
    }
}