package o1.adventure

/**
 * The `Adventure` class represents a text adventure game.
 *
 * An adventure consists of a world defined by [[Area]] objects, a [[Player]]
 * who explores it, and a set of rules for game state and interaction.
 *
 * @param title The title of the adventure game.
 */
class Adventure:

  // The title of the game.
  val title = "Gone Girl"

  // --- 1. AREA DEFINITIONS ---
  // These are the locations the player can visit.
  val livingRoom  = new Area("Living Room", "A messy living room, cloaked in shadows. The air is stale. It's too dark to read.")
  val kitchen     = new Area("Kitchen", "A cold, damp kitchen. There's a faint, rotten smell coming from the sink. A large pot sits on the stove.")
  val bathroom    = new Area("Bathroom", "A small, grimy bathroom. The mirror is so fogged, it barely reflects the light.")
  val bedroom2    = new Area("Bedroom 2", "A child's bedroom. It's eerily clean, as if no one has been here for a long time.")
  val mainBedroom = new Area("Main Bedroom", "A large master bedroom. The furniture is covered in dusty sheets. A heavy closet is pushed against the east wall.")
  val keyRoom     = new Area("Secret Room", "It's pitch black. You can't see a thing, but you can feel an intense cold.")

  /** The "win" location. The game is won if the player is here and has solved the final puzzle. */
  val destination = keyRoom

  // --- 2. ITEM DEFINITIONS ---
  // These items are required to solve the final puzzle (opening the closet).
  val woodAmulet  = new Item("wood_amulet", "A small, hand-carved wooden bird. A child's toy.")
  val metalAmulet = new Item("metal_amulet", "A cold, metallic locket. It's tarnished and empty.")
  val fireAmulet  = new Item("fire_amulet", "A reddish stone, smooth and warm, like a worry stone.")
  val waterAmulet = new Item("water_amulet", "A smooth, blue-glass charm. It reminds you of the ocean.")
  val earthAmulet = new Item("earth_amulet", "A heavy, brown rock, clearly from a riverbed. It's strangely comforting.")

  // These items are used for other puzzles or provide story clues.
  val hammer    = new Item("hammer", "A heavy-duty hammer. Good for breaking things.")
  val phone     = new Item("phone", "A smartphone. The screen is cracked.")
  val ticket    = new Item("ticket", "A train ticket for a 7:30 PM departure.")
  val report    = new Item("report", "A police report. It's about a missing girl.")
  val remote    = new Item("remote", "A standard TV remote.")
  val vase        = new Item("vase", "A large, ornate flower vase. Looks like there's something inside it.")
  val pot         = new Item("pot", "A large pot on the stove. The lid is sealed tight.")
  val bin         = new Item("bin", "A small trash bin under the sink.")
  val bed         = new Item("bed", "A large, messy bed.")
  val closet      = new Item("closet", "A heavy wooden closet. It's locked with a strange 5-symbol mechanism that feels cold to the touch.")
  val mirror      = new Item("mirror", "A fogged bathroom mirror. You can barely see your reflection.")
  val lamp        = new Item("lamp", "An oil lamp and a box of matches.")
  // These items only appear after the keyRoom is lit
  val bone        = new Item("bone", "A small, human-looking bone. It's clean.")
  val wall        = new Item("wall", "A message is scrawled on the wall. It's hard to read in the dim light.")


  // --- 3. ITEM PLACEMENT ---
  // Place items into their starting areas.
  livingRoom.addItem(vase)
  livingRoom.addItem(metalAmulet)
  kitchen.addItem(pot)
  bathroom.addItem(bin)
  bathroom.addItem(mirror)
  bedroom2.addItem(hammer)
  bedroom2.addItem(lamp)
  bedroom2.addItem(phone)
  bedroom2.addItem(ticket)
  mainBedroom.addItem(bed)
  mainBedroom.addItem(closet)

  // --- 4. AREA CONNECTIONS (Map Layout) ---
  // Define the exits for each area.
  livingRoom.setNeighbors(Vector("west" -> kitchen, "south" -> bathroom))
  // The 'north' door in the kitchen is locked at the start. It's unlocked by the 'use mirror' puzzle.
  kitchen.setNeighbors(Vector("east" -> livingRoom))
  bathroom.setNeighbors(Vector("north" -> livingRoom))
  bedroom2.setNeighbors(Vector("south" -> kitchen))
  // The 'east' door in the main bedroom is locked at the start. It's unlocked by the 'open closet' puzzle.
  mainBedroom.setNeighbors(Vector("south" -> livingRoom))
  keyRoom.setNeighbors(Vector("west" -> mainBedroom))


  // --- 5. GAME STATE VARIABLES ---
  // These variables track the game's status and puzzle progression.
  var turnCount = 0                       // Counts how many commands the player has entered.
  var answeredQ1 = false                // Final puzzle flag: Has the player answered the first question?
  var answeredQ2 = false                // Final puzzle flag: Has the... second question?
  var mirrorChallengeActive = false     // Puzzle flag: Is the 3-turn 'use mirror' timer active?
  var keyRoomLit = false                // Puzzle flag: Has the player used the 'lamp' in the 'keyRoom'?
  var livingRoomLit = false             // Puzzle flag: Has the player used the 'remote' in the 'livingRoom'?
  var mirrorChallengeTimer = 0          // Tracks the countdown for the mirror challenge.
  var playerIsGirl = false              // Puzzle flag: Is the player transformed by the mirror?

  // --- LOSE CONDITION VARIABLES ---
  var restCounter = 0                   // Counts how many times the player has 'rested'.
  val restLimit = 5                     // Player loses on the 5th 'rest' command.
  var playerLost = false                // General lose flag.
  var loseMessage = ""                  // Stores "rest" or "noise" to show the correct game over message.
  var isNoisyActionPending = false      // DANGER FLAG: Set to true after a noisy action ('drop', 'break', etc.).
                                        // The player *must* 'hide' on the next turn or they lose.

  // --- 6. PLAYER CREATION ---
  // Create the player object, placing them in the starting area.
  val player = Player(livingRoom, this)


  // --- 7. GAME METHODS ---

  /** Determines if the adventure is complete (win condition). */
  def isComplete =
    // The player must be in the final room AND have answered both questions.
    this.player.location == this.destination && this.answeredQ1 && this.answeredQ2

  /** Determines whether the game is over (win, lose, or quit). */
  def isOver = this.isComplete || this.player.hasQuit || this.playerLost

  /** The welcome message. */
  def welcomeMessage = "You wake up in a daze. You're in a living room, but it's not yours. Find a way out.\nType 'help' for commands."

  /** The goodbye message, including the win story. */
  def goodbyeMessage =
    if this.isComplete then
      // The "good ending": Displayed when isComplete is true.
      s"""
      You've uncovered the truth. The bones, the writing on the wall... it all makes sense.

      The girl's mother remarried a terrible man. He abused the girl, and she became pregnant.
      She was trying to escape—that's what the money, the train ticket, and the phone were for.
      But her stepfather found out before he could leave.

      He locked her in that secret room, where she died.
      To break her spirit, he scattered her most precious mementos around the house—the carved bird,
      the locket, the river stone... things from her old life.

      But you found them. You gathered her memories. You gave her spirit the strength to show you the truth.

      You've found her. You've uncovered the secret.
      """
    else if this.playerLost && this.loseMessage == "rest" then
      // The "rested too long" ending.
      """
      You've rested for too long.

      You hear a key rattling in the front door. The deadbolt... the one you smashed...

      The door swings open. A large man stands in the entryway, his eyes finding yours in the dark.
      'Who... who are you?'

      He's home. You have nowhere to run.

      Game Over.
      """
    else if this.playerLost && this.loseMessage == "noise" then
      // The "made too much noise" ending.
      "Game Over."
    else if this.player.hasQuit then
      // The "player quit" ending.
      "A cold presence envelops you... You have failed."
    else
      // Default quit message (a fallback).
      "You quit."
  end goodbyeMessage


  /**
   * Plays a turn by executing the given in-game command.
   * This is the main game loop logic, called by the GUI.
   * @param command The command string from the player.
   * @return A string describing the result of the turn.
   */
  def playTurn(command: String): String =

    val formattedCommand = command.trim.toLowerCase

    // --- A. DANGER CHECK (NOISY ACTION) ---
    // This is the *first* thing we check.
    // 1. Check if the player is in danger from the *previous* turn.
    if this.isNoisyActionPending then
      if formattedCommand == "hide" then
        // Player is safe! Clear the danger flag.
        this.isNoisyActionPending = false
        // Execute the "hide" action and return its safe message.
        return Action(command).execute(this.player).getOrElse("You hide.")
      else
        // Player failed to hide! This is a lose condition.
        this.playerLost = true
        this.loseMessage = "noise" // Set the lose message for the goodbye screen.
        // Return the final, game-ending message.
        return s"You decided to '$formattedCommand' instead of hiding. It was a fatal mistake.\n\nYou hear heavy footsteps... The man of the house is home. He finds you.\nYou have nowhere to run."
      end if
    // --- END DANGER CHECK ---


    // --- B. NORMAL TURN LOGIC ---
    // 2. If not in danger, proceed with the normal turn.

    // Check for 'rest' lose condition *before* executing the action.
    if formattedCommand == "rest" then
      this.restCounter += 1
      if this.restCounter >= this.restLimit then
        this.playerLost = true
        this.loseMessage = "rest" // Set the lose message.
      end if
    end if

    // 3. Execute the player's action.
    // The Action object parses the command and calls the correct method on the player.
    val action = Action(command)
    // The action itself (e.g., player.drop) might set isNoisyActionPending to true for the *next* turn.
    val outcomeReport = action.execute(this.player)

    var eventReport = "" // A string to hold any other game event messages (like timers).

    // 4. Check the mirror challenge status *after* the action.
    if this.mirrorChallengeActive then
      if this.player.location == this.bedroom2 then
        // Player successfully reached the target room.
        this.mirrorChallengeActive = false // They made it!
      else if !command.startsWith("use") then // Don't tick down on the turn they *start* the challenge.
        this.mirrorChallengeTimer -= 1
        if this.mirrorChallengeTimer <= 0 then
          // Timer ran out!
          this.mirrorChallengeActive = false
          this.kitchen.removeNeighbor("north") // Re-lock the door.
          this.playerIsGirl = false // The transformation fades.
          eventReport = "\nYou took too long... The door clicks shut again."
        end if
      end if
    end if

    // 5. Finalize the turn
    // We only increment the turn counter if the command was valid.
    if outcomeReport.isDefined then
      this.turnCount += 1
    end if

    // 6. Return the combined report of the action and any other events.
    outcomeReport.getOrElse(s"""Unknown command: "$command".""") + eventReport

end Adventure