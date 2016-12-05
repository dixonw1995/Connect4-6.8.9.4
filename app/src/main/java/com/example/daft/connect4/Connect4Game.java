package com.example.daft.connect4;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by dixon on 1/12/2016.
 */

public class Connect4Game {
    private byte[][] board;
    private List<Byte> players;
    private List<Turn> turns;
    public static final byte EMPTY = 0;
    public static final byte AI = 41;
    public static final byte NEXT = 0;
    public static final byte DRAW = 1;
    private static final String LOG = "Game";

    //Constructor
    Connect4Game(int width, int height, List<Byte> players) {
        Log.i(LOG, "Constructing game");
        board = new byte[width][height];
        for (byte[] col: board) {
            Arrays.fill(col, (byte) 0);
        }
        this.players = players;
        turns = new ArrayList<>();
    }

    @Override
    public Connect4Game clone() {
        Log.i(LOG, "Cloning game");
        List<Byte> players = this.players.subList(0, this.players.size());
        Connect4Game cloneGame = new Connect4Game(width(), height(), players);
        cloneGame.setTurns(turns);
        cloneGame.resume();
        return cloneGame;
    }

    static Connect4Game gameFactory(String record) {
        File recordFile = new File(record);
        Log.i(LOG, String.format("Reading record file %s",record));
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GameReader gameReader = new GameReader();
            saxParser.parse(recordFile, gameReader);
            Connect4Game game = gameReader.gameFactory();
            game.resume();
            Log.i(LOG, "Deleting record");
            recordFile.delete();
            return game;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void resume() {
        Log.i(LOG, "Replaying record");
        resume(0);
    }
    private void resume(int index) {
        if (index >= turns.size()) return;
        Turn turn = turns.get(index);
        byte player = turn.getPlayer();
        byte col = turn.getColumn();
        byte row = turn.getRow();
        Log.i(LOG, String.format("Replay turn%d: %d put a disc at [%d, %d]",
                index, player, col, row));
        board[col][row] = player;
        resume(index+1);
    }

    //Put a disc into the board
    boolean put(byte player, byte col) {
        byte row = bottom(col);
        if (row == -1) return false;
        board[col][row] = player;
        Turn turn = new Turn(player, col, row);
        Log.i(LOG, String.format("%d is putting a disc at [%d, %d]",
                player, col, row));
        turns.add(turn);
        return true;
    }

    private byte bottom(byte col) {
        return bottom(col, (byte) 0);
    }
    private byte bottom(byte col, byte row) {
        if (row >= height()) return -1;
        if (board[col][row] == EMPTY) {
            return row;
        }
        return bottom(col, (byte) (row+1));
    }

    //Judge the game
    byte judge() {
        Log.i(LOG, "Judging");
        return judge(lastTurn());
    }
    private byte judge(Turn turn) {
        byte player = turn.getPlayer();
        byte col = turn.getColumn();
        byte row = turn.getRow();
        if (win(player, col, row)) {
            Log.i(LOG, String.format("%d wins", player));
            return player;
        }
        if (turns.size() == width() * height()) {
            Log.i(LOG, "Draw");
            return DRAW;
        }
        Log.i(LOG, "Next turn");
        return NEXT;
    }

    private boolean win(byte player, byte col, byte row) {
        //win in any direction
        return winV(player, col, row, (byte) 0, false) ||
                winH(player, col, row, (byte) 0, false) ||
                winL(player, col, row, (byte) 0, false) ||
                winR(player, col, row, (byte) 0, false);
    }
    //Win vertically
    private boolean winV(byte player, byte col, byte row, byte connect, boolean reversed){
        if (row < 0 || row >= height() ||
                board[col][row] != player) {
            if (reversed) return false; //both heads are blocked
            //one head's blocked, go back
            return winV(player, col, (byte) (row-connect-1), connect, true);
        }
        connect += 1;
        if (connect == 4) return true;
        if (!reversed)
            return winV(player, col, (byte) (row+1), connect, reversed);
        return winV(player, col, (byte) (row-1), connect, reversed);
    }
    //Win horizontally
    private boolean winH(byte player, byte col, byte row, byte connect, boolean reversed){
        if (col < 0 || col >= width() ||
                board[col][row] != player) {
            if (reversed) return false; //both heads are blocked
            //one head's blocked, go back
            return winH(player, (byte) (col-connect-1), row, connect, true);
        }
        connect += 1;
        if (connect == 4) return true;
        if (!reversed)
            return winH(player, (byte) (col+1), row, connect, reversed);
        return winH(player, (byte) (col-1), row, connect, reversed);
    }
    //Win left-diagonally
    private boolean winL(byte player, byte col, byte row, byte connect, boolean reversed){
        if (row < 0 || row >= height() ||
                col < 0 || col >= width() ||
                board[col][row] != player) {
            if (reversed) return false; //both heads are blocked
            //one head's blocked, go back
            return winL(player, (byte) (col+connect+1), (byte) (row-connect-1), connect, true);
        }
        connect += 1;
        if (connect == 4) return true;
        if (!reversed)
            return winL(player, (byte) (col-1), (byte) (row+1), connect, reversed);
        return winL(player, (byte) (col+1), (byte) (row-1), connect, reversed);
    }
    //Win right-diagonally
    private boolean winR(byte player, byte col, byte row, byte connect, boolean reversed){
        if (row < 0 || row >= height() ||
                col < 0 || col >= width() ||
                board[col][row] != player) {
            if (reversed) return false; //both heads are blocked
            //one head's blocked, go back
            return winR(player, (byte) (col-connect-1), (byte) (row-connect-1), connect, true);
        }
        connect += 1;
        if (connect == 4) return true;
        if (!reversed)
            return winR(player, (byte) (col+1), (byte) (row+1), connect, reversed);
        return winR(player, (byte) (col-1), (byte) (row-1), connect, reversed);
    }

    String toXML() {
        return String.format("<game width='%d' height='%d' players='%d, %d'>" +
                "%s" +
                "</game>", width(), height(), players.get(0), players.get(1), toXML(0));
    }

    private String toXML(int index) {
        if (index == turns.size()) {
            return "";
        }
        return turns.get(index).toXML(index) + toXML(index + 1);
    }

    public byte[][] getBoard() {
        return board;
    }
    public short size() {
        return (short) (width() * height());
    }
    public byte width() {
        return (byte) board.length;
    }
    public byte height() {
        return (byte) board[0].length;
    }
    public List<Byte> getPlayers() {
        return players;
    }
    List<Turn> getTurns() {
        return turns;
    }
    public Turn lastTurn() {
        return turns.get(turns.size()-1);
    }
    void setTurns(List<Turn> turns) {
        this.turns = turns;
    }
    byte currentPlayer() {
        return players.get(turns.size() % players.size());
    }

    public static void main(String[] argv) {
        List<Byte> players = new ArrayList<>();
        //Human have no constant ID, randomly add other than (0, 1, 41)
        players.add((byte) 11);//PvP
        players.add((byte) 22);
        Connect4Game game = new Connect4Game(7, 6, players);
    }
}