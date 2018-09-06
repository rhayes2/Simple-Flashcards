package com.randomappsinc.simpleflashcards.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.SendFlashcardsAdapter;
import com.randomappsinc.simpleflashcards.managers.NearbyConnectionsManager;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.UIUtils;
import com.randomappsinc.simpleflashcards.views.SimpleDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SendFlashcardsFragment extends Fragment {

    @BindView(R.id.no_flashcards_to_send) View noFlashcards;
    @BindView(R.id.flashcard_sets_to_send) RecyclerView flashcardSetList;

    protected NearbyConnectionsManager nearbyConnectionsManager = NearbyConnectionsManager.get();
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.send_flashcards,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<FlashcardSet> flashcardSets = DatabaseManager.get().getFlashcardSets("");
        if (flashcardSets.isEmpty()) {
            flashcardSetList.setVisibility(View.GONE);
        } else {
            flashcardSetList.setAdapter(new SendFlashcardsAdapter(flashcardSets, sendFlashcardsListener));
            flashcardSetList.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
            noFlashcards.setVisibility(View.GONE);
        }
    }

    private final SendFlashcardsAdapter.Listener sendFlashcardsListener = new SendFlashcardsAdapter.Listener() {
        @Override
        public void onSendFlashcardSet(FlashcardSet flashcardSet) {
            nearbyConnectionsManager.sendFlashcardSet(flashcardSet, getContext());
        }

        @Override
        public void onFlashcardSetTransferFailure(FlashcardSet flashcardSet) {
            UIUtils.showLongToast(getString(
                    R.string.failed_to_send_set,
                    flashcardSet.getName()), getContext());
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
