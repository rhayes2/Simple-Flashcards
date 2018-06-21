package com.randomappsinc.simpleflashcards.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.ReceivedFlashcardsAdapter;
import com.randomappsinc.simpleflashcards.managers.NearbyConnectionsManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.views.SimpleDividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReceivedFlashcardsFragment extends Fragment {

    @BindView(R.id.no_flashcards_received) TextView noFlashcards;
    @BindView(R.id.flashcard_sets_received) RecyclerView flashcardSets;

    protected ReceivedFlashcardsAdapter receivedFlashcardsAdapter;
    private NearbyConnectionsManager nearbyConnectionsManager = NearbyConnectionsManager.get();
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.received_flashcards,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        noFlashcards.setText(getString(
                R.string.no_flashcards_received,
                nearbyConnectionsManager.getOtherSideName()));
        receivedFlashcardsAdapter = new ReceivedFlashcardsAdapter();
        flashcardSets.setAdapter(receivedFlashcardsAdapter);
        flashcardSets.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        nearbyConnectionsManager.setFlashcardSetReceiptListener(flashcardSetReceiptListener);
    }

    private final NearbyConnectionsManager.FlashcardSetReceiptListener flashcardSetReceiptListener
            = new NearbyConnectionsManager.FlashcardSetReceiptListener() {
        @Override
        public void onFlashcardSetReceived(FlashcardSet flashcardSet) {
            receivedFlashcardsAdapter.addFlashcardSet(flashcardSet);
            if (noFlashcards.getVisibility() == View.VISIBLE) {
                noFlashcards.setVisibility(View.GONE);
                flashcardSets.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
