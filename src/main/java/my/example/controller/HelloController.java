package my.example.controller;

import my.example.model.entity.Comment;
import my.example.model.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HelloController {

    @Autowired
    private CommentRepository repository;

    @GetMapping("hello")
    public String hello(Model model){

        Comment comment = new Comment();
        comment.setContent("demo");
        repository.saveAndFlush(comment);

        List<Comment> comments = repository.findAll();

        model.addAttribute("commentCount", comments.size());

        return "hello";
    }

}

