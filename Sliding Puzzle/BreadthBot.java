import java.util.Random;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

class BreadthBot extends SlidingPlayer {

    ArrayList<SlidingMove> sol_set = new ArrayList<SlidingMove>();
    // The constructor gets the initial board
    public BreadthBot(SlidingBoard _sb) {
        super(_sb);
        findpath(_sb);
    }

    public void findpath(SlidingBoard board)
    {
      Queue<SlidingBoard> q = new LinkedList<SlidingBoard>();
      ArrayList<SlidingMove> legalMoves = new ArrayList<SlidingMove>();
      q.add(board);
      SlidingBoard currentBoard = new SlidingBoard(board.size);
      currentBoard.setBoard(q.remove());
      while(!currentBoard.isSolved())
      {
        legalMoves = currentBoard.getLegalMoves();
        for(int i = 0; i < legalMoves.size(); i++)
        {
          sol_set.add(legalMoves.get(i));
          int x = currentBoard.doMove(legalMoves.get(i));
          SlidingBoard nextBoard = new SlidingBoard(board.size);
          nextBoard.setBoard(currentBoard);
          q.add(nextBoard);
          currentBoard.undoMove(legalMoves.get(i), x);
          sol_set.add(getUndomove(legalMoves.get(i), x));
        }
        currentBoard.setBoard(q.remove());
      }
    }

    public SlidingMove getUndomove(SlidingMove m, int direction)
    {
      if (direction == 0) {
          return new SlidingMove(m.row-1, m.col);
      }
      else if (direction == 1) {
        return new SlidingMove(m.row+1, m.col);
      }
      else if (direction == 2) {
          return new SlidingMove(m.row, m.col-1);
      }
      else if (direction == 3) {
          return new SlidingMove(m.row, m.col+1);
      }
      else {
        return null;
      }
    }
    // Perform a single move based on the current given board state
    public SlidingMove makeMove(SlidingBoard board) {
        for(int i = 0; i < sol_set.size(); i ++)
        {
          return sol_set.get(i);
        }
        return null;
    }
}
