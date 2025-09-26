package com.iesa.dep.controllers;
import com.iesa.dep.services.FirestoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KpiController {

    @Autowired
    FirestoreService fs;

    @GetMapping("/api/kpi")
    public Object kpi() {
        // Very basic placeholder KPIs (in prod, count docs)
        try {
            int users = fs.listUsers(null).size();
            int books = fs.listBooks(null).size();
            int loans = fs.listLoans().size();
            int active = (int)fs.listLoans().stream().filter(l->!l.returned).count();
            return java.util.Map.of("users", users, "books", books, "activeLoans", active, "monthLoans", loans);
        } catch(Exception e){
            return java.util.Map.of("users",0,"books",0,"activeLoans",0,"monthLoans",0);
        }
    }
}
