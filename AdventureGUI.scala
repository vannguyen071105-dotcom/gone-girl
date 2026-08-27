package o1.adventure

import java.awt.{Dimension, Insets, Point, Font, Color} // <-- IMPORT FONT AND COLOR
import javax.swing.UIManager
import scala.language.adhocExtensions
import scala.swing.*
import scala.swing.event.* // enable extension of Swing classes

/**
 * This singleton object represents a text-only GUI for Adventure game.
 * This is the text-only GUI.
 */
object AdventureGUI extends SimpleSwingApplication:
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)

  def top = new MainFrame:

    // --- ACCESS TO GAME LOGIC ---
    val game = new Adventure()
    val player = game.player

    // --- FONTS AND COLORS ---
    val gameFont = new Font("Georgia", Font.PLAIN, 14) // <-- The new font
    val colorWin = new Color(255, 255, 224)
    val colorDanger = new Color(128, 0, 32)
    val colorDangerText = Color.WHITE
    val colorLose = Color.BLACK
    val colorWinText = Color.BLACK
    val colorLoseText = Color.WHITE
    val colorNormalText = Color.BLACK
    val colorNormalBg = Color.WHITE
    val colorChallenge = new Color(224, 255, 255)
    val colorChallengeText = Color.BLACK

    // --- GUI COMPONENTS ---
    val locationInfo = new TextArea(7, 80):
      editable = false
      wordWrap = true
      lineWrap = true
      font = gameFont // <-- Apply font
    val turnOutput = new TextArea(7, 80):
      editable = false
      wordWrap = true
      lineWrap = true
      font = gameFont // <-- Apply font
    val input = new TextField(40):
      minimumSize = preferredSize
      font = gameFont // <-- Apply font
    this.listenTo(input.keys)
    val turnCounter = new Label():
      font = gameFont // <-- Apply font

    // Create labels separately to apply font
    val locationLabel = new Label("Location:"):
      font = gameFont
    val commandLabel = new Label("Command:"):
      font = gameFont
    val eventsLabel = new Label("Events:"):
      font = gameFont

    // --- EVENT HANDLING ---
    // Listen for the "Enter" key in the input field
    this.reactions += {
      case keyEvent: KeyPressed =>
        if keyEvent.source == this.input && keyEvent.key == Key.Enter && !this.game.isOver then
          val command = this.input.text.trim
          if command.nonEmpty then
            this.input.text = ""
            this.playTurn(command)
    }

    // --- LAYOUT ---
    // Arrange all the components in a grid
    val mainPanel = new GridBagPanel: // <-- Renamed to mainPanel
      import scala.swing.GridBagPanel.Anchor.*
      import scala.swing.GridBagPanel.Fill
      layout += locationLabel -> Constraints(0, 0, 1, 1, 0, 1, NorthWest.id, Fill.None.id, Insets(8, 5, 5, 5), 0, 0)
      layout += commandLabel  -> Constraints(0, 1, 1, 1, 0, 0, NorthWest.id, Fill.None.id, Insets(8, 5, 5, 5), 0, 0)
      layout += eventsLabel   -> Constraints(0, 2, 1, 1, 0, 0, NorthWest.id, Fill.None.id, Insets(8, 5, 5, 5), 0, 0)
      layout += turnCounter   -> Constraints(0, 3, 2, 1, 0, 0, NorthWest.id, Fill.None.id, Insets(8, 5, 5, 5), 0, 0)

      // --- ScrollPanes Added ---
      // This ensures you can read long messages
      layout += new ScrollPane(locationInfo) -> Constraints(1, 0, 1, 1, 1, 1, NorthWest.id, Fill.Both.id, Insets(5, 5, 5, 5), 0, 0)
      layout += new ScrollPane(turnOutput)   -> Constraints(1, 2, 1, 1, 1, 1, SouthWest.id, Fill.Both.id, Insets(5, 5, 5, 5), 0, 0)
      // --- End ScrollPanes ---

      layout += input         -> Constraints(1, 1, 1, 1, 1, 0, NorthWest.id, Fill.None.id, Insets(5, 5, 5, 5), 0, 0)

    this.contents = mainPanel // <-- Assign panel to contents

    // --- MENU BAR ---
    this.menuBar = new MenuBar:
      contents += new Menu("Program"):
        val quitAction = Action("Quit")( dispose() )
        contents += MenuItem(quitAction)

    // --- GUI INITIALIZATION ---
    // Set up the GUI’s initial state
    this.title = game.title
    this.updateInfo(this.game.welcomeMessage) // Show welcome message
    this.location = Point(50, 50)
    this.minimumSize = Dimension(200, 200)
    this.pack()
    this.input.requestFocusInWindow() // Put cursor in input box


    /** Passes a command to the game logic and updates the GUI. */
    def playTurn(command: String) =
      val turnReport = this.game.playTurn(command)
      // Check if the game ended
      if this.game.isOver then
        this.updateInfo(turnReport) // Show final report
        this.input.enabled = false  // Disable input
      else
        this.updateInfo(turnReport) // Show turn report

    /** Updates the GUI text areas with the latest game state. */
    def updateInfo(info: String) =
      if !this.game.isOver then
        this.turnOutput.text = info
      else
        // On game over, show the final report + the goodbye message
        this.turnOutput.text = info + "\n\n" + this.game.goodbyeMessage
      this.locationInfo.text = this.player.location.fullDescription
      this.turnCounter.text = "Turns played: " + this.game.turnCount

      this.updateAppearance() // <-- CALL NEW METHOD

    /**
     * This new method updates the colors of the GUI based on the game state.
     */
    def updateAppearance() =
      // 1. Determine the correct colors
      // --- MODIFIED ---
      val (bgColor, fgColor) =
        if this.game.isComplete then
          (colorWin, colorWinText) // --- WIN ---
        else if this.game.playerLost then
          (colorLose, colorLoseText) // --- LOSE ---
        else if this.game.mirrorChallengeActive then
          (colorChallenge, colorChallengeText) // --- CHALLENGE ACTIVE ---
        else if this.game.isNoisyActionPending || this.game.restCounter == this.game.restLimit - 1 then
          (colorDanger, colorDangerText) // --- DANGER ---
        else
          (colorNormalBg, colorNormalText) // --- NORMAL ---
      // --- END MODIFIED ---

      // 2. Apply the colors to all components
      this.mainPanel.background = bgColor

      locationInfo.background = bgColor
      locationInfo.foreground = fgColor

      turnOutput.background = bgColor
      turnOutput.foreground = fgColor

      input.background = bgColor
      input.foreground = fgColor

      turnCounter.foreground = fgColor
      locationLabel.foreground = fgColor
      commandLabel.foreground = fgColor
      eventsLabel.foreground = fgColor
    end updateAppearance

  end top

  // Enable this code to work even under the -language:strictEquality compiler option:
  private given CanEqual[Component, Component] = CanEqual.derived
  private given CanEqual[Key.Value, Key.Value] = CanEqual.derived

end AdventureGUI