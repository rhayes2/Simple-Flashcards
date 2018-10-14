package com.randomappsinc.simpleflashcards.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.managers.BackupDataManager;
import com.randomappsinc.simpleflashcards.persistence.PreferencesManager;
import com.randomappsinc.simpleflashcards.utils.PermissionUtils;
import com.randomappsinc.simpleflashcards.utils.TimeUtils;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BackupDataFragment extends Fragment {

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    private static final int WRITE_BACKUP_FILE_CODE = 350;

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
        preferencesManager = new PreferencesManager(getContext());
        setBackupSubtitle();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        folderChooserDialog = new FolderChooserDialog.Builder(getActivity())
                .tag(Constants.BACKUP_KEY)
                .chooseButton(R.string.choose)
                .build();
        backupDataManager.setListener(backupDataListener);
    }

    protected void setBackupSubtitle() {
        if (backupDataManager.getBackupPath(getContext()) == null) {
            backupSubtitle.setText(R.string.backup_data_explanation);
        } else {
            long lastBackupUnixTime = preferencesManager.getLastBackupTime();
            String lastBackupTime = TimeUtils.getLastBackupTime(lastBackupUnixTime);
            backupSubtitle.setText(String.format(subtitleTemplate, lastBackupTime));
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
        String backupPath = backupDataManager.getBackupPath(getContext());
        if (backupPath == null) {
            chooseBackupLocation();
        } else {
            backupDataManager.backupData(getContext(), true);
        }
    }

    private void chooseBackupLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            folderChooserDialog.show(getActivity());
        } else {
            UIUtils.showLongToast(R.string.choose_backup_folder, getContext());
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TITLE, BackupDataManager.BACKUP_FILE_NAME);
            startActivityForResult(intent, WRITE_BACKUP_FILE_CODE);
        }
    }

    @OnClick(R.id.change_backup_folder)
    public void changeBackupFolder() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE, getContext())) {
            chooseBackupLocation();
        } else {
            PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE_CODE);
        }
    }

    @OnClick(R.id.export_data)
    public void exportData() {
        Uri backupUri = backupDataManager.getBackupUriForExporting(getContext());
        if (backupUri == null) {
            UIUtils.showLongToast(R.string.cannot_export_nothing, getContext());
            return;
        }

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

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == WRITE_BACKUP_FILE_CODE && resultCode == Activity.RESULT_OK && resultData != null) {
            Context context = getContext();
            Uri uri = resultData.getData();
            if (uri == null || context == null) {
                backupDataListener.onBackupFailed();
            } else {
                // Persist ability to read/write to this file
                int takeFlags = resultData.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                context.getContentResolver().takePersistableUriPermission(uri, takeFlags);

                // Clear out existing saved file path (legacy)
                preferencesManager.setBackupFilePath(null);

                preferencesManager.setBackupUri(uri.toString());
                backupDataManager.backupData(getContext(), true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            backupData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        backupDataManager.setListener(null);
        unbinder.unbind();
    }
}
