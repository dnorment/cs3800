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
            Message init1 = new Message(Message.MSG_REQUEST_INIT, P1FBoard, null, null, 1);
            Message init2 = new Message(Message.MSG_REQUEST_INIT, P2FBoard, null, null, 2);
            objToP1.reset();
            objToP1.writeObject(init1);
            objToP1.flush();
            objToP2.reset();
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
                System.out.printf("Game #%d: Requesting play from Player 1%n", gameNum);
                msg = new Message(Message.MSG_REQUEST_PLAY, P1FBoard, P1PBoard, null, 1);
                objToP1.reset();
                objToP1.writeObject(msg);
                objToP1.flush();
                //parse play from P1
                try
                {
                    msg = (Message)objFromP1.readObject();
                    System.out.printf("Game #%d: Received play from Player 1%n", gameNum);
                }
                catch (ClassNotFoundException e)
                {
                    System.out.println(e.getMessage());
                }
                int[] bomb = msg.getBomb();
                boolean hit = P2FBoard.bomb(bomb);
                if (hit)
                {
                    P1PBoard.hit(bomb);
                }
                else
                {
                    P1PBoard.miss(bomb);
                }
                //return to player if bomb hit, if ship sunk
                System.out.printf("Game #%d: Delivering hit success to Player 1%n", gameNum);
                msg = new Message(hit ? Message.MSG_REQUEST_BOMB_HIT : Message.MSG_REQUEST_BOMB_MISS, null, null, null, 1);
                objToP1.reset();
                objToP1.writeObject(msg);
                objToP1.flush();
                //if player 2 not dead from player 1's play
                if (P2FBoard.isAlive())
                {
                    //request play from P2
                    System.out.printf("Game #%d: Requesting play from Player 2%n", gameNum);
                    msg = new Message(Message.MSG_REQUEST_PLAY, P2FBoard, P2PBoard, null, 2); //MSG_REQUEST_PLAY
                    objToP2.reset();
                    objToP2.writeObject(msg);
                    objToP2.flush();
                    //parse play from P2
                    try
                    {
                        msg = (Message)objFromP2.readObject();
                        System.out.printf("Game #%d: Received play from Player 2%n", gameNum);
                    }
                    catch (ClassNotFoundException e)
                    {
                        System.out.println(e.getMessage());
                    }
                    bomb = msg.getBomb();
                    hit = P1FBoard.bomb(bomb);
                    if (hit)
                    {
                        P2PBoard.hit(bomb);
                    }
                    else
                    {
                        P2PBoard.miss(bomb);
                    }
                    //return to play if bomb him, if ship sunk
                    System.out.printf("Game #%d: Delivering hit success to Player 2%n", gameNum);
                    msg = new Message(hit ? Message.MSG_REQUEST_BOMB_HIT : Message.MSG_REQUEST_BOMB_MISS, null, null, null, 2);
                    objToP2.reset();
                    objToP2.writeObject(msg);
                    objToP2.flush();
                }
            }

            //send game over/game win to correct players
            if (P1FBoard.isAlive())
            {
                //P1 wins, P2 loses
                System.out.printf("Game #%d: Delivering win message to Player 1%n", gameNum);
                msg = new Message(Message.MSG_REQUEST_GAME_WIN, null, null, null, 1); //P1 wins
                objToP1.reset();
                objToP1.writeObject(msg);
                objToP1.flush();
                System.out.printf("Game #%d: Delivering loss message to Player 2%n", gameNum);
                msg = new Message(Message.MSG_REQUEST_GAME_OVER, null, null, null, 2); //P2 loses
                objToP2.reset();
                objToP2.writeObject(msg);
                objToP2.flush();
            }
            else
            {
                //P1 loses, P2 wins
                System.out.printf("Game #%d: Delivering loss message to Player 1%n", gameNum);
                msg = new Message(Message.MSG_REQUEST_GAME_OVER, null, null, null, 1); //P1 loses
                objToP1.reset();
                objToP1.writeObject(msg);
                objToP1.flush();
                System.out.printf("Game #%d: Delivering win message to Player 2%n", gameNum);
                msg = new Message(Message.MSG_REQUEST_GAME_WIN, null, null, null, 2); //P2 wins
                objToP2.reset();
                objToP2.writeObject(msg);
                objToP2.flush();
            }

            //close GameThread and streams/Sockets
            System.out.printf("Game #%d: Closing game thread%n", gameNum);
            p1.close();
            p2.close();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
}