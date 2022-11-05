// Homework Assignment 07
// Group22_HW07
// Stephanie Lee Karp & Ken Stanley

package edu.uncc.hw07;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import edu.uncc.hw07.databinding.FragmentCreateForumBinding;

public class CreateForumFragment extends Fragment {

    FragmentCreateForumBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateForumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonCancel.setOnClickListener(v -> mListener.goForums());

        binding.buttonSubmit.setOnClickListener(v -> {
            String forumTitle = binding.editTextForumTitle.getText().toString();
            String forumDescription = binding.editTextForumDescription.getText().toString();

            if (forumTitle.isEmpty() || forumDescription.isEmpty()) {
                Toast.makeText(getContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show();
            } else {
                mListener.createForum(forumTitle, forumDescription);
            }
        });

        requireActivity().setTitle(R.string.create_forum);
    }

    CreateForumListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreateForumListener) context;
    }

    interface CreateForumListener {
        void goForums();
        void createForum(String forumTitle, String forumDescription);
    }
}