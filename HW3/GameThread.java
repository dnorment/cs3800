import java.io.*;
import java.net.*;

public class GameThread implements Runnable
{
    private int gameNum;
    private Socket p1;
    private Socket p2;
    private BattleShipTable P1FBoard; //player 1's board of ships
    private BattleShipTable P1PBoard; //player 1's board of enemy ships
    private BattleShipTable P2FBoard; //player 2's board of ships
    private BattleShipTable P2PBoard; //player 2's board of enemy ships

    public GameThread(int gameNum, Socket p1, Socket p2) throws IOException
    {
        this.gameNum = gameNum;
        this.p1 = p1;
        this.p2 = p2;
    }

    public void run()
    {
        try
        {
            BufferedReader inFromP1 = new BufferedReader(new InputStreamReader(this.p1.getInputStream()));
            BufferedReader inFromP2 = new BufferedReader(new InputStreamReader(this.p2.getInputStream()));
            DataOutputStream outToP1 = new DataOutputStream(this.p1.getOutputStream());
            DataOutputStream outToP2 = new DataOutputStream(this.p2.getOutputStream());

            outToP1.writeBytes("HI P1\n");
            outToP1.flush();
            System.out.println("Greeting P1");
            outToP2.writeBytes("HI P2\n");
            outToP2.flush();
            System.out.println("Greeting P2");
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

        System.out.printf("Game #%d: Closing game thread", gameNum);
    }
}