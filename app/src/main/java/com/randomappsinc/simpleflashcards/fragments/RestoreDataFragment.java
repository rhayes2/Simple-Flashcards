package com.randomappsinc.simpleflashcards.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.managers.RestoreDataManager;
import com.randomappsinc.simpleflashcards.utils.PermissionUtils;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RestoreDataFragment extends Fragment {

    private static final int READ_EXTERNAL_STORAGE_CODE = 2;

    private FolderChooserDialog folderChooserDialog;
    private RestoreDataManager restoreDataManager = RestoreDataManager.get();
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.restore_data,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        folderChooserDialog = new FolderChooserDialog.Builder(getActivity())
                .tag(Constants.RESTORE_KEY)
                .chooseButton(R.string.choose)
                .build();
        restoreDataManager.setListener(restoreDataListener);
    }

    private final RestoreDataManager.Listener restoreDataListener = new RestoreDataManager.Listener() {
        @Override
        public void onDataRestorationComplete() {
            UIUtils.showShortToast(R.string.flashcard_sets_restored, getContext());
        }

        @Override
        public void onFileNotFound() {
            UIUtils.showLongToast(R.string.backup_file_not_found, getContext());
        }
    };

    @OnClick(R.id.restore_data)
    public void restoreDataClicked() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, getContext())) {
            folderChooserDialog.show(getActivity());
        } else {
            PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        if (requestCode != READ_EXTERNAL_STORAGE_CODE
                || grantResults.length <= 0
                || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        folderChooserDialog.show(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        restoreDataManager.setListener(null);
        unbinder.unbind();
    }
}
