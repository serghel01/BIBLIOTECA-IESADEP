package com.iesa.dep.controllers;
import com.iesa.dep.models.Loan;
import com.iesa.dep.services.FirestoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/loans")
public class LoansController {

    @Autowired
    FirestoreService fs;

    @GetMapping
    public ResponseEntity<?> list() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(fs.listLoans());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Loan l, @RequestHeader(value="Authorization", required=false) String auth) throws Exception {
        if(!isAdmin(auth)) return ResponseEntity.status(401).build();
        String id = fs.createLoan(l);
        return ResponseEntity.ok(java.util.Map.of("id", id));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<?> ret(@PathVariable String id, @RequestHeader(value="Authorization", required=false) String auth) throws Exception {
        if(!isAdmin(auth)) return ResponseEntity.status(401).build();
        fs.returnLoan(id);
        return ResponseEntity.ok().build();
    }

    private boolean isAdmin(String auth){
        if(auth==null || !auth.startsWith("Bearer ")) return false;
        String token = auth.substring(7);
        try {
            java.lang.reflect.Field f = Class.forName("com.iesa.dep.controllers.AuthController").getDeclaredField("tokens");
            f.setAccessible(true);
            java.util.Map<String,String> tokens = (java.util.Map<String,String>)f.get(null);
            return tokens.containsKey(token);
        } catch(Exception e){ return false; }
    }
}
