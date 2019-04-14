package com.webcheckers.appl;

import com.webcheckers.model.Game;
import com.webcheckers.model.Move;
import com.webcheckers.model.Player;
import com.webcheckers.model.ReplayGame;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * GameCenter Class
 * GameCenter retains all available Games that are currently being played.
 */
public class GameCenter {

    //Enumeration for the View Mode
    public enum ViewMode{
        PLAY,
        SPECTATOR,
        REPLAY
    }

    //
    // Private fields
    //

    private HashMap<String, Game> currentGames;
    private int gamesCompleted;
    private HashMap<String, ReplayGame> previousGames;

    /**
     * GameCenter constructor
     */
    public GameCenter( ){
        this.currentGames = new HashMap<>();
        this.gamesCompleted = 0;
        this.previousGames = new HashMap<>();
    }

    /**
     * newGame creates a new checkers game that players can interact with
     * @param redPlayer the player to be red
     * @param whitePlayer the player to be white
     * @return a gameID, that is, a unique string to identify the newly made game
     */
    public String newGame(Player redPlayer, Player whitePlayer){
        String gameId = createGameId(redPlayer, whitePlayer);
        Game newGame = new Game(redPlayer, whitePlayer, gameId);
        newGame.initializeGame();

        currentGames.put(gameId, newGame);

        return gameId;
    }

    /**
     * Gets an active game by their unique gameID
     * @param gameId the unique string to find the game with
     * @return the game identified by the gameID
     */
    public Game getGame(String gameId){
        return currentGames.get(gameId);
    }

    public ReplayGame getReplayGame(String gameId) { return previousGames.get(gameId); }

    public void removeGame(String gameId) {
        currentGames.remove(gameId);
    }

    /**
     * Creates a new, unique ID to identify a game with
     * @param redPlayer the redPlayer
     * @param whitePlayer the whitePlayer
     * @return a new ID, that is, a string
     */
    private String createGameId(Player redPlayer, Player whitePlayer){
        return redPlayer.getName() + "Vs" + whitePlayer.getName();
    }

    private String createFinishedGameId(){
        return "Game #" + gamesCompleted;
    }

    /**
     * Moves a piece of a game
     * @param gameId the gameID of the game
     * @param move how is the piece going to be moved
     * @return true if the movement is successful, false if not
     */
    public boolean requestMove(String gameId, Move move){
        Game game = currentGames.get(gameId);
        return game.makeMove(move);
    }

    /**
     * Submits a turn so the other player can play
     * @param gameId the game ID of the game
     * @return true if successful, false if it is not
     */
    public boolean submitTurn(String gameId){
        Game game = currentGames.get(gameId);
        return game.submitTurn();
    }

    /**
     * Backs-up a move
     * @param gameId the game ID of the game
     * @return true if successful, false if not
     */
    public boolean backupMove(String gameId){
        Game game = currentGames.get(gameId);
        return game.backup();
    }


    /**
     * Determines if 2 players are in a game or not
     * @param player1 the 1st player to determine if they have a game
     * @param player2 the 2nd player to determine if they have a game
     * @return true if either one of the 2 players have a game, and false if both players do not have a game
     */
    public boolean hasGame(Player player1, Player player2) {
        String key1 = player1.getName() + "Vs" + player2.getName();
        String key2 = player2.getName() + "Vs" + player1.getName();

        return currentGames.get(key1) != null || currentGames.get(key2) != null;
    }

    /**
     * Determines if a player is in a game or not
     * @param player the player in question
     * @return true if the player is within a game, false if they are not in a game
     */
    public boolean isInAnyGame(Player player) {
        for( Game game : currentGames.values() ) {
            if( game.isInGame(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if it's a player's turn
     * @param gameId the unique identifier for the game that the player is in
     * @param currentPlayer the current player in question
     * @return true if it is the current player's turn, false if it is not the current player's turn
     */
    public boolean isMyTurn(String gameId, Player currentPlayer){
        Game game = currentGames.get(gameId);
        return game.getActivePlayer().equals(currentPlayer);
    }

    /**
     * Adds a game to the list of finished games
     * @param game the game to be added
     * @param gameId the subsequent game ID
     */
    public void addToPreviousGames(Game game, String gameId){
        gamesCompleted++;

        String previousGameId = createFinishedGameId();
        ReplayGame previousGame = new ReplayGame(game.getRedPlayer(), game.getWhitePlayer(),
                                                 game.getPreviousTurns(), previousGameId);

        previousGames.put(previousGameId, previousGame);

        if(currentGames.containsKey(gameId))
            currentGames.remove(gameId);

    }

    /**
     * Goes through the list of games finished and sorts them by game ID
     * @return a new list the the newly sorted games
     */
    public ArrayList<ReplayGame> sortPreviousGames(){
        ArrayList<ReplayGame> sortedPreviousGames = new ArrayList<>();
        ReplayGame tempGame;

        for(int i = 1; i < previousGames.size() + 1; i++){
            tempGame = previousGames.get("Game #" + i);
            sortedPreviousGames.add(tempGame);
        }

        return sortedPreviousGames;
    }

    /**
     * Determine if the list of finished games has any games.
     * @return true if the list has any games
     */
    public boolean hasPreviousGames(){
        return previousGames.size() > 0;
    }

    public Collection<Game> getCurrentGames(){
        return currentGames.values();
    }
}
