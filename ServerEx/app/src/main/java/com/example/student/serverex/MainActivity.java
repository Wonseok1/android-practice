package com.example.student.serverex;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    TextView tv_data;
    ImageView iv_poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_data = (TextView)findViewById(R.id.tv_data);
        iv_poster = (ImageView)findViewById(R.id.iv_poster);

        String url = "http://70.12.110.50:3000";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("number","1");

        MyHttpTask myHttpTask = new MyHttpTask(url, map);
        myHttpTask.execute();

        String url_img = "http://70.12.110.50:3000/files";

        MyImageHttpTask myImageHttpTask = new MyImageHttpTask(url_img, map);
        myImageHttpTask.execute();

    }

    class MyImageHttpTask extends AsyncTask<Void, Void, Bitmap> {

        String url_str;
        HashMap<String, String> map;

        public MyImageHttpTask(String url_str, HashMap<String, String> map) {
            super();

            this.url_str = url_str;
            this.map = map;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap result = null;
            String post_query = "";
            PrintWriter printWriter = null;

            try {
                URL text = new URL(url_str);
                HttpURLConnection http = (HttpURLConnection)text.openConnection();
                http.setRequestProperty("Content-type",
                        "application/x-www-form-urlencoded;charset=UTF-8");
                http.setConnectTimeout(10000);
                http.setReadTimeout(10000);
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                if(map != null && map.size() > 0) {

                    Iterator<String> keys = map.keySet().iterator();

                    boolean first_query_part = true;
                    while(keys.hasNext()) {

                        if(!first_query_part) {
                            post_query += "&";
                        }

                        String key = keys.next();
                        post_query += (key + "=" + URLEncoder.encode(map.get(key), "UTF-8"));

                        first_query_part = false;
                    }

                    // sending to server
                    printWriter = new PrintWriter(new OutputStreamWriter(
                            http.getOutputStream(), "UTF-8"));
                    printWriter.write(post_query);
                    printWriter.flush();

                    // receive from server
                    result = BitmapFactory.decodeStream(http.getInputStream());

                }
            } catch(Exception e) {
                e.printStackTrace();
                result = null;
            } finally {
                try{
                    if(printWriter != null) printWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            // do something
            iv_poster.setImageBitmap(s);
            this.cancel(true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    class MyHttpTask extends AsyncTask<Void, Void, String> {

        String url_str;
        HashMap<String, String> map;

        public MyHttpTask(String url_str, HashMap<String, String> map) {
            super();

            this.url_str = url_str;
            this.map = map;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            String post_query = "";
            PrintWriter printWriter = null;
            BufferedReader bufferedReader = null;

            try {
                URL text = new URL(url_str);
                HttpURLConnection http = (HttpURLConnection)text.openConnection();
                http.setRequestProperty("Content-type",
                        "application/x-www-form-urlencoded;charset=UTF-8");
                http.setConnectTimeout(10000);
                http.setReadTimeout(10000);
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                if(map != null && map.size() > 0) {

                    Iterator<String> keys = map.keySet().iterator();

                    boolean first_query_part = true;
                    while(keys.hasNext()) {

                        if(!first_query_part) {
                            post_query += "&";
                        }

                        String key = keys.next();
                        post_query += (key + "=" + URLEncoder.encode(map.get(key), "UTF-8"));

                        first_query_part = false;
                    }

                    // sending to server
                    printWriter = new PrintWriter(new OutputStreamWriter(
                            http.getOutputStream(), "UTF-8"));
                    printWriter.write(post_query);
                    printWriter.flush();

                    // receive from server
                    bufferedReader = new BufferedReader(new InputStreamReader(
                            http.getInputStream(), "UTF-8"));
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;

                    while((line = bufferedReader.readLine()) != null) {
                        stringBuffer.append(line);
                    }

                    result = stringBuffer.toString();
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                try{
                    if(printWriter != null) printWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if(bufferedReader != null) bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            // do something
            try {
                JSONObject root = new JSONObject(s);
                final String title = root.getString("title");
                final JSONArray director = root.getJSONArray("director");

                final JSONArray actor = root.getJSONArray("actor");
                final JSONArray category = root.getJSONArray("category");
                final String runningTime = root.getString("runningTime");
                final String openDate = root.getString("openDate");


                Realm.init(MainActivity.this);
                Realm mRealm = Realm.getDefaultInstance();

                mRealm.executeTransaction(new Realm.Transaction() {

                    @Override
                    public void execute(Realm realm) {
                        MovieVO vo = realm.createObject(MovieVO.class);
                        ActorVO actorLists = new ActorVO();



                            for(int i=0; i<actor.length(); i++) {
                                ActorVO actorVO = new ActorVO();
                                try {
                                    actorVO.setActor(actor.getString(i));
                                } catch (JSONException e) {

                                }

                                vo.actorList.add(actorVO);

                            }
                           /* for(int i=0; i<category.length(); i++) {
                                vo.category.set(i,category.getString(i));
                            }
                            for(int i=0; i<director.length(); i++) {
                                vo.director.set(i,director.getString(i));
                            }*/

                            vo.setOpenDate(openDate);
                            vo.setRunningTime(runningTime);
                            vo.setTitle(title);
                            String result = "제목: " + vo.getTitle()
                                    +"\n개봉일: "+vo.getOpenDate()
                                    +"\n러닝타임: "+vo.getRunningTime()+"\n배우: ";


                            /*for(int i=0; i<)*/



                            tv_data.setText(result);







                    }
                });






                /*result += "\n-----------------------";

                result += "\n감독";
                for(int i = 0; i<director.length(); i++) {
                    result += "\n" + director.getString(i);
                }
                result += "\n-----------------------";

                result += "\n배우";
                for(int i = 0; i<actor.length(); i++) {
                    result += "\n" + actor.getString(i);
                }
                result += "\n-----------------------";
                result += "\n장르";
                for(int i = 0; i<category.length(); i++) {
                    result += "\n" + category.getString(i);
                }
                result += "\n-----------------------";
                result += "\nrunningTime: " + runningTime;
                result += "\n-----------------------";
                result += "\nopenDate: " + openDate;*/


            } catch (JSONException e) {

            }





            this.cancel(true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}