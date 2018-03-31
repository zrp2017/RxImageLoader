package com.zrp.imageloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.zrp.imageloadermodule.ImageLoader;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_load)
    Button btnLoad;
    @BindView(R.id.imv)
    ImageView imv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_load)
    public void onViewClicked() {
        ImageLoader.with(this).load("http://img01.sogoucdn.com/net/a/04/link?appid=100520145&url=http%3A%2F%2Fimg03.sogoucdn.com%2Fapp%2Fa%2F100520021%2F5925bfd2087bc4031d588db535551ae2").into(imv);
    }
}
