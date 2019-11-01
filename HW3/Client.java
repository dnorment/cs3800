import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    public static void testBoard(BattleShipTable table)
    {
        table.insertAirCarrier("A0", "A1");
        table.insertAirCarrier("B0", "B1");
        table.insertDestroyer("C0", "C1");
        table.insertDestroyer("D0", "D1");
        table.insertSubmarine("E0");
        table.insertSubmarine("F0");
        System.out.println(table.toString());
    }

    public static void initBoard(BattleShipTable table, Scanner kb)
        boolean done = false;
        while (!done)
        {
            String[] loc;
            String locn;

            System.out.println(table.toString());
            System.out.print("Place aircraft carrier (length 5): ");
            loc = kb.nextLine().split(",");
            table.insertAirCarrier(loc[0], loc[1]);
            System.out.println(table.toString());
            System.out.print("Place aircraft carrier (length 5): ");
            loc = kb.nextLine().split(",");
            table.insertAirCarrier(loc[0], loc[1]);

            System.out.println(table.toString());
            System.out.print("Place destroyer (length 3): ");
            loc = kb.nextLine().split(",");
            table.insertDestroyer(loc[0], loc[1]);
            System.out.println(table.toString());
            System.out.print("Place destroyer (length 3): ");
            loc = kb.nextLine().split(",");
            table.insertDestroyer(loc[0], loc[1]);

            System.out.println(table.toString());
            System.out.print("Place submarine (length 1): ");
            locn = kb.nextLine();
            table.insertSubmarine(locn);
            System.out.println(table.toString());
            System.out.print("Place submarine (length 1): ");
            locn = kb.nextLine();
            table.insertSubmarine(locn);
            System.out.println(table.toString());
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

            int msgType = msg.getMsgType();
            int playerNum = msg.getPlayerNum();
            BattleShipTable FTable = msg.Ftable;
            BattleShipTable PTable = msg.Ptable;
            switch(msgType)
            {
                case 1: //MSG_REQUEST_INIT, initialize board
                    System.out.printf("Player %d, place your ships!%n", playerNum);
                    System.out.println("Example inputs: 'A3,A4' or 'B1,C1' or 'D2', must be adjacent spaces");
                    
                    System.out.println(table.toString());

                    //send board to server
                    msg = new Message(2, FTable, null, null, playerNum);
                    outToServer.writeObject(msg);
                    outToServer.flush();
                    break;
                case 3: //MSG_REQUEST_PLAY, get boards and send block to bomb
                    System.out.printf("Player %d, fire a bomb (ex. 'G0'): ", playerNum);
                    String input = kb.nextLine();
                    int[] blockBomb = new int[2];
                    blockBomb[0] = (int)input.charAt(0) - (int)'A';
                    blockBomb[1] = Integer.parseInt(input.substring(1));
                    msg = new Message(4, FTable, PTable, blockBomb, playerNum);
                    outToServer.writeObject(msg);
                    outToServer.flush();
                    break;
                case 5: //MSG_REQUEST_GAME_OVER, game is over
                    gameAlive = false;
                    System.out.println("Game over!");
                    break;
                case 6: //MSG_REQUEST_GAME_WIN, game is over
                    gameAlive = false;
                    System.out.println("Game won!");
                    break;
                case 7: //MSG_REQUEST_BOMB_MISS
                    System.out.println("Bomb miss");
                    break;
                case 8: //MSG_REQUEST_BOMB_HIT
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
