package com.ezpass.smopaye_mobile.vuesAdmin;

import android.support.annotation.NonNull;

import com.ezpass.smopaye_mobile.RemoteModel.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GoogleUtilisateur {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUsers;
    private List<User> users = new ArrayList<>();

    public interface DataStatus{
        void DataIsLoaded(List<User> users, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public GoogleUtilisateur() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUsers = mDatabase.getReference("Users");
    }

    public void readUser(final DataStatus dataStatus){

        mReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    User user1 = keyNode.getValue(User.class);
                    users.add(user1);
                }
                dataStatus.DataIsLoaded(users, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void addUser(User user, final DataStatus dataStatus){

      String key = mReferenceUsers.push().getKey();
      mReferenceUsers.child(key).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {
              dataStatus.DataIsInserted();
          }
      });
    }



    public void updateUser(String key, User user, final DataStatus dataStatus){

        mReferenceUsers.child(key).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
             dataStatus.DataIsUpdated();
            }
        });
    }

    public void deleteUser(String key, final DataStatus dataStatus){

        mReferenceUsers.child(key).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dataStatus.DataIsDeleted();
            }
        });
    }

}
