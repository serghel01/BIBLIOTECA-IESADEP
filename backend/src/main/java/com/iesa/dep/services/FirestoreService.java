package com.iesa.dep.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.iesa.dep.models.Book;
import com.iesa.dep.models.Loan;
import com.iesa.dep.models.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {

    private Firestore db() {
        try { return FirestoreClient.getFirestore(); }
        catch(Exception e){ return null; }
    }

    // Users
    public List<User> listUsers(String q) throws ExecutionException, InterruptedException {
        Firestore f = db(); if(f==null) return new ArrayList<>();
        CollectionReference col = f.collection("users");
        ApiFuture<QuerySnapshot> future = (q==null || q.isBlank()) ? col.get() : col.whereEqualTo("document", q).get();
        List<User> out = new ArrayList<>();
        for(DocumentSnapshot ds: future.get().getDocuments()){
            User u = ds.toObject(User.class);
            u.id = ds.getId();
            out.add(u);
        }
        return out;
    }
    public String createUser(User u) throws Exception {
        Firestore f = db(); if(f==null) throw new Exception("Firestore no disponible");
        ApiFuture<DocumentReference> ref = f.collection("users").add(u);
        return ref.get().getId();
    }
    public void deleteUser(String id) throws Exception {
        Firestore f = db(); if(f==null) throw new Exception("Firestore no disponible");
        f.collection("users").document(id).delete();
    }

    // Books
    public List<Book> listBooks(String q) throws ExecutionException, InterruptedException {
        Firestore f = db(); if(f==null) return new ArrayList<>();
        CollectionReference col = f.collection("books");
        ApiFuture<QuerySnapshot> future = (q==null || q.isBlank()) ? col.get() : col.whereEqualTo("isbn", q).get();
        List<Book> out = new ArrayList<>();
        for(DocumentSnapshot ds: future.get().getDocuments()){
            Book b = ds.toObject(Book.class);
            b.id = ds.getId();
            out.add(b);
        }
        return out;
    }
    public String createBook(Book b) throws Exception {
        Firestore f = db(); if(f==null) throw new Exception("Firestore no disponible");
        ApiFuture<DocumentReference> ref = f.collection("books").add(b);
        return ref.get().getId();
    }
    public void deleteBook(String id) throws Exception {
        Firestore f = db(); if(f==null) throw new Exception("Firestore no disponible");
        f.collection("books").document(id).delete();
    }

    // Loans (very basic)
    public List<Loan> listLoans() throws ExecutionException, InterruptedException {
        Firestore f = db(); if(f==null) return new ArrayList<>();
        ApiFuture<QuerySnapshot> future = f.collection("loans").get();
        List<Loan> out = new ArrayList<>();
        for(DocumentSnapshot ds: future.get().getDocuments()){
            Loan l = ds.toObject(Loan.class);
            l.id = ds.getId();
            out.add(l);
        }
        return out;
    }
    public String createLoan(Loan l) throws Exception {
        Firestore f = db(); if(f==null) throw new Exception("Firestore no disponible");
        ApiFuture<DocumentReference> ref = f.collection("loans").add(l);
        return ref.get().getId();
    }
    public void returnLoan(String id) throws Exception {
        Firestore f = db(); if(f==null) throw new Exception("Firestore no disponible");
        f.collection("loans").document(id).update("returned", true);
    }
}
