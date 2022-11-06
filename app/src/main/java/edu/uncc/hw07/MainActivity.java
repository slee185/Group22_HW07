// Homework Assignment 07
// Group22_HW07
// Stephanie Lee Karp & Ken Stanley

package edu.uncc.hw07;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, SignUpFragment.SignUpListener, ForumsFragment.ForumsListener, CreateForumFragment.CreateForumListener, ForumFragment.ForumListener {
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void goCreateNewAccount() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new SignUpFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void authenticate(String username, String password) {
        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Exception exception = task.getException();
                assert exception != null;
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("An Error Occurred")
                        .setMessage(exception.getLocalizedMessage())
                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                        .show();

                return;
            }

            this.firebaseUser = task.getResult().getUser();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rootView, ForumsFragment.newInstance(this.firebaseUser))
                    .commit();
        });
    }

    @Override
    public void createAccount(String name, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(createTask -> {
            if (!createTask.isSuccessful()) {
                Exception exception = createTask.getException();
                assert exception != null;
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("An Error Occurred")
                        .setMessage(exception.getLocalizedMessage())
                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                        .show();

                return;
            }

            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            FirebaseUser user = createTask.getResult().getUser();
            assert user != null;
            user.updateProfile(request).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Exception exception = task.getException();
                    assert exception != null;
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("An Error Occurred")
                            .setMessage(exception.getLocalizedMessage())
                            .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                            .show();

                    return;
                }

                this.firebaseUser = user;

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.rootView, ForumsFragment.newInstance(this.firebaseUser))
                        .commit();
            });
        });
    }

    @Override
    public void goLogin() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void logout() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void goToForum(String forum_id, FirebaseUser user, String forumTitle, String forumText, String forumAuthor, String userId) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, ForumFragment.newInstance(forum_id, firebaseUser, forumTitle, forumText, forumAuthor, userId))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goAddForum() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new CreateForumFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goForums() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void createForum(String forumTitle, String forumDescription) {
        Forum forum = new Forum(firebaseUser.getUid(), firebaseUser.getDisplayName(), forumTitle, forumDescription);

        firebaseFirestore
                .collection("Users")
                .document(forum.getUser_id())
                .collection("Forums")
                .document(forum.getForum_id())
                .set(forum)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Exception exception = task.getException();
                        assert exception != null;
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("An Error Occurred")
                                .setMessage(exception.getLocalizedMessage())
                                .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                                .show();
                        return;
                    }
                    goForums();
                });
    }

    @Override
    public void createComment(FirebaseUser firebaseUser, String commentText, String forumId, String userId) {
        Comment comment = new Comment(firebaseUser.getUid(), firebaseUser.getDisplayName(), commentText);

        firebaseFirestore
                .collection("Users")
                .document(userId)
                .collection("Forums")
                .document(forumId)
                .collection("comments")
                .document(comment.getComment_id())
                .set(comment)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Exception exception = task.getException();
                        assert exception != null;
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("An Error Occurred")
                                .setMessage(exception.getLocalizedMessage())
                                .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                });
    }
}