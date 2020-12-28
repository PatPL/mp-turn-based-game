package Webserver.tests;

import Webserver.Response;
import Webserver.enums.Status;
import org.junit.Assert;

public class ResponseTest {
	
	@org.junit.Test
	public void responseTest_EmptyResponse() {
		Response res = new Response();
		
		String a = res.toString();
		String b = String.join("\r\n",
			"HTTP/1.1 501 Not Implemented"
		);
		
		Assert.assertEquals(a, b);
	}
	
	@org.junit.Test
	public void responseTest_ChangedStatus() {
		Response res = new Response();
		res.setStatus(Status.ExpectationFailed_417);
		
		String a = res.toString();
		String b = String.join("\r\n",
			"HTTP/1.1 417 Expectation Failed"
		);
		
		Assert.assertEquals(a, b);
	}
	
	@org.junit.Test
	public void responseTest_CustomStatus() {
		Response res = new Response();
		res.setStatus(1234, "aa bb CC DD");
		
		String a = res.toString();
		String b = String.join("\r\n",
			"HTTP/1.1 1234 aa bb CC DD"
		);
		
		Assert.assertEquals(a, b);
	}
	
	@org.junit.Test
	public void responseTest_TextBody() {
		Response res = new Response();
		res.setStatus(Status.BadRequest_400);
		res.setBody("qqqqqqqqqqqqqqqqqqqqqqqqqqqqq", Response.BodyType.Text);
		
		String a = res.toString();
		String b = String.join("\r\n",
			"HTTP/1.1 400 Bad Request",
			"Content-Type: text/plain; charset=utf-8",
			"",
			"qqqqqqqqqqqqqqqqqqqqqqqqqqqqq"
		);
		
		Assert.assertEquals(a, b);
	}
	
	@org.junit.Test
	public void responseTest_HtmlBody() {
		Response res = new Response();
		res.setStatus(Status.BadRequest_400);
		res.setBody("<u>qqqqqqqqqqqqqqqqqqqqqqqqqqqqq</u>", Response.BodyType.HTML);
		
		String a = res.toString();
		String b = String.join("\r\n",
			"HTTP/1.1 400 Bad Request",
			"Content-Type: text/html; charset=utf-8",
			"",
			"<u>qqqqqqqqqqqqqqqqqqqqqqqqqqqqq</u>"
		);
		
		Assert.assertEquals(a, b);
	}
	
	@org.junit.Test
	public void responseTest_MultilineBody() {
		Response res = new Response();
		res.setStatus(Status.OK_200);
		res.setBody(String.join("\r\n",
			"<h1>Header</h1>",
			"<p>",
			"Paragraph contents, <b>bold</b>",
			"</p>"
		), Response.BodyType.HTML);
		
		String a = res.toString();
		String b = String.join("\r\n",
			"HTTP/1.1 200 OK",
			"Content-Type: text/html; charset=utf-8",
			"",
			"<h1>Header</h1>",
			"<p>",
			"Paragraph contents, <b>bold</b>",
			"</p>"
		);
		
		Assert.assertEquals(a, b);
	}
	
	@org.junit.Test
	public void responseTest_CustomHeaders() {
		Response res = new Response();
		res.setStatus(Status.Conflict_409);
		res.setHeader("test1", "value1");
		res.setHeader("test2", "value2");
		res.setHeader("test3", "value3");
		res.removeHeader("test2");
		
		res.setBody("test4: value4", Response.BodyType.Text);
		
		String a = res.toString();
		// Should order matter?
		// Check header order if this test keeps failing
		String b = String.join("\r\n",
			"HTTP/1.1 409 Conflict",
			"test3: value3",
			"test1: value1",
			"Content-Type: text/plain; charset=utf-8",
			"",
			"test4: value4"
		);
		
		Assert.assertEquals(a, b);
	}
	
	@org.junit.Test
	public void toStringResponseTest_CustomStatus() throws Exception {
		String rawResponse = String.join("\r\n",
			"HTTP/1.1 1234 aa bb CC DD"
		);
		
		Assert.assertEquals(rawResponse, new Response(rawResponse).toString());
	}
	
	@org.junit.Test
	public void toStringResponseTest_HtmlBody() throws Exception {
		String rawResponse = String.join("\r\n",
			"HTTP/1.1 400 Bad Request",
			"Content-Type: text/html; charset=utf-8",
			"",
			"<u>qqqqqqqqqqqqqqqqqqqqqqqqqqqqq</u>"
		);
		
		Assert.assertEquals(rawResponse, new Response(rawResponse).toString());
	}
	
	@org.junit.Test
	public void toStringResponseTest_MultilineBody() throws Exception {
		String rawResponse = String.join("\r\n",
			"HTTP/1.1 200 OK",
			"Content-Type: text/html; charset=utf-8",
			"",
			"<h1>Header</h1>",
			"<p>",
			"Paragraph contents, <b>bold</b>",
			"</p>"
		);
		
		Assert.assertEquals(rawResponse, new Response(rawResponse).toString());
	}
	
	@org.junit.Test
	public void toStringResponseTest_CustomHeaders() throws Exception {
		// Should order matter?
		// Check header order if this test keeps failing
		String rawResponse = String.join("\r\n",
			"HTTP/1.1 409 Conflict",
			"test3: value3",
			"test1: value1",
			"Content-Type: text/plain; charset=utf-8",
			"",
			"test4: value4"
		);
		
		Assert.assertEquals(rawResponse, new Response(rawResponse).toString());
	}
	
}