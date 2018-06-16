package com.example.mitko.randomteampicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    private EditText namesEdit;
    private EditText countEdit;
    private Button startButton;
    private Button endButton;
    private TextView teams;

    private List<String> names;
    private int teamsCount;
    private boolean isShuffling;
    private Thread shuffle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.namesEdit = findViewById(R.id.names_editText);
        this.countEdit = findViewById(R.id.count_editText);
        this.startButton = findViewById(R.id.start_shuffle);
        this.endButton = findViewById(R.id.end_shuffle);
        this.teams = findViewById(R.id.teams_textView);

        this.endButton.setEnabled(false);

        this.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                names = new ArrayList<>();
                final String[] namesArr = namesEdit.getText().toString().split(" ");
                Collections.addAll(names, namesArr);

                try {
                    teamsCount = Integer.parseInt(countEdit.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Invalid number", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!isValidInput()) {
                    return;
                }

                startButton.setEnabled(false);
                endButton.setEnabled(true);

                shuffle = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        isShuffling = true;

                        while (isShuffling) {
                            Collections.shuffle(names);
                        }
                    }
                });

                shuffle.start();
            }
        });

        this.endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startButton.setEnabled(true);
                endButton.setEnabled(false);

                isShuffling = false;
                viewTeams(getTeams());
            }
        });
    }

    private boolean isValidInput() {
        if (countEdit.getText().toString().equals("")) {
            Toast.makeText(this, "Input teams count", Toast.LENGTH_LONG).show();
            return false;
        }

        if (teamsCount == 0) {
            Toast.makeText(this, "No teams?", Toast.LENGTH_LONG).show();
            return false;
        }

        if (namesEdit.getText().toString().equals("")) {
            Toast.makeText(this, "Input some names", Toast.LENGTH_LONG).show();
            return false;
        }

        if (teamsCount > names.size()) {
            Toast.makeText(this, "Too many teams", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void viewTeams(List<List<String>> teamsResult) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < teamsResult.size(); i++) {
            sb.append(String.format("Team %d: ", i + 1));
            for (int j = 0; j < teamsResult.get(i).size(); j++) {
                if (j != 0) {
                    sb.append(", ");
                }

                sb.append(teamsResult.get(i).get(j));
            }

            sb.append('\n');
        }

        this.teams.setText(sb.toString());
    }

    private List<List<String>> getTeams() {
        List<List<String>> teamsResult = new ArrayList<>();
        for (int i = 0; i < teamsCount; i++) {
            teamsResult.add(new ArrayList<String>());
        }

        Queue<String> namesQueue = new LinkedList<>(names);

        int i = 0;
        while (!namesQueue.isEmpty()) {
            teamsResult.get(i % teamsCount).add(namesQueue.poll());
            i++;
        }

        return teamsResult;
    }
}
