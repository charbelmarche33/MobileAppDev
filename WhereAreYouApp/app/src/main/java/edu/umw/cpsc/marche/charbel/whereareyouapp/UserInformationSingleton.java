package edu.umw.cpsc.marche.charbel.whereareyouapp;

/**
 * Created by Charbel on 4/19/2018.
 */

public class UserInformationSingleton {

    private static final UserInformationSingleton USER_LOGGED_IN_SINGLETON_INSTANCE = new UserInformationSingleton();
    private boolean loggedIn = false;
    private String userEmail = null;
    private String username = null;

    //private constructor to avoid client applications to use constructor
    private UserInformationSingleton(){}

    public static UserInformationSingleton getInstance(){
        return USER_LOGGED_IN_SINGLETON_INSTANCE;
    }

    public boolean getLoggedIn(){
        return loggedIn;
    }

    public void setLoggedIn(boolean logIn){
        loggedIn = logIn;
    }

    public String getUserEmail(){
        return userEmail;
    }

    public void setUserEmail(String newEmail){
        userEmail = newEmail;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String name){
        username = name;
    }
}
