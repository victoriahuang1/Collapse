How to play: we start out with bricks of different colors on the board. We click on 
a brick is in a group of at least 2 adjacent bricks of the same color to get rid 
of them and earn points. To get to the next level, we need to get at least a 
certain number of points.

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an approprate use of the concept. You may copy and paste from your proposal
  document if you did not change the features you are implementing.

  1. Inheritance and subtyping
    - What specific feature of your game will be implemented using this concept?
    
    We will use an inheritance hierarchy to model the different types of bricks
    
    - Why does it make sense to implement this feature with this concept? Justify
      why this is a non-trivial application of the concept in question.
      
    There will be a normal brick so it makes sense to have a generic Brick 
    class since we are going to instantiate it. The special effect bricks will 
    be subclasses of the Brick class. It will also implement the PowerUp 
    interface. All special bricks behave similarly so it makes sense to make 
    this interface.

  2. 2D array
    - What specific feature of your game will be implemented using this concept?
    
    The Board will have a 2D array that contains the bricks in the level. When 
    the player gets rid of bricks, we need to make the bricks above it to fall
    down onto the bricks below. When there is a gap between 2 columns, we need 
    them to regroup. We can do this by shifting the bricks in the array.
    
    - Why does it make sense to implement this feature with this concept? Justify
      why this is a non-trivial application of the concept in question.
      
    This makes it easier to place and arrange the bricks. Also, the board can't 
    be resized.

  3. I/O
    - What specific feature of your game will be implemented using this concept?
    
    We will keep track of players and their scores. We will also read from a
    file to get the types of bricks to put in a level and the points needed 
    to progress to the next level. As the game progresses, there will be 
    more colors and more types of power-ups.
    Also, the player will be able to save the game and resume it later.
    
    - Why does it make sense to implement this feature with this concept? Justify
      why this is a non-trivial application of the concept in question.
      
    This way we can make the levels increasingly harder. We need to make 
    sure to pick the right bricks and get the right number of points for 
    each level.
    We need to save the game state and write the position of the bricks to a 
    file and recreate it when we resume the game.
    We need to not mix up the information for each player.

  4. JUnit testing
    
    - What specific feature of your game will be implemented using this concept?

    We will need to test if the bricks are in the correct positions after being
    updated.

    - Why does it make sense to implement this feature with this concept? Justify
      why this is a non-trivial application of the concept in question.

    We need to make sure the bricks fall and regroup correctly. There should not
    be any gaps in between bricks. We need to make sure to not find "false gaps".


=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.

    The Board class is a board that stores all the bricks in a 2D array. Is it 
    where the game is played.
    The Brick class is a normal brick. PowerUps is an abstract class that 
    extends Brick. The different types of "special bricks" are all subclasses of 
    PowerUps. These bricks are what we play with in the game.
    Stats is a JPanel that displays the score, the level, and the points needed 
    to get to the next level.
    SavedFileReader interprets the savefiles and loads the saves to the board.
    SaveGameToFile writes the current game's information to a savefile.

- Revisit your proposal document. What components of your plan did you end up
  keeping? What did you have to change? Why?

    I decided to make more power-ups appear as the game goes on since we wouldn't
    be able to otherwise get to the next level.
    I decided to test the repositioning of the blocks since this was more
    challenging than getting the hints.
    I decided to also use a 2D array to keep track of the bricks checked when 
    searching for groups of bricks of the same color.

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
  
    

- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

    I think there is mostly a good separation of functionality. 
    I think the privates states are encapsulated. We can only access them 
    through methods. I don't think the methods alias the private states, so we 
    can't change them.
    I would refactor Game, Stats, and Board. I would make Stats a part of the 
    board since Stats uses a lot of information from Board. I would make Game 
    more concise.



