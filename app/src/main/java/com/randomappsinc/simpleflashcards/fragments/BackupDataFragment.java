package com.randomappsinc.simpleflashcards.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.managers.BackupDataManager;
import com.randomappsinc.simpleflashcards.persistence.PreferencesManager;
import com.randomappsinc.simpleflashcards.utils.FileUtils;
import com.randomappsinc.simpleflashcards.utils.PermissionUtils;
import com.randomappsinc.simpleflashcards.utils.TimeUtils;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import java.io.File;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BackupDataFragment extends Fragment {

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;

    @BindView(R.id.backup_subtitle) TextView backupSubtitle;
    @BindString(R.string.backup_subtitle_with_backup) String subtitleTemplate;

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
        setBackupSubtitle();
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

    protected void setBackupSubtitle() {
        File backupFile = FileUtils.getBackupFile(getContext());
        if (backupFile == null) {
            backupSubtitle.setText(R.string.backup_data_explanation);
        } else {
            String lastBackupTime = TimeUtils.getLastBackupTime(backupFile.lastModified());
            backupSubtitle.setText(String.format(subtitleTemplate, lastBackupTime, backupFile.getAbsolutePath()));
        }
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
        File backupFile = FileUtils.getBackupFile(getContext());
        if (backupFile == null) {
            UIUtils.showLongToast(R.string.cannot_export_nothing, getContext());
            return;
        }

        Uri backupUri = FileProvider.getUriForFile(
                getContext(),
                Constants.FILE_AUTHORITY,
                backupFile);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, backupUri);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.export_data_with)));
    }

    private final BackupDataManager.Listener backupDataListener = new BackupDataManager.Listener() {
        @Override
        public void onBackupComplete() {
            UIUtils.showShortToast(R.string.backup_successful, getContext());
            setBackupSubtitle();
        }

        @Override
        public void onBackupFailed() {
            UIUtils.showLongToast(R.string.backup_failed, getContext());
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
