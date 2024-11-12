package com.example.finalproject_socialnetwork.activities.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.model.Chat;
import com.example.finalproject_socialnetwork.model.ChatMessage;
import com.example.finalproject_socialnetwork.model.Pet;
import com.example.finalproject_socialnetwork.model.PetOwner;
import com.example.finalproject_socialnetwork.model.Professional;
import com.example.finalproject_socialnetwork.model.User;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    public static final String OTHER_USER_UID_EXTRA = "EXTRA_OTHER_USER_UID";
    public static final String PET_ID_EXTRA = "EXTRA_PET_ID";

    private LinearLayout topLinearLayout;
    private CircleImageView petImageView;
    private TextView petNameText;
    private TextView partnerNameText;
    private MaterialButton sendMessageButton;
    private EditText editTextMessage;
    private RecyclerView recyclerViewMessages;

    private User currentUser;
    private User otherUser;
    private Pet pet;
    private Chat chat;
    private Map<String, ChatMessage> chatMessageMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        findViews();
        topLinearLayout.setOnClickListener(v -> onTopLinearLayoutClick());
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        sendMessageButton.setOnClickListener(v -> onSendMessageButtonClick());

        String otherUserUidExtra = getIntent().getStringExtra(OTHER_USER_UID_EXTRA);
        String petIdExtra = getIntent().getStringExtra(PET_ID_EXTRA);

        if(FirebaseUtils.assertUserSignedIn(this)) {
            loadUserDataFromFirebase(otherUserUidExtra, petIdExtra);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtils.assertUserSignedIn(this);
    }


    private void findViews() {
        topLinearLayout = findViewById(R.id.topLinearLayout);
        petImageView = findViewById(R.id.petImageView);
        petNameText = findViewById(R.id.petNameText);
        partnerNameText = findViewById(R.id.partnerNameText);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        editTextMessage = findViewById(R.id.editTextMessage);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

    }

    private void loadUserDataFromFirebase(String otherUserUidExtra, String petIdExtra) {
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtils.USERS_REALTIME_DB_PATH)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshotChild: snapshot.getChildren()) {
                            User user = FirebaseUtils.getUserByDataSnapshot(dataSnapshotChild);

                            if(user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                                currentUser = user;
                            } else if(user.getUid().equals(otherUserUidExtra)) {
                                otherUser = user;
                            }
                        }

                        loadPetFromFirebase(petIdExtra);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatActivity.this, "Failed fetching user data", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void loadPetFromFirebase(String petIdExtra) {
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtils.PETS_REALTIME_DB_PATH)
                .child(petIdExtra)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pet = snapshot.getValue(Pet.class);
                        loadChatFromFirebase();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatActivity.this, "Failed fetching pet data", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void loadChatFromFirebase() {
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtils.CHATS_REALTIME_DB_PATH)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshotChild: snapshot.getChildren()) {
                            Chat chat = dataSnapshotChild.getValue(Chat.class);

                            if(chat.getPetOwnerUid().equals(getPetOwner().getUid())
                                && chat.getProfessionalUid().equals(getProfessional().getUid())
                                && chat.getPetId().equals(pet.getId())) {
                                ChatActivity.this.chat = chat;
                                break;
                            }
                        }

                        if(chat == null) {
                            addNewChatToDatabase();
                        } else {
                            loadChatMessages();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadChatMessages() {
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtils.CHAT_MESSAGES_REALTIME_DB_PATH)
                .orderByChild("chatId")
                .equalTo(chat.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatMessageMap = new HashMap<>();

                        for(DataSnapshot dataSnapshotChild: snapshot.getChildren()) {
                            ChatMessage chatMessage = dataSnapshotChild.getValue(ChatMessage.class);
                            chatMessageMap.put(chatMessage.getId(), chatMessage);
                        }

                        initLayout();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addNewChatToDatabase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtils.CHATS_REALTIME_DB_PATH);
        String chatId = databaseReference.push().getKey();
        chat = new Chat(chatId, getPetOwner().getUid(), getProfessional().getUid(), pet.getId());
        databaseReference.child(chatId).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ChatActivity.this, "New chat created", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ChatActivity.this, "Failed creating a new chat", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void initLayout() {
        FirebaseUtils.initPetImageView(petImageView, pet.getId());
        petNameText.setText(pet.getName());
        partnerNameText.setText(otherUser.getName());

        List<ChatMessage> chatMessages = new ArrayList<>(chatMessageMap.values());
        chatMessages.sort(new Comparator<ChatMessage>() {
            @Override
            public int compare(ChatMessage c1, ChatMessage c2) {
                return Long.compare(c1.getTimestamp(), c2.getTimestamp());
            }
        });

        recyclerViewMessages.setAdapter(new MessageAdapter(chatMessages));
        sendMessageButton.setEnabled(true);
        editTextMessage.setEnabled(true);
    }

    private PetOwner getPetOwner() {
        return currentUser.getType().equals(User.USER_TYPE_PET_OWNER) ? (PetOwner)currentUser : (PetOwner)otherUser;
    }

    private Professional getProfessional() {
        return currentUser.getType().equals(User.USER_TYPE_PROFESSIONAL) ? (Professional) currentUser : (Professional)otherUser;
    }

    private User getUserByUid(String uid) {
        return currentUser.getUid().equals(uid) ? currentUser : otherUser;
    }

    private void onTopLinearLayoutClick() {
        if(pet == null) { // not loaded yet
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.pet_dialog_display, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        FirebaseUtils.initPetImageView(dialogView.findViewById(R.id.petImageView), pet.getId());
        ((TextView)dialogView.findViewById(R.id.petNameTextView)).setText(pet.getName());
        ((TextView)dialogView.findViewById(R.id.petKindTextView)).setText(pet.getKind());
        ((TextView)dialogView.findViewById(R.id.petGenderTextView)).setText(pet.getGender());
        ((TextView)dialogView.findViewById(R.id.petAgeTextView)).setText(String.valueOf(pet.calculateAge()));
        ((TextView)dialogView.findViewById(R.id.petBreedTextView)).setText(pet.getBreed());
        ((TextView)dialogView.findViewById(R.id.petBirthDateTextView)).setText(pet.getBirthDate().toString());
        ((TextView)dialogView.findViewById(R.id.petWeightTextView)).setText(String.valueOf(pet.getWeight()));

        alertDialog.show();
    }

    private void onSendMessageButtonClick() {
        String messageContent = editTextMessage.getText().toString().trim();

        if(messageContent.isEmpty()) {
            return;
        }

        sendMessageButton.setEnabled(false);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtils.CHAT_MESSAGES_REALTIME_DB_PATH);

        String chatMessageId = databaseReference.push().getKey();
        ChatMessage chatMessage = new ChatMessage(chatMessageId, chat.getId(), currentUser.getUid(), messageContent,
                System.currentTimeMillis());
        databaseReference.child(chatMessageId).setValue(chatMessage)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        sendMessageButton.setEnabled(true);

                        if(task.isSuccessful()) {
                            editTextMessage.setText("");
                        } else {
                            Toast.makeText(ChatActivity.this, "Failed sending message, please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
        private static final int VIEW_TYPE_CURRENT_USER = 0;
        private static final int VIEW_TYPE_OTHER_USER = 1;

        private List<ChatMessage> chatMessageList;

        public MessageAdapter(List<ChatMessage> chatMessageList) {
            this.chatMessageList = chatMessageList;
        }


        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == VIEW_TYPE_CURRENT_USER) {
                View view = LayoutInflater.from(ChatActivity.this).inflate(R.layout.sent_message_chat_list_item, parent, false);
                return new MessageViewHolder(view);
            } else {
                View view = LayoutInflater.from(ChatActivity.this).inflate(R.layout.received_message_chat_list_item, parent, false);
                return new MessageViewHolder(view);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(chatMessageList.get(position).getAuthorUid().equals(currentUser.getUid())) {
                return VIEW_TYPE_CURRENT_USER;
            } else {
                return VIEW_TYPE_OTHER_USER;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            ChatMessage chatMessage = chatMessageList.get(position);

            holder.senderNameText.setText(getUserByUid(chatMessage.getAuthorUid()).getName());
            holder.messageContentText.setText(chatMessage.getContent());
            holder.timestampText.setText(chatMessage.timeStampTimeString());
        }

        @Override
        public int getItemCount() {
            return chatMessageList.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            private TextView senderNameText;
            private TextView messageContentText;
            private TextView timestampText;

            public MessageViewHolder(@NonNull View itemView) {
                super(itemView);
                senderNameText = itemView.findViewById(R.id.senderNameText);
                messageContentText = itemView.findViewById(R.id.messageContentText);
                timestampText = itemView.findViewById(R.id.timestampText);

            }
        }

    }
}