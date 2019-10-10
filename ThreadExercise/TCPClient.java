import java.io.*; 
import java.net.*; 
class TCPClient implements Runnable { 

	public void run()
	{
		try
		{
			String sentence; 
			String modifiedSentence; 

			BufferedReader inFromUser = 
			new BufferedReader(new InputStreamReader(System.in)); 

			Socket clientSocket = new Socket("localhost", 6789); 

			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 

			sentence = inFromUser.readLine(); 

			outToServer.writeBytes(sentence + '\n'); 

			modifiedSentence = inFromServer.readLine(); 

			System.out.println("FROM SERVER: " + modifiedSentence); 

			clientSocket.close(); 
		} catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}

    public static void main(String argv[]) throws Exception 
    { 
		for (int i=0; i<10; i++)
		{
			TCPClient cli = new TCPClient();
			Thread t1 = new Thread(cli);
			t1.start();
		}
    } 
} 
