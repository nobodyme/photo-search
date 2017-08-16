package com.example.imnobody.photosearch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class Utils {

    private static final String BASEURL = "https://www.googleapis.com/customsearch/v1";

    private Utils() {
    }

    //make URL from base url and userText
    public static Uri.Builder getUri(String query) {
        Uri baseuri = Uri.parse(BASEURL);
        Uri.Builder uriBuilder = baseuri.buildUpon();
        uriBuilder.appendQueryParameter("q", query);
        uriBuilder.appendQueryParameter("cx", "005308678709096321560:t8ixbcpz85m");
        uriBuilder.appendQueryParameter("safe", "high");
        uriBuilder.appendQueryParameter("searchType", "image");
        uriBuilder.appendQueryParameter("key", "AIzaSyBLzBgge9AT42jw7aH3JdtBtp58nYx-h44"); // <--YOUR-API-KEY_HERE

        return uriBuilder;
    }

    // Extract json from string response

    public static List<String> extractImages(String sampleJsonResponse) {

        List<String> imageUrlList = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject(sampleJsonResponse);

            JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < itemsArray.length(); i++) {

                JSONObject itemsArrayJSONObject = itemsArray.getJSONObject(i);

                /**
                 * To retrieve  thumbnails -- omitted
                 *
                 * JSONObject imageJSONObject = itemsArrayJSONObject.getJSONObject("image");
                 * imageUrl.add(imageJSONObject.getString("thumbnailLink"));
                 **/

                imageUrlList.add(itemsArrayJSONObject.getString("link"));


            }
        } catch (JSONException e) {
            Log.e("Utils", "Problem parsing", e);
        }

        // return list of image urls after parsing
        return imageUrlList;

    }

    // get resource from imageview
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static boolean checkImageResource(Context ctx, ImageView imageView, int imageResource) {
        boolean result = false;

        if (ctx != null && imageView != null && imageView.getDrawable() != null) {
            Drawable.ConstantState constantState;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                constantState = ctx.getResources()
                        .getDrawable(imageResource, ctx.getTheme())
                        .getConstantState();
            } else {
                constantState = ctx.getResources().getDrawable(imageResource)
                        .getConstantState();
            }

            if (imageView.getDrawable().getConstantState() == constantState) {
                result = true;
            }
        }

        return result;
    }



}
