// Homework Assignment 07
// Group22_HW07
// Stephanie Lee Karp & Ken Stanley

package edu.uncc.hw07;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import edu.uncc.hw07.databinding.FragmentForumBinding;

public class ForumFragment extends Fragment {

    FragmentForumBinding binding;

    private static final String ARG_FORUMID = "forumId";
    private static final String ARG_USER = "user";
    private static final String ARG_FORUMTITLE = "forumTitle";
    private static final String ARG_FORUMTEXT = "forumText";
    private static final String ARG_FORUMAUTHOR = "forumAuthor";
    private static final String ARG_USERID = "userId";

    private String forumId;
    private String forumTitle;
    private String forumText;
    private String forumAuthor;
    private String userId;

    final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter<Comment, CommentHolder> adapter;
    FirebaseUser firebaseUser;

    public ForumFragment() {
        // Required empty public constructor
    }

    public static ForumFragment newInstance(String forumId, FirebaseUser user, String forumTitle, String forumText, String forumAuthor, String userId) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FORUMID, forumId);
        args.putParcelable(ARG_USER, user);
        args.putString(ARG_FORUMTITLE, forumTitle);
        args.putString(ARG_FORUMTEXT, forumText);
        args.putString(ARG_FORUMAUTHOR, forumAuthor);
        args.putString(ARG_USERID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            forumId = getArguments().getString(ARG_FORUMID);
            firebaseUser = getArguments().getParcelable(ARG_USER);
            forumTitle = getArguments().getString(ARG_FORUMTITLE);
            forumText = getArguments().getString(ARG_FORUMTEXT);
            forumAuthor = getArguments().getString(ARG_FORUMAUTHOR);
            userId = getArguments().getString(ARG_USERID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.textViewForumTitle.findViewById(R.id.textViewForumTitle);
        binding.textViewForumCreatedBy.findViewById(R.id.textViewForumCreatedBy);
        binding.textViewForumText.findViewById(R.id.textViewForumText);
        binding.textViewCommentsCount.findViewById(R.id.textViewCommentsCount);

        binding.textViewForumTitle.setText(forumTitle);
        binding.textViewForumCreatedBy.setText(forumAuthor);
        binding.textViewForumText.setText(forumText);
        binding.textViewCommentsCount.setText(" Comments");

        binding.buttonSubmitComment.setOnClickListener(v -> {
            String commentText = binding.editTextComment.getText().toString();

            if (commentText.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a comment", Toast.LENGTH_SHORT).show();
            } else
                mListener.createComment(firebaseUser, commentText, forumId, userId);
        });

        binding.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Query query = firebaseFirestore
                .collection("Users")
                .document(userId)
                .collection("Forums")
                .document(forumId)
                .collection("comments")
                .orderBy("created_at", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Comment, CommentHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentHolder holder, int position, @NonNull Comment model) {
                holder.setCommented_by(model.getUser_name());
                holder.setCommented_by_uid(model.getUser_id(), firebaseUser);
                holder.setComment_text(model.getComment_text());
                holder.setComment_dateTime(model.getCreated_at());
                holder.setComment_id(model.getComment_id(), forumId);
            }

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);
            }

            @NonNull
            @Override
            public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row_item, parent, false);
                return new CommentHolder(view);
            }
        };

        binding.commentsRecyclerView.setAdapter(adapter);

        requireActivity().setTitle(R.string.forum_label);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    ForumListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ForumListener) context;
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        private final View view;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
        }

        void setCommented_by(String user_name) {
            TextView textView = view.findViewById(R.id.textViewCommentCreatedBy);
            textView.setText(user_name);
        }

        void setCommented_by_uid(String commented_by_uid, FirebaseUser firebaseUser) {
            ImageView imageViewDelete = view.findViewById(R.id.imageViewDelete);
            imageViewDelete.setEnabled(Objects.equals(commented_by_uid, firebaseUser.getUid()));
            imageViewDelete.setVisibility(imageViewDelete.isEnabled() ? View.VISIBLE : View.INVISIBLE);
        }

        void setComment_text(String comment_text) {
            TextView textView = view.findViewById(R.id.textViewCommentText);
            textView.setText(comment_text);
        }

        void setComment_dateTime(Timestamp comment_dateTime) {
            TextView textView = view.findViewById(R.id.textViewCommentCreatedAt);
            Date date = comment_dateTime.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());
            String dateFormat = sdf.format(date);
            textView.setText(dateFormat);
        }

        void setComment_id(String comment_id, String forum_id) {
            ImageView imageViewDelete = view.findViewById(R.id.imageViewDelete);
            if (imageViewDelete.isEnabled()) {
                imageViewDelete.setOnClickListener(view -> firebaseFirestore
                        .collection("Users")
                        .document(firebaseUser.getUid())
                        .collection("Forums")
                        .document(forum_id)
                        .collection("comments")
                        .document(comment_id)
                        .delete()
                        .addOnSuccessListener(unused -> Log.d("demo", "Comment successfully deleted"))
                        .addOnFailureListener(e -> Log.w("demo", "Error deleting comment", e))
                );
            }
        }
    }

    interface ForumListener {
        void createComment(FirebaseUser firebaseUser, String commentText, String forumId, String userId);
    }
}