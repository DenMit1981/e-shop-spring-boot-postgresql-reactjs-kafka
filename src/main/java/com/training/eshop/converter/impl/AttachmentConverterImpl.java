package com.training.eshop.converter.impl;

import com.training.eshop.converter.AttachmentConverter;
import com.training.eshop.dto.attachment.AttachmentDto;
import com.training.eshop.model.Attachment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AttachmentConverterImpl implements AttachmentConverter {

    @Override
    public AttachmentDto convertToAttachmentDto(Attachment attachment) {
        AttachmentDto attachmentDto = new AttachmentDto();

        attachmentDto.setId(attachment.getId());
        attachmentDto.setName(attachment.getName());
        attachmentDto.setFile(attachment.getFile());

        return attachmentDto;
    }

    @Override
    public Attachment fromAttachmentDto(AttachmentDto attachmentDto) {
        Attachment attachment = new Attachment();

        attachment.setId(attachmentDto.getId());
        attachment.setName(attachmentDto.getName());
        attachment.setFile(attachmentDto.getFile());

        return attachment;
    }
}
