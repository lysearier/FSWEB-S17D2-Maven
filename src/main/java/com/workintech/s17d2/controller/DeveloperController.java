package com.workintech.s17d2.controller;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers;
    private final Taxable taxService;

    public DeveloperController(Taxable taxService) {
        this.taxService = taxService;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAllDevelopers() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Developer> getDeveloperById(@PathVariable int id) {
        Developer developer = developers.get(id);
        if (developer != null) {
            return ResponseEntity.ok(developer);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping
    public ResponseEntity<Developer> addDeveloper(@RequestBody Developer developer) {
        Developer newDev = switch (developer.getExperience()) {
            case JUNIOR -> new JuniorDeveloper(developer.getId(), developer.getName(),
                    developer.getSalary() - (developer.getSalary() * taxService.getSimpleTaxRate() / 100));
            case MID -> new MidDeveloper(developer.getId(), developer.getName(),
                    developer.getSalary() - (developer.getSalary() * taxService.getMiddleTaxRate() / 100));
            case SENIOR -> new SeniorDeveloper(developer.getId(), developer.getName(),
                    developer.getSalary() - (developer.getSalary() * taxService.getUpperTaxRate() / 100));
        };

        developers.put(newDev.getId(), newDev);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDev);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Developer> updateDeveloper(@PathVariable int id, @RequestBody Developer updatedDev) {
        if (developers.containsKey(id)) {
            developers.put(id, updatedDev);
            return ResponseEntity.ok(updatedDev);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeveloper(@PathVariable int id) {
        if (developers.containsKey(id)) {
            developers.remove(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
