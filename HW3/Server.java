import java.io.*;
import java.net.*;

public class Server
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket server = new ServerSocket(5000);
        System.out.println("Listening on port 5000");

        int gameNum = 1;
        while(true)
        {
            //connect 2 players
            Socket playerOne = server.accept();
            System.out.printf("Game #%d: Accepted Player 1 connection%n", gameNum);
            Socket playerTwo = server.accept();
            System.out.printf("Game #%d: Accepted Player 2 connection%n", gameNum);
            
            //start game thread with 2 players
            GameThread game = new GameThread(gameNum, playerOne, playerTwo);
            Thread gameThread = new Thread(game);
            gameThread.start();
            gameNum++;
        }
    }
}