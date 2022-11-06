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

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import edu.uncc.hw07.databinding.FragmentForumsBinding;

public class ForumsFragment extends Fragment {

    FragmentForumsBinding binding;

    private static final String ARG_USER = "user";

    private FirebaseUser firebaseUser;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter<Forum, ForumHolder> adapter;

    public ForumsFragment() {
        // Required empty public constructor
    }

    public static ForumsFragment newInstance(FirebaseUser user) {
        ForumsFragment fragment = new ForumsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            firebaseUser = getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForumsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonLogout.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            mListener.logout();
        });

        binding.buttonCreateForum.setOnClickListener(v -> mListener.goAddForum());

        binding.forumsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Query query = firebaseFirestore
                .collection("Users")
                .document(firebaseUser.getUid())
                .collection("Forums")
                .orderBy("created_at", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Forum> options = new FirestoreRecyclerOptions.Builder<Forum>()
                .setQuery(query, Forum.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Forum, ForumHolder>(options) {

            @Override
            public void onBindViewHolder(@NonNull ForumHolder holder, int position, @NonNull Forum model) {
                holder.setForum_title(model.getForum_title());
                holder.setCreated_by(model.getUser_name());
                holder.setCreated_by_uid(model.getUser_id(), firebaseUser);
                holder.setForum_text(model.getForum_description());
                holder.setForum_likes(model.getForum_likes(), model.getForum_id());
                holder.setForum_date(model.getCreated_at());
                holder.setForum_id(model.getForum_id(), model.getForum_title(), model.getForum_description(), model.getUser_name());
                holder.position = holder.getLayoutPosition();
            }

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);
            }

            @NonNull
            @Override
            public ForumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_row_item, parent, false);
                return new ForumHolder(view);
            }
        };

        binding.forumsRecyclerView.setAdapter(adapter);

        requireActivity().setTitle(R.string.forums_label);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.buttonCreateForum) {
            mListener.goAddForum();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    ForumsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ForumsListener) context;
    }

    public class ForumHolder extends RecyclerView.ViewHolder {
        private final View view;
        Boolean liked = false;
        int position;

        public ForumHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
        }

        void setForum_title(String forum_title) {
            TextView textView = view.findViewById(R.id.textViewForumTitle);
            textView.setText(forum_title);
        }

        void setCreated_by(String user_name) {
            TextView textView = view.findViewById(R.id.textViewForumCreatedBy);
            textView.setText(user_name);
        }

        void setCreated_by_uid(String created_by_uid, FirebaseUser firebaseUser) {
            ImageView imageViewDelete = view.findViewById(R.id.imageViewDelete);
            imageViewDelete.setEnabled(Objects.equals(created_by_uid, firebaseUser.getUid()));
            imageViewDelete.setVisibility(imageViewDelete.isEnabled() ? View.VISIBLE : View.INVISIBLE);
        }

        void setForum_text(String forum_text) {
            TextView textView = view.findViewById(R.id.textViewForumText);
            textView.setText(forum_text);
        }

        void setForum_likes(int forum_likes, String forum_id) {
            TextView textView = view.findViewById(R.id.textViewForumLikes);
            textView.setText(forum_likes + " Likes | ");

            ImageView imageViewLike = view.findViewById(R.id.imageViewLike);
            imageViewLike.setOnClickListener(view -> {
                if (!liked) {
                    imageViewLike.setImageResource(R.drawable.like_favorite);
                    addLike(forum_id);
                    liked = true;
                    Log.d("demo", "onClick: Post liked");
                } else {
                    imageViewLike.setImageResource(R.drawable.like_not_favorite);
                    removeLike(forum_id);
                    liked = false;
                    Log.d("demo", "onClick: Post unliked");
                }
            });
        }

        void setForum_date(Timestamp forum_date) {
            TextView textView = view.findViewById(R.id.textViewForumDate);
            Date date = forum_date.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());
            String dateFormat = sdf.format(date);
            textView.setText(dateFormat);
        }

        void setForum_id(String forum_id, String forumTitle, String forumText, String forumAuthor) {
            ImageView imageViewDelete = view.findViewById(R.id.imageViewDelete);
            if (imageViewDelete.isEnabled()) {
                imageViewDelete.setOnClickListener(view -> firebaseFirestore
                        .collection("Users")
                        .document(firebaseUser.getUid())
                        .collection("Forums")
                        .document(forum_id)
                        .delete()
                        .addOnSuccessListener(unused -> Log.d("demo", "Forum successfully deleted"))
                        .addOnFailureListener(e -> Log.w("demo", "Error deleting forum", e))
                );
            }

            itemView.setOnClickListener(view -> mListener.goToForum(forum_id, firebaseUser, forumTitle, forumText, forumAuthor));
        }
    }

    public void addLike(String forum_id) {
        firebaseFirestore
                .collection("Users")
                .document(firebaseUser.getUid())
                .collection("Forums")
                .document(forum_id)
                .update("forum_likes", FieldValue.increment(1));

        firebaseFirestore
                .collection("Users")
                .document(firebaseUser.getUid())
                .collection("Forums")
                .document(forum_id)
                .update("likedByUsers", FieldValue.arrayUnion(firebaseUser.getUid()));
    }

    public void removeLike(String forum_id) {
        firebaseFirestore
                .collection("Users")
                .document(firebaseUser.getUid())
                .collection("Forums")
                .document(forum_id)
                .update("forum_likes", FieldValue.increment(-1));

        firebaseFirestore
                .collection("Users")
                .document(firebaseUser.getUid())
                .collection("Forums")
                .document(forum_id)
                .update("likedByUsers", FieldValue.arrayRemove(firebaseUser.getUid()));
    }

    interface ForumsListener {
        void goAddForum();

        void logout();

        void goToForum(String forum_id, FirebaseUser user, String forumTitle, String forumText, String forumAuthor);
    }
}