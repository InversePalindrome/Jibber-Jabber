/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ForumFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.fasterxml.uuid.Generators;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ForumFragment extends Fragment {
    private String senderID;
    private String username;
    private FirebaseDatabase database;
    private ArrayList<TopicModel> topicModelItems;
    private TopicViewAdapter topicViewAdapter;
    private RecyclerView topicView;
    private FloatingActionButton startTopicButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        senderID = user.getUid();
        username = user.getDisplayName();

        database = FirebaseDatabase.getInstance();

        topicModelItems = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_forum, container, false);

        topicViewAdapter = new TopicViewAdapter(R.layout.item_topic, topicModelItems, new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                int itemPosition = topicView.getChildLayoutPosition(view);
                TopicModel topicModelItem = topicModelItems.get(itemPosition);

                openTopicActivity(topicModelItem);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        topicView = view.findViewById(R.id.forum_topic_view);
        startTopicButton = view.findViewById(R.id.forum_start_topic_button);

        topicView.setLayoutManager(linearLayoutManager);
        topicView.setItemAnimator(new DefaultItemAnimator());
        topicView.setAdapter(topicViewAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(topicView.getContext(),
                linearLayoutManager.getOrientation());

        topicView.addItemDecoration(dividerItemDecoration);

        startTopicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStartTopicDialog();
            }
        });

        loadTopicsFromDatabase();

        return view;
    }

    private void openStartTopicDialog() {
        AlertDialog.Builder startTopicDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
        startTopicDialogBuilder.setTitle("Start Topic");

        final View startTopicView = getLayoutInflater().inflate(R.layout.dialog_start_topic, null);
        startTopicDialogBuilder.setView(startTopicView);

        startTopicDialogBuilder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final EditText titleEntry = startTopicView.findViewById(R.id.start_topic_title_entry);
                final EditText bodyEntry = startTopicView.findViewById(R.id.start_topic_body_entry);

                final String title = titleEntry.getText().toString();
                final String body = bodyEntry.getText().toString();

                final String timeStamp = DateUtility.getDate(System.currentTimeMillis() / 1000);

                final String uID = Generators.randomBasedGenerator().generate().toString();
                final TopicModel topicModel = new TopicModel(uID, title, body, senderID, username, timeStamp);

                addTopicModelToView(topicModel);
                addTopicToDatabase(topicModel);
                openTopicActivity(topicModel);
            }
        });
        startTopicDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog startTopicDialog = startTopicDialogBuilder.create();
        startTopicDialog.show();
    }

    private void addTopicToDatabase(TopicModel topicModel) {
        DatabaseReference forumReference = database.getReference().child(Constants.DATABASE_FORUM_NODE);
        DatabaseReference topicReference = forumReference.child(topicModel.topicID);

        topicReference.setValue(topicModel);

        DatabaseReference topicsReference = database.getReference().child(Constants.DATABASE_TOPICS_NODE);
        topicsReference.child(topicModel.topicID);

    }

    private void openTopicActivity(TopicModel topicModel) {
        Intent intent = new Intent(getActivity(), TopicActivity.class);
        intent.putExtra("topic", topicModel);
        startActivity(intent);
    }

    private void addTopicModelToView(TopicModel topicModel) {
        topicModelItems.add(topicModel);
        topicViewAdapter.notifyDataSetChanged();
    }

    private void loadTopicsFromDatabase() {
        DatabaseReference forumReference = database.getReference().child(Constants.DATABASE_FORUM_NODE);
        forumReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot topicDataSnapshot : dataSnapshot.getChildren()) {
                    TopicModel topicModel = topicDataSnapshot.getValue(TopicModel.class);
                    addTopicModelToView(topicModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}