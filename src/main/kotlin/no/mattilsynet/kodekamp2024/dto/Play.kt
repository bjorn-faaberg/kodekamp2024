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
    val friendlyUnits: List<Unit>,
    val uuid: String
)

data class Unit(
    val y: Int,
    val moves: Int,
    val maxHealth: Int,
    val attackStrength: Int,
    val id: String,
    val kind: String,
    val health: Int,
    val side: String,
    val armor: Int,
    val x: Int,
    val attacks: Int
)

data class BoardSize(
    val w: Int,
    val h: Int
)
