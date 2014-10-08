package net.bither.image;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import net.bither.image.cache.ImageManageUtil;
import net.bither.image.ui.base.ImageProgressView;

import java.util.Locale;

public class PicDetailActivity extends Activity {
    private static final int LIST_TOP_OFFSET = ImageManageUtil.dip2pix(50);
    private boolean useMemoryCache = false;
    private ImageProgressView ipvPhoto;
    private ImageButton ibBack;
    private Button btnBack;
    private Top top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, 0);
        setContentView(R.layout.activity_pic_detail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().getAttributes().dimAmount = 0.75f;
        if (getIntent().getExtras() != null
                && getIntent().getExtras().containsKey(
                BitherApplication.PIC_PASS_VALUE_TAG)) {
            top = (Top) getIntent().getExtras().get(
                    BitherApplication.PIC_PASS_VALUE_TAG);
        }
        if (top == null) {
            finish();
            return;
        }


        showPiCommentInfo();

    }

    private void showPiCommentInfo() {
        ipvPhoto = (ImageProgressView) findViewById(R.id.ipv_photo_big);
        ibBack=(ImageButton)findViewById(R.id.ibtn_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.slide_out_right);
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.slide_out_right);
            }
        });
        ipvPhoto.dowloadPicameraImage(top.getPicName(),
                useMemoryCache);
    }


    private boolean scrolled = false;


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

}
