package com.SmartParking.ai_service.services.llmInteractionServices;

import com.SmartParking.ai_service.dtos.BoundingBoxes;
import com.SmartParking.ai_service.dtos.LlmSlotInfoResponseDto;
import com.SmartParking.ai_service.dtos.SlotCountInfo;

public interface LlmService {

    LlmSlotInfoResponseDto getSlotInfo(byte[] imageBytes, BoundingBoxes boundingBoxes,SlotCountInfo slotCountInfo);
}
