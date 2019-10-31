import java.io.*;
import java.net.*;

public class GameThread implements Runnable
{
    private int gameNum;
    private Socket p1;
    private Socket p2;
    private BattleShipTable P1FBoard = new BattleShipTable(); //player 1's board of ships
    private BattleShipTable P1PBoard = new BattleShipTable(); //player 1's board of enemy ships
    private BattleShipTable P2FBoard = new BattleShipTable(); //player 2's board of ships
    private BattleShipTable P2PBoard = new BattleShipTable(); //player 2's board of enemy ships

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
            //setup streams and readers for client communication
            DataOutputStream outToP1 = new DataOutputStream(this.p1.getOutputStream());
            DataOutputStream outToP2 = new DataOutputStream(this.p2.getOutputStream());
            ObjectOutputStream objToP1 = new ObjectOutputStream(outToP1);
            ObjectOutputStream objToP2 = new ObjectOutputStream(outToP2);
            ObjectInputStream objFromP1 = new ObjectInputStream(this.p1.getInputStream());
            ObjectInputStream objFromP2 = new ObjectInputStream(this.p2.getInputStream());
            
            //send MSG_REQUEST_INIT to players to initialize boards
            System.out.printf("Game #%d: Initializing boards%n", gameNum);
            Message init1 = new Message(1, P1FBoard, null, null, 1);
            Message init2 = new Message(1, P2FBoard, null, null, 2);
            objToP1.writeObject(init1);
            objToP1.flush();
            objToP2.writeObject(init2);
            objToP2.flush();

            //receive MSG_RESPONSE_INIT and set boards
            try
            {
                P1FBoard = ((Message)objFromP1.readObject()).Ftable;
                P2FBoard = ((Message)objFromP2.readObject()).Ftable;
            }
            catch (ClassNotFoundException e)
            {
                System.out.println(e.getMessage());
            }

        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

        System.out.printf("Game #%d: Closing game thread", gameNum);
    }
}