package com.training.eshop.controller;

import com.training.eshop.dto.comment.CommentDto;
import com.training.eshop.model.Comment;
import com.training.eshop.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/orders/{orderId}/comments")
@Api("Comment controller")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ApiOperation(value = "Create a new comment")
    public ResponseEntity<?> save(@Valid @RequestBody CommentDto commentDto, @PathVariable("orderId") Long orderId) {
        Comment savedComment = commentService.save(commentDto, orderId);

        String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        String savedCommentLocation = currentUri + "/" + savedComment.getId();

        return ResponseEntity.status(CREATED)
                .header(HttpHeaders.LOCATION, savedCommentLocation)
                .body(savedComment);
    }

    @GetMapping
    @ApiOperation(value = "Get all comments by order ID")
    public ResponseEntity<List<CommentDto>> getAllByOrderId(@PathVariable("orderId") Long orderId,
                                                            @RequestParam(value = "buttonValue", defaultValue = "default") String buttonValue) {
        return ResponseEntity.ok(commentService.getAllByOrderId(orderId, buttonValue));
    }
}
