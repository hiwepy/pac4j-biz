package org.pac4j.core.ext;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TokenExample {

	public static void main(String[] args) throws Exception {

		System.out.println(StandardCharsets.UTF_8.name());
		
		System.out.println(URLEncoder.encode("V+yUhswUbJ2FaDOH6gumdTbNUyOOfD7+/AXBuiQo8JyzxTaHlFJJRxb+436eay0oMvA16WoUDIdi72RoaKRDJSFUkn9eaxkYQBXwkC0xC2HC4r8AQlYmg4SYPMjoTOWdiKZum+5ToE8PfEX+8V8E4lBAxYg1uva3JHBi0/J85ak=",StandardCharsets.UTF_8.name()));
		
		System.out.println(URLEncoder.encode("ST2ACrGrKv/U4aRp8gA9qD+vIqQB/+XZ3n81TwErzo/3ymjPh+YgyIyKEN7C8S4A", StandardCharsets.UTF_8.name()));
		
		
	}
	
}
