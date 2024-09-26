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
        LOG.info("Behandle request")

        // Implementere en metode som sjekker om vi kan angripe noen
        val nextAttackActions = mutableListOf<PlayResponse>()
        val nextMoveActions = mutableListOf<PlayResponse>()

        val friendlyUnits = state.friendlyUnits
        val enemyUnits = state.enemyUnits

        val enemyUnitsPositions = enemyUnits.map { it.x to it.y }
        val occupiedCells = friendlyUnits.map { it.x to it.y } + enemyUnitsPositions

        var attackActionsAvailable: Int = state.attackActionsAvailable
        var moveActionsAvailable: Int = state.moveActionsAvailable

        for (unit in friendlyUnits) {
            LOG.info("Starter runden til enhet med id=${unit.id}")
            LOG.info("Antall angrep vi kan gjøre: $attackActionsAvailable")
            LOG.info("Antall bevegelser vi kan gjøre: $moveActionsAvailable")

            val enemy = isEnemyNextToMe(state, unit)

            if (enemy != null) {
                // Angrip
                LOG.info("Angriper enemy=${enemy.id} ved siden av oss")
                if (attackActionsAvailable > 0 && unit.attacks > 0) {
                    attackActionsAvailable--
                    unit.attacks--
                    nextAttackActions.add(
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
                    unit.x,
                    unit.y,
                    state.boardSize
                )
                LOG.info("Neste celle vi flytter til: $nextCell")
                if (moveActionsAvailable > 0 && unit.moves > 0) {
                    moveActionsAvailable--
                    unit.moves--
                    nextMoveActions.add(
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

        return nextAttackActions + nextMoveActions
    }

    private fun isEnemyNextToMe(state: GameState, currentPlayer: Unit): Unit? {
        return state.enemyUnits.firstOrNull { enemyUnit ->
            if (isLeftToUnit(currentPlayer, enemyUnit) ||
                isRightToUnit(currentPlayer, enemyUnit) ||
                isAboveUnit(currentPlayer, enemyUnit) ||
                isBelowUnit(currentPlayer, enemyUnit)
            ) {
                LOG.info("Fant en enemy ved siden av oss. Vår posisjon: (${currentPlayer.x}, ${currentPlayer.y}), enemy posisjon: (${enemyUnit.x}, ${enemyUnit.y})")
                return enemyUnit
            } else {
                return null
            }
        }
    }

    private fun isLeftToUnit(currentPlayer: Unit, enemyUnit: Unit): Boolean {
        return enemyUnit.x == currentPlayer.x - 1 && enemyUnit.y == currentPlayer.y
    }

    private fun isRightToUnit(currentPlayer: Unit, enemyUnit: Unit): Boolean {
        return enemyUnit.x == currentPlayer.x + 1 && enemyUnit.y == currentPlayer.y
    }

    private fun isAboveUnit(currentPlayer: Unit, enemyUnit: Unit): Boolean {
        return enemyUnit.x == currentPlayer.x && enemyUnit.y == currentPlayer.y - 1
    }

    private fun isBelowUnit(currentPlayer: Unit, enemyUnit: Unit): Boolean {
        return enemyUnit.x == currentPlayer.x && enemyUnit.y == currentPlayer.y + 1
    }

    private fun findNextCellToMoveTo(
        occupiedCells: List<Pair<Int, Int>>,
        enemyPosititions: List<Pair<Int, Int>>,
        x: Int,
        y: Int,
        boardSize: BoardSize
    ): Pair<Int, Int> {
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

        return possibleCells
            .filterNot { occupiedCells.contains(it) }
            .minByOrNull { cell ->
                enemyPosititions.minOf { enemy ->
                    findDistance(cell, enemy)
                }
            } ?: (x to y)
    }

    private fun findDistance(cell1: Pair<Int, Int>, cell2: Pair<Int, Int>): Int {
        return Math.abs(cell1.first - cell2.first) + Math.abs(cell1.second - cell2.second)
    }
}
