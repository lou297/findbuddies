package org.capstone.findbuddies;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter{

    private Activity context;

    public CustomInfoWindow(Activity context){
        this.context = context;
    }



    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.custom_window,null);
        ImageView imageView = view.findViewById(R.id.paranomaView);
        imageView.setImageDrawable(LoadImageFromWebOperations(37.422, -122.084));
        return view;
    }

    public static Drawable LoadImageFromWeb(double latitude,double longitude) {
        String url = "http://maps.google.com/cbk?output=xml&ll=" + latitude + "," + longitude;
        String panoID = "";
        Log.d("dubug","zzz");
        Log.d("dubug","qqq12");
        try {
            Log.d("dubug","qqq1");
            InputStream is = (InputStream) new URL(url).getContent();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            Log.d("dubug","qqq");
            xpp.setInput(is, "utf-8");
            int eventType = xpp.getEventType();
            boolean isItemTag = false;

            String tagName = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                Log.d("dubug","pppp");
                if (eventType == XmlPullParser.START_TAG) {
                    tagName = xpp.getName();

                    if (tagName.equals("data_properties")) {
                        panoID = xpp.getAttributeValue(null, "pano_id");
                        Log.i("dubug", "xpp.getAttributeValue:" + xpp.getAttributeValue(null, "pano_id"));
                        break;
                    }
                } else if (eventType == XmlPullParser.TEXT) {

                } else if (eventType == XmlPullParser.END_TAG) {

                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            return null;
        }

        url = "http://cbk0.google.com/cbk?output=tile&panoid=" + panoID + "&zoom=3&x=1&y=1";
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            Log.d("dubug", "img Success");
            Log.d("dubug", "url: " + url.toString());

            Log.d("dubug", "is: " + is.toString());

            return d;
        } catch (Exception e) {
            Log.d("dubug", "img falied" + e.toString());
            return null;
        }

    }

    public static Drawable LoadImageFromWebOperations(double latitude,double longitude) {
        String url = "http://maps.google.com/cbk?output=xml&ll=" + latitude + "," + longitude;
        try {
            Log.d("dubug","qqq1");
            InputStream is = (InputStream) new URL(url).getContent();
            Log.d("dubug","qqq2");
            Drawable d = Drawable.createFromStream(is, "src name");
            Log.d("dubug","qqq3");
            return d;
        } catch (Exception e) {
            return null;
        }
    }


}


