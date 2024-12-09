package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Views for Main Menu
    private lateinit var mainMenuLayout: View
    private lateinit var startNewGameButton: Button

    // Views for Game Layout
    private lateinit var gameLayout: View
    private lateinit var board: Array<Array<TextView>>
    private lateinit var startNewGameInGameButton: Button
    private lateinit var playerOneScoreText: TextView
    private lateinit var playerTwoScoreText: TextView
    private lateinit var currentPlayerTextView: TextView
    private lateinit var gameResultMessage: TextView

    // Game Variables
    private var currentPlayer = "X"
    private var gameOver = false
    private var playerOneScore = 0
    private var playerTwoScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Main Menu Views
        mainMenuLayout = findViewById(R.id.main_menu_layout)
        startNewGameButton = findViewById(R.id.startNewGameButton)

        // Initialize Game Layout Views
        gameLayout = findViewById(R.id.game_layout)
        startNewGameInGameButton = findViewById(R.id.start_new_game_button)
        playerOneScoreText = findViewById(R.id.player_one_score)
        playerTwoScoreText = findViewById(R.id.player_two_score)
        currentPlayerTextView = findViewById(R.id.current_player)
        gameResultMessage = findViewById(R.id.game_result_message)


        // Initialize Game Board
        board = arrayOf(
            arrayOf(findViewById(R.id.one), findViewById(R.id.two), findViewById(R.id.three)),
            arrayOf(findViewById(R.id.four), findViewById(R.id.five), findViewById(R.id.six)),
            arrayOf(findViewById(R.id.seven), findViewById(R.id.eight), findViewById(R.id.nine))
        )

        // Set Listeners
        startNewGameButton.setOnClickListener {
            showGameLayout()
        }

        startNewGameInGameButton.setOnClickListener {
            resetGame()
        }

        initializeBoard()
    }

    private fun showGameLayout() {
        mainMenuLayout.visibility = View.GONE
        gameLayout.visibility = View.VISIBLE
        resetGame()
    }

    private fun initializeBoard() {
        for (row in board) {
            for (cell in row) {
                cell.text = ""
                cell.setOnClickListener { onCellClicked(cell) }
            }
        }
        updateCurrentPlayer()
    }

    private fun onCellClicked(cell: TextView) {
        if (cell.text.isNotEmpty() || gameOver) return

        cell.text = currentPlayer
        cell.setTextColor(if (currentPlayer == "X") getColor(R.color.x_color) else getColor(R.color.o_color))
        if (checkWin()) {
            gameOver = true
            startNewGameInGameButton.visibility = View.VISIBLE
            updateScores()
            return
        }

        if (checkDraw()) {
            gameOver = true
            startNewGameInGameButton.visibility = View.VISIBLE
            return
        }

        currentPlayer = if (currentPlayer == "X") "O" else "X"
        updateCurrentPlayer()
    }

    private fun displayResult(message: String) {
        gameResultMessage.text = message
        gameResultMessage.visibility = View.VISIBLE
        startNewGameInGameButton.visibility = View.VISIBLE
        gameOver = true
    }


    private fun checkWin(): Boolean {
        // Check rows
        for (i in 0..2) {
            if (board[i][0].text == currentPlayer && board[i][1].text == currentPlayer && board[i][2].text == currentPlayer) {
                displayResult("$currentPlayer Wins!")
                return true
            }
        }

        // Check columns
        for (i in 0..2) {
            if (board[0][i].text == currentPlayer && board[1][i].text == currentPlayer && board[2][i].text == currentPlayer) {
                displayResult("$currentPlayer Wins!")
                return true
            }
        }

        // Check diagonals
        if (board[0][0].text == currentPlayer && board[1][1].text == currentPlayer && board[2][2].text == currentPlayer) {
            displayResult("$currentPlayer Wins!")
            return true
        }
        if (board[0][2].text == currentPlayer && board[1][1].text == currentPlayer && board[2][0].text == currentPlayer) {
            displayResult("$currentPlayer Wins!")
            return true
        }

        return false
    }

    private fun checkDraw(): Boolean {
        for (row in board) {
            for (cell in row) {
                if (cell.text.isEmpty()) return false
            }
        }
        displayResult("It's a Tie!")
        return true
    }

    private fun updateScores() {
        if (currentPlayer == "X") {
            playerOneScore++
            playerOneScoreText.text = "Player 1 Points: $playerOneScore"
        } else {
            playerTwoScore++
            playerTwoScoreText.text = "Player 2 Points: $playerTwoScore"
        }
    }

    private fun updateCurrentPlayer() {
        currentPlayerTextView.text = "Current Player: $currentPlayer"
    }

    private fun resetGame() {
        gameOver = false
        currentPlayer = "X"
        startNewGameInGameButton.visibility = View.GONE
        gameResultMessage.visibility = View.GONE
        initializeBoard()
    }
}
