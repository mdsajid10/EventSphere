package org.eventSpheres.DB;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseCustomAuth {
    private DatabaseReference mDatabase;

    public FirebaseCustomAuth() {
        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("admins");
    }

    public void authenticateUser(String name, String enteredPassword) {
        mDatabase.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Iterate over the users with matching name (ideally only one)
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String storedPassword = userSnapshot.child("password").getValue(String.class);

                        // Check if the entered password matches the stored password
                        if (storedPassword.equals(enteredPassword)) {
                            // Password matches, authentication successful
                            System.out.println("Authentication successful for: " + name);
                            // Here you can proceed to access other user data, e.g., email, contactNumber, etc.
                            String email = userSnapshot.child("email").getValue(String.class);
                            String contactNumber = userSnapshot.child("contactNumber").getValue(String.class);
                            String image = userSnapshot.child("image").getValue(String.class);

                            System.out.println("User Email: " + email);
                            System.out.println("User Contact Number: " + contactNumber);
                            System.out.println("User Image: " + image);
                        } else {
                            // Password doesn't match
                            System.out.println("Invalid password.");
                        }
                    }
                } else {
                    // No user found with the given name
                    System.out.println("User not found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors, such as database connection issues
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }
}
