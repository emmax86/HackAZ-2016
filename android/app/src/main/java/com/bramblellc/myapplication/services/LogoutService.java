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

public class LogoutService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LogoutService(String name) {
        super(name);
    }

    public LogoutService() {
        this("SignUpService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Route route = new Route("http://guarddog.stevex86.com/log_out");
            Request request = new Request(route, new Post());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", intent.getStringExtra("token"));

            JsonBodyContent content = new JsonBodyContent(jsonObject.toString());

            request.setBodyContent(content);

            ConnectionHandler connectionHandler = new ConnectionHandler(request);

            Response response = connectionHandler.getResponse();
        }
        catch (JSONException e) {
            Log.d("Guard-Dog", "Ayy lmao, JSON Exception thrown");
        }

        catch (IOException e) {
            Log.d("Guard-Dog", "Ayy lmao, IOException thrown");
        }
    }


}
