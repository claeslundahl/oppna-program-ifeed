package se.vgregion.ifeed.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/main")
public class MainController {

    @GetMapping("/{id}")
    public Map get(@PathVariable("id") Long id) throws HttpRequestMethodNotSupportedException {
        return Map.of("id", id);
    }

}
