import java.net.*;
import java.io.*;
import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class Server
{
	private Socket socket = null;
	private ServerSocket server = null;
	private DataInputStream in	 = null;
	private DataOutputStream out = null;
	private TimeClient tc = new TimeClient();

	public Server() throws IOException
	{
		//open server socket
		server = new ServerSocket(5000);
		System.out.println("Listening on port 5000");

		while(true) { //accept any number of requests
			//blocking call to accept HTTP request
			final Socket client = server.accept();

			//parse date returned from TimeClient
			String[] tcdt = tc.getDateTime().split(" ");

			//hold datetimes of 3 zones (GMT+0, EST-5, PST-8)
			String off = "20" + tcdt[1] + "T" + tcdt[2] + "+00:00";
			OffsetDateTime datetime = OffsetDateTime.parse(off);
			//format times in zone
			String gmt = "GMT Date/Time: " + datetime.format(DateTimeFormatter.ofPattern("MM/dd/yy hh:mm a"));
			String est = "EST Date/Time: " + datetime.plusHours(-5).format(DateTimeFormatter.ofPattern("MM/dd/yy hh:mm a"));
			String pst = "PST Date/Time: " + datetime.plusHours(-8).format(DateTimeFormatter.ofPattern("MM/dd/yy hh:mm a"));

			//read request from client
			in = new DataInputStream(client.getInputStream());
			BufferedReader rd = new BufferedReader(new InputStreamReader(in));
			String line = rd.readLine();
			System.out.println("Connection: " + line);

			//parse requested URL to fetch
			String req = line.replace("GET ", "");
			req = req.replace(" HTTP/1.1", "");

			//initialize output to client, http 200 header
			out = new DataOutputStream(client.getOutputStream());
			String response = "HTTP/1.1 200 OK\r\n\r\n";
			out.write(response.getBytes("UTF-8"));
			out.write("<title>Output from server</title>".getBytes("UTF-8"));

			//respond to client request
			switch (req)
			{
				case "/favicon.ico":
					System.out.println("Ignoring icon request");
					break;
				case "/time":
				case "/time?zone=all":
					response = "<h1>"+gmt+"</h1><h1>"+est+"</h1><h1>"+pst+"</h1>";
					System.out.println("Sending client each time");
					break;
				case "/time?zone=gmt":
					response = "<h1>"+gmt+"</h1>";
					System.out.println("Sending client GMT time");
					break;
				case "/time?zone=est":
					response = "<h1>"+est+"</h1>";
					System.out.println("Sending client EST time");
					break;
				case "/time?zone=pst":
					response = "<h1>"+pst+"</h1>";
					System.out.println("Sending client PST time");
					break;
				default:
					response = "<h1>Invalid request</h1>";
					System.out.println("Sending client invalid request message");
					break;
			}
			out.write(response.getBytes("UTF-8"));

			client.close();
			System.out.println();
		}
	}

	public static void main(String args[]) throws IOException
	{
		Server server = new Server();
	}
}
