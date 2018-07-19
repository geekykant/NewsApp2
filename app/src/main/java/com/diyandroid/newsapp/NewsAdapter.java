package com.diyandroid.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    private HashMap hashMap;

    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }

        News currentNews = getItem(position);

        // Find the TextView with view ID magnitude
        TextView newsTime = (TextView) convertView.findViewById(R.id.newsTime);
        TextView newsCategory = (TextView) convertView.findViewById(R.id.newsCategory);
        TextView newsDate = (TextView) convertView.findViewById(R.id.newsDate);
        TextView newsTitle = (TextView) convertView.findViewById(R.id.newsTitle);

        hashMap = new HashMap();
        formatDate(currentNews.getWebPublicationDate());

        newsDate.setText(hashMap.get("onlyDate").toString());
        newsTime.setText(hashMap.get("onlyTime").toString());

        newsCategory.setText(currentNews.getSectionName());
        newsTitle.setText(currentNews.getWebTitle());

        return convertView;
    }

    private void formatDate(String real_date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = null;
        try {
            date = format.parse(real_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        hashMap.put("onlyDate", dateFormat.format(date));
        hashMap.put("onlyTime", timeFormat.format(date));
    }
}