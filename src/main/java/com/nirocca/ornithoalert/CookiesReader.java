package com.nirocca.ornithoalert;

import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CookiesReader {
    
    public BasicCookieStore readCookies() throws IOException {
        BasicCookieStore cookieStore = new BasicCookieStore();
        InputStream stream = MySightingsReader.class.getResourceAsStream("/cookies.txt");
        if (stream == null) stream =  MySightingsReader.class.getResourceAsStream("/resources/cookies.txt");
        List<String> cookies = IOUtils.readLines(stream);
        Pattern pattern = Pattern.compile("^(\\S+)\\s+\\S+\\s+(\\S+)\\s+\\S+\\s+((\\d+)\\s+)?(\\S+)\\s+(\\S+)$");
        for (String cookieLine : cookies) {
            if (cookieLine.startsWith("#")) continue;
            Matcher matcher = pattern.matcher(cookieLine);
            if (matcher.matches()) {
                BasicClientCookie cookie = new BasicClientCookie(matcher.group(5), matcher.group(6));
                cookie.setDomain(matcher.group(1));
                cookie.setPath(matcher.group(2));
                if (matcher.group(4) != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(1000 * Long.parseLong(matcher.group(4)));
                    cookie.setExpiryDate(cal.getTime());
                    cookie.setSecure(false);
                }
                cookieStore.addCookie(cookie);
            } else {
                System.out.println("Cookie entry doesn't match: " + cookieLine);
            }
        }

        return cookieStore;
    }

}
