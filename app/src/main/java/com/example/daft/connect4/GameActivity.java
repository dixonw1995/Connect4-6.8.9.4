package com.example.daft.connect4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class GameActivity extends AppCompatActivity {

    private List<Byte> players;
    private Connect4Game game;
    private List<ImageView> columns;
    private List<LinearLayout> colContainers;
    private TextView[] pv;
    private MediaPlayer mediaPlayer;
    public static boolean isSoundOff=false;
    private String record;
    private static final String LOG = "Game UI";
    private boolean save = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.i(LOG, "Creating new game");

        players = new ArrayList<>();
        //random id, make sure 2 are different and not 0, 1, 41
        players.add((byte) 11);
        players.add((byte) 22);

        game = new Connect4Game(7, 6, players);

        //resume previous game
        record = getFilesDir().getPath() + "/record.xml";
        File file = new File(record);
        if (file.exists()) {
            AlertDialog.Builder resumeDialog = new AlertDialog.Builder(this);
            resumeDialog.setTitle("Continue previous game?");
            resumeDialog.setPositiveButton("YES", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resumeGame();
                }
            });
            resumeDialog.setNegativeButton("NO", null);
            resumeDialog.show();
        }

        columns = new ArrayList<>();
        columns.add((ImageView) findViewById(R.id.col0));
        columns.add((ImageView) findViewById(R.id.col1));
        columns.add((ImageView) findViewById(R.id.col2));
        columns.add((ImageView) findViewById(R.id.col3));
        columns.add((ImageView) findViewById(R.id.col4));
        columns.add((ImageView) findViewById(R.id.col5));
        columns.add((ImageView) findViewById(R.id.col6));

        colContainers = new ArrayList<>();
        colContainers.add((LinearLayout) findViewById(R.id.contain0));
        colContainers.add((LinearLayout) findViewById(R.id.contain1));
        colContainers.add((LinearLayout) findViewById(R.id.contain2));
        colContainers.add((LinearLayout) findViewById(R.id.contain3));
        colContainers.add((LinearLayout) findViewById(R.id.contain4));
        colContainers.add((LinearLayout) findViewById(R.id.contain5));
        colContainers.add((LinearLayout) findViewById(R.id.contain6));

        pv = new TextView[] { (TextView) findViewById(R.id.p1),
                (TextView) findViewById(R.id.p2) };

        for (ImageView col: columns) {
            col.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    byte col = (byte) columns.indexOf(view);
                    if (game.put(game.currentPlayer(), col)) {
                        Log.i(LOG, String.format("User puts a disc on column%d", col));
                        putDisc(col);
                        byte result = game.judge();
                        //dialog telling result and asking users play again
                        AlertDialog.Builder endDialog = new AlertDialog.Builder(GameActivity.this)
                                .setPositiveButton("Restart", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        recreate();
                                    }
                                })
                                .setNegativeButton("Quit", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                        switch(result) {
                            case Connect4Game.DRAW:
                                Log.i(LOG, "Deleting record");
                                new File(record).delete();
                                save = false;
                                endDialog.setTitle("Draw")
                                        .setMessage("Play again")
                                        .show();
                                break;
                            case Connect4Game.NEXT:
                                break;
                            default: //someone wins
                                Log.i(LOG, "Deleting record");
                                new File(record).delete();
                                save = false;
                                int winner = players.indexOf(result);
                                endDialog.setTitle("Congratulation")
                                        .setMessage(String.format("Player %d Wins", winner+1))
                                        .show();
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG, "On pause");
        if (save && game.getTurns().size() > 0) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            try {
                Log.i(LOG, "Saving record");
                File file = new File(record);
                builder = factory.newDocumentBuilder();
                // Use String reader
                Document document = builder.parse(new InputSource(
                        new StringReader(game.toXML())));

                TransformerFactory tranFactory = TransformerFactory.newInstance();
                Transformer aTransformer = tranFactory.newTransformer();
                Source src = new DOMSource(document);
                file.createNewFile();
                Result dest = new StreamResult(file);
                aTransformer.transform(src, dest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void putDisc(byte col) {
        Log.i(LOG, String.format("Disc falls at column%d", col));
        playSound();
        int currentPlayerIndex = players.indexOf(game.currentPlayer());
        pv[currentPlayerIndex].setVisibility(View.VISIBLE);
        pv[(currentPlayerIndex+1) % pv.length].setVisibility(View.INVISIBLE);
        Log.i(LOG, String.format("It will be Player%d(%d)'s turn",
                currentPlayerIndex, game.currentPlayer()));

        ImageView disc = new ImageView(this);
        disc.setAdjustViewBounds(true);
        disc.setLayoutParams(new LinearLayout.LayoutParams(
                columns.get(col).getWidth(),
                LinearLayout.LayoutParams.WRAP_CONTENT));
        Animation am = new TranslateAnimation(0.0f, 0.0f, -800.0f, 0.0f);
        am.setDuration(1000);
        if (currentPlayerIndex == 1) //this disc is put in last turn
            disc.setImageResource(R.drawable.disc1_transp);
        else disc.setImageResource(R.drawable.disc2_transp);

        disc.startAnimation(am);
        colContainers.get(col).addView(disc, 0);
    }

    public void playSound(){
        if(mediaPlayer==null) {
            mediaPlayer = MediaPlayer.create(GameActivity.this, R.raw.coin);
            if(isSoundOff){
                mediaPlayer.setVolume(0.0f,0.0f);
            }
        }

            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
            mediaPlayer.start();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(GameActivity.this);
        quitDialog.setTitle("Are you sure to quit the game?");

        quitDialog.setPositiveButton("YES", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }});

        quitDialog.setNegativeButton("NO", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                onResume();
            }});

        quitDialog.show();
    }

    public void resumeGame() {
        Log.i(LOG, "Resuming previous game");
        game = Connect4Game.gameFactory(record);
        resumeGame(game.getTurns(), 0);
        int currentPlayerIndex = players.indexOf(game.currentPlayer());
        pv[currentPlayerIndex].setVisibility(View.VISIBLE);
        pv[(currentPlayerIndex+1) % pv.length].setVisibility(View.INVISIBLE);
    }
    public void resumeGame(List<Turn> turns, int index) {
        if (index >= turns.size()) return;
        Turn turn = turns.get(index);
        byte player = turn.getPlayer();
        byte col = turn.getColumn();
        byte row = turn.getRow();
        int currentPlayerIndex = players.indexOf(player);
        Log.i(LOG, String.format("Replay turn%d: Player%d(%d) put a disc at [%d, %d]",
                index, currentPlayerIndex, player, col, row));

        ImageView disc = new ImageView(this);
        disc.setAdjustViewBounds(true);
        disc.setLayoutParams(new LinearLayout.LayoutParams(
                columns.get(col).getWidth(),
                LinearLayout.LayoutParams.WRAP_CONTENT));
        if (currentPlayerIndex == 0) //this disc is put in this turn
            disc.setImageResource(R.drawable.disc1_transp);
        else disc.setImageResource(R.drawable.disc2_transp);
        colContainers.get(col).addView(disc, 0);
        resumeGame(turns, index+1);
    }
}
