package no.mattilsynet.kodekamp2024.dto

data class GameState(
    val turnNumber: Int,
    val yourId: String,
    val enemyUnits: List<Unit>,
    val boardSize: BoardSize,
    val player2: String,
    val player1: String,
    val moveActionsAvailable: Int,
    val attackActionsAvailable: Int,
    var friendlyUnits: MutableList<Unit>,
    val uuid: String
)

data class Unit(
    val y: Int,
    var moves: Int,
    val maxHealth: Int,
    val attackStrength: Int,
    val id: String,
    val kind: String,
    val health: Int,
    val side: String,
    val armor: Int,
    val x: Int,
    var attacks: Int,
    val range: Int,
    val isPiercing: Boolean
)

data class BoardSize(
    val w: Int,
    val h: Int
)
