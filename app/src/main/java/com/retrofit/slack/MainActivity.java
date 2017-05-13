package com.retrofit.slack;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.retrofit.constants.Constants;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button mButton;
    private TextView mTextView;

    private SlackApi slackApi;
    private Call<SlackApi.UploadFileResponse> call;
    private RequestBody file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.mButton);
        mTextView = (TextView) findViewById(R.id.mTextView);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button Clicked");
                clickMe(view);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        slackApi = new Retrofit.Builder().baseUrl(Constants.slackURL).client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(SlackApi.class);

        String str = "Message to be displayed on the SLACK Channel\n";
        file = RequestBody.create(MediaType.parse("multipart/form-data"), str.getBytes());

        Map<String, RequestBody> map = new HashMap<>();
        map.put("file\"; filename=\"heapDump.md\"", file);
        call = slackApi.uploadFile(Constants.TOKEN, map, "text", "heapDump.md", "Test Dump", "Check this out", Constants.MEMORY_LEAK_CHANNEL);
    }

    public static RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    public void clickMe(View view) {

        call.clone().enqueue(new Callback<SlackApi.UploadFileResponse>() {
            @Override
            public void onResponse(Call<SlackApi.UploadFileResponse> call, Response<SlackApi.UploadFileResponse> response) {
                if (response != null) {
                    Log.e("GAG", response.body().toString());
                    Log.d(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<SlackApi.UploadFileResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d(TAG, "onFailure:" + t.getMessage());
            }
        });
    }


}
