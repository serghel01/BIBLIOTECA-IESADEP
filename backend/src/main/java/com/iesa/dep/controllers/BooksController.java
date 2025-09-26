
package com.iesa.dep.controllers;
import com.iesa.dep.models.Book;
import com.iesa.dep.services.FirestoreService;
import com.iesa.dep.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.ExecutionException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;

@RestController
@RequestMapping("/api/books")
public class BooksController {

    @Autowired
    FirestoreService fs;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(value="q", required=false) String q) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(fs.listBooks(q));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Book b, @RequestHeader(value="Authorization", required=false) String auth) throws Exception {
        if(!isAdmin(auth)) return ResponseEntity.status(401).body("No autorizado");
        String id = fs.createBook(b);
        return ResponseEntity.ok(java.util.Map.of("id", id));
    }

    @PostMapping("/import")
    public ResponseEntity<?> importXlsx(@RequestParam("file") MultipartFile file, @RequestHeader(value="Authorization", required=false) String auth) throws Exception {
        if(!isAdmin(auth)) return ResponseEntity.status(401).body("No autorizado");
        try(InputStream is = file.getInputStream()){
            Workbook wb = new XSSFWorkbook(is);
            Sheet s = wb.getSheetAt(0);
            for(int i=1;i<=s.getLastRowNum();i++){
                Row r = s.getRow(i);
                if(r==null) continue;
                Book b = new Book();
                b.isbn = r.getCell(0).getStringCellValue();
                b.title = r.getCell(1).getStringCellValue();
                b.author = r.getCell(2).getStringCellValue();
                b.state = r.getCell(3)==null? "Bueno": r.getCell(3).getStringCellValue();
                b.location = r.getCell(4)==null? "": r.getCell(4).getStringCellValue();
                b.branch = r.getCell(5)==null? "Sede Central": r.getCell(5).getStringCellValue();
                fs.createBook(b);
            }
            return ResponseEntity.ok(java.util.Map.of("imported", true));
        } catch(Exception e){
            String txt = new String(file.getBytes());
            String[] lines = txt.split("\n");
            for(int i=1;i<lines.length;i++){
                String line = lines[i].trim();
                if(line.isEmpty()) continue;
                String[] cols = line.split(",");
                Book b = new Book();
                b.isbn = cols.length>0?cols[0].trim():"";
                b.title = cols.length>1?cols[1].trim():"";
                b.author = cols.length>2?cols[2].trim():"";
                b.state = cols.length>3?cols[3].trim():"Bueno";
                b.location = cols.length>4?cols[4].trim():"";
                b.branch = cols.length>5?cols[5].trim():"Sede Central";
                fs.createBook(b);
            }
            return ResponseEntity.ok(java.util.Map.of("imported", true));
        }
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportCsv(){
        String csv = "isbn,title,author,state,location,branch\n978-123,Ejemplo,Autor,Bueno,Stand A,Sede Central\n";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=libros.csv")
                .body(csv);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id, @RequestHeader(value="Authorization", required=false) String auth) throws Exception {
        if(!isAdmin(auth)) return ResponseEntity.status(401).body("No autorizado");
        fs.deleteBook(id);
        return ResponseEntity.ok().build();
    }

    private boolean isAdmin(String auth){
        if(auth==null || !auth.startsWith("Bearer ")) return false;
        String token = auth.substring(7);
        String user = JwtUtil.validateTokenAndGetUser(token);
        return user!=null;
    }
}
