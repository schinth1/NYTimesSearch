package com.schintha.nytimessearch.models;

/**
 * Created by sc043016 on 7/28/16.
 */
import android.text.Html;
import android.text.Spanned;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Parcel
public class ArticleModel {
    String webUrl;
    String headline;
    List<String> thumbNail;

    public ArticleModel() {
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {return headline; }

    public Spanned getFormattedHeadline() {
        return Html.fromHtml("<font color=\"black\">" + headline + "</font>");
    }

    public String getRandomThumbNail() {
        if (thumbNail.size() == 0) {
            return "";
        }
        return thumbNail.get(new Random().nextInt(thumbNail.size()));
    }

    public ArticleModel(JSONObject jsonObject) {
        try {
            webUrl = jsonObject.getString("web_url");
            headline = jsonObject.getJSONObject("headline").getString("main");

            thumbNail = new ArrayList<>();
            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            for (int i = 0; i < multimedia.length(); i++) {
                thumbNail.add("http://www.nytimes.com/" + multimedia.getJSONObject(i).getString("url"));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ArticleModel> fromJSONArray(JSONArray array) {
        ArrayList<ArticleModel> results = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new ArticleModel(array.getJSONObject(i)));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return results;
    }
}
