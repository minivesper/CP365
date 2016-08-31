import java.util.Random;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Stack;
import java.util.HashSet;

class BreadthBot extends SlidingPlayer {

  ArrayList<SlidingMove> sol_set;
  int move_number = -1;

  // The constructor gets the initial board
  public BreadthBot(SlidingBoard _sb) {
      super(_sb);
      sol_set = findPath(_sb);
  }

  public ArrayList<SlidingMove> findPath(SlidingBoard board) {
    HashSet<String> seen = new HashSet<String>();
    Queue<StackNode> stack = new LinkedList<StackNode>();
    StackNode currNode = new StackNode(board, new ArrayList<SlidingMove>());

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
          stack.add(new StackNode(childBoard, childPath));
        }
      }
      currNode = stack.remove();
    }
    return currNode.path;
    }

    // Perform a single move based on the current given board state
    public SlidingMove makeMove(SlidingBoard board) {
          if(!board.isSolved())
          {
            this.findPath(board);
          }
          move_number++;
          return sol_set.get(move_number);
    }
}
