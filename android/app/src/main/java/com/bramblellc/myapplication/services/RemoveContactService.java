package com.bramblellc.myapplication.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bramblellc.myapplication.data.Globals;
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
import java.util.ArrayList;

public class RemoveContactService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RemoveContactService(String name) {
        super(name);
    }

    public RemoveContactService() {
        this("RemoveContactService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Route route = new Route("http://guarddog.stevex86.com/remove_contact");
            Request request = new Request(route, new Post());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", intent.getStringExtra("token"));
            jsonObject.put("phone_number", intent.getStringExtra("phone_number"));

            JsonBodyContent content = new JsonBodyContent(jsonObject.toString());

            request.setBodyContent(content);

            ConnectionHandler connectionHandler = new ConnectionHandler(request);

            Response response = connectionHandler.getResponse();
            JSONArray jsonArray = new JSONArray(response.getBodyContent().getOutputString());
            ArrayList<String> arrayList = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                arrayList.add(jsonArray.getString(i));
            }
            Globals.getContacts().clear();
            Globals.addContacts(arrayList);
        }
        catch (JSONException e) {
            Log.d("Guard-Dog", "Ayy lmao, JSON Exception thrown");
        }

        catch (IOException e) {
            Log.d("Guard-Dog", "Ayy lmao, IOException thrown");
        }
    }


}
