package com.example.daft.connect4;

/**
 * Created by dixon on 1/12/2016.
 */

public class Turn {
    private byte player;
    private byte col;
    private byte row;

    public Turn(byte player, byte col, byte row) {
        this.player = player;
        this.col = col;
        this.row = row;
    }

    public boolean equals(Turn turn) {
        if (player == turn.getPlayer() &&
                col == turn.getColumn() &&
                row == turn.getRow())
            return true;
        return false;
    }

    public byte getPlayer() {
        return player;
    }

    public byte getColumn() {
        return col;
    }

    public byte getRow() {
        return row;
    }

    @Override
    public String toString() {
        return String.format("Player %d put a disc to [col: %d, row: %d]", player, col, row);
    }
    public String toString(int index) {
        return String.format("Turn %d: %s", index, toString());
    }

    public String toXML(int index) {
        return String.format("<turn index='%d'>" +
                "<player>%d</player>" +
                "<column>%d</column>" +
                "<row>%d</row>" +
                "</turn>", index, player, col, row);
    }
}
