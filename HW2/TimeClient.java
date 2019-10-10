import java.net.*;
import java.io.*;

public class TimeClient
{
	private Socket socket = null;
	private DataInputStream input = null;
	private String datetime = null;

	public String getDateTime() throws IOException
	{
		while (datetime == null)
		{
			socket = new Socket("time.nist.gov", 13); //open socket with NIST

			input = new DataInputStream(socket.getInputStream());
			BufferedReader rd = new BufferedReader(new InputStreamReader(input));

			rd.readLine(); //clear first line (empty)
			datetime = rd.readLine(); //set next line to datetime
			System.out.println("TimeClient: " + datetime);

			socket.close(); //close socket with NIST
		}
		return datetime;
	}

	public TimeClient()
	{

	}
}
