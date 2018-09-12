package com.randomappsinc.simpleflashcards.fragments;

import android.Manifest;
import android.app.Activity;
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

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.activities.AddedFlashcardSetsActivity;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.managers.RestoreDataManager;
import com.randomappsinc.simpleflashcards.utils.PermissionUtils;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RestoreDataFragment extends Fragment {

    private static final int READ_EXTERNAL_STORAGE_CODE = 2;
    private static final int READ_BACKUP_REQUEST_CODE = 9001;

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
        public void onDataRestorationComplete(int[] addedSetIds) {
            UIUtils.showShortToast(R.string.flashcard_sets_restored, getContext());

            Activity activity = getActivity();
            if (activity != null) {
                Intent intent = new Intent(activity, AddedFlashcardSetsActivity.class)
                        .putExtra(Constants.ADDED_SET_IDS_KEY, addedSetIds);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.stay);
            }
        }

        @Override
        public void onFileNotFound() {
            UIUtils.showLongToast(R.string.backup_file_not_found, getContext());
        }
    };

    @OnClick(R.id.restore_data)
    public void restoreDataClicked() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, getContext())) {
            chooseBackupLocation();
        } else {
            PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE_CODE);
        }
    }

    private void chooseBackupLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            folderChooserDialog.show(getActivity());
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/*");
            startActivityForResult(intent, READ_BACKUP_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_BACKUP_REQUEST_CODE && resultCode == Activity.RESULT_OK && resultData != null) {
            Uri uri = resultData.getData();
            restoreDataManager.restoreDataFromUri(uri, getContext());
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            chooseBackupLocation();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        restoreDataManager.setListener(null);
        unbinder.unbind();
    }
}
