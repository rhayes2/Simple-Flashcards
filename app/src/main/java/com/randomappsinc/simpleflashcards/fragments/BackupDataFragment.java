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
import com.randomappsinc.simpleflashcards.managers.BackupDataManager;
import com.randomappsinc.simpleflashcards.persistence.PreferencesManager;
import com.randomappsinc.simpleflashcards.utils.PermissionUtils;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BackupDataFragment extends Fragment {

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;

    private FolderChooserDialog folderChooserDialog;
    private BackupDataManager backupDataManager = BackupDataManager.get();
    private PreferencesManager preferencesManager;
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
        preferencesManager = new PreferencesManager(getContext());
        folderChooserDialog = new FolderChooserDialog.Builder(getActivity())
                .tag(Constants.BACKUP_KEY)
                .chooseButton(R.string.choose)
                .build();
        backupDataManager.setListener(backupDataListener);
    }

    @OnClick(R.id.backup_data)
    public void backupDataClicked() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE, getContext())) {
            backupData();
        } else {
            PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE_CODE);
        }
    }

    private void backupData() {
        if (preferencesManager.getBackupFolderPath() == null) {
            folderChooserDialog.show(getActivity());
        } else {
            backupDataManager.backupData(getContext());
        }
    }

    @OnClick(R.id.export_data)
    public void exportData() {

    }

    private final BackupDataManager.Listener backupDataListener = new BackupDataManager.Listener() {
        @Override
        public void onBackupStarted() {

        }

        @Override
        public void onBackupComplete() {
            UIUtils.showLongToast("Backup complete!", getContext());
        }

        @Override
        public void onBackupFailed() {

        }
    };

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        if (requestCode != WRITE_EXTERNAL_STORAGE_CODE
                || grantResults.length <= 0
                || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        backupData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        backupDataManager.setListener(null);
        unbinder.unbind();
    }
}
