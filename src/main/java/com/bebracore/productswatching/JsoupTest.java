package com.bebracore.productswatching;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class JsoupTest {
	public static void main(String[] args) throws IOException {
		Document doc = Jsoup.parse(new File("C:/Users/jonti/Documents/testtttt.txt"));
		Element elem = doc.select("body").get(0);
		elem.select(".specificationTextTable").get(0);
	}
}
