# Dungeon Maze
Move through a maze, collecting relics and fighting creatures. Collect at least three of the six relics and find the end of the maze to win.

## Controls
Type an instruction and hit enter. Only the last character in a typed line is counted, so no backspaces are necessary. Move through relics to collect them.

### Movement
"w" = move forward 2 spaces

"a" = move left 2 spaces

"s" = move down 2 spaces

"d" = move right 2 spaces

Capitalizing one of the above letters allows you to move only 1 space in that direction.

### Combat
You must be within 1 space of a creature to attack it with your sword. You must be more than 1 space away from a creature to attack it with your bow.

" " = attack; if multiple creatures are visible, target one of them (enter space again to attack)

"a" or "d" = switch target back ("a") or forward ("d")

"e" = switch weapon (between sword and bow)

### Menu
"f" = toggle help

"q" = quit

## Symbols
 P  = player

 O  = wall

 \*  = unknown
 
 S  = start
 
 X  = creature
 
(X) = selected creature

 | or - = door
 
 @  = relic
 
 E  = end
 
 
## Generating Your Own Dungeon
You can generate your own dungeon maps using [donjon](https://donjon.bin.sh/d20/dungeon/). Customize or randomize a dungeon and click "Construct." Then click "TSV Map" in the "Download:" section underneath the map. Move the .txt file into your Dungeon Maze folder. Then edit the MazeModel.java file, replacing "MazeMap45.txt" in "FileReader reader = new FileReader("MazeMap45.txt");" on line 54 with the name of your .txt file. You can edit the placement of relics to fit the rooms in your map by changing the relic coordinates on line 19: "private final int[][] RELICS = {{38, 33}, {40, 7}, {11, 27}, {23, 32}, {29, 17}, {9, 9}};".
