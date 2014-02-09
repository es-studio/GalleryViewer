package com.esstudio.gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.R.array;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.fedorvlasov.lazylist.ImageLoader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;

public class ElementItem {

    private String mUrl;
    private String mImageUrl;
    private String mImageName;
    private String mTitle;
    private String mBody;
    private String mComment;
    private ArrayList<String> mTorrent;
    private ArrayList<String> mYoutube;
    private String mSWF;
    private ArrayList<String> mMP4;
    private boolean onKeyword;

    public ElementItem() {
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String mBody) {
        this.mBody = mBody;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public boolean isOnKeyword() {
        return onKeyword;
    }

    public void setOnKeyword(boolean onKeyword) {
        this.onKeyword = onKeyword;
    }

    public ArrayList<String> getmTorrent() {
        return mTorrent;
    }

    public void setmTorrent(ArrayList<String> mTorrent) {
        this.mTorrent = mTorrent;
    }

    public ArrayList<String> getmYoutube() {
        return mYoutube;
    }

    public void setmYoutube(ArrayList<String> mYoutube) {
        this.mYoutube = mYoutube;
    }

    public String getmSWF() {
        return mSWF;
    }

    public void setmSWF(String mSWF) {
        this.mSWF = mSWF;
    }

    public ArrayList<String> getmMP4() {
        return mMP4;
    }

    public void setmMP4(ArrayList<String> mMP4) {
        this.mMP4 = mMP4;
    }
    
    public String getImageName(){
    	return this.mImageName;
    }
    
    public void setImageNmae(String name){
    	this.mImageName = name;
    }
    
    public String getNum(){
        String url = getUrl();
        String num = url.substring(url.indexOf("no=") + 3, url.length());
        return num;
    }
    
    public String getGalName(){
        String url = getUrl();
        String num = url.substring(url.indexOf("id=") + 3, url.indexOf("&"));
        return num;
    }
    
    @Override
    public String toString() {
    	return this.mTitle + " " + mUrl;
    }
    
    
}
