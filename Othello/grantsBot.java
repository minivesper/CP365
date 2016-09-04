import java.util.Random;
import java.util.ArrayList;
import java.util.LinkedList;

class moveandScore {

  OthelloMove move;
  int score;

  public moveandScore(OthelloMove m, int bs)
  {
    move = m;
    score = bs;
  }
}

public class grantsBot extends OthelloPlayer {

    // makeMove gets a current OthelloBoard game state as input
    // and then returns an OthelloMove object

    // Your bot knows what color it is playing
    //    because it has a playerColor int field

    // Your bot can get an ArrayList of legal moves:
    //    ArrayList<OthelloMove> moves = board.legalMoves(playerColor);

    // The constructor for OthelloMove needs the row, col, and player color ints.
    // For example, play your token in row 1, col 2:
    //   OthelloMove m = new OthelloMove(1, 2, playerColor);

    // OthelloBoard objects have a public size field defining the
    // size of the game board:
    //   board.size

    // You can ask the OthelloBoard if a particular OthelloMove
    //  flanks in a certain direction.
    // For example:
    //  board.flanksLeft(m) will return true if you can capture pieces to the left of move, m

    // You can ask the board what the current score is.
    //  This is just the difference in checker counts
    //  return the point differential in black's favor
    //  +3 means black is up by 3
    //  -5 means white is up by 5
    // int score = board.getBoardScore();

    // OthelloBoard has a toString:
    //  System.out.println(board);

    // OthelloPlayer superclass has a method to get the color for your opponent:
    //  int opponentColor = getOpponentColor();


    public grantsBot(Integer _color) {
        super(_color);
    }

    public moveandScore findbestMove(int pc, OthelloBoard board, OthelloMove lastMove, moveandScore bestScore, int depth, int depthLimit) {
      System.out.println(depth);
      if (depth < depthLimit)
      {
        if(pc == 1 && depth != 0) { pc++; }
        else if(depth != 0) { pc --; }
        ArrayList<OthelloMove> legal = board.legalMoves(pc);
        for (OthelloMove move : legal)
        {
          if(depth == 0 && legal.size() == 1)
          {
            moveandScore ms = new moveandScore(move, 0);
            return ms;
          }
          board.addPiece(move);
          moveandScore ms = findbestMove(pc, board, move, bestScore, depth+1, depthLimit);
          if(ms.score >= bestScore.score)
          {
            bestScore.score = ms.score;
            if(depth == 1)
            {
              bestScore.move = lastMove;
            }
          }
        }
      }
      else
      {
        moveandScore ms = new moveandScore(lastMove, board.getBoardScore());
        return ms;
      }
      System.out.println(bestScore.move + " " + bestScore.score);
      if(bestScore.move == null)
      {
        bestScore.move = lastMove;
        bestScore.score = board.getBoardScore();
      }
      return bestScore;
    }

    public int getRealBoardScore(OthelloBoard board) {
      if(playerColor == 1)
      {
        return board.getBoardScore();
      }
      else
      {
        return -1*board.getBoardScore();
      }
    }

    public void setBoard(OthelloBoard board, OthelloBoard otherBoard) {
        for (int r = 0; r < board.size; r++) {
            for (int c = 0; c < board.size; c++) {
                board.board[r][c] = otherBoard.board[r][c];
            }
        }
    }

    public OthelloMove makeMove(OthelloBoard board) {
      OthelloBoard fuboard = new OthelloBoard(board.size, false);
      setBoard(fuboard, board);
      moveandScore bestScore = new moveandScore(null, 0);
      bestScore = findbestMove(playerColor, fuboard, null, bestScore, 0, 10);
      System.out.println("final move: " + bestScore.move);
      return bestScore.move;
    }
}
