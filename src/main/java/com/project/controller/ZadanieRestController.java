package com.project.controller;

import com.project.model.Projekt;
import com.project.model.Zadanie;
import com.project.service.ProjektService;
import com.project.service.ZadanieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
public class ZadanieRestController {

    private final ZadanieService zadanieService;
    private final ProjektService projektService;

    @Autowired
    public ZadanieRestController(ZadanieService zadanieService, ProjektService projektService) {
        this.zadanieService = zadanieService;
        this.projektService = projektService;
    }

    @GetMapping("/zadania/{zadanieId}")
    ResponseEntity<Zadanie> getZadanie(@PathVariable Integer zadanieId) {
        return ResponseEntity.of(zadanieService.getZadanie(zadanieId));
    }

    @PostMapping(path = "/zadania", params = "projektId")
    ResponseEntity<Void> createZadanie(@Valid @RequestBody Zadanie zadanie, @RequestParam Integer projektId) {
        // Utworzenie zadania
        Zadanie createdZadanie = zadanieService.setZadanie(zadanie);

        // Wyszukanie odpowiadajacego projektu
        Optional<Projekt> projekt = projektService.getProjekt(projektId);

        if (projekt.isPresent()) {
            // Przypisanie zadania do projektu
            List<Zadanie> newZadania = Stream.concat(
                    projekt.get().getZadania().stream(),
                    Stream.of(createdZadanie)
            ).collect(Collectors.toList());

            projekt.get().setZadania(newZadania);
        } else {
            // Projekt o podanym ID nie zostal znaleziony - 404
            return ResponseEntity.notFound().build();
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{zadanieId}").buildAndExpand(createdZadanie.getZadanieId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/zadania/{zadanieId}")
    ResponseEntity<Void> updateZadanie(@Valid @RequestBody Zadanie zadanie, @PathVariable Integer zadanieId) {
        return zadanieService.getZadanie(zadanieId)
                .map(z -> {
                    zadanieService.setZadanie(zadanie);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/zadania/{zadanieId}")
    public ResponseEntity<Void> deleteZadanie(@PathVariable Integer zadanieId) {
        return zadanieService.getZadanie(zadanieId).map(z -> {
            zadanieService.deleteZadanie(zadanieId);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/zadania")
    Page<Zadanie> getZadania(Pageable pageable) {
        return zadanieService.getZadania(pageable);
    }

    @GetMapping(value = "/zadania", params = "projektId")
    Page<Zadanie> getZadaniaByProjektId(@RequestParam Integer projektId, Pageable pageable) {
        return zadanieService.getZadaniaProjektu(projektId, pageable);
    }
}
