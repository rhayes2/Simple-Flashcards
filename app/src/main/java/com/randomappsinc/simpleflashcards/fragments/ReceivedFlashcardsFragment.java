package com.randomappsinc.simpleflashcards.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.activities.PlainSetViewActivity;
import com.randomappsinc.simpleflashcards.adapters.ReceivedFlashcardsAdapter;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.managers.NearbyConnectionsManager;
import com.randomappsinc.simpleflashcards.models.FlashcardSetPreview;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.UIUtils;
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
        receivedFlashcardsAdapter = new ReceivedFlashcardsAdapter(listClickListener);
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
            UIUtils.showLongToast(getString(R.string.received_set, flashcardSet.getName()), getContext());
        }
    };

    private final ReceivedFlashcardsAdapter.Listener listClickListener = new ReceivedFlashcardsAdapter.Listener() {
        @Override
        public void onCellClicked(FlashcardSetPreview setPreview) {
            Activity activity = getActivity();
            if (activity != null) {
                Intent intent = new Intent(activity, PlainSetViewActivity.class)
                        .putExtra(Constants.SET_PREVIEW_KEY, setPreview);
                startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.stay);
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
