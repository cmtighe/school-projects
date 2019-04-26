-- Topher Tighe
module Board where

-- These are treated as finals.
height:: Int
height = 6
width:: Int
width = 7
mkPlayer = 1
mkOpponent = 2

-- Create a List of Lists populated by 0s
mkBoard = mkBoardHelper height width
mkBoardHelper m n = replicate n (replicate m 0)

-- This will place a token as far down in a slot as is available
dropInSlot bd i p = findSlot bd i p 0
findSlot (h:t) i p count
	 | count /= i = h : findSlot t i p (count+1)
	 | otherwise = dropToken h p : t
dropToken (h:t) p
	  | h /= 0 = (h:t)
	  | t == [] = [p]
	  | head(t) /= 0 = (p:t)
	  | otherwise = h : dropToken t p 

-- Checks if a slot is open by testing the value of top slot
isSlotOpen bd i = if head (bd!!i) == 0 then True else False

-- Returns the number of slots.
numSlot bd = width

-- True if the whole board is occupied.
isFull bd = length (filter (\x->x == 0)(concat bd)) == 0

-- Checks if player p has 4 in a row in any direction
isWonBy bd p = isWinVert bd p || isWinHorz bd p || isWinDR bd p || isWinUR bd p

-- Checks down each row for 4-in-a-row
isWinVert (h:t) p
	  | t == [] = isWinVertHelper h p
	  | otherwise = isWinVertHelper h p || isWinVert t p
isWinVertHelper l p = foldl countVert 0 l >= 4 where
		countVert count y = if y == p then count + 1 else (if count >= 4 then count else 0)

-- Checks across the rows for 4-in-a-row
isWinHorz bd p = isWinHorzRow bd p 0
-- Traverse the rows to check for 4-in-a-row 
isWinHorzRow bd p row
	     | row == height = False
	     | otherwise = isWinHorzCol bd p row || isWinHorzRow bd p (row+1)
-- Traverse the columns, and preform the actual check 
isWinHorzCol (h1:h2:h3:t) p row
	     | t == [] = False  --If the fourth is off the board
	     | h1!!row == p && h2!!row == p && h3!!row == p && head(t)!!row == p = True
	     | otherwise = isWinHorzCol (h2:h3:t) p row

-- Mostly identical to isWinHorz,
-- except that each column has to be checked on a different row.
-- DR stands for "Down-Right"
isWinDR bd p = isWinDRRow bd p 0
-- Traverse the rows to check for 4-in-a-row
isWinDRRow bd p row
	   | row == height-3 = False
	   | otherwise = isWinDRCol bd p row || isWinDRRow bd p (row+1)
-- Traverse the columns, and preform the actual check
isWinDRCol (h1:h2:h3:t) p row
	   | t == [] = False --If the fourth is off the board
	   | h1!!row == p && h2!!(row+1) == p && h3!!(row+2) == p && head(t)!!(row+3) == p = True
	   | otherwise = isWinDRCol (h2:h3:t) p row

-- Mostly identical to to isWinHorz and isWinDR,
-- Except it starts low, and looks upwards.
-- UR stands for "Up-Right"
isWinUR bd p = isWinURRow bd p 0
-- Traverse the rows to check for 4-in-a-row 
isWinURRow bd p row
	   | row == height-3 = False
	   | otherwise = isWinURCol bd p row || isWinURRow bd p (row+1)
-- Traverse the columns, and preform the actual check 
isWinURCol (h1:h2:h3:t) p row
	   | t == [] = False --If the fourth is off the board
	   | h1!!(row+3) == p && h2!!(row+2) == p && h3!!(row+1) == p && head(t)!!row == p = True
	   | otherwise = isWinURCol (h2:h3:t) p row

-- Converts to board to a string to be displayed.
boardToStr bd = boardToStrHelper bd 0 where
	   boardToStrHelper (h:t) row
	   		    | row == (height-1) && t == [] = [convert (h!!row)]
			    | t == [] = convert (h!!row) : '\n' : boardToStrHelper bd (row+1)
			    | otherwise = convert (h!!row) : boardToStrHelper t row
convert num
	| num == 0 = '.'
	| num == 1 = 'O'
	| num == 2 = 'X'


-- This is used for testing purposes only.
-- Feel free to edit it whimsically.
testList = [[0,0,2,1,1,1],[0,0,0,2,2,1],[0,0,0,0,2,1],[0,0,0,0,0,2],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]]