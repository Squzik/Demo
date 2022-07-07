package com.example.demo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HttpUrlConnectionExample {

		private List<String> cookies;
		private HttpsURLConnection conn;

		private final String USER_AGENT = "Mozilla/5.0";

		public void main() throws Exception {

			String url = "https://askubuntu.com/users/login";
			String gmail = "https://askubuntu.com/topbar/inbox?_=1657132214356";

			HttpUrlConnectionExample http = new HttpUrlConnectionExample();


			CookieHandler.setDefault(new CookieManager());

			String page = http.GetPageContent(url);
			System.out.println(page);
			String postParams = http.getFormParams(page, "Lucaspw@yandex.ru", "iy5-X34-mZ2-gZY");


			http.sendPost(url, postParams);


			String result = http.GetPageContent(gmail);
			System.out.println(result);
		}

		private void sendPost(String url, String postParams) throws Exception {

			URL obj = new URL(url);
			conn = (HttpsURLConnection) obj.openConnection();


			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Host", "accounts.google.com");
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			for (String cookie : this.cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Referer", "https://accounts.google.com/ServiceLoginAuth");
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
			StringBuffer response = new StringBuffer();

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
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			setCookies(conn.getHeaderFields().get("Set-Cookie"));

			return response.toString();

		}

		public String getFormParams(String html, String username, String password)
				throws UnsupportedEncodingException {

			System.out.println("Extracting form's data...");

			Document doc = Jsoup.parse(html);

			Element loginform = doc.getElementById("login-form");
			Elements inputElements = loginform.getElementsByTag("input");
			List<String> paramList = new ArrayList<String>();
			for (Element inputElement : inputElements) {
				String key = inputElement.attr("name");
				String value = inputElement.attr("value");
				System.out.println("key = " + key + " , val = " + value);
				if (key.equals("email"))
					value = username;
				else if (key.equals("password"))
					value = password;
				paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
			}

			StringBuilder result = new StringBuilder();
			for (String param : paramList) {
				if (result.length() == 0) {
					result.append(param);
				} else {
					result.append("&" + param);
				}
			}
			return result.toString();
		}

		public List<String> getCookies() {
			return cookies;
		}

		public void setCookies(List<String> cookies) {
			this.cookies = cookies;
		}

	}