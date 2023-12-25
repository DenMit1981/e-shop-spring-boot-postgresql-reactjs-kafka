package com.training.eshop.converter.impl;

import com.training.eshop.converter.FeedbackConverter;
import com.training.eshop.dto.feedback.FeedbackDto;
import com.training.eshop.model.Feedback;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FeedbackConverterImpl implements FeedbackConverter {

    @Override
    public FeedbackDto convertToFeedbackDto(Feedback feedback) {
        FeedbackDto feedbackDto = new FeedbackDto();

        feedbackDto.setUser(feedback.getUser().getName());
        feedbackDto.setRate(feedback.getRate());
        feedbackDto.setDate(feedback.getDate());
        feedbackDto.setText(feedback.getText());

        return feedbackDto;
    }

    @Override
    public Feedback fromFeedbackDto(FeedbackDto feedbackDto) {
        Feedback feedback = new Feedback();

        feedback.setRate(feedbackDto.getRate());
        feedback.setText(feedbackDto.getText());

        return feedback;
    }
}
