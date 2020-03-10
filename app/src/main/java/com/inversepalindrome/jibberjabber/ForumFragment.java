/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ForumFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.fasterxml.uuid.Generators;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
    private OpenTopicListener listener;
    private FirebaseDatabase database;
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

        initializeTopicViewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_forum, container, false);

        topicView = view.findViewById(R.id.forum_topic_view);
        startTopicButton = view.findViewById(R.id.forum_start_topic_button);

        setupTopicView();
        setupTopicCallbacks();

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

    public void setOpenTopicListener(OpenTopicListener listener) {
        this.listener = listener;
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

                final String uID = Generators.randomBasedGenerator().generate().toString();

                final TopicModel topicModel = new TopicModel(uID, title, body, senderID, username);

                addTopicToDatabase(topicModel);
                listener.onOpenTopic(topicModel);
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

    private void initializeTopicViewAdapter() {
        Query query = database.getReference().child(Constants.DATABASE_FORUM_NODE).orderByChild("timeStamp");

        FirebaseRecyclerOptions<TopicModel> options = new FirebaseRecyclerOptions.Builder<TopicModel>
                ().setQuery(query, TopicModel.class).build();

        topicViewAdapter = new TopicViewAdapter(options, listener);
    }

    private void setupTopicView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        topicView.setLayoutManager(linearLayoutManager);
        topicView.setItemAnimator(new DefaultItemAnimator());
        topicView.setAdapter(topicViewAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(topicView.getContext(),
                linearLayoutManager.getOrientation());

        topicView.addItemDecoration(dividerItemDecoration);
    }

    private void setupTopicCallbacks() {
        startTopicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStartTopicDialog();
            }
        });
    }

    public interface OpenTopicListener {
        void onOpenTopic(TopicModel topicModel);
    }
}