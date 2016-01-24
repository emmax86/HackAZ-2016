package com.bramblellc.myapplication.data;

import java.util.ArrayList;
import java.util.Collection;

public class Globals {

    private static String token;
    private static ArrayList<String> contacts = new ArrayList<String>();
    public static int userCounter = 0;
    public static int contactsCounter = 0;

    public static void setToken(String token) {
        Globals.token = token;
    }

    public static String getToken() {
        return token;
    }

    public static void addContacts(Collection<String> contacts) {
        Globals.contacts.addAll(contacts);
    }

    public static ArrayList<String> getContacts() {
        return contacts;
    }

}
