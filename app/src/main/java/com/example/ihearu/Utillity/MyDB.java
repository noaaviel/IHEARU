package com.example.ihearu.Utillity;

import androidx.annotation.NonNull;

import com.example.ihearu.LoginAndMain.Song;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class MyDB {

    public static void read(CallBack_oneSong oneSong) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("songs");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Song> songs = new ArrayList<>();
                for (DataSnapshot s: snapshot.getChildren()){
                    Song song = s.getValue(Song.class);
                    songs.add(song);
                }


                oneSong.dataReady(songs.get(new Random().nextInt(songs.size())));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public interface CallBack_oneSong{
        void dataReady(Song song);
    }

    public interface CallBack_Songs{
        void dataReady(ArrayList<Song> songs);
    }

    public static void getAllSongs(CallBack_Songs callBack_songs) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("songs");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Song> songs = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        Song s = child.getValue(Song.class);
                        songs.add(s);
                    } catch (Exception ex) {}

                }
                if (callBack_songs != null) {
                    callBack_songs.dataReady(songs);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
