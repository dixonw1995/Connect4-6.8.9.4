package com.example.daft.connect4;

import android.util.Log;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by dixon on 2/12/2016.
 */

public class GameReader extends DefaultHandler {
    private List<Turn> turns;
    private byte width;
    private byte height;
    private List<Byte> players;
    private byte player;
    private byte col;
    private byte row;
    private byte value;
    private boolean finish;

    GameReader() {
        turns = new ArrayList<>();
        width = 0;
        height = 0;
        players = new ArrayList<>();
        finish = false;
    }

    Connect4Game gameFactory() {
        while (!finish) {}
        Connect4Game game = new Connect4Game(width, height, players);
        game.setTurns(turns);
        return game;
    }

    @Override
    public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("game")) {
            width = Byte.parseByte(attributes.getValue("width"));
            height = Byte.parseByte(attributes.getValue("height"));
            for (String player: attributes.getValue("players").split(", ")){
                players.add(Byte.parseByte(player));
            }
        }
        if (qName.equalsIgnoreCase("turn")) {
            player = -1;
            col = -1;
            row = -1;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName.toLowerCase()) {
            case "turn":
                if (player > -1 && col > -1 && row > -1)
                    turns.add(new Turn(player, col, row));
                break;
            case "player":
                player = value;
                break;
            case "column":
                col = value;
                break;
            case "row":
                row = value;
                break;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        value = Byte.parseByte(new String(ch, start, length));
    }

    public void endDocument() {
        finish = true;
    }

    public static void main(String[] args){
        try {
            File inputFile = new File("input.txt");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GameReader gameReader = new GameReader();
            saxParser.parse(inputFile, gameReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}