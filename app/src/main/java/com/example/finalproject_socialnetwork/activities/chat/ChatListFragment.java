package com.example.finalproject_socialnetwork.activities.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.model.Chat;
import com.example.finalproject_socialnetwork.model.Pet;
import com.example.finalproject_socialnetwork.model.User;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListFragment extends Fragment {
    private RecyclerView chatListRecyclerView;

    private Map<String, User> userMap;
    private Map<String, Pet> petMap;
    private Map<String, Chat> chatMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_chat_list, container, false);
        findViews(v);
        chatListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(FirebaseUtils.assertUserSignedIn(getActivity())) {
            loadUsersFromFirebase();
        }

        return v;
    }

    private void findViews(View root) {
        chatListRecyclerView = root.findViewById(R.id.chatListRecyclerView);
    }

    private void loadUsersFromFirebase() {
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtils.USERS_REALTIME_DB_PATH)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userMap = new HashMap<>();

                        for(DataSnapshot dataSnapshotChild: snapshot.getChildren()) {
                            User user = FirebaseUtils.getUserByDataSnapshot(dataSnapshotChild);
                            userMap.put(user.getUid(), user);
                        }

                        loadPetsFromFirebase();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if(getActivity() != null) {
                            Toast.makeText(getActivity(), "Failed fetching user data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void loadPetsFromFirebase() {
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtils.PETS_REALTIME_DB_PATH)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        petMap = new HashMap<>();

                        for(DataSnapshot dataSnapshotChild: snapshot.getChildren()) {
                            Pet pet = dataSnapshotChild.getValue(Pet.class);
                            petMap.put(pet.getId(), pet);
                        }

                        loadChatsFromFirebase();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if(getActivity() != null) {
                            Toast.makeText(getActivity(), "Failed fetching pet data", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void loadChatsFromFirebase() {
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtils.CHATS_REALTIME_DB_PATH)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatMap = new HashMap<>();

                        for(DataSnapshot dataSnapshotChild: snapshot.getChildren()) {
                            Chat chat = dataSnapshotChild.getValue(Chat.class);

                            if(chat.getProfessionalUid().equals(FirebaseAuth.getInstance().getUid())
                                || chat.getPetOwnerUid().equals(FirebaseAuth.getInstance().getUid())) {
                                chatMap.put(chat.getId(), chat);
                            }

                            initRecyclerView();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if(getActivity() != null) {
                            Toast.makeText(getActivity(), "Failed fetching chat data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void initRecyclerView() {
        List<Chat> chatList = new ArrayList<>(chatMap.values());

        chatList.sort((c1, c2) -> {
            String otherUserUidC1 = getOtherUser(c1).getUid();
            String otherUserUidC2 = getOtherUser(c2).getUid();
            return (otherUserUidC1 + c1.getPetOwnerUid()).compareTo(otherUserUidC2 + c2.getPetOwnerUid());
        });

        chatListRecyclerView.setAdapter(new ChatAdapter(chatList));
    }

    private User getOtherUser(Chat chat) {
        return FirebaseAuth.getInstance().getUid().equals(chat.getPetOwnerUid())
                ? userMap.get(chat.getProfessionalUid())
                : userMap.get(chat.getPetOwnerUid());
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private List<Chat> chatList;

        public ChatAdapter(List<Chat> chatList) {
            this.chatList = chatList;
        }


        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.chat_list_item, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            Chat chat = chatList.get(position);
            Pet pet = petMap.get(chat.getPetId());
            User otherUser = getOtherUser(chat);

            holder.otherUserNameText.setText(String.format("With %s" , otherUser.getName()));
            holder.otherUserPhoneText.setText(String.format("Contact phone: %s" , otherUser.getPhone()));
            holder.petDetailsText.setText(String.format("About %s, the %s, %s", pet.getName(), pet.getBreed() , pet.getKind()));
            FirebaseUtils.initUserProfileImageView(holder.otherUserImageView, otherUser.getUid());

            holder.rootView.setOnClickListener(v -> onChatClicked(chat));
        }

        @Override
        public int getItemCount() {
            return chatList.size();
        }

        private void onChatClicked(Chat chat) {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra(ChatActivity.OTHER_USER_UID_EXTRA, getOtherUser(chat).getUid());
            intent.putExtra(ChatActivity.PET_ID_EXTRA, chat.getPetId());
            startActivity(intent);
        }


        private class ChatViewHolder extends RecyclerView.ViewHolder {
            private View rootView;
            private CircleImageView otherUserImageView;
            private TextView petDetailsText;
            private TextView otherUserNameText;
            private TextView otherUserPhoneText;

            public ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                rootView = itemView;
                otherUserImageView = itemView.findViewById(R.id.otherUserImageView);
                petDetailsText = itemView.findViewById(R.id.petDetailsText);
                otherUserNameText = itemView.findViewById(R.id.otherUserNameText);
                otherUserPhoneText = itemView.findViewById(R.id.otherUserPhoneText);
            }
        }
    }
}