package com.esstudio.gallery;

import com.esstudio.gallery.util.log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.jsoup.Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by EunSung on 14. 3. 8.
 */
public class HttpLogin {


    private static HttpLogin singleInstance;

    public static HttpLogin getInstance() {
        if (singleInstance == null) {
            singleInstance = new HttpLogin();
        }
        return singleInstance;
    }


    private BasicHttpContext localContext;

    public BasicHttpContext getCookie() {
        return localContext;
    }

    public void pclogin() throws IOException {


        HttpClient http = new DefaultHttpClient();
        HttpResponse response;
        HttpPost post = new HttpPost("http://dcid.dcinside.com/join/member_check.php");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("s_url", ""));
        params.add(new BasicNameValuePair("user_id", "anvil21x"));
        params.add(new BasicNameValuePair("password", "winterhall"));
        params.add(new BasicNameValuePair("x", "35"));
        params.add(new BasicNameValuePair("y", "16"));
        post.setEntity(new UrlEncodedFormEntity(params));

        post.setHeader("Host", "dcid.dcinside.com");
        post.setHeader("Connection", "keep-alive");
        post.setHeader("Cache-Control", "max-age=0");
        post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        post.setHeader("Origin", "http://dcid.dcinside.com");
        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setHeader("Referer", "http://dcid.dcinside.com/join/login.php");
        post.setHeader("Accept-Encoding", "gzip,deflate,sdch");
        post.setHeader("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");
        post.setHeader("Cookie", "ssl=N");

        localContext = new BasicHttpContext();
        BasicCookieStore cookieStore = new BasicCookieStore();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        http = new DefaultHttpClient();
        response = http.execute(post, localContext);
        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            // Set HttpGet
            System.out.println("ok");

            HttpEntity entity1 = response.getEntity();
            BufferedReader is = new BufferedReader(new InputStreamReader(entity1.getContent()));
            String line = "";
            while ((line = is.readLine()) != null) {
                System.out.println(line);
            }

        }

        HttpGet ddr = new HttpGet("http://gall.dcinside.com/index.php");
        ddr.setHeader("Host", "gall.dcinside.com");
        ddr.setHeader("Connection", "keep-alive");
        ddr.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        ddr.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        ddr.setHeader("Referer", "http://dcid.dcinside.com/join/login.php");
        ddr.setHeader("Accept-Encoding", "gzip,deflate,sdch");
        ddr.setHeader("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");

        http = new DefaultHttpClient();
        response = http.execute(ddr, localContext);
        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            // Set HttpGet
            System.out.println("ok");

            HttpEntity entity1 = response.getEntity();
            BufferedReader is = new BufferedReader(new InputStreamReader(entity1.getContent()));
            String line = "";
            while ((line = is.readLine()) != null) {
                System.out.println(line);
            }

        }

        ddr = new HttpGet("http://gall.dcinside.com/board/lists/?id=adult2");
        ddr.setHeader("Host", "gall.dcinside.com");
        ddr.setHeader("Connection", "keep-alive");
        ddr.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        ddr.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        ddr.setHeader("Referer", "http://dcid.dcinside.com/join/login.php");
        ddr.setHeader("Accept-Encoding", "gzip,deflate,sdch");
        ddr.setHeader("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");

        http = new DefaultHttpClient();
        response = http.execute(ddr, localContext);
        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            // Set HttpGet
            System.out.println("ok");

            HttpEntity entity1 = response.getEntity();
            BufferedReader is = new BufferedReader(new InputStreamReader(entity1.getContent()));
            String line = "";
            while ((line = is.readLine()) != null) {
                System.out.println(line);
            }

        }

    }

    public void mobileLogin() throws IOException {
        HttpClient http = new DefaultHttpClient();
        HttpResponse response;
        HttpPost post = new HttpPost("http://dcid.dcinside.com/join/mobile_login_ok.php");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", "anvil21x"));
        params.add(new BasicNameValuePair("user_pw", "winterhall"));
        params.add(new BasicNameValuePair("id_chk", "on"));
        params.add(new BasicNameValuePair("mode", ""));
        params.add(new BasicNameValuePair("id", ""));
        params.add(new BasicNameValuePair("r_url", "%02F"));
        post.setEntity(new UrlEncodedFormEntity(params));

        post.setHeader("Host", "dcid.dcinside.com");
        post.setHeader("Connection", "keep-alive");
        post.setHeader("Cache-Control", "max-age=0");
        post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        post.setHeader("Origin", "http://m.dcinside.com");
        post.setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166  Safari/535.19");
//        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setHeader("Referer", "http://m.dcinside.com/login.php?r_url=%2F");
        post.setHeader("Accept-Encoding", "gzip,deflate,sdch");
        post.setHeader("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");

        localContext = new BasicHttpContext();
        BasicCookieStore cookieStore = new BasicCookieStore();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        http = new DefaultHttpClient();
        response = http.execute(post, localContext);
        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            // Set HttpGet
            System.out.println("ok");

            HttpEntity entity1 = response.getEntity();
            BufferedReader is = new BufferedReader(new InputStreamReader(entity1.getContent()));
            String line = "";
            while ((line = is.readLine()) != null) {
                System.out.println(line);
            }

        }


        HttpGet ddr = new HttpGet("http://m.dcinside.com/list.php?id=adult2");
        ddr.setHeader("Host", "m.dcinside.com");
        ddr.setHeader("Connection", "keep-alive");
        ddr.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//        ddr.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        ddr.setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166  Safari/535.19");
        ddr.setHeader("Referer", "http://dcid.dcinside.com/join/mobile_login_ok.php");
        ddr.setHeader("Accept-Encoding", "deflate,sdch");
        ddr.setHeader("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");

        http = new DefaultHttpClient();
        response = http.execute(ddr, localContext);
        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            // Set HttpGet
            System.out.println("ok");

            HttpEntity entity1 = response.getEntity();
            BufferedReader is = new BufferedReader(new InputStreamReader(entity1.getContent()));
            String line = "";
            while ((line = is.readLine()) != null) {
                System.out.println(line);
            }

        }
    }

}
