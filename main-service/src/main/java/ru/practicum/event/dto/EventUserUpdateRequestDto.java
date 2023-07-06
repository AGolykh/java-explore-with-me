package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUserUpdateRequestDto extends EventUpdateRequestDto {
    private StateAction stateAction;

    public enum StateAction {
        SEND_TO_REVIEW, CANCEL_REVIEW
    }
}
