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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
    private FirebaseRecyclerAdapter topicViewAdapter;
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

        initializeTopicViewAdapter();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_forum, container, false);

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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        topicViewAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        topicViewAdapter.stopListening();
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
        DatabaseReference topicReference = forumReference.child(topicModel.getTopicID());

        topicReference.setValue(topicModel);

        DatabaseReference topicsReference = database.getReference().child(Constants.DATABASE_TOPICS_NODE);
        topicsReference.child(topicModel.getTopicID());

    }

    private void openTopicActivity(TopicModel topicModel) {
        Intent intent = new Intent(getActivity(), TopicActivity.class);
        intent.putExtra("topic", topicModel);
        startActivity(intent);
    }

    private void initializeTopicViewAdapter(){
        Query query = database.getReference().child(Constants.DATABASE_FORUM_NODE);
        FirebaseRecyclerOptions<TopicModel> options = new FirebaseRecyclerOptions.Builder<TopicModel>
                ().setQuery(query, new SnapshotParser<TopicModel>() {
            @NonNull
            @Override
            public TopicModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new TopicModel(snapshot.child(Constants.DATABASE_TOPIC_ID_NODE).getValue().toString(),
                        snapshot.child(Constants.DATABASE_TITLE_NODE).getValue().toString(),
                        snapshot.child(Constants.DATABASE_BODY_NODE).getValue().toString(),
                        snapshot.child(Constants.DATABASE_SENDER_NODE).getValue().toString(),
                        snapshot.child(Constants.DATABASE_USERNAME_NODE).getValue().toString(),
                        snapshot.child(Constants.DATABASE_TIMESTAMP_NODE).getValue().toString());
            }
        }).build();

        topicViewAdapter = new FirebaseRecyclerAdapter<TopicModel, TopicViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TopicViewHolder holder, int position, @NonNull TopicModel topicModel) {
                holder.setTitleText(topicModel.getTitle());
                holder.setBodyText(topicModel.getBody());
                holder.setUsernameText(topicModel.getUsername());
                holder.setTimeStampText(topicModel.getTimeStamp());
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openTopicActivity(topicModel);
                    }
                });
            }

            @NonNull
            @Override
            public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_topic, parent, false);

                return new TopicViewHolder(view);
            }
        };
    }
}