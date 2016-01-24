package com.bramblellc.myapplication.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.stevex86.napper.http.connection.ConnectionHandler;
import com.stevex86.napper.http.elements.content.JsonBodyContent;
import com.stevex86.napper.http.elements.method.Post;
import com.stevex86.napper.http.elements.route.Route;
import com.stevex86.napper.request.Request;
import com.stevex86.napper.response.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AnalyzeService extends IntentService {

    public AnalyzeService(String name) {
        super(name);
    }

    public AnalyzeService() {
        this("AnalyzeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Route route = new Route("http://guarddog.stevex86.com/classify");
            Request request = new Request(route, new Post());

            JSONObject jsonObject = new JSONObject();
            JSONArray batch_phone = new JSONArray(intent.getStringExtra("batch-phone"));
            JSONArray batch_watch = new JSONArray(intent.getStringExtra("batch-watch"));
            jsonObject.put("batch-phone", batch_phone);
            jsonObject.put("batch-watch", batch_watch);
            jsonObject.put("token", intent.getStringExtra("token"));

            JsonBodyContent content = new JsonBodyContent(jsonObject.toString());

            request.setBodyContent(content);

            ConnectionHandler connectionHandler = new ConnectionHandler(request);

            Response response = connectionHandler.getResponse();

            Intent localIntent = new Intent(ActionConstants.ANALYZE_ACTION);
            JSONObject responseJSON = new JSONObject(response.getBodyContent().getOutputString());
            localIntent.putExtra("guess", responseJSON.getBoolean("guess"));
            localIntent.putExtra("content", responseJSON.getJSONObject("content").toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d("Guard-Dog", "Ayy lmao, IOException thrown");
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d("Guard-Dog", "Ayy lmao, JSONException thrown");
        }
    }


}
