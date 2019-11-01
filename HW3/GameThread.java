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

            //begin game loop
            Message msg = null;
            while (P1FBoard.isAlive() && P2FBoard.isAlive())
            {
                //request play from P1
                msg = new Message(3, P1FBoard, P1PBoard, null, 1); //MSG_REQUEST_PLAY
                objToP1.writeObject(msg);
                objToP1.flush();
                //parse play from P1
                try
                {
                    msg = (Message)objFromP1.readObject();
                }
                catch (ClassNotFoundException e)
                {
                    System.out.println(e.getMessage());
                }
                int[] bomb = msg.getBomb();
                boolean hit = P2FBoard.bomb(bomb);
                P1PBoard.table[bomb[0]][bomb[1]] = hit ? BattleShipTable.HIT_SYMBOL : BattleShipTable.MISS_SYMBOL;
                //return to player if bomb hit, if ship sunk
                msg = new Message(hit ? 8 : 9, null, null, null, 1);
                objToP1.writeObject(msg);
                objToP1.flush();
                //if player 2 not dead from player 1's play
                if (P2FBoard.isAlive())
                {
                    //request play from P2
                    msg = new Message(3, P2FBoard, P2PBoard, null, 2); //MSG_REQUEST_PLAY
                    objToP2.writeObject(msg);
                    objToP2.flush();
                    //parse play from P2
                    try
                    {
                        msg = (Message)objFromP2.readObject();
                    }
                    catch (ClassNotFoundException e)
                    {
                        System.out.println(e.getMessage());
                    }
                    bomb = msg.getBomb();
                    hit = P1FBoard.bomb(bomb);
                    P2PBoard.table[bomb[0]][bomb[1]] = hit ? BattleShipTable.HIT_SYMBOL : BattleShipTable.MISS_SYMBOL;
                    //return to play if bomb him, if ship sunk
                    msg = new Message(hit ? 8 : 9, null, null, null, 2);
                    objToP2.writeObject(msg);
                    objToP2.flush();
                }
            }

            //send game over/game win to correct players
            if (P1FBoard.isAlive())
            {
                //P1 wins, P2 loses
                msg = new Message(6, null, null, null, 1); //P1 wins
                objToP1.writeObject(msg);
                objToP1.flush();
                msg = new Message(5, null, null, null, 2); //P2 loses
                objToP2.writeObject(msg);
                objToP2.flush();
            }
            else
            {
                //P1 loses, P2 wins
                msg = new Message(5, null, null, null, 1); //P1 loses
                objToP1.writeObject(msg);
                objToP1.flush();
                msg = new Message(6, null, null, null, 2); //P2 wins
                objToP2.writeObject(msg);
                objToP2.flush();
                }

            //close GameThread and streams/Sockets
            outToP1.close();
            outToP2.close();
            objToP1.close();
            objToP2.close();
            objFromP1.close();
            objFromP2.close();
            p1.close();
            p2.close();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

        System.out.printf("Game #%d: Closing game thread", gameNum);
    }
}