import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    public static void main(String[] args) throws IOException
    {
        Scanner kb = new Scanner(System.in);
        Socket clientSocket = new Socket("localhost", 5000);

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
        System.out.println(inFromServer.readLine());
    }
}
