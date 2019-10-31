import java.io.Serializable;

public class Message implements Serializable
{
	private static final long serialVersionUID = 2L;
	
	//message types
	static final int MSG_REQUEST_INIT = 1;//sent from server to client
	static final int MSG_RESPONSE_INIT = 2; //sent from client to server
	static final int MSG_REQUEST_PLAY = 3; //sent from server to client
	static final int MSG_RESPONSE_PLAY = 4; //sent from client to server
	static final int MSG_REQUEST_GAME_OVER = 5; //sent from server to client
	static final int MSG_REQUEST_GAME_WIN = 6; //sent from server to client
	static final int MSG_REQUEST_BOMB_MISS = 7; //sent from server to client
	static final int MSG_REQUEST_BOMB_HIT = 8; //sent from server to client

	private int msgType;
	private int player;
	private int[] blockBomb; //x, y coordinates of the block on the opponent's board to be bombed; this is for the MSG_RESPONSE_PLAY message


	public BattleShipTable Ftable = null; //the player's own board (F-board)
	public BattleShipTable Ptable = null; //the player hits and misses on the opponent board (P-board)

	public int getMsgType()
	{
		return this.msgType;
	}

	public int getPlayerNum()
	{
		return this.player;
	}

	public int[] getBomb()
	{
		return this.blockBomb;
	}

	// constructor
	public Message(int type, BattleShipTable FTable, BattleShipTable PTable, int[] blockBomb, int player)
	{
		this.msgType = type;
		this.Ftable = FTable;
		this.Ptable = PTable;
		this.blockBomb = blockBomb;
		this.player = player;
	}

}
