package com.training.eshop.controller;

import com.training.eshop.dto.feedback.FeedbackDto;
import com.training.eshop.model.Feedback;
import com.training.eshop.service.FeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/orders/{orderId}/feedbacks")
@Api("Feedback controller")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @ApiOperation(value = "Create a new feedback", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> save(@Valid @RequestBody FeedbackDto feedbackDto,
                                  @PathVariable("orderId") Long orderId,
                                  Principal principal) {
        Feedback savedFeedback = feedbackService.save(feedbackDto, orderId, principal.getName());

        String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        String savedFeedbackLocation = currentUri + "/" + savedFeedback.getId();

        return ResponseEntity.status(CREATED)
                .header(HttpHeaders.LOCATION, savedFeedbackLocation)
                .body(savedFeedback);
    }

    @GetMapping
    @ApiOperation(value = "Get all feedbacks by order ID")
    public ResponseEntity<List<FeedbackDto>> getAllByOrderId(@PathVariable("orderId") Long orderId,
                                                             @RequestParam(value = "buttonValue", defaultValue = "default") String buttonValue) {
        return ResponseEntity.ok(feedbackService.getAllByOrderId(orderId, buttonValue));
    }
}
