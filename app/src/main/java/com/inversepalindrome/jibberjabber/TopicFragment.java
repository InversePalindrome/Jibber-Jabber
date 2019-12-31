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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    private FirebaseUser currentUser;
    private OnPostSelectedListener postListener;
    private ArrayList<PostModel> postModelItems;
    private FirebaseRecyclerAdapter postViewAdapter;
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

        database = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        postModelItems = new ArrayList<>();

        Bundle bundle = getArguments();
        topicModel = bundle.getParcelable("topic");

        initializePostViewAdapter(topicModel.getTopicID());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_topic, container, false);

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

        titleText.setText(topicModel.getTitle());
        bodyText.setText(topicModel.getBody());
        usernameText.setText(topicModel.getUsername());
        timeStampText.setText(topicModel.getTimeStamp());

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String post = postEntry.getText().toString();
                final String timeStamp = DateUtility.getDate(System.currentTimeMillis() / 1000);

                PostModel postModel = new PostModel(post, currentUser.getUid(), currentUser.getDisplayName(), timeStamp);

                addPostToDatabase(postModel);

                updateUIAfterPost();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        postViewAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        postViewAdapter.stopListening();
    }

    public void setOnPostListener(OnPostSelectedListener postListener){
        this.postListener = postListener;
    }

    private void addPostToDatabase(PostModel postModel) {
        DatabaseReference topicsReference = database.getReference().child(Constants.DATABASE_TOPICS_NODE);
        DatabaseReference topicReference = topicsReference.child(topicModel.getTopicID());

        DatabaseReference postReference = topicReference.push();

        postReference.setValue(postModel);
    }

    public void onUsernameSelected(PostModel postModel) {
        DatabaseReference usersReference = database.getReference().child(Constants.DATABASE_USERS_NODE);
        DatabaseReference userReference = usersReference.child(postModel.getSenderID());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);

                postListener.onUsernameSelected(userModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateUIAfterPost() {
        postEntry.setText("");

        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void initializePostViewAdapter(String topicID){
         DatabaseReference topicsReference = database.getReference().child(Constants.DATABASE_TOPICS_NODE);
         Query query = topicsReference.child(topicID);

        FirebaseRecyclerOptions<PostModel> options = new FirebaseRecyclerOptions.Builder<PostModel>()
                .setQuery(query, new SnapshotParser<PostModel>() {
                    @NonNull
                    @Override
                    public PostModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new PostModel(snapshot.child(Constants.DATABASE_BODY_NODE).getValue().toString(),
                                snapshot.child(Constants.DATABASE_SENDER_NODE).getValue().toString(),
                                snapshot.child(Constants.DATABASE_USERNAME_NODE).getValue().toString(),
                                snapshot.child(Constants.DATABASE_TIMESTAMP_NODE).getValue().toString());
                    }
                }).build();

        postViewAdapter = new FirebaseRecyclerAdapter<PostModel, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostModel postModel) {
                holder.setBodyText(postModel.getBody());
                holder.setUsernameText(postModel.getUsername());
                holder.setTimeStampText(postModel.getTimeStamp());
                holder.setUsernameOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onUsernameSelected(postModel);
                    }
                });
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);

                return new PostViewHolder(view);
            }
        };
    }

    public interface OnPostSelectedListener{
        void onUsernameSelected(UserModel userModel);
    }
}
