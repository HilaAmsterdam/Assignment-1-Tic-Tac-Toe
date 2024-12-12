package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private data class GameState(
        val currentPlayer: String = "X",
        val gameOver: Boolean = false,
        val scores: Pair<Int, Int> = Pair(0, 0)
    )

    private data class Views(
        val mainMenu: View,
        val gameLayout: View,
        val board: List<List<TextView>>,
        val startGameButton: Button,
        val newGameButton: Button,
        val playerOneScore: TextView,
        val playerTwoScore: TextView,
        val currentPlayerView: TextView,
        val resultMessage: TextView
    )

    private lateinit var views: Views
    private var gameState = GameState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupClickListeners()
        resetBoard()
    }

    private fun initializeViews() {
        views = Views(
            mainMenu = findViewById(R.id.main_menu_layout),
            gameLayout = findViewById(R.id.game_layout),
            board = listOf(
                listOf(R.id.one, R.id.two, R.id.three),
                listOf(R.id.four, R.id.five, R.id.six),
                listOf(R.id.seven, R.id.eight, R.id.nine)
            ).map { row -> row.map { findViewById(it) } },
            startGameButton = findViewById(R.id.startNewGameButton),
            newGameButton = findViewById(R.id.start_new_game_button),
            playerOneScore = findViewById(R.id.player_one_score),
            playerTwoScore = findViewById(R.id.player_two_score),
            currentPlayerView = findViewById(R.id.current_player),
            resultMessage = findViewById(R.id.game_result_message)
        )
    }

    private fun setupClickListeners() {
        views.startGameButton.setOnClickListener { showGameLayout() }
        views.newGameButton.setOnClickListener { resetGame() }
        views.board.flatten().forEach { cell ->
            cell.setOnClickListener { handleMove(cell) }
        }
    }

    private fun showGameLayout() {
        views.mainMenu.visibility = View.GONE
        views.gameLayout.visibility = View.VISIBLE
        resetGame()
    }

    private fun handleMove(cell: TextView) {
        if (cell.text.isNotEmpty() || gameState.gameOver) return

        cell.apply {
            text = gameState.currentPlayer
            setTextColor(getColor(if (gameState.currentPlayer == "X") R.color.x_color else R.color.o_color))
        }

        checkGameStatus()?.let { result ->
            handleGameEnd(result)
        } ?: switchPlayer()
    }

    private fun checkGameStatus(): GameResult? = when {
        isWinningMove() -> GameResult.Win(gameState.currentPlayer)
        isBoardFull() -> GameResult.Draw
        else -> null
    }

    private fun isWinningMove(): Boolean {
        val board = views.board
        val player = gameState.currentPlayer

        val winningCombinations = listOf(
            // Rows
            { board[0][0].text == player && board[0][1].text == player && board[0][2].text == player },
            { board[1][0].text == player && board[1][1].text == player && board[1][2].text == player },
            { board[2][0].text == player && board[2][1].text == player && board[2][2].text == player },
            // Columns
            { board[0][0].text == player && board[1][0].text == player && board[2][0].text == player },
            { board[0][1].text == player && board[1][1].text == player && board[2][1].text == player },
            { board[0][2].text == player && board[1][2].text == player && board[2][2].text == player },
            // Diagonals
            { board[0][0].text == player && board[1][1].text == player && board[2][2].text == player },
            { board[0][2].text == player && board[1][1].text == player && board[2][0].text == player }
        )

        return winningCombinations.any { it() }
    }

    private fun isBoardFull(): Boolean =
        views.board.flatten().none { it.text.isEmpty() }

    private sealed class GameResult {
        data class Win(val player: String) : GameResult()
        object Draw : GameResult()
    }

    private fun handleGameEnd(result: GameResult) {
        when (result) {
            is GameResult.Win -> {
                updateScores()
                displayResult("${result.player} Wins!")
            }
            is GameResult.Draw -> displayResult("It's a Tie!")
        }
        gameState = gameState.copy(gameOver = true)
        views.newGameButton.visibility = View.VISIBLE
    }

    private fun switchPlayer() {
        gameState = gameState.copy(
            currentPlayer = if (gameState.currentPlayer == "X") "O" else "X"
        )
        updateCurrentPlayerDisplay()
    }

    private fun updateScores() {
        gameState = gameState.copy(
            scores = when (gameState.currentPlayer) {
                "X" -> Pair(gameState.scores.first + 1, gameState.scores.second)
                else -> Pair(gameState.scores.first, gameState.scores.second + 1)
            }
        )
        updateScoreDisplay()
    }

    private fun updateScoreDisplay() {
        views.playerOneScore.text = "Player 1 Points: ${gameState.scores.first}"
        views.playerTwoScore.text = "Player 2 Points: ${gameState.scores.second}"
    }

    private fun updateCurrentPlayerDisplay() {
        views.currentPlayerView.text = "Current Player: ${gameState.currentPlayer}"
    }

    private fun displayResult(message: String) {
        views.resultMessage.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun resetGame() {
        gameState = gameState.copy(
            currentPlayer = "X",
            gameOver = false
        )
        resetBoard()
        views.apply {
            newGameButton.visibility = View.GONE
            resultMessage.visibility = View.GONE
        }
        updateCurrentPlayerDisplay()
    }

    private fun resetBoard() {
        views.board.flatten().forEach { cell ->
            cell.text = ""
        }
    }
}