package com.randomappsinc.simpleflashcards.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.utils.UIUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PictureFullViewActivity extends AppCompatActivity {

    @BindView(R.id.parent) View parent;
    @BindView(R.id.picture) ImageView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_full_view_activity);
        ButterKnife.bind(this);

        String imageUrl = getIntent().getStringExtra(Constants.IMAGE_URL_KEY);
        if (!TextUtils.isEmpty(imageUrl)) {
            Drawable defaultThumbnail = new IconDrawable(
                    this,
                    IoniconsIcons.ion_image).colorRes(R.color.dark_gray);

            Picasso.get()
                    .load(imageUrl)
                    .error(defaultThumbnail)
                    .fit()
                    .centerInside()
                    .into(picture, imageLoadingCallback);
        } else {
            throw new IllegalArgumentException("Started full view with a blank/null picture URL");
        }
    }

    private final Callback imageLoadingCallback = new Callback() {
        @Override
        public void onSuccess() {}

        @Override
        public void onError(Exception e) {
            UIUtils.showLongToast(R.string.image_load_fail);
        }
    };

    @OnClick(R.id.close)
    public void closePage() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
    }
}
