package com.cyzest.ogosbourne.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class OpenGraph {

    private static final String FAKE_USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6)"
                    + " AppleWebKit/537.36 (KHTML, like Gecko)"
                    + " Chrome/69.0.3497.100 Safari/537.36";

    private static final int MAX_REDIRECT_TRY_COUNT = 2;

    public static Map<String, String> getOpenGraphByUrl(String url) {

        Map<String, String> ogMap = new LinkedHashMap<>();

        HttpURLConnection urlConnection = null;

        try {

            urlConnection = getHttpUrlConnection(url);

            int statusCode = urlConnection.getResponseCode();

            int redirectTryCount = 0;

            while (isRedirect(statusCode) && redirectTryCount < MAX_REDIRECT_TRY_COUNT) {

                String redirectUrl = urlConnection.getHeaderField("Location");

                if (!isNotEmpty(redirectUrl)) {
                    break;
                }

                urlConnection.disconnect();

                urlConnection = getHttpUrlConnection(redirectUrl);

                statusCode = urlConnection.getResponseCode();

                redirectTryCount++;
            }

            if (statusCode == 200) {

                StringBuilder headContentsBuilder = new StringBuilder();

                Charset charset = getConnectionCharset(urlConnection);

                try (InputStream is = urlConnection.getInputStream();
                     BufferedReader dis = new BufferedReader(new InputStreamReader(is, charset))) {

                    String inputLine;

                    while ((inputLine = dis.readLine()) != null) {

                        if (inputLine.contains("</head>")) {
                            inputLine = inputLine.substring(0, inputLine.indexOf("</head>") + 7);
                            inputLine = inputLine.concat("<body></body></html>");
                            headContentsBuilder.append(inputLine).append("\r\n");
                            break;
                        }

                        headContentsBuilder.append(inputLine).append("\r\n");
                    }

                } finally {
                    urlConnection.disconnect();
                }

                String headContents = headContentsBuilder.toString();

                if (isNotEmpty(headContents)) {

                    Document parsedDocument = Jsoup.parse(headContents);

                    Optional.ofNullable(parsedDocument.select("title"))
                            .filter(elementsNotEmpty).map(Elements::first)
                            .ifPresent(element -> ogMap.put("title", element.ownText()));

                    Optional.ofNullable(parsedDocument.select("meta[name=description]"))
                            .filter(elementsNotEmpty).map(Elements::first)
                            .ifPresent(element -> ogMap.put("description", element.attr("content")));

                    Optional.ofNullable(parsedDocument.select("meta[property^=og]"))
                            .filter(elementsNotEmpty).ifPresent(elements -> elements.forEach(
                            element -> ogMap.put(element.attr("property"), element.attr("content"))));

                    Optional.ofNullable(parsedDocument.select("meta[name^=twitter]"))
                            .filter(elementsNotEmpty).ifPresent(elements -> elements.forEach(
                            element -> ogMap.put(element.attr("name"), element.attr("content"))));
                }
            }

        } catch (Throwable ex) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return ogMap;
    }

    private static final Predicate<Elements> elementsNotEmpty = elements -> !elements.isEmpty();

    private static HttpURLConnection getHttpUrlConnection(String url) throws Exception {

        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();

        urlConnection.setRequestProperty("User-Agent", FAKE_USER_AGENT);
        urlConnection.setConnectTimeout(1000);

        return urlConnection;
    }

    private static Charset getConnectionCharset(URLConnection connection) {

        String contentType = connection.getContentType();

        if (isNotEmpty(contentType)) {

            String charsetName = extractCharsetName(contentType.toLowerCase());

            if (isNotEmpty(charsetName)) {
                try {
                    return Charset.forName(charsetName);
                } catch (Throwable ex) {

                }
            }
        }

        return Charset.defaultCharset();
    }

    private static String extractCharsetName(String contentType) {

        String[] mediaTypes = contentType.split(":");

        if (mediaTypes.length > 0) {

            String[] params = mediaTypes[0].split(";");

            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset=")) {
                    return param.substring(8).trim();
                }
            }
        }

        return null;
    }

    private static boolean isNotEmpty(final String str) {
        return str != null && !str.isEmpty();
    }

    private static boolean isRedirect(int statusCode) {
        return statusCode / 100 == 3;
    }

}