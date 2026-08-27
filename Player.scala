package o1.adventure

import scala.collection.mutable.Map

/** A `Player` object represents a player character in the game.
  *
  * @param startingArea  the player’s initial location
  * @param adventure     a reference to the main Adventure object, used to find items and areas
  */
class Player(startingArea: Area, val adventure: Adventure):

  private var currentLocation = startingArea    // The player's current location, which can change.
  private var quitCommandGiven = false          // Tracks if the player has typed 'quit'.
  private val items = Map[String, Item]()       // The player's inventory.

  /** Returns true if the player has quit the game. */
  def hasQuit = this.quitCommandGiven

  /** Returns the player's current location. */
  def location = this.currentLocation

  // --- HELPER METHOD ---
  /**
   * Sets the "noisy action" flag in the Adventure class.
   * This is a private helper called by actions that make noise.
   * @return A warning message to be appended to the action's report.
   */
  private def makeNoise(): String =
    // This sets the "danger" flag. The player must 'hide' next turn.
    this.adventure.isNoisyActionPending = true
    "\n\n**CLATTER!** You made a loud noise. You'd better hide!"
  // --- END HELPER METHOD ---

  /** Attempts to move the player in the given direction. */
  def go(direction: String) =
    val destination = this.location.neighbor(direction)

    // --- PUZZLE: The Mirror Challenge Door ---
    // This is a special check for the locked door to Bedroom 2.
    if this.location == adventure.kitchen && direction == "north" then

      if adventure.playerIsGirl && adventure.mirrorChallengeActive then
        // SUCCESS: Player is "the girl" and the timer is active.
        this.currentLocation = adventure.bedroom2
        adventure.playerIsGirl = false        // The transformation fades upon entry.
        adventure.mirrorChallengeActive = false // The challenge is over.

        var eventReport = "\nYou push on the door and your hand passes right through it, as if you are a ghost. You are in the child's bedroom.\nThe cold feeling leaves you."

        // --- Auto-get event for Bedroom 2 ---
        // When the player enters, they automatically pick up the key items in the room.
        this.location.removeItem("phone") match
          case Some(item) =>
            this.items += item.name -> item
            eventReport += s"\n\nYou find a ${item.name} on a small table and pick it up.\n"
            eventReport += this.examine(item.name) // Auto-examine the item for the player.
          case None => // Item already taken

        this.location.removeItem("ticket") match
          case Some(item) =>
            this.items += item.name -> item
            eventReport += s"\n\nYou see a ${item.name} on the floor and pick it up.\n"
            eventReport += this.examine(item.name) // Auto-examine the item.
          case None => // Item already taken

        eventReport // Return the success message + item report.

      else if adventure.playerIsGirl && !adventure.mirrorChallengeActive then
        // FAILED: You were the girl, but the timer ran out.
        "You took too long... The feeling is gone. The door is cold and sealed."
      else
        // DEFAULT: You are not the girl.
        "You push on the door, but it's cold. You feel a presence on the other side. You can't open it."
      // --- END PUZZLE LOGIC ---

    // Standard 'go' logic for all other, normal exits.
    else if destination.isDefined then
      this.currentLocation = destination.get
      "You go " + direction + "."
    else
      "You can't go " + direction + "."
  end go


  /** Causes the player to rest for a short while. Adds to the "rest" lose condition. */
  def rest() =
    // The restCounter is incremented in Adventure.scala *before* this method is called.
    if this.adventure.restCounter == 1 then
      // First rest: Give a special hint.
      "You rest for a while, trying to clear your head. As your mind settles, you remember reading something...\n**The police report mentioned the family was 'uncooperative, particularly the new stepfather.'**"
    else
      // Subsequent rests: Show a spooky/warning message.
      val randomMessage = scala.util.Random.nextInt(5) // Show a spooky message
      randomMessage match
        case 0 => "You rest for a while. You feel a cold draft."
        case 1 => "You rest. In the silence, you think you hear a floorboard creak."
        case 2 => "You try to rest, but a sudden chill runs down your spine. (You feel you are wasting time...)"
        case 3 => "You rest. A floorboard creaks... but it sounds like it's in the *room with you*."
        case _ => "You try to rest, but you can't shake the feeling... you are being watched. (He might be coming back...)"
  end rest

  /** Signals that the player wants to quit the game. */
  def quit() =
    this.quitCommandGiven = true
    "" // No message needed, Adventure.scala handles the goodbye.

  /** Tries to pick up an item from the player's current location. */
  def get(itemName: String): String =
    // Prevent player from picking up scenery items.
    if (itemName == "closet" || itemName == "wall" || itemName == "bed") && this.location.contains(itemName) then
      "You can't pick that up."
    else
      // Attempt to remove the item from the area.
      this.location.removeItem(itemName) match
        case Some(item) =>
          // Success: Add item to inventory.
          this.items += item.name -> item
          s"You pick up the ${item.name}."
        case None =>
          // Failure: Item not in the room or 'bed' was already 'lifted'.
          s"There is no ${itemName} here to pick up."

  /** Tries to drop an item from the player's inventory. This action makes noise. */
  def drop(itemName: String): String =
    this.items.remove(itemName) match
      case Some(item) =>
        // Success: Remove from inventory, add to area.
        this.location.addItem(item)
        s"You drop the ${item.name}." + this.makeNoise() // <-- Triggers noise!
      case None =>
        "You don't have that!"

  /** Tries to examine an item in the player's inventory or in the room. */
  def examine(itemName: String): String =
    // Special non-item examine cases
    if itemName == "money" then
      if this.has("ticket") then
        "You look at the money tucked in with the train ticket. It's a small wad of cash. Just enough for a one-way trip... and maybe a little extra."
      else
        "You don't see any money."
    else
      // Standard item-based examine logic
      // 1. Find the item (check inventory first, then the room).
      val itemToDescribe = this.items.get(itemName)
        .orElse(this.location.getItem(itemName))

      // 2. Provide a description.
      itemToDescribe match
        // These are the 5 "key" items with special, flavorful descriptions.
        case Some(item) if item.name == "wood_amulet" =>
          "You examine the small, hand-carved bird. The wood is smooth, worn by a small hand. It feels full of old joy."
        case Some(item) if item.name == "metal_ amulet" =>
          "You open the cold, metallic locket. It's empty. On the inside is a tiny, faded picture of a woman you don't recognize. She looks sad."
        case Some(item) if item.name == "fire_amulet" =>
          "You look at the reddish stone. It's a 'worry stone,' designed to be rubbed. It's unnervingly warm."
        case Some(item) if item.name == "water_amulet" =>
          "You examine the smooth, blue-glass charm. It looks like a cheap souvenir from a beach, but it seems to have been treasured."
        case Some(item) if item.name == "earth_amulet" =>
          "You look at the heavy, brown rock. It's just a simple river rock, but it has been polished smooth by a running current... or by a nervous thumb."

        // These items provide direct clues to the story.
        case Some(item) if item.name == "phone" =>
          "You turn on the phone. The screen is cracked.\nOne voice memo: 'Why don't you come, its been 2 hours, too long. The train has gone.'\nOne unread text: 'I'm at the station. Where are you? The train leaves in 10 minutes...'"
        case Some(item) if item.name == "ticket" =>
          "It's a one-way train ticket for a 7:30 PM departure. It was never used. Below the ticket is some money."
        // Conditional description: Only works if the 'keyRoom' is lit.
        case Some(item) if item.name == "bone" && adventure.keyRoomLit =>
          "A small, human-looking bone. It's clean."
        // Conditional description: Only works if the 'keyRoom' is lit.
        case Some(item) if item.name == "wall" && adventure.keyRoomLit =>
          """
          You look at the wall. The plaster is cracked and damp.
          Near the floor, you see frantic scratch marks, as if from fingernails.

          Below them, a message is scrawled in what looks like dried blood:

          'FORGIVE ME PLEASE. I WILL NEVER ESCAPE...
          ... I'M SO SORRY, I CAN'T PROTECT YOU MY SON.
          NO FOOD... NO WATER... JUST THE COLD.
          I'M SO... ALONE.'
          """
        // Conditional description: Only works if the 'livingRoom' is lit.
        case Some(item) if item.name == "report" =>
          if !adventure.livingRoomLit then
            "It's too dark in this room to read the report. You need to turn a light on."
          else
            // This text provides the final puzzle hints.
            s"""
            You read the police report. It's about a local girl who disappeared a month ago. The case went cold.
            **The report notes the family was uncooperative, particularly the new stepfather.**

            As you finish reading, two questions burn into your mind:
            1. Where is the girl?
            2. What did this to her?

            You get a chilling feeling that you must find the answers to these questions in the final room to win the game.

            Hint: 1. You should try to **examine** everything before you choose some command on it
                   2. When you know the answers, just answer them. No need of order.
            """

        // Default cases
        // For any other item.
        case Some(item) if this.has(item.name) =>
          s"You look closely at the ${item.name}.\n${item.description}" // Item is in inventory
        case Some(item) =>
          s"You look at the ${item.name}. ${item.description}" // Item is in the room
        case None =>
          "You don't see that here, and you're not carrying it."
  end examine


  /** Returns a string listing all the items the player is carrying. */
  def inventory: String =
    if this.items.isEmpty then
      "You are empty-handed."
    else
      "You are carrying:\n" + this.items.keys.mkString("\n")

  /** Checks if the player is carrying an item with the given name. */
  def has(itemName: String): Boolean =
    this.items.contains(itemName)


  // --- STORY & PUZZLE METHODS ---

  /** Pours the contents of a target item. This action makes noise. */
  def pour(target: String): String =
    if this.has(target) then
      s"You can't pour the $target while you're holding it. Try dropping it first."
    // Puzzle: Pour the vase in the living room
    else if this.location == adventure.livingRoom && target == "vase" then
      this.location.removeItem("vase") match
        case Some(vaseItem) =>
          // Add the hidden items to the room
          this.location.addItem(adventure.woodAmulet)
          this.location.addItem(adventure.report)
          this.location.addItem(adventure.remote)
          "You pour out the vase. A musty smell fills the air. A wooden bird, a police report, and a TV remote fall out." + this.makeNoise() // <-- Triggers noise!
        case None =>
          "There is no vase here to pour."
    else
      "You can't pour that."

  /** Opens a target item. (This is a quiet action). */
  def open(target: String): String =
    if this.has(target) then
      s"You can't open the $target while you're holding it. Try dropping it first."

    // Puzzle: Open the pot in the kitchen
    else if this.location == adventure.kitchen && target == "pot" then
      this.location.removeItem("pot") match
        case Some(_) =>
          this.location.addItem(adventure.fireAmulet) // Add the hidden item
          "You manage to pry open the sealed pot. A small, warm 'fire amulet' is inside."
        case None => "There is no pot here to open."

    // Puzzle: Open the bin in the bathroom
    else if this.location == adventure.bathroom && target == "bin" then
      this.location.removeItem("bin") match
        case Some(_) =>
          this.location.addItem(adventure.waterAmulet) // Add the hidden item
          "You open the bin. It's... gross. But you see a 'water amulet' inside."
        case None => "There is no bin here to open."

    // Puzzle: Open the closet (the main puzzle)
    else if this.location == adventure.mainBedroom && target == "closet" then
      // Check if player has all 5 "memento" items
      if this.has("wood_amulet") && this.has("metal_amulet") && this.has("fire_amulet") && this.has("water_amulet") && this.has("earth_amulet") then
        // Success: Unlock the final room
        this.location.setNeighbor("east", adventure.keyRoom)
        "You touch the 5-symbol mechanism. The 5 items you've collected—the carved bird, the locket, the warm stone, the blue charm, and the heavy rock—begin to glow in your bag." +
        "\nThey aren't amulets... they're her memories." +
        "\nAs you hold them, the cold energy from the lock seems to shatter. A hidden latch clicks open. The back of the closet is a secret door!"
      else
        // Failure: Missing items
        "It's locked with a strange 5-symbol mechanism that feels cold. You feel you are missing... something. Pieces of a story."
    else
      "You can't open that."

  /** Breaks a target item. This action makes noise. */
  def break(target: String): String =
    // Puzzle: Break the door in the living room
    if this.location == adventure.livingRoom && (target == "door" || target == "lock") then
      if this.has("hammer") then
        // This command re-routes to 'use hammer' for code reuse.
        this.use("hammer")
      else
        // Trying to break it without the hammer
        "You slam your shoulder against the door, but it's solid. You'll need a tool to break this." + this.makeNoise() // <-- Triggers noise!

    else if !this.has("hammer") && (target == "door" || target == "lock") then
      "You'll need a tool to break that."
    else
      "You can't break that."

  /** Uses an item from the inventory. */
  def use(itemName: String): String =
    if !this.has(itemName) then
      s"You don't have a ${itemName}."

    // --- Puzzle: Mirror Challenge ---
    else if itemName == "mirror" && this.location == adventure.bathroom then
      adventure.mirrorChallengeActive = true
      adventure.playerIsGirl = true // This flag is required to enter the room.
      adventure.mirrorChallengeTimer = 3 // Give the player 3 turns.
      adventure.kitchen.setNeighbor("north", adventure.bedroom2) // Temporarily unlock the door.
      """
      You stare into the fogged mirror. For a moment, the fog clears, and your reflection changes.
      **You see the terrified, wounded face of a young girl.**

      You *are* her.

      You feel her panic. 'He's coming... I have to hide...'

      A door clicks open to the north. You feel this connection to her won't last.
      **You must get to her room (north of the kitchen) within 3 turns** before the presence fades and the door seals again!
      """

    // --- Puzzle: Break Door (using the hammer) ---
    else if itemName == "hammer" && this.location == adventure.livingRoom then
      this.location.setNeighbor("north", adventure.mainBedroom) // Permanently unlock the door.
      "You use the hammer to smash the deadbolt on the north door. It swings open." + this.makeNoise() // <-- Triggers noise!

    // --- Puzzle: Light Lamp ---
    else if itemName == "lamp" && this.location == adventure.keyRoom then
      if adventure.keyRoomLit then
        "The room is already lit by the lamp."
      else
        // Set the "lit" flag, which reveals new items and descriptions.
        adventure.keyRoomLit = true
        // Update the room's description now that it's lit.
        adventure.keyRoom.description = "The lamp's small flame illuminates the room. You see human bones in the corner and a message scrawled on the wall."
        // Add the hidden items to the room.
        adventure.keyRoom.addItem(adventure.bone)
        adventure.keyRoom.addItem(adventure.wall)
        "You light the oil lamp. The darkness recedes, revealing the grim contents of the room."

    // --- Default Case ---
    else
      s"You can't use the ${itemName} here."

  /** Lifts a target item. This action makes noise. */
  def lift(target: String): String =
    // Puzzle: Lift the bed in the main bedroom
    if this.location == adventure.mainBedroom && target == "bed" then
      this.location.removeItem("bed") // Remove the "bed" so it can't be lifted again.
      this.location.addItem(adventure.earthAmulet) // Add the hidden item.
      "You lift the heavy mattress. Taped underneath, you find an 'earth amulet'!" + this.makeNoise() // <-- Triggers noise!
    else
      "You can't lift that."

  /** Turns on a target item. (This is a quiet action). */
  def turnOn(target: String): String =
    // Puzzle: Turn on the TV with the remote
    if target == "remote" && this.has("remote") && this.location == adventure.livingRoom then
      // Set the "lit" flag, which allows the 'report' to be read.
      adventure.livingRoomLit = true
      adventure.livingRoom.description = "You turn on the TV. The bright light fills the living room, chasing the shadows away."
      "You turn on the TV. The room is now brightly lit. You can probably read that report now.\nWith the light on, you notice the heavy deadbolt on the north door. It looks like it could be broken open."
    else
      "That doesn't do anything."

  /** Reads a target item. This is an alias for 'examine'. */
  def read(target: String): String =
    // This is a special "alias" command for the 'report'
    if (target == "report") then
      this.examine(target) // Just call the 'examine' logic
    else
      "You can't read that. Try 'examine' instead."

  /** Provides a list of all available commands. */
  def help(): String =
    """
    Available commands:
    go [direction]  - Moves to a new area.
    get [item]      - Picks up an item.
    drop [item]     - Drops an item.
    inventory       - Lists your carried items.
    examine [item]  - Looks closely at an item.
    rest            - Takes a short while. Can offer a brief moment of clarity, but be careful... you're not alone.
    quit            - Quits the game.

    Puzzle commands:
    pour [item]
    open [item]
    break [item]
    use [item]
    lift [item]
    turnon [item]
    read [item]
    answer [text]
    hide            - (Sometimes you need to be quiet.)

    Hint:
        1. You have to read the report!
        2. Pick up all possible items.
        3. Any 'typo' mistake has its cost.
    """

  // --- HIDE COMMAND ---
  /** Hides the player. This is the "safe" command after making noise. */
  def hide(): String =
    // This command does nothing except prevent the "noise" lose condition.
    "You duck behind some furniture, holding your breath. The house is silent.\nYou wait... and nothing happens.\nYou seem tobe safe. For now."
  // --- END HIDE COMMAND ---

  /** Attempts to answer one of the final questions. */
  def answer(guess: String): String =
    val formattedGuess = guess.toLowerCase.trim

    // Player must be in the final room *and* have lit the lamp.
    if this.location != adventure.keyRoom then
      "You can't answer from here. You must be in the secret room to understand the truth."
    else if !adventure.keyRoomLit then // Check if room is lit
      "It's too dark to answer. You need to light the room first."

    // Check for the answer to "Where is the girl?"
    else if formattedGuess.contains("secret room") || formattedGuess.contains("here") then
      adventure.answeredQ1 = true // Set the win condition flag
      "You realize where she is. She's here."
    // Check for the answer to "What did this to her?"
    else if formattedGuess.contains("hunger") || formattedGuess.contains("thirst") || formattedGuess.contains("loneliness") then
      adventure.answeredQ2 = true // Set the win condition flag
      "You realize what... or who... did this. Hunger, thirst, and loneliness."
    // A hint for a common wrong answer.
    else if formattedGuess.contains("step father") || formattedGuess.contains("stepfather") then
      "The second question is WHAT, not WHO!"
    else
      // Default wrong answer.
      "That doesn't seem to be the right answer."

  /** Returns a brief description of the player’s state, for debugging purposes. */
  override def toString = "Now at: " + this.location.name

end Player