Gone Girl - A Text Adventure Game

📖 Synopsis
You wake up in a daze, finding yourself in a dark, unfamiliar living room.
The air is stale, the house is silent, and you get the chilling feeling that you're not alone.
"Gone Girl" is a short, horror-themed text adventure where you must explore
a chilling environment, uncover a dark story, and find a way out.
Solve puzzles, find items, and be very, very quiet. He might be coming back.

------------------------------------------------------------------------------------------------------

🎮 How to Play
You interact with the world by typing commands into the text prompt.
The game will describe your location, and you decide what to do next.
Basic Commands
   go [direction] - Moves to a new area (e.g., go north).
get [item] - Picks up an item from the room (e.g., get hammer).
drop [item] - Drops an item from your inventory.
   inventory - Lists all items you are currently carrying.
examine [item] - Looks closely at an item in the room or in your inventory.
   rest - Takes a short while to rest. This may give you a hint, but don't do it too often...
   quit - Quits the game.
help - Shows a list of available commands.

Special Puzzle Commands
This game is more than just walking around.
You'll need to solve puzzles using special commands:
   pour [item]
   open [item]
   break [item]
   use [item]
   lift [item]
   turnon [item]
   read [item]
   answer [text]
   hide

-------------------------------------------------------------------------------------------------
⚙️ Key Features & Puzzles

😨 The Noise Mechanic
    Be careful!
    Some actions are noisy. Actions like drop, break, pour, and lift
    will make a loud noise and trigger a **CLATTER!** warning.
    When this happens, you are in immediate danger.
    You must type hide as your very next command.
    If you do anything else, you will be found, and it's game over.

💡 Light and Shadow
    Some rooms are too dark to see or read in.
    You'll need to find items like the remote or the lamp
    and use them to light up these areas and discover their secrets.

⏱️ The Mirror Challenge
    Interacting with the mirror in the bathroom is a key puzzle.
    It will trigger a timed event, giving you only 3 turns to get to
    a newly unlocked room (Bedroom 2, north of the kitchen).
    If you don't make it in time, the door will seal again.

🏆 How to Win
Your goal is to uncover the truth of what happened in this house.
To do this, you must:
   1. Collect 5 Mementos: Find the five "amulet" items scattered and hidden throughout the house:
      wood_amulet, metal_amulet, fire_amulet, water_amulet, and earth_amulet.
   2. Unlock the Secret Room: Once you have all five mementos,
      go to the Main Bedroom and open closet.
      The items will unlock a hidden door to the Secret Room.
   Find the Truth: Once inside the secret room, you must use lamp to see.
   You need to read the report, which gives you two final questions.
   Answer the Questions: Use the answer [your answer] command to answer both questions correctly.
   This will solve the mystery and win the game.

☠️ How to Lose
There are two ways to get a "Game Over":
   1. Making Noise: Failing to hide immediately after making a loud noise.
   2. Resting Too Much: Using the rest command 5 times. The man of the house will return.

-----------------------------------------------------------------------------------------------------

🛠️ Futher Details
   1. Language: Scala 3
   2. GUI: Built with scala.swing.
      with the help of Google Gemini for the color and font parts
   3. Game Engine: A custom text adventure engine built across:
      - Adventure.scala: The main game "rulebook" and loop.
      - Player.scala: Defines all player actions and puzzle logic.
      - Area.scala: Defines the map and locations.
      - Item.scala: Defines the game's items.
      - Action.scala: The command parser.
      - AdventureGUI.scala: The view and user-input handler.
4. The story based on real case happen in my country,
      with the help of Google Gemini to hone the narrative