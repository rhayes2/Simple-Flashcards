package com.randomappsinc.simpleflashcards.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.managers.NearbyConnectionsManager;
import com.randomappsinc.simpleflashcards.utils.MyApplication;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReceivedFlashcardsFragment extends Fragment {

    @BindView(R.id.no_flashcards_received) TextView noFlashcards;

    private Unbinder unbinder;
    private NearbyConnectionsManager nearbyConnectionsManager = NearbyConnectionsManager.get();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.received_flashcards,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        noFlashcards.setText(MyApplication
                .getAppContext()
                .getString(R.string.no_flashcards_received, nearbyConnectionsManager.getOtherSideName()));
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
