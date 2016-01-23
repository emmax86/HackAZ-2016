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

public class LoginService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LoginService(String name) {
        super(name);
    }

    public LoginService() {
        this("LoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Route route = new Route("http://guarddog.stevex86.com/login");
            Request request = new Request(route, new Post());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", intent.getStringExtra("username"));
            jsonObject.put("password", intent.getStringExtra("password"));

            JsonBodyContent content = new JsonBodyContent(jsonObject.toString());

            request.setBodyContent(content);

            ConnectionHandler connectionHandler = new ConnectionHandler(request);

            Response response = connectionHandler.getResponse();

            Intent localIntent = new Intent(ActionConstants.LOGIN_ACTION);
            localIntent.putExtra("successful", response.getResponseCode() < 400);
            localIntent.putExtra("message", response.getBodyContent().getOutputString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }
        catch (JSONException e) {
            Log.d("Guard-Dog", "Ayy lmao, JSON Exception thrown");
        }

        catch (IOException e) {
            Log.d("Guard-Dog", "Ayy lmao, IOException thrown");
        }
    }

}
