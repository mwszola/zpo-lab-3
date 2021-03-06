package com.project.controller;

import com.project.model.Projekt;
import com.project.model.Student;
import com.project.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api")
public class StudentRestController {

    private final StudentService studentService;

    @Autowired
    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/studenci/{studentId}")
    ResponseEntity<Student> getStudent(@PathVariable Integer studentId) {
        return ResponseEntity.of(studentService.getStudent(studentId));
    }

    @PostMapping(path = "/studenci")
    ResponseEntity<Void> createStudent(@Valid @RequestBody Student student) {
        Student createdStudent = studentService.setStudent(student);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{studentId}").buildAndExpand(createdStudent.getStudentId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/studenci/{studentId}")
    public ResponseEntity<Void> updateStudent(@Valid @RequestBody Student student, @PathVariable Integer studentId) {
        return studentService.getStudent(studentId)
                .map(p -> {
                    studentService.setStudent(student);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/studenci/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer studentId) {
        return studentService.getStudent(studentId).map(s -> {
            studentService.deleteStudent(studentId);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/studenci")
    Page<Student> getStudenci(Pageable pageable) {
        return studentService.getStudenci(pageable);
    }

    @GetMapping(value = "/studenci/student", params = "nrIndeksu")
    ResponseEntity<Student> getStudentByNrIndeksu(@RequestParam String nrIndeksu) {
        return ResponseEntity.of(studentService.getStudentByNrIndeksu(nrIndeksu));
    }

    @GetMapping(value = "/studenci", params = "nrIndeksu")
    Page<Student> getStudenciByNrIndeksuStartsWith(@RequestParam String nrIndeksu, Pageable pageable) {
        return studentService.getStudenciByNrIndeksuStartsWith(nrIndeksu, pageable);
    }

    @GetMapping(value = "/studenci", params = "nazwisko")
    Page<Student> getStudenciByNazwiskoStartsWith(@RequestParam String nazwisko, Pageable pageable) {
        return studentService.getStudenciByNazwiskoStartsWith(nazwisko, pageable);
    }
}
