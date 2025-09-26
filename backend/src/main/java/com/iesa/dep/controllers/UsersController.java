
package com.iesa.dep.controllers;
import com.iesa.dep.models.User;
import com.iesa.dep.services.FirestoreService;
import com.iesa.dep.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    FirestoreService fs;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(value="q", required=false) String q) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(fs.listUsers(q));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody User u, @RequestHeader(value="Authorization", required=false) String auth) throws Exception {
        if(!isAdmin(auth)) return ResponseEntity.status(401).body(Map.of("error","No autorizado"));
        String id = fs.createUser(u);
        return ResponseEntity.ok(Map.of("id", id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id, @RequestHeader(value="Authorization", required=false) String auth) throws Exception {
        if(!isAdmin(auth)) return ResponseEntity.status(401).body(Map.of("error","No autorizado"));
        fs.deleteUser(id);
        return ResponseEntity.ok(Map.of("deleted", true));
    }

    @PostMapping("/import")
    public ResponseEntity<?> importXlsx(@RequestParam("file") MultipartFile file, @RequestHeader(value="Authorization", required=false) String auth) throws Exception {
        if(!isAdmin(auth)) return ResponseEntity.status(401).body(Map.of("error","No autorizado"));
        // Soporta CSV y XLSX. Intentamos leer como XLSX, si falla leemos como CSV.
        try(InputStream is = file.getInputStream()){
            Workbook wb = new XSSFWorkbook(is);
            Sheet s = wb.getSheetAt(0);
            for(int i=1;i<=s.getLastRowNum();i++){
                Row r = s.getRow(i);
                if(r==null) continue;
                String name = r.getCell(0).getStringCellValue();
                String document = r.getCell(1).getStringCellValue();
                String phone = r.getCell(2)==null? "": r.getCell(2).getStringCellValue();
                String type = r.getCell(3)==null? "Alumno": r.getCell(3).getStringCellValue();
                User u = new User(name, document, phone, type);
                fs.createUser(u);
            }
            return ResponseEntity.ok(Map.of("imported", true));
        } catch(Exception e){
            // fallback CSV
            String txt = new String(file.getBytes());
            String[] lines = txt.split("\n");
            for(int i=1;i<lines.length;i++){
                String line = lines[i].trim();
                if(line.isEmpty()) continue;
                String[] cols = line.split(",");
                User u = new User(cols[0].trim(), cols[1].trim(), cols.length>2?cols[2].trim():"", cols.length>3?cols[3].trim():"Alumno");
                fs.createUser(u);
            }
            return ResponseEntity.ok(Map.of("imported", true));
        }
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportXlsx(){
        // Export simple CSV (XLSX export puede implementarse más adelante)
        String csv = "name,document,phone,type\nEjemplo,1234,321000,Alumno\n";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=usuarios.csv")
                .body(csv);
    }

    private boolean isAdmin(String auth){
        if(auth==null || !auth.startsWith("Bearer ")) return false;
        String token = auth.substring(7);
        String user = JwtUtil.validateTokenAndGetUser(token);
        return user!=null;
    }
}
