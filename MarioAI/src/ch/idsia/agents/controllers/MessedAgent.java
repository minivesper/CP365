
package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;


public class MyAgent extends BasicMarioAIAgent implements Agent
{

	public MyAgent()
	{
		super("MyAgent");
		reset();
	}

	// Does (row, col) contain an enemy?
	public boolean hasEnemy(int row, int col) {
		return enemies[row][col] != 0;
	}

	// Is (row, col) empty?
	public boolean isEmpty(int row, int col) {
		return (levelScene[row][col] == 0);
	}

  public boolean frontBlocked()
  {
    return !isEmpty(9,10) || !isEmpty(9,11) || !isEmpty(9, 12) || !isEmpty(9,13);
  }

  public boolean frontEnemy()
  {
    return hasEnemy(9,10) || hasEnemy(9,11) || hasEnemy(8,10) || hasEnemy(8,11) || hasEnemy(10,10) || hasEnemy(10,11);
  }

  public boolean dropoff()
  {
    return isEmpty(10, 10) || isEmpty(10, 11);
  }

  public boolean aboveDanger()
  {
    boolean dangerousEnemy = false;
    for(int i = 4; i <= 8; i ++)
    {
      for(int j = 9; j <= 11; j++)
      {
        dangerousEnemy = dangerousEnemy || hasEnemy(i,j);
      }
    }
    return dangerousEnemy;
  }


	// Display Mario's view of the world
	public void printObservation() {
		System.out.println("**********OBSERVATIONS**************");
		for (int i = 0; i < mergedObservation.length; i++) {
			for (int j = 0; j < mergedObservation[0].length; j++) {
				if (i == mergedObservation.length / 2 && j == mergedObservation.length / 2) {
					System.out.print("M ");
				}
				else if (hasEnemy(i, j)) {
					System.out.print("E ");
				}
				else if (!isEmpty(i, j)) {
					System.out.print("B ");
				}
				else {
					System.out.print(" ");
				}
			}
			System.out.println();
		}
		System.out.println("************************");
	}

	// Actually perform an action by setting a slot in the action array to be true
	public boolean[] getAction()
	{
		action[Mario.KEY_JUMP] = (dropoff() || frontBlocked() && isMarioAbleToJump) || (!isMarioOnGround && !aboveDanger());
    action[Mario.KEY_SPEED] = aboveDanger() || !frontEnemy() ;
		action[Mario.KEY_RIGHT] = true;
    action[Mario.KEY_LEFT] =  (!isMarioOnGround && aboveDanger());
        printObservation();
		return action;
	}

	// Do the processing necessary to make decisions in getAction
	public void integrateObservation(Environment environment)
	{
		super.integrateObservation(environment);
    	levelScene = environment.getLevelSceneObservationZ(2);
	}

	// Clear out old actions by creating a new action array
	public void reset()
	{
		action = new boolean[Environment.numberOfKeys];
	}
}
