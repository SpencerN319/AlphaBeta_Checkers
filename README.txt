To run in terminal
	- go to the folder containing the AlphaBeta.jar file
	- java -jar AlphaBeta_<level>.jar
	- Play game!

To run in eclipse
	- import as a new project
	- hit play

I have put three different versions of the AlphaBeta game Beginner, Intermediate, and Advanced.
- Beginner has a depth of 4 (milliseconds to decide move)
- Intermediate has a depth of 8 (1-2 seconds to decide move)
- Advanced has a depth of 16 (30-90 seconds to decide move)

All of these use the same evaluation functions 
- eval_pieces
	- Evaluates the current standing of pieces. Normal pieces are worth 1, kings are worth 2.
- eval_location
	- moving forward in numbers is better
- eval_safety
	- tries to protect the home row, and move pieces to safe spaces (left/right side).

- by increasing the depth and improving the evaluation function the AI will make smarter moves.
  In the beginning the AI was making the same first move, so I made it choose randomly from equivalent moves.
  Next by increasing the depth the AI is able to look further ahead in the game and see what the highest
  outcome can be from its current move. The stronger the evaluation and the deeper the depth the better
  the AI will play.


****	Sources    ****
http://www.cs.ucf.edu/~dmarino/ucf/java/Checkers.java

-this code was heavily modified