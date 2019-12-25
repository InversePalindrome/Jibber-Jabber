/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - TopicFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class TopicFragment extends Fragment {
    private ArrayList<PostModel> postModelItems;
    private PostViewAdapter postViewAdapter;
    private RecyclerView postView;
    private TopicModel topicModel;
    private TextView titleText;
    private TextView bodyText;

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

        postModelItems = new ArrayList<>();

        postViewAdapter = new PostViewAdapter(R.layout.item_post, postModelItems, new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

            }
        });

        postView = view.findViewById(R.id.topic_post_view);
        titleText = view.findViewById(R.id.topic_title_text);
        bodyText = view.findViewById(R.id.topic_body_text);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        postView.setLayoutManager(linearLayoutManager);
        postView.setItemAnimator(new DefaultItemAnimator());
        postView.setAdapter(postViewAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(postView.getContext(),
                linearLayoutManager.getOrientation());

        postView.addItemDecoration(dividerItemDecoration);

        titleText.setText(topicModel.title);
        bodyText.setText(topicModel.body);

        return view;
    }
}
