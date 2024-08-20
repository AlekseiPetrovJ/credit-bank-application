package ru.petrov.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.petrov.dto.StatementDto;
import ru.petrov.services.DealService;
import ru.petrov.util.RestUtil;
import ru.petrov.util.exceptions.StatementNotFoundException;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/deal/admin", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DealAdminController {
    private final DealService dealService;
    private final RestUtil restUtil;

    @PutMapping("/statement/{statementId}/status")
    public ResponseEntity updateDocument(@PathVariable("statementId") UUID uuid) {
        log.info("PUT response to path /admin/statement/{}/status", uuid);
        try {
            dealService.getStatementById(uuid);
            log.info("PUT to path /admin/statement/{}/status returned HttpStatus.OK");
            return new ResponseEntity(HttpStatus.OK);
        } catch (StatementNotFoundException e) {
            log.error("PUT response to path /admin/statement/{}/status was NOT_FOUND", uuid);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/statement/{statementId}")
    public ResponseEntity<StatementDto> getStatement(@PathVariable("statementId") UUID uuid) {
        log.info("GET response to path /admin/statement/{}", uuid);
        try {
            ResponseEntity responseEntity = new ResponseEntity(dealService.getStatementById(uuid), HttpStatus.OK);
            log.info("GET to path /admin/statement/{} returned response {}", uuid, responseEntity);
            return responseEntity;
        } catch (StatementNotFoundException e) {
            log.error("GET response to path /admin/statement/{} returned NOT_FOUND", uuid);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/statement")
    public ResponseEntity<StatementDto> getStatement() {
        log.info("GET response to path /admin/statement/");
        try {
            ResponseEntity responseEntity = new ResponseEntity(dealService.getAllStatement(), HttpStatus.OK);
            log.info("GET to path /admin/statement returned response {}", responseEntity);
            return responseEntity;
        } catch (StatementNotFoundException e) {
            log.error("\"GET to path /admin/statement returned NOT_FOUND");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
