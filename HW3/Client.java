import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    public static void testBoard(BattleShipTable table)
    {
        table.insertAirCarrier("A0", "A1");
        table.insertDestroyer("C0", "C1");
        table.insertSubmarine("F0");
        System.out.println(table.toString());
    }

    public static void initBoard(BattleShipTable table, Scanner kb)
    {
        String[] loc;
        String locn;

        for (int i=0; i<2; i++)
        {
            System.out.println(table.toString());
            System.out.print("Place aircraft carrier (length 5): ");
            loc = kb.nextLine().split(",");
            while (!table.insertAirCarrier(loc[0], loc[1]))
            {
                System.out.println("Could not place ship, retrying: ");
                System.out.print("Place aircraft carrier (length 5): ");
                loc = kb.nextLine().split(",");
            }
        }

        for (int i=0; i<2; i++)
        {
            System.out.println(table.toString());
            System.out.print("Place destroyer (length 3): ");
            loc = kb.nextLine().split(",");
            while (!table.insertDestroyer(loc[0], loc[1]))
            {
                System.out.println("Could not place ship, retrying: ");
                System.out.print("Place destroyer (length 3): ");
                loc = kb.nextLine().split(",");
            }
        }

        for (int i=0; i<2; i++)
        {
            System.out.println(table.toString());
            System.out.print("Place submarine (length 1): ");
            locn = kb.nextLine();
            while (!table.insertSubmarine(locn))
            {
                System.out.println("Could not place ship, retrying: ");
                System.out.print("Place submarine (length 1): ");
                loc = kb.nextLine().split(",");
            }
        }
    }
    public static void main(String[] args) throws IOException
    {
        Scanner kb = new Scanner(System.in);
        Socket clientSocket = new Socket("localhost", 5000);
        System.out.println("Connected to server");
        boolean gameAlive = true;

        //while not dead, keep game connection alive
        ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
        ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        while(gameAlive)
        {
            //read message from server
            Message msg = null;
            try
            {
                msg = (Message)inFromServer.readObject();
            }
            catch (ClassNotFoundException e)
            {
                System.out.println(e.getMessage());
            }
            //read data from message
            int msgType = msg.getMsgType();
            int playerNum = msg.getPlayerNum();
            BattleShipTable FTable = msg.Ftable;
            BattleShipTable PTable = msg.Ptable;
            switch(msgType)
            {
                case Message.MSG_REQUEST_INIT: //initialize board
                    System.out.printf("Player %d, place your ships!%n", playerNum);
                    System.out.println("Example inputs: 'A3,A4' or 'B1,C1' or 'D2', must be adjacent spaces");

                    Client.initBoard(FTable, kb);

                    //send board to server
                    msg = new Message(Message.MSG_RESPONSE_INIT, FTable, null, null, playerNum);
                    outToServer.reset();
                    outToServer.writeObject(msg);
                    outToServer.flush();
                    break;
                case Message.MSG_REQUEST_PLAY: //get boards and send block to bomb
                    //print Pboard, Fboard
                    System.out.println("Opponent's field:");
                    System.out.println(PTable.toString());
                    System.out.println("My field:");
                    System.out.println(FTable.toString());
                    System.out.printf("Player %d, fire a bomb (ex. 'G0'): ", playerNum);
                    //get bomb from player
                    String input = kb.nextLine();
                    int[] blockBomb = new int[2];
                    blockBomb[0] = (int)input.charAt(0) - (int)'A';
                    blockBomb[1] = Integer.parseInt(input.substring(1));
                    //send bomb to server
                    msg = new Message(Message.MSG_RESPONSE_PLAY, null, null, blockBomb, playerNum);
                    outToServer.reset();
                    outToServer.writeObject(msg);
                    outToServer.flush();
                    break;
                case Message.MSG_REQUEST_GAME_OVER: //game is over
                    gameAlive = false;
                    System.out.println("Game over!");
                    break;
                case Message.MSG_REQUEST_GAME_WIN: //game is over
                    gameAlive = false;
                    System.out.println("Game won!");
                    break;
                case Message.MSG_REQUEST_BOMB_MISS:
                    System.out.println("Bomb miss");
                    break;
                case Message.MSG_REQUEST_BOMB_HIT:
                    System.out.println("Bomb hit");
                    break;
                default:
                    System.out.println("Message error");
                    break;
            }
        }
        kb.close();
        clientSocket.close();
    }
}
