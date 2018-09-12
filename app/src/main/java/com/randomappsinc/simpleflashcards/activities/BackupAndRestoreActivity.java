package com.randomappsinc.simpleflashcards.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.BackupAndRestoreTabsAdapter;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.managers.BackupDataManager;
import com.randomappsinc.simpleflashcards.managers.RestoreDataManager;

import java.io.File;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BackupAndRestoreActivity extends StandardActivity implements FolderChooserDialog.FolderCallback {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.backup_and_restore_tabs) TabLayout tabs;
    @BindView(R.id.backup_and_restore_pager) ViewPager viewPager;
    @BindArray(R.array.backup_and_restore_tabs) String[] tabNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_and_restore);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager.setAdapter(new BackupAndRestoreTabsAdapter(getSupportFragmentManager(), tabNames));
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull File folder) {
        if (dialog.getTag() == null) {
            return;
        }

        if (dialog.getTag().equals(Constants.BACKUP_KEY)) {
            BackupDataManager.get().setBackupLocation(folder.getAbsolutePath(), this);
        } else if (dialog.getTag().equals(Constants.RESTORE_KEY)) {
            RestoreDataManager.get().restoreData(folder.getAbsolutePath());
        }
    }

    @Override
    public void onFolderChooserDismissed(@NonNull FolderChooserDialog dialog) {}
}
