/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - TopicFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class TopicFragment extends Fragment {
    private FirebaseDatabase database;
    private ArrayList<PostModel> postModelItems;
    private PostViewAdapter postViewAdapter;
    private RecyclerView postView;
    private TopicModel topicModel;
    private TextView titleText;
    private TextView bodyText;
    private TextView usernameText;
    private TextView timeStampText;
    private EditText postEntry;
    private ImageButton postButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        topicModel = bundle.getParcelable("topic");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_topic, container, false);

        database = FirebaseDatabase.getInstance();

        postModelItems = new ArrayList<>();

        postViewAdapter = new PostViewAdapter(R.layout.item_post, postModelItems, new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

            }
        });

        postView = view.findViewById(R.id.topic_post_view);
        titleText = view.findViewById(R.id.topic_title_text);
        bodyText = view.findViewById(R.id.topic_body_text);
        usernameText = view.findViewById(R.id.topic_username_text);
        timeStampText = view.findViewById(R.id.topic_timestamp_text);
        postEntry = view.findViewById(R.id.topic_post_entry);
        postButton = view.findViewById(R.id.topic_post_button);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        postView.setLayoutManager(linearLayoutManager);
        postView.setItemAnimator(new DefaultItemAnimator());
        postView.setAdapter(postViewAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(postView.getContext(),
                linearLayoutManager.getOrientation());

        postView.addItemDecoration(dividerItemDecoration);

        titleText.setText(topicModel.title);
        bodyText.setText(topicModel.body);
        usernameText.setText(topicModel.username);
        timeStampText.setText(topicModel.timeStamp);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String post = postEntry.getText().toString();

                addPost(post);
                updateUIAfterPost();
            }
        });

        loadPostsFromDatabase();

        return view;
    }

    private void addPost(String body) {
        final String username = usernameText.getText().toString();
        final String timeStamp = DateUtility.getDate(System.currentTimeMillis() / 1000);

        PostModel postModel = new PostModel(body, topicModel.senderID, username, timeStamp);

        addPostToDatabase(postModel);
        addPostToView(postModel);
    }

    private void addPostToDatabase(PostModel postModel) {
        DatabaseReference topicsReference = database.getReference().child(Constants.DATABASE_TOPICS_NODE);
        DatabaseReference topicReference = topicsReference.child(topicModel.topicID);

        DatabaseReference postReference = topicReference.push();

        postReference.setValue(postModel);
    }

    private void addPostToView(PostModel postModel) {
        postModelItems.add(postModel);
        postViewAdapter.notifyDataSetChanged();
    }

    private void updateUIAfterPost() {
        postEntry.setText("");

        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void loadPostsFromDatabase() {
        DatabaseReference topicsReference = database.getReference().child(Constants.DATABASE_TOPICS_NODE);
        DatabaseReference topicReference = topicsReference.child(topicModel.topicID);

        topicReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PostModel postModel = postSnapshot.getValue(PostModel.class);
                    addPostToView(postModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
