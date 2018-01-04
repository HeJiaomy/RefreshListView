package com.example.refreshlistview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.refreshlistview.view.RefreshListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RefreshListView listView;
    List<String> dataLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);
        dataLists = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            dataLists.add("这是一条ListView的数据：" + i);
        }
        listView.setAdapter(new MyAdapter());
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataLists == null ? 0 : dataLists.size();
        }

        @Override
        public Object getItem(int position) {
            return dataLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView= new TextView(parent.getContext());
            textView.setTextSize(18f);
            textView.setText(dataLists.get(position));
            return textView;
        }
    }
}
