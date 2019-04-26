-- Topher Tighe
module Main where
import Board

--Assuming no value is passed but 1 or 2
playerToChar p = if p == 1 then 'O' else 'X'

-- Read a slot from input. Must be between 1 and 7 inclusive.
readSlot bd p = do
	 putStrLn (playerToChar p : "'s Turn! Enter 1-7.")
	 line <- getLine
	 let parsed = reads line :: [(Int, String)] in
	     if length parsed == 0
	     then getX'
	     else let (x, _) = head parsed in
	     	  if (x > 0 && x <= numSlot bd && isSlotOpen bd (x-1))
		  then return (x-1)
		  else getX'
	     where
		getX' = do 
		      putStrLn "Invalid input!"
		      readSlot bd p

-- Calls readSlot to make a turn for the current player
takeTurn bd p = do
	 x <- readSlot bd p
	 return (dropInSlot bd x p)

-- Begins the game
main = do
     putStrLn (boardToStr(mkBoard))
     play mkBoard mkPlayer mkOpponent

-- Each call is one turn for a player,
-- then the player is switched.
play bd p o = do
     bd <- (takeTurn bd p)
     putStrLn (boardToStr(bd))
     if isFull bd
     then putStrLn ("No more move! It's a draw!")
     else ( if isWonBy bd p
     then putStrLn (playerToChar p : " is the WINNER!")
     else play bd o p ) -- Switch current player