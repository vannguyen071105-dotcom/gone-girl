package o1.adventure

import scala.collection.mutable.Map

// Reverted to text-only: removed 'imageName'
class Area(var name: String, var description: String):

  private val neighbors = Map[String, Area]()
  private val items = Map[String, Item]()

  /** Returns the area that can be reached from this area by moving in the given direction. The result
    * is returned in an `Option`; `None` is returned if there is no exit in the given direction. */
  def neighbor(direction: String) = this.neighbors.get(direction)

  /** Adds an exit from this area to the given area. The neighboring area is reached by moving in
    * the specified direction from this area. */
  def setNeighbor(direction: String, neighbor: Area) =
    this.neighbors += direction -> neighbor

  /** Adds exits from this area to the given areas. Calling this method is equivalent to calling
    * the `setNeighbor` method on each of the given direction–area pairs.
    * @param exits  contains pairs consisting of a direction and the neighboring area in that direction
    * @see [[setNeighbor]] */
  def setNeighbors(exits: Vector[(String, Area)]) =
    this.neighbors ++= exits

  /** Removes the exit (neighbor) in the given direction. */
  def removeNeighbor(direction: String) =
    this.neighbors.remove(direction)

  /** Adds an item to the area. */
  def addItem(item: Item) =
    this.items += item.name -> item

  /** Returns the item with the given name from the area, if it's there.
    * This does NOT remove the item.
    * @return the item, wrapped in an `Option`; `None` if no such item was present */
  def getItem(itemName: String): Option[Item] =
    this.items.get(itemName)

  /** Removes the item with the given name from the area, if it's there.
    * @return the item that was removed, wrapped in an `Option`; `None` if no such item was present */
  def removeItem(itemName: String): Option[Item] =
    this.items.remove(itemName)

  /** Returns `true` if the area contains an item with the given name, `false` otherwise. */
  def contains(itemName: String): Boolean =
    this.items.contains(itemName)

  /** Returns a multi-line description of the area as a player sees it. This includes a basic
    * description of the area as well as information about exits and items. */
  def fullDescription: String =
    val exitList = "\n\nExits available: " + this.neighbors.keys.mkString(" ")
    val itemList = if this.items.nonEmpty then "\nYou see here: " + this.items.keys.mkString(" ") else " "
    this.description + itemList + exitList

  /** Returns a single-line description of the area for debugging purposes. */
  override def toString =
    this.name + ": " + this.description.replaceAll("\n", " ").take(150)

end Area