package o1.adventure 

/**
 * The class `Action` represents actions that a player may take in a text adventure game.
 *
 * `Action` objects are constructed on the basis of textual commands and are, in effect,
 * parsers for such commands. An action object is immutable after creation.
 *
 * @param input  a textual in-game command such as “go east” or “rest”
 */
class Action(input: String):

  private val commandText = input.trim.toLowerCase
  private val verb        = commandText.takeWhile( _ != ' ' )
  private val modifiers   = commandText.drop(verb.length).trim

  /**
   * Causes the given player to take the action represented by this object.
   *
   * @param actor  the player who is taking the action
   * @return a description of what happened as a result of the action (in an `Option`)
   * or `None` if the command was not recognized.
   */
  def execute(actor: Player): Option[String] =
    this.verb match
      case "go"        => Some(actor.go(this.modifiers))
      case "rest"      => Some(actor.rest())
      case "quit"      => Some(actor.quit())
      case "get"       => Some(actor.get(this.modifiers))
      case "drop"      => Some(actor.drop(this.modifiers))
      case "examine"   => Some(actor.examine(this.modifiers))
      case "inventory" => Some(actor.inventory)
      case "pour"      => Some(actor.pour(this.modifiers))
      case "open"      => Some(actor.open(this.modifiers))
      case "break"     => Some(actor.break(this.modifiers))
      case "use"       => Some(actor.use(this.modifiers))
      case "lift"      => Some(actor.lift(this.modifiers))
      case "turnon"    => Some(actor.turnOn(this.modifiers))
      case "read"      => Some(actor.read(this.modifiers))
      case "help"      => Some(actor.help())
      case "answer"    => Some(actor.answer(this.modifiers))
      case "hide"      => Some(actor.hide()) // The command to avoid the "noise" penalty
      case other       => None

  /** Returns a textual description of the action object, for debugging purposes. */
  override def toString = s"$verb (modifiers: $modifiers)"

end Action