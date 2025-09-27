# Rubiks Cube

This program simulates the process of solving the famous Rubiks Cube. Cube's size is fully customisable. 
After each move the cube's state is displayed.
It features complicated 3 dimentional arrays management.

Cube's walls representation:

        u                       0
      l|f|r|b  - wall names   1|2|3|4 - starting colors
        d                       5

## Instructions:
  Commands consist of: {number of layers} + {wall name} + {angle}
  
  E.g. 5b' moves 5 layers of wall 'b' 180 deg. right
  
  Leaving angle slot empty = 90 degrees   
  ' - means 180 degrees
  " - is -90 degrees right
  . - ends the game

