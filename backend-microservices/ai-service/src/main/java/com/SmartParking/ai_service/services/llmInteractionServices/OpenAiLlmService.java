package com.SmartParking.ai_service.services.llmInteractionServices;


import com.SmartParking.ai_service.constants.Prompts.ImporvementPromptConstants;
import com.SmartParking.ai_service.constants.Prompts.SlotCountFindPromptsConstants;
import com.SmartParking.ai_service.dtos.BoundingBoxes;
import com.SmartParking.ai_service.dtos.LlmSlotInfoResponseDto;
import com.SmartParking.ai_service.dtos.SlotCountInfo;
import com.SmartParking.ai_service.exceptions.LlmServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Service
public class OpenAiLlmService implements LlmService{

    private static final Logger log = LogManager.getLogger(OpenAiLlmService.class);

    private final ChatModel chatModel;

    public OpenAiLlmService(@Qualifier("openAiChatModel")ChatModel chatModel) {
        this.chatModel = chatModel;
    }


    public LlmSlotInfoResponseDto getSlotInfo(byte[] imageBytes, BoundingBoxes boundingBoxes,SlotCountInfo slotCountInfo){
//        var imageResource = new ClassPathResource("esp01_chn_4_11.31.01.jpeg");
        Resource imageResource = new ByteArrayResource(imageBytes);
        var outputConverter = new BeanOutputConverter<>(LlmSlotInfoResponseDto.class);
        var jsonSchema = outputConverter.getJsonSchema();

        // prompt sent to llm
        String promptString =   SlotCountFindPromptsConstants.HEADER +
                                boundingBoxes +
                                SlotCountFindPromptsConstants.BODY_1+
                                slotCountInfo +
                                SlotCountFindPromptsConstants.FOOTER+
                                ImporvementPromptConstants.ZERO_SHOT_CHAIN_OF_THOUGHTS_ADD_ON_1;
        log.debug("prompt-----------------------------------------------------------");
        log.debug(promptString);
        log.debug("prompt-----------------------------------------------------------");


        var userMessage = UserMessage.builder()
                .text(promptString)
                .media(new Media(MimeTypeUtils.IMAGE_JPEG, imageResource)) // media
                .build();

        Prompt prompt = new Prompt(List.of(userMessage),
                OpenAiChatOptions.builder()
                       // .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
                        .build());

        try {
            ChatResponse response = chatModel.call(prompt);
            String content = response.getResult().getOutput().getText();

            log.info("LLM response received");

            return outputConverter.convert(content);
        } catch (RuntimeException ex) {
            String message = "Failed to obtain slot information from LLM";
            log.error("{}: {}", message, ex.getMessage(), ex);
            throw new LlmServiceException(message, ex);
        }
    }


}
