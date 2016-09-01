import java.util.Random;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.PriorityQueue;

class AstarNode implements Comparable{

  public SlidingBoard correctBoard;
  public SlidingBoard board;
  public ArrayList<SlidingMove> path;
  public int cost;

  public AstarNode(SlidingBoard b, ArrayList<SlidingMove> p) {
    board = b;
    path = p;
    correctBoard = new SlidingBoard(b.size);
    correctBoard.initBoard();
    cost = getCost();
  }

  public int compareTo(Object otherNode)
  {
    AstarNode othernode = (AstarNode) otherNode;
    return new Double(cost).compareTo(new Double(othernode.cost));
  }

  public int getCost()
  {
      return this.path.size() + this.manhattanCost();
  }

  public int manhattanCost()
  {
    for(int i = 0; i < this.board.size; i ++)
    {
      for(int j = 0; j < this.board.size; j ++)
      {
          //case where the number is in the right place
         if(this.board.board[i][j] == this.correctBoard.board[i][j])
         {
           return 0;
         }
         else
         {
           for(int r = 0; r < this.correctBoard.size; r++)
           {
             for(int c = 0; c < this.correctBoard.size; c++)
             {
               if(this.board.board[i][j] == this.correctBoard.board[r][c])
               {
                 return -1*((r-i)+(j-c));
               }
             }
           }
         }
      }
    }
    return 0;
  }
}

class AstarBot extends SlidingPlayer {

    ArrayList<SlidingMove> path;
    int move_number = -1;

    public AstarBot(SlidingBoard _sb) {
        super(_sb);
        path = findPath(_sb);
    }

    public ArrayList<SlidingMove> findPath(SlidingBoard board) {
      HashSet<String> seen = new HashSet<String>();
      PriorityQueue<AstarNode> q = new PriorityQueue<AstarNode>();
      AstarNode currNode = new AstarNode(board, new ArrayList<SlidingMove>());

      while (!currNode.board.isSolved()) {
        ArrayList<SlidingMove> legal = currNode.board.getLegalMoves();
        for (SlidingMove move : legal) {
          SlidingBoard childBoard = new SlidingBoard(currNode.board.size);
          childBoard.setBoard(currNode.board);
          childBoard.doMove(move);
          if (!seen.contains(childBoard.toString())) {
            seen.add(childBoard.toString());
            ArrayList<SlidingMove> childPath = (ArrayList<SlidingMove>)currNode.path.clone();
            childPath.add(move);
            q.add(new AstarNode(childBoard, childPath));
          }
        }
        currNode = q.poll();
      }
      return currNode.path;
    }

    // Perform a single move based on the current given board state
    public SlidingMove makeMove(SlidingBoard board) {
      move_number++;
      return path.get(move_number);
    }
}
