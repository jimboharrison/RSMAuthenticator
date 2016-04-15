package com.rsm.rsmauthenticator;

/**
 * Created by James on 14/04/2016.
 */
public class AccessRequest{
    public int RequestId;
    public String RequestTime;
    public String Otp;
    public String AppName;
    public Boolean IsAwaitingResponse;
    public Boolean IsExpired;

    public AccessRequest(int id, String time, String otp, String appname, boolean isAwait, boolean isExpired){
        this.RequestId = id;
        this.RequestTime = time;
        this.Otp = otp;
        this.AppName = appname;
        this.IsAwaitingResponse = isAwait;
        this.IsExpired = isExpired;
    }

    public String toString(){
        return this.AppName + ": " + this.Otp  + " " + this.RequestTime;
    }
}
