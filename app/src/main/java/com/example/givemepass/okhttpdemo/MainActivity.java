package com.example.givemepass.okhttpdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Button sendBtnInBg;
    private Button sendBtn;
    private Button getJsonBtn;
    private ExecutorService service;
    private OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        client = new OkHttpClient();
        service = Executors.newSingleThreadExecutor();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
        sendBtnInBg = (Button) findViewById(R.id.send_request_in_background);
        sendBtnInBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRequestInBackground();
            }
        });
        sendBtn = (Button) findViewById(R.id.send_request);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRequest();
            }
        });
        getJsonBtn = (Button) findViewById(R.id.get_json_btn);
        getJsonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleJson();
            }
        });
    }
    private class JsonData{
        @SerializedName("Name")
        private String name;
        @SerializedName("City")
        private String city;
        @SerializedName("Country")
        private String country;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    private void handleJson(){
        Request request = new Request.Builder()
                .url("http://www.w3schools.com/website/customers_mysql.php")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final String resStr = response.body().string();
                final List<JsonData> jsonData = new Gson().fromJson(resStr, new TypeToken<List<JsonData>>(){}.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuffer sb = new StringBuffer();
                        for(JsonData json : jsonData) {
                            sb.append("name:");
                            sb.append(json.getName());
                            sb.append("\n");
                            sb.append("city:");
                            sb.append(json.getCity());
                            sb.append("\n");
                            sb.append("country:");
                            sb.append(json.getCountry());
                            sb.append("\n");
                        }
                        textView.setText(sb.toString());
                    }
                });
            }
        });
    }

    private void handleRequest(){
        Request request = new Request.Builder()
                .url("http://www.google.com.tw")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final String resStr = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(resStr);
                    }
                });
            }
        });
    }

    private void handleRequestInBackground(){
        service.submit(new Runnable() {
            @Override
            public void run() {
                HttpUrl.Builder builder = HttpUrl.parse("https://www.google.com.tw/search?").newBuilder();
                builder.addQueryParameter("q","givemepass");
                builder.addQueryParameter("oq","givemepass");

                Request request = new Request.Builder()
                        .url(builder.toString())
                        .build();
                try {
                    final Response response = client.newCall(request).execute();
                    final String resStr = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(resStr);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
