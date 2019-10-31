import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    public static void initBoard(BattleShipTable table, Scanner kb)
    {

    }
    public static void main(String[] args) throws IOException
    {
        Scanner kb = new Scanner(System.in);
        Socket clientSocket = new Socket("localhost", 5000);
        System.out.println("Connected to server");
        boolean gameAlive = true;

        //while not dead, keep game connection alive
        while(gameAlive)
        {
            //read message from server
            Message msg = null;
            ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
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
                    System.out.println("Example inputs: 'A3,A8' or 'B1,D1' or 'D2'");
                    BattleShipTable table = FTable;
                    System.out.println(table.toString());
                    System.out.print("Place aircraft carrier (length 5): ");
                    String[] loc = kb.nextLine().split(",");
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
                    String locn = kb.nextLine();
                    table.insertSubmarine(locn);
                    System.out.println(table.toString());
                    System.out.print("Place submarine (length 1): ");
                    locn = kb.nextLine();
                    table.insertSubmarine(locn);

                    break;
                case 3: //MSG_REQUEST_PLAY, get boards and send block to bomb

                    break;
                case 5: //MSG_REQUEST_GAME_OVER, game is over
                    gameAlive = false;
                    System.out.println("Game over!");
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
