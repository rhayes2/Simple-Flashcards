package com.randomappsinc.simpleflashcards.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.constants.Constants;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BackupDataFragment extends Fragment {

    private FolderChooserDialog folderChooserDialog;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.backup_data,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        folderChooserDialog = new FolderChooserDialog.Builder(getActivity())
                .tag(Constants.BACKUP_KEY)
                .chooseButton(R.string.choose)
                .build();
    }

    @OnClick(R.id.backup_data)
    public void backupData() {
        folderChooserDialog.show(getActivity());
    }

    @OnClick(R.id.export_data)
    public void exportData() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
