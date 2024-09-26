package no.mattilsynet.kodekamp2024.service

import no.mattilsynet.kodekamp2024.dto.BoardSize
import no.mattilsynet.kodekamp2024.dto.GameState
import no.mattilsynet.kodekamp2024.dto.PlayResponse
import no.mattilsynet.kodekamp2024.dto.Unit
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class KodekampService {

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java)
    }

    fun behandleRequest(state: GameState): List<PlayResponse> {
        LOG.info("Behandle request $state")

        // Implementere en metode som sjekker om vi kan angripe noen

        val friendlyUnits = state.friendlyUnits
        val enemyUnits = state.enemyUnits

        val enemyUnitsPositions = enemyUnits.map { it.x to it.y }

        val actionsList = mutableListOf<PlayResponse>()

        var attackActionsAvailable: Int = state.attackActionsAvailable
        var moveActionsAvailable: Int = state.moveActionsAvailable

        for (unit in friendlyUnits) {
            val occupiedCells = friendlyUnits.map { it.x to it.y } + enemyUnitsPositions

            LOG.info("Starter runden til enhet med id=${unit.id} som har posisjon (${unit.x}, ${unit.y})")
            LOG.info("Antall angrep vi kan gjøre: $attackActionsAvailable")
            LOG.info("Antall bevegelser vi kan gjøre: $moveActionsAvailable")

            val enemy = isEnemyCloseToMe(state, unit)

            if (enemy != null) {
                // Angrip
                LOG.info("Angriper enemy=${enemy.id} ved siden av oss")
                if (attackActionsAvailable > 0 && unit.attacks > 0) {
                    attackActionsAvailable--
                    unit.attacks--
                    actionsList.add(
                        PlayResponse(
                            unit = unit.id,
                            action = "attack",
                            x = enemy.x,
                            y = enemy.y
                        )
                    )
                }
            } else {
                LOG.info("Fant ingen enemy ved siden av oss, flytter derfor på unit")
                val nextCell = findNextCellToMoveTo(
                    occupiedCells,
                    enemyPosititions = enemyUnitsPositions,
                    unit,
                    state.boardSize
                )
                LOG.info("Neste celle vi flytter til: $nextCell")
                if (moveActionsAvailable > 0 && unit.moves > 0) {
                    moveActionsAvailable--
                    unit.moves--
                    actionsList.add(
                        PlayResponse(
                            unit = unit.id,
                            action = "move",
                            x = nextCell.first,
                            y = nextCell.second
                        )
                    )
                    state.friendlyUnits[state.friendlyUnits.indexOf(unit)] =
                        unit.copy(x = nextCell.first, y = nextCell.second)
                } else {
                    LOG.info("Ingen flere bevegelser igjen for enhet med id=${unit.id}")
                }
            }

            LOG.info("Enhet med id=${unit.id} sin runde er over")
        }

        if (attackActionsAvailable + moveActionsAvailable > friendlyUnits.size) {
            for (unit in friendlyUnits) {
                val occupiedCells = friendlyUnits.map { it.x to it.y } + enemyUnitsPositions

                LOG.info("Starter runden til enhet med id=${unit.id} som har posisjon (${unit.x}, ${unit.y})")
                LOG.info("Antall angrep vi kan gjøre: $attackActionsAvailable")
                LOG.info("Antall bevegelser vi kan gjøre: $moveActionsAvailable")

                val enemy = isEnemyCloseToMe(state, unit)

                if (enemy != null) {
                    // Angrip
                    LOG.info("Angriper enemy=${enemy.id} ved siden av oss")
                    if (attackActionsAvailable > 0 && unit.attacks > 0) {
                        attackActionsAvailable--
                        unit.attacks--
                        actionsList.add(
                            PlayResponse(
                                unit = unit.id,
                                action = "attack",
                                x = enemy.x,
                                y = enemy.y
                            )
                        )
                    }
                } else {
                    LOG.info("Fant ingen enemy ved siden av oss, flytter derfor på unit")
                    val nextCell = findNextCellToMoveTo(
                        occupiedCells,
                        enemyPosititions = enemyUnitsPositions,
                        unit,
                        state.boardSize
                    )
                    LOG.info("Neste celle vi flytter til: $nextCell")
                    if (moveActionsAvailable > 0 && unit.moves > 0) {
                        moveActionsAvailable--
                        unit.moves--
                        actionsList.add(
                            PlayResponse(
                                unit = unit.id,
                                action = "move",
                                x = nextCell.first,
                                y = nextCell.second
                            )
                        )
                        state.friendlyUnits[state.friendlyUnits.indexOf(unit)] =
                            unit.copy(x = nextCell.first, y = nextCell.second)
                    } else {
                        LOG.info("Ingen flere bevegelser igjen for enhet med id=${unit.id}")
                    }
                }

                LOG.info("Enhet med id=${unit.id} sin runde er over")
            }
        }

        return actionsList
    }

    private fun isEnemyCloseToMe(state: GameState, myUnit: Unit): Unit? {
        var unitToReturn: Unit? = null
        state.enemyUnits.forEach { enemyUnit ->
            LOG.info("Enemy=${enemyUnit.id} positions: (${enemyUnit.x}, ${enemyUnit.y})")
            if (isLeftToUnit(myUnit, enemyUnit) ||
                isRightToUnit(myUnit, enemyUnit) ||
                isAboveUnit(myUnit, enemyUnit) ||
                isBelowUnit(myUnit, enemyUnit)
            ) {
                LOG.info("Fant en enemy ved siden av oss. Vår posisjon: (${myUnit.x}, ${myUnit.y}), enemy posisjon: (${enemyUnit.x}, ${enemyUnit.y})")
                unitToReturn = enemyUnit
            }
        }
        return unitToReturn
    }

    private fun isLeftToUnit(currentPlayer: Unit, enemyUnit: Unit): Boolean {
        val rangeToCheck = if (isArcher(currentPlayer)) 4 else 1
        for (i in 1..rangeToCheck) {
            if (currentPlayer.x == enemyUnit.x - i && currentPlayer.y == enemyUnit.y) {
                LOG.info("isLeftToUnit: true")
                return true
            }
        }
        val isRight = currentPlayer.x == enemyUnit.x - 1 && currentPlayer.y == enemyUnit.y
        LOG.info("isLeftToUnit: $isRight")
        return isRight
    }

    private fun isRightToUnit(currentPlayer: Unit, enemyUnit: Unit): Boolean {
        val rangeToCheck = if (isArcher(currentPlayer)) 4 else 1
        for (i in 1..rangeToCheck) {
            if (currentPlayer.x == enemyUnit.x + i && currentPlayer.y == enemyUnit.y) {
                LOG.info("isRightToUnit: true")
                return true
            }
        }
        val isRight = currentPlayer.x == enemyUnit.x + 1 && currentPlayer.y == enemyUnit.y
        LOG.info("isRightToUnit: $isRight")
        return isRight
    }

    private fun isAboveUnit(currentPlayer: Unit, enemyUnit: Unit): Boolean {
        val rangeToCheck = if (isArcher(currentPlayer)) 4 else 1
        for (i in 1..rangeToCheck) {
            if (currentPlayer.x == enemyUnit.x && currentPlayer.y == enemyUnit.y - i) {
                LOG.info("isAboveUnit: true")
                return true
            }
        }
        val isAbove = currentPlayer.x == enemyUnit.x && currentPlayer.y == enemyUnit.y - 1
        LOG.info("isAboveUnit: $isAbove")
        return isAbove
    }

    private fun isBelowUnit(currentPlayer: Unit, enemyUnit: Unit): Boolean {
        val rangeToCheck = if (isArcher(currentPlayer)) 4 else 1
        for (i in 1..rangeToCheck) {
            if (currentPlayer.x == enemyUnit.x && currentPlayer.y == enemyUnit.y + i) {
                LOG.info("isBelowUnit: true")
                return true
            }
        }
        val isBelow = currentPlayer.x == enemyUnit.x && currentPlayer.y == enemyUnit.y + 1
        LOG.info("isBelowUnit: $isBelow")
        return isBelow
    }

    private fun isArcher(unit: Unit) = unit.kind == "archer"

    private fun findNextCellToMoveTo(
        occupiedCells: List<Pair<Int, Int>>,
        enemyPosititions: List<Pair<Int, Int>>,
        unit: Unit,
        boardSize: BoardSize
    ): Pair<Int, Int> {
        val x = unit.x
        val y = unit.y
        val possibleCells = listOfNotNull(
            // Opp
            if (y - 1 < 0) null else x to y - 1,

            // Ned
            if (y + 1 > boardSize.h - 1) null else x to y + 1,

            // Venstre
            if (x - 1 < 0) null else x - 1 to y,

            // Høyre
            if (x + 1 > boardSize.w - 1) null else x + 1 to y
        )

        LOG.info("Vår position: ($x, $y), mulige celler vi kan flytte til: $possibleCells")

        if (unit.kind == "archer") {
            return possibleCells.filterNot { occupiedCells.contains(it) }.random()
        }

        return possibleCells
            .filterNot { occupiedCells.contains(it) }
            .minByOrNull { cell ->
                enemyPosititions.minOf { enemy -> findDistance(cell, enemy) }
            } ?: (x to y)
    }

    private fun findDistance(cell1: Pair<Int, Int>, cell2: Pair<Int, Int>): Int {
        return Math.abs(cell1.first - cell2.first) + Math.abs(cell1.second - cell2.second)
    }
}
