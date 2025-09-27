# Sokoban
This programe is an implementation of a game Sokoban where player's goal is to move boxes to the highlighted spots on the map withouth getting them stuck.
Implements highly efficient recursive pathfinding algorithm and features sophisticated handling of multidimensional arrays.
Stack usage allows player to undo an unwanted move.

To play the player needs to paste their own board first upon program start.

## Symbols:

- @    - player

- '*'  - player on a target spot

- a..z - boxes

- A..Z - box on a target spot

- '-'  - free tile

- '+'  - free target spot
  
- '#'  - wall

- 2/4/6/8 - movement directions

## Commands:

- '0'     - undo the last move

- '.'     - quit the game

{box symbol}{direction} - move command (e.g., b6)

