package ru.practicum.event.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUserUpdateRequestDto extends EventUpdateRequestDto {
    private StateAction stateAction;

    public enum StateAction {
        SEND_TO_REVIEW, CANCEL_REVIEW
    }
}
