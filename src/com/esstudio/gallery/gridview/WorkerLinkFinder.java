package com.esstudio.gallery.gridview;

import com.esstudio.gallery.MainActivity;
import com.esstudio.gallery.util.log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WorkerLinkFinder {

	public WorkerLinkFinder() {

	}

	public static void find(String url, String sb) {

		Document doc = Jsoup.parse(sb.toString());
		Elements contentElement = doc.select("div.m_contents");
		Elements imageUrl = contentElement.select("img[src]");
		Elements titleEl = doc.select("p.contens_title");
		Elements bodyEl = doc.select("div#memo_img");
		Elements commentEl = doc.select("div.m_list_text");

		// no contents
		if (contentElement.isEmpty()) {
			log.out("No Contents : " + url);
			return;
		}

		String contentAll = titleEl.text() + " " + bodyEl.html() + " "
				+ commentEl.text();

		// match keyword check
		boolean keyword = Keyword(contentAll);
		// boolean keyword = Keyword(titleEl.text() + bodyEl.text()
		// + commentEl.text());

		// find magnet
		ArrayList<String> magnets = findMagnet(contentAll);
		ArrayList<String> youtube = findYoutube(contentAll);
		ArrayList<String> mp4 = findMp4(contentAll);

		if (magnets.size() + youtube.size() + mp4.size() > 0)
			keyword = true;

		// keyword
		if (imageUrl.isEmpty() && keyword == true) {

			ElementItem el = new ElementItem();
			el.setTitle(titleEl.text());
			el.setBody(bodyEl.text());
			el.setmComment(commentEl.text());
			el.setUrl(url);
			el.setOnKeyword(keyword);
			el.setImageUrl("");
			el.setmTorrent(magnets);
			el.setmYoutube(youtube);
			el.setmMP4(mp4);

			MainActivity.getInstance().addElementItem(el);

		} else {

			// multiple image
			for (Iterator iterator = imageUrl.iterator(); iterator.hasNext();) {

				ElementItem el = new ElementItem();
				Element element = (Element) iterator.next();
				String src = element.attr("src");

				src = src.replace("//dcimg1", "//image");

				el.setTitle(titleEl.text());
				el.setImageUrl(src);
				el.setBody(bodyEl.text());
				el.setmComment(commentEl.text());
				el.setUrl(url);
				el.setOnKeyword(keyword);
				el.setmTorrent(magnets);
				el.setmYoutube(youtube);
				el.setmMP4(mp4);

				MainActivity.getInstance().addElementItem(el);
			}
		}


	}

	public static boolean Keyword(String str) {
		// System.out.println(str);
		if (str == null) {
			str = "";
		}
		String tmp = "";
		Pattern p1 = Pattern.compile(
		// "(07|08|09|10|11|12|13|14|15).?(01|02|03|04|05|06|07|08|09|10|11|12).?[0-9]{2}[^0-9]|"
		// // 날짜 검
				"마그넷|자석|magnet|gnet|urn|torrent|torr|rent|기차|직캠|픽짜|토렝|토랭|토렌트|토런트|토렌|토런|"
						+ "flvs.daum.net|www.youtube.com|"
						+ "스티큐브|꾸러미|\\.avi|\\.mkv|\\.tp|\\.ts|\\.mp4|\\.flv|\\.mov|\\.wmv|\\.swf");

		// magnet:?xt=urn:btih:
		// Pattern p1 =
		// Pattern.compile("마그넷|자석|magnet|torrent|torr|rent|기차|직캠|픽짜|토렝|토렌트|토런트|토렌|토런|스티큐브|꾸러미|avi|mkv|tp|ts|mp4");
		Matcher m1 = p1.matcher(str);

		StringBuffer stringBuffer = new StringBuffer();
		while (m1.find()) {
			tmp = m1.group();
			stringBuffer.append(tmp + ", ");
		}

		return !tmp.equals("");
	}

	public static ArrayList<String> findMagnet(String str) {

		ArrayList<String> links = new ArrayList<String>();
		if (str == null) {
			str = "";
		}

		String tmp = "";
        Pattern p1 = Pattern.compile("urn:\\w*:\\w*\\s|urn:\\w*:\\w*\\&|urn:\\w*:\\w{40}|urn:\\w*:\\w{32}");


        Matcher m1 = p1.matcher(str);

		while (m1.find()) {
			tmp = m1.group();
			links.add("magnet:?xt=" + tmp.trim());
			log.out("magnet = " + tmp);
		}

		return links;
	}

	public static ArrayList<String> findYoutube(String str) {

		ArrayList<String> links = new ArrayList<String>();
		if (str == null) {
			str = "";
		}

		String tmp = "";
		Pattern p1 = Pattern
				.compile(""
						+ "//www.youtube.com/v/.{11}"
						+ "|"
						+ "//www.youtube.com/watch\\?v=[a-z0-9A-Z-_]*"
						+ "|"
						+ "//youtube.com/v/[a-z0-9A-Z-_]*"
						+ "|"
						+ "//www.youtube-nocookie.com/v/[a-z0-9A-Z-_]*"
						+ "|"
						+ "//www.youtube.com/watch\\?list=[a-z0-9A-Z-_&=]*\\&v=[a-z0-9A-Z-_&=]*");
		Matcher m1 = p1.matcher(str);

		while (m1.find()) {
			tmp = m1.group();
			links.add("http:" + tmp.trim());
			log.out("youtube = " + tmp);
		}

		return links;
	}

	public static ArrayList<String> findMp4(String str) {

		ArrayList<String> links = new ArrayList<String>();
		if (str == null) {
			str = "";
		}

		String tmp = "";
		Pattern p1 = Pattern
				.compile(""
						+ "http://[^;|^\"]*\\.mp4|http://[^;|^\"]*\\.flv|http://mgnet.me/\\w*");
		Matcher m1 = p1.matcher(str);

		while (m1.find()) {
			tmp = m1.group();
			links.add(tmp.trim());
			System.out.println(tmp);
		}

		return links;
	}

}
