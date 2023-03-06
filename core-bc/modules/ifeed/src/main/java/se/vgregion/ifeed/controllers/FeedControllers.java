package se.vgregion.ifeed.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.vgregion.ifeed.repository.FeedRepository;
import se.vgregion.ifeed.types.Feed;

import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedControllers {

    @Autowired
    private FeedRepository feedRepository;

    @GetMapping("/{id}")
    public Feed get(@PathVariable("id") Long id) throws HttpRequestMethodNotSupportedException {
        return feedRepository.findById(id).orElse(null);
    }

    @GetMapping
    public List<Feed> getAmnes() {
        return feedRepository.findAll();
    }

}
