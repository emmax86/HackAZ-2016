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
            Route route = new Route("http://guarddog.stevex86.com/analyze");
            Request request = new Request(route, new Post());

            JsonBodyContent content = new JsonBodyContent(intent.getStringExtra("content"));

            request.setBodyContent(content);

            ConnectionHandler connectionHandler = new ConnectionHandler(request);

            Response response = connectionHandler.getResponse();

            Intent localIntent = new Intent(ActionConstants.ANALYZE_ACTION);
            JSONObject jsonObject = new JSONObject(response.getBodyContent().getOutputString());
            localIntent.putExtra("guess", jsonObject.getBoolean("guess"));
            localIntent.putExtra("content", jsonObject.getString("content"));
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
