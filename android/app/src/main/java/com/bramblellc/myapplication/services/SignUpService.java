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

public class SignUpService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SignUpService(String name) {
        super(name);
    }

    public SignUpService() {
        this("SignUpService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Route route = new Route("http://guarddog.stevex86.com/register");
            Request request = new Request(route, new Post());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", intent.getStringExtra("username"));
            jsonObject.put("phone_number", intent.getStringExtra("phone_number"));
            jsonObject.put("password", intent.getStringExtra("password"));

            JsonBodyContent content = new JsonBodyContent(jsonObject.toString());

            request.setBodyContent(content);

            ConnectionHandler connectionHandler = new ConnectionHandler(request);

            Response response = connectionHandler.getResponse();

            Intent localIntent = new Intent(ActionConstants.REGISTER_ACTION);
            if (response.getResponseCode() < 400) {
                JSONObject responseObject = new JSONObject(response.getBodyContent().getOutputString());
                localIntent.putExtra("token", responseObject.getString("token"));
            }
            else {
                localIntent.putExtra("message", response.getBodyContent().getOutputString());
            }
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
