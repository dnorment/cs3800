import java.io.*; 
import java.net.*; 

class TCPServer implements Runnable
{ 
	protected Socket echoSocket = null;

	public TCPServer(Socket sck)
	{
		echoSocket = sck;
	}

	public void run()
	{
		try
		{
			//setup streams
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.echoSocket.getInputStream())); 
			DataOutputStream  outToClient = new DataOutputStream(this.echoSocket.getOutputStream()); 

			//process input and send
			String clientSentence = inFromClient.readLine(); 
			String capitalizedSentence = clientSentence.toUpperCase() + '\n'; 
			outToClient.writeBytes(capitalizedSentence); 
		} catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static void main(String argv[]) throws IOException
	{ 
		ServerSocket welcomeSocket = new ServerSocket(6789); 

		while(true) { 

			Socket connectionSocket = welcomeSocket.accept();
			//create thread to service the new connection
			Thread t1 = new Thread(new TCPServer(connectionSocket));
			t1.start();

		}
    }
} 
 