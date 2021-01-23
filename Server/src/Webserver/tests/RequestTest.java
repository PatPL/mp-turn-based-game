package Webserver.tests;

import Webserver.Request;
import org.junit.Assert;

public class RequestTest {
    
    @org.junit.Test
    public void invalidRequestTest_EmptyRequest () {
        boolean success;
        
        try {
            new Request ("");
            success = true;
        } catch (Exception e) {
            success = false;
        }
        
        assert (!success);
    }
    
    @org.junit.Test
    public void invalidRequestTest_InvalidRequestLine () {
        boolean success;
        
        try {
            new Request (String.join ("\r\n",
                "GET/random HTTP/1.1"
            ));
            success = true;
        } catch (Exception e) {
            success = false;
        }
        
        assert (!success);
    }
    
    @org.junit.Test
    public void invalidRequestTest_MissingRequestLine () {
        boolean success;
        
        try {
            new Request (String.join ("\r\n",
                "Accept: */*",
                "Host: example.com",
                "",
                "body body body"
            ));
            success = true;
        } catch (Exception e) {
            success = false;
        }
        
        assert (!success);
    }
    
    @org.junit.Test
    public void invalidRequestTest_RandomText () {
        boolean success;
        
        try {
            new Request (String.join ("\r\n",
                "",
                "",
                "",
                "OQQCGZVKCAOJOEBM",
                "CDISBHSQMPQHJENR",
                "",
                "                ",
                "TSEWITHNUSHFUTVA",
                "JFLSWYHSVUKAPAAB",
                "",
                "",
                "",
                "GMRIIGLRWCXTLLEP",
                "XSMUHGQCVAMBJXVR",
                "",
                ""
            ));
            success = true;
        } catch (Exception e) {
            success = false;
        }
        
        assert (!success);
    }
    
    @org.junit.Test
    public void validRequestTest_OnlyRequestLine () throws Exception {
        Request a = new Request (String.join ("\r\n",
            "GET / HTTP/1.1"
        ));
        
        Request b = new Request ();
        b.method = "GET";
        b.URI = "/";
        b.HTTPVersion = "HTTP/1.1";
        
        Assert.assertEquals (a, b);
    }
    
    @org.junit.Test
    public void validRequestTest_RegularRequest () throws Exception {
        Request a = new Request (String.join ("\r\n",
            "POST /list.php HTTP/2",
            "Accept: */*",
            "Host: example.com",
            "",
            "body body body"
        ));
        
        Request b = new Request ();
        b.method = "POST";
        b.URI = "/list.php";
        b.HTTPVersion = "HTTP/2";
        b.headers.put ("Accept", "*/*");
        b.headers.put ("Host", "example.com");
        b.body = "body body body";
        
        Assert.assertEquals (a, b);
    }
    
    @org.junit.Test
    public void validRequestTest_ShortRequest () throws Exception {
        Request a = new Request (String.join ("\r\n",
            "DELETE /user/abcabc HTTP/1.1",
            "",
            "bbbb bbbb",
            "hfg fhfhfh"
        ));
        
        Request b = new Request ();
        b.method = "DELETE";
        b.URI = "/user/abcabc";
        b.HTTPVersion = "HTTP/1.1";
        b.body = "bbbb bbbb\r\nhfg fhfhfh";
        
        Assert.assertEquals (a, b);
    }
    
    @org.junit.Test
    public void validRequestTest_ExcessiveWhitespace () throws Exception {
        Request a = new Request (String.join ("\r\n",
            "",
            "",
            "",
            "",
            "",
            "",
            "TRACE /feed HTTP/1.1",
            "",
            "",
            "",
            "",
            "a",
            "",
            "",
            "",
            "b",
            "",
            ""
        ));
        
        Request b = new Request ();
        b.method = "TRACE";
        b.URI = "/feed";
        b.HTTPVersion = "HTTP/1.1";
        b.body = "a\r\n\r\n\r\n\r\nb";
        
        Assert.assertEquals (a, b);
    }
    
    @org.junit.Test
    public void validRequestTest_NoBody () throws Exception {
        Request a = new Request (String.join ("\r\n",
            "OPTIONS /api/test/exampleendpoint HTTP/1234",
            "Accept: application/json",
            "Host: website.org"
        ));
        
        Request b = new Request ();
        b.method = "OPTIONS";
        b.URI = "/api/test/exampleendpoint";
        b.HTTPVersion = "HTTP/1234";
        b.headers.put ("Accept", "application/json");
        b.headers.put ("Host", "website.org");
        
        Assert.assertEquals (a, b);
    }
    
    @org.junit.Test
    public void toStringRequestTest_OnlyRequestLine () throws Exception {
        String rawRequest = String.join ("\r\n",
            "GET / HTTP/1.1"
        );
        
        Assert.assertEquals (rawRequest, new Request (rawRequest).toString ());
    }
    
    @org.junit.Test
    public void toStringRequestTest_RegularRequest () throws Exception {
        String rawRequest = String.join ("\r\n",
            "POST /list.php HTTP/2",
            "Accept: */*",
            "Host: example.com",
            "",
            "body body body"
        );
        
        Assert.assertEquals (rawRequest, new Request (rawRequest).toString ());
    }
    
    @org.junit.Test
    public void toStringRequestTest_ShortRequest () throws Exception {
        String rawRequest = String.join ("\r\n",
            "DELETE /user/abcabc HTTP/1.1",
            "",
            "bbbb bbbb",
            "hfg fhfhfh"
        );
        
        Assert.assertEquals (rawRequest, new Request (rawRequest).toString ());
    }
    
    @org.junit.Test
    public void toStringRequestTest_NoBody () throws Exception {
        String rawRequest = String.join ("\r\n",
            "OPTIONS /api/test/exampleendpoint HTTP/1234",
            "Accept: application/json",
            "Host: website.org"
        );
        
        Assert.assertEquals (rawRequest, new Request (rawRequest).toString ());
    }
    
}