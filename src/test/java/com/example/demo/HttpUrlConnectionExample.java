package com.example.demo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUrlConnectionExample {

		private List<String> cookies;
		private HttpsURLConnection conn;
		private static final Logger LOG = LoggerFactory.getLogger(HttpUrlConnectionExample.class);
		private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.160 Safari/537.36 Edg/100.0.100.0";

		public void main() throws Exception {

			String url = "https://account.habr.com/login/?state=b19ae298dac772ddbe1784898b43512f&consumer=habr&hl=ru_RU";
			String habr = "https://habr.com/ru/users/Squzik/";
			HttpUrlConnectionExample http = new HttpUrlConnectionExample();
			CookieHandler.setDefault(new CookieManager());
			String page = http.GetPageContent(url);
			System.out.println(page);
			String postParams = http.getFormParams(page, "Lucaspw@yandex.ru", "Lucaspw123123");
			http.sendPost(url, postParams);
			String result = http.GetPageContent(habr);
			System.out.println(result);
		}

		private void sendPost(String url, String postParams) throws Exception {

			URL obj = new URL(url);
			conn = (HttpsURLConnection) obj.openConnection();
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Host", "account.habr.com");
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
			conn.setRequestProperty("Accept-Language", " ru,en;q=0.9");
			for (String cookie : this.cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Referer", "https://habr.com/ru/all/");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
			conn.setDoOutput(true);
			conn.setDoInput(true);

			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(postParams);
			wr.flush();
			wr.close();

			int responseCode = conn.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + postParams);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in =
					new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		}

		private String GetPageContent(String url) throws Exception {

			URL obj = new URL(url);
			conn = (HttpsURLConnection) obj.openConnection();
			conn.setRequestMethod("GET");
			conn.setUseCaches(false);
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			if (cookies != null) {
				for (String cookie : this.cookies) {
					conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
				}
			}
			int responseCode = conn.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in =
					new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			setCookies(conn.getHeaderFields().get("Set-Cookie"));

			LOG.info(response.toString());
			return response.toString();

		}

		public String getFormParams(String html, String username, String password)
				throws UnsupportedEncodingException {

			System.out.println("Extracting form's data...");

			Document doc = Jsoup.parse(html);

			Element loginform = doc.getElementById("login_form");
			Elements inputElements = loginform.getElementsByTag("input");
			List<String> paramList = new ArrayList<String>();
			for (Element inputElement : inputElements) {
				String key = inputElement.attr("name");
				String value = inputElement.attr("value");
				System.out.println("key = " + key + " , val = " + value);
				if (key.equals("email_field"))
					value = username;
				else if (key.equals("password_field"))
					value = password;
				paramList.add(key + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8));
			}

			StringBuilder result = new StringBuilder();
			for (String param : paramList) {
				if (result.length() == 0) {
					result.append(param);
				} else {
					result.append("&").append(param);
				}
			}
			return result.toString();
		}

		public void setCookies(List<String> cookies) {
			this.cookies = cookies;
		}

	}